package com.mapscloud.download;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadEntity;
import com.arialyy.aria.core.download.DownloadTarget;
import com.arialyy.aria.core.download.DownloadTask;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapscloud.download.bean.DownLoadInfoEntity;
import com.mapscloud.download.bean.TileGridBean;
import com.mapscloud.download.bean.TileGridPolyBean;
import com.mapscloud.download.http.RetrofitManager;
import com.mapscloud.download.tools.MapUtils;
import com.mapscloud.download.tools.MercatorUtils;
import com.mapscloud.download.view.DownloadTileActivity;
import com.mapscloud.download.view.ItemsDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by mapscloud8 on 2018/5/21.
 */

public class DownloadTileManager {

    private Activity activity;
    private MapView mapView;
    private MapboxMap mMapboxMap;

    private int ZOOM = 3;
    private int width = 0;         // 屏幕宽度（像素）
    private int height = 0;       // 屏幕高度（像素）

    List<List<LatLng>> lineLatlng = new ArrayList<>();
    List<List<LatLng>> polyLatlng = new ArrayList<>();
    List<List<LatLng>> downloadedLatlng = new ArrayList<>();
    public static List<TileGridPolyBean> listPoly = new ArrayList<>();
    boolean isShowGrid = false;
    List<String> sourceName = new ArrayList<>();
    public static final String  PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mapboom/data/";

    public static String DOWNLOADTILE_SOURCE_ID = "downloadtile_source_id";
    public static String DOWNLOADTILE_LAYER_NAME = "downloadtile_layer_name";

    public boolean isContinueDownload = false;

    private DownloadTileManager(){}
    private static DownloadTileManager recordUtils = new DownloadTileManager();

    public static DownloadTileManager getInstance(){
        if(recordUtils == null)
            recordUtils = new DownloadTileManager();
        return recordUtils;
    }

    public void init(Activity activity, MapView mapView, MapboxMap mMapboxMap, List<String> sourceName){
        this.activity = activity;
        this.mapView = mapView;
        this.mMapboxMap = mMapboxMap;
        if(sourceName != null && sourceName.size() > 0){
            this.sourceName.clear();
            this.sourceName.addAll(sourceName);
        }
        if(mMapboxMap != null){
            mMapboxMap.addOnMoveListener(new MapboxMap.OnMoveListener() {
                @Override
                public void onMoveBegin(@NonNull MoveGestureDetector moveGestureDetector) {
                }
                @Override
                public void onMove(@NonNull MoveGestureDetector moveGestureDetector) {
                }
                @Override
                public void onMoveEnd(@NonNull MoveGestureDetector moveGestureDetector) {
                    Log.e("<<<<----", "111111111111111111111111");
                    if(isShowGrid && ZOOM > 3)
                        handler.sendEmptyMessage(0);
                }
            });

            mMapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                @Override
                public void onMapClick(@NonNull LatLng latLng) {
                    if(isShowGrid){
                        if(isContain(latLng)){//如果已包含点击格子，则删除格子并刷新
                            MapUtils.getInstance().addPolysGeojson(polyLatlng, false, "", "", true);
                        } else {//如果不包含格子，则添加格子并显示
                            addPolyTile(latLng);
                        }
                    }
                }
            });
        }

        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;         // 屏幕宽度（像素）
        height = dm.heightPixels;       // 屏幕高度（像素）

        //继续未完成的下载
        if(isContinueDownload){
            continueDownload();
        }

    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0){
                calculateGridNumber(1);
            }
        }
    };

    public void showDialog(final Activity activity){
        ItemsDialogFragment itemsDialogFragment = new ItemsDialogFragment();
        String[] items = {"展示3级格子", "展示8级格子", "清除地图", "下载基础功能包", "下载当前选中格子", "下载列表"};
        itemsDialogFragment.show("地图格子下载管理", items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    ZOOM = 3;
                    isShowGrid = true;
                    calculateGridNumber(0);
                } else if(which == 1){
                    ZOOM = 8;
                    isShowGrid = true;
                    calculateGridNumber(0);
                } else if(which == 2){
                    MapUtils.getInstance().clearMap();
                    lineLatlng.clear();
                    MapUtils.getInstance().addLinesGeojson(lineLatlng, false, null, null, null, true);
                    listPoly.clear();
                    polyLatlng.clear();
                    MapUtils.getInstance().addPolysGeojson(polyLatlng, false, "", "", true);
                    downloadedLatlng.clear();
                    MapUtils.getInstance().addPolysGeojson(downloadedLatlng, false, DOWNLOADTILE_SOURCE_ID, DOWNLOADTILE_LAYER_NAME, true);
                    isShowGrid = false;
                } else if(which == 3){
                    getDonwloadBaseData(getStyleNameByStyleUrl(mMapboxMap.getStyleUrl()), new Handler(){
                        @SuppressLint("CheckResult")
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            if(msg.what == 0){//成功
                                JSONObject json = (JSONObject) msg.obj;
                                try {
                                    if(json.getInt("code") == 0){
                                        List<DownLoadInfoEntity> list = getBaseMapData(json);

                                        if(list != null && list.size() > 0){
                                            for(DownLoadInfoEntity entity : list){
                                                File file = new File(PATH + "/" + entity.getSoureceName() + "/" + entity.getTileName() + ".std");
                                                if(file != null && file.exists()){
                                                    file.delete();
                                                }

                                                DownloadTarget downloadTarget = Aria.download(activity).load(entity.getDownLoadUrl());
                                                downloadTarget.getDownloadEntity().setDisposition(PATH + "/" + entity.getSoureceName() + "/" + entity.getTileName() + ".std");
                                                downloadTarget.setFilePath(PATH + "/" + entity.getSoureceName() + "/" + entity.getTileName() + ".std").start();
                                                entity.setState(4);
                                            }
                                        }

                                        Toast.makeText(activity, "开始下载!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(activity, "参数错误!", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else if(msg.what == -1){//失败
                                Toast.makeText(activity, "网络错误!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                } else if(which == 4){
                    String packages = "";
                    for(int i = 0; i < listPoly.size(); i ++){
                        packages += listPoly.get(i).getBean().getTileStrCode() + ((i + 1) == listPoly.size() ? "" : ";");
                    }
                    getDonwloadData(getStyleNameByStyleUrl(mMapboxMap.getStyleUrl()), packages, new Handler(){
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            if(msg.what == 0){//成功
                                JSONObject json = (JSONObject) msg.obj;
                                try {
                                    if(json.getInt("code") == 0){
                                        JSONArray data = json.getJSONArray("file");
                                        List<DownLoadInfoEntity> list = new ArrayList<>();
                                        List<DownLoadInfoEntity> list3 = getDownloadBeanByJsonArray(data);
                                        if(list3 != null && list3.size() > 0){
                                            for(DownLoadInfoEntity entity : list3){
                                                File file = new File(PATH + "/" + entity.getSoureceName() + "/" + entity.getTileName() + ".std");
                                                if(file != null && file.exists()){
                                                    file.delete();
                                                }

                                                DownloadTarget downloadTarget = Aria.download(activity).load(entity.getDownLoadUrl());
                                                downloadTarget.getDownloadEntity().setDisposition(PATH + "/" + entity.getSoureceName() + "/" + entity.getTileName() + ".std");
                                                downloadTarget.setFilePath(PATH + "/" + entity.getSoureceName() + "/" + entity.getTileName() + ".std").start();
                                                entity.setState(4);
                                            }
                                        }

                                        if(list3 != null)
                                            list.addAll(list3);

                                        Toast.makeText(activity, "开始下载!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(activity, "参数错误!", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else if(msg.what == -1){//失败
                                Toast.makeText(activity, "网络错误!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else if(which == 5){
                    activity.startActivity(new Intent(activity, DownloadTileActivity.class));

                }

            }
        }, activity.getFragmentManager());
    }

    /***
     * 根据当前屏幕尺寸计算瓦片网格
     * @param type 0 初始展示网格， 1 拖动地图加载新网格
     */
    private void calculateGridNumber(int type){
        LatLng latLng_lt = mMapboxMap.getProjection().fromScreenLocation(new PointF(0, 0));
        LatLng latLng_rb = mMapboxMap.getProjection().fromScreenLocation(new PointF(width, height));

        TileGridBean lt_xy = null;
        TileGridBean rb_xy = null;
        if(ZOOM == 8){
            lt_xy = new TileGridBean(MercatorUtils.latlon2TileXY(latLng_lt.getLatitude(), latLng_lt.getLongitude(), ZOOM), ZOOM);
            rb_xy = new TileGridBean(MercatorUtils.latlon2TileXY(latLng_rb.getLatitude(), latLng_rb.getLongitude(), ZOOM), ZOOM);
        } else if(ZOOM == 3){
            lt_xy = new TileGridBean(0, 0, ZOOM);
            rb_xy = new TileGridBean((int) (Math.pow(2, ZOOM) - 1), (int) (Math.pow(2, ZOOM) - 1),ZOOM);
        }

        List<List<TileGridBean>> data = new ArrayList<>();
        for(int i = lt_xy.getX(); i <= rb_xy.getX() + 1; i ++){
            List<TileGridBean> list = new ArrayList<>();
            int x = i;
            if(i == Math.pow(2, ZOOM))
                x = 0;
            for(int c = lt_xy.getY(); c <= rb_xy.getY() + 1; c ++){
                int y = c;
                if(c == Math.pow(2, ZOOM))
                    y = 0;
                TileGridBean bean = new TileGridBean(x, y, ZOOM);
                list.add(bean);
            }
            data.add(list);
        }
        drawGrid(data);

        if(type == 0){
            //添加已下载的格子数据
//            List<DownLoadInfoEntity> list = DownLoadInfoEntity.listAll(DownLoadInfoEntity.class);
            List<DownloadEntity> list = Aria.download(activity).getAllNotCompletTask();
            if(list != null && list.size() > 0){
                List<TileGridBean> beans = new ArrayList<>();
                for(DownloadEntity downloadEntity : list){
                    TileGridBean bean = DownloadEntity2TileGridBean(downloadEntity);
                    if(bean != null)
                        beans.add(bean);
                }
                addPolyTiles(beans);
            }
        }

    }

    //根据网格画到地图上
    private void drawGrid(List<List<TileGridBean>> data){
        if(data == null || data.size() < 1)
            return;
        lineLatlng.clear();

        //画横线
        for(int i =0; i < data.size(); i ++){
            List<LatLng> list = new ArrayList<>();
            for(int c = 0; c < data.get(i).size(); c ++){
                double[] latlng = MercatorUtils.tileXY2LatLon(data.get(i).get(c).getX(), data.get(i).get(c).getY(), data.get(i).get(c).getZ());
                list.add(new LatLng(latlng[0], latlng[1]));
            }
            lineLatlng.add(list);
        }
        for(int i = 0; i < data.get(0).size(); i ++){
            List<LatLng> list = new ArrayList<>();
            for(int c = 0; c < data.size(); c ++){
                double[] latlng = MercatorUtils.tileXY2LatLon(data.get(c).get(i).getX(), data.get(c).get(i).getY(), data.get(c).get(i).getZ());
                list.add(new LatLng(latlng[0], latlng[1]));
            }
            lineLatlng.add(list);
        }
        MapUtils.getInstance().addLinesGeojson(lineLatlng, false, null, null, null, true);
    }

    //判断是否是已有瓦片格子，如果包含，则直接删除
    private boolean isContain(LatLng latLng){
        TileGridBean grid = new TileGridBean(MercatorUtils.latlon2TileXY(latLng.getLatitude(), latLng.getLongitude(), ZOOM), ZOOM);
        for(int i = 0; i < listPoly.size(); i ++){
            if(grid.getTileStrCode().equals(listPoly.get(i).getBean().getTileStrCode())){
                listPoly.remove(i);
                polyLatlng.remove(i);
                return true;
            }
        }
        return false;
    }

    //根据点击的经纬度将瓦片格子画上
    public void addPolyTile(LatLng latLng){
        TileGridBean bean = new TileGridBean(MercatorUtils.latlon2TileXY(latLng.getLatitude(), latLng.getLongitude(), ZOOM), ZOOM);
        addPolyTile(bean);
    }

    //将用户点击的，未下载的格子上图，并保存到预下载列表中。
    public void addPolyTile(TileGridBean bean){
        List<LatLng> list = new ArrayList<>();
        double[] latlng1 = MercatorUtils.tileXY2LatLon(bean.getX(), bean.getY(), bean.getZ());
        double[] latlng2 = MercatorUtils.tileXY2LatLon((bean.getX() + 1) > (Math.pow(2, bean.getZ()) - 1) ? 0 : (bean.getX() + 1), bean.getY(), bean.getZ());
        double[] latlng3 = MercatorUtils.tileXY2LatLon((bean.getX() + 1) > (Math.pow(2, bean.getZ()) - 1) ? 0 : (bean.getX() + 1), (bean.getY() + 1) > (Math.pow(2, bean.getZ()) - 1) ? 0 : (bean.getY() + 1), bean.getZ());
        double[] latlng4 = MercatorUtils.tileXY2LatLon(bean.getX(), (bean.getY() + 1) > (Math.pow(2, bean.getZ()) - 1) ? 0 : (bean.getY() + 1), bean.getZ());
        list.add(new LatLng(latlng1[0], latlng1[1]));
        list.add(new LatLng(latlng2[0], latlng2[1]));
        list.add(new LatLng(latlng3[0], latlng3[1]));
        list.add(new LatLng(latlng4[0], latlng4[1]));
        listPoly.add(new TileGridPolyBean(bean, latlng1, latlng2, latlng3, latlng4));
        polyLatlng.add(list);
        MapUtils.getInstance().addPolysGeojson(polyLatlng, false, "", "", true);
    }

    //将已下载的格子上图，不保存到预下载列表
    private void addPolyTiles(List<TileGridBean> beans){
        if(beans != null && beans.size() > 0){
            for(TileGridBean bean : beans){
                List<LatLng> list = new ArrayList<>();
                double[] latlng1 = MercatorUtils.tileXY2LatLon(bean.getX(), bean.getY(), bean.getZ());
                double[] latlng2 = MercatorUtils.tileXY2LatLon((bean.getX() + 1) > (Math.pow(2, bean.getZ()) - 1) ? 0 : (bean.getX() + 1), bean.getY(), bean.getZ());
                double[] latlng3 = MercatorUtils.tileXY2LatLon((bean.getX() + 1) > (Math.pow(2, bean.getZ()) - 1) ? 0 : (bean.getX() + 1), (bean.getY() + 1) > (Math.pow(2, bean.getZ()) - 1) ? 0 : (bean.getY() + 1), bean.getZ());
                double[] latlng4 = MercatorUtils.tileXY2LatLon(bean.getX(), (bean.getY() + 1) > (Math.pow(2, bean.getZ()) - 1) ? 0 : (bean.getY() + 1), bean.getZ());
                list.add(new LatLng(latlng1[0], latlng1[1]));
                list.add(new LatLng(latlng2[0], latlng2[1]));
                list.add(new LatLng(latlng3[0], latlng3[1]));
                list.add(new LatLng(latlng4[0], latlng4[1]));
                downloadedLatlng.add(list);
            }
            MapUtils.getInstance().addPolysGeojson(downloadedLatlng, false, DOWNLOADTILE_SOURCE_ID, DOWNLOADTILE_LAYER_NAME, true);
        }
    }

    /***
     * 根据样式名称和格子号列表获取下载列表
     * @param styleName 样式名称:global
     * @param handler 用于回调使用，waht=0 成功；waht=-1失败。
     */
    private void getDonwloadData(String styleName, String packages, final Handler handler){
        RetrofitManager.getInstance()
            .getDownloadDataService()
            .getDownloadList(styleName, packages)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Subscriber<ResponseBody>() {
                @Override
                public void onCompleted() {
                }
                @Override
                public void onError(Throwable e) {
                    if(handler != null)
                        handler.sendEmptyMessage(-1);
                }
                @Override
                public void onNext(ResponseBody body) {
                    try {
                        if(handler != null){
                            String s = body.string();
                            JSONObject json = new JSONObject(s);
                            Message msg = new Message();
                            msg.what = 0;
                            msg.obj = json;
                            handler.sendMessage(msg);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
    }

    /***
     * 根据样式名称表获基础功能包取下载列表
     * @param styleName 样式名称:global
     * @param handler 用于回调使用，waht=0 成功；waht=-1失败。
     */
    private void getDonwloadBaseData(String styleName, final Handler handler){
        RetrofitManager.getInstance()
                .getDownloadDataService()
                .getDownloadBaseMap(styleName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {
                    }
                    @Override
                    public void onError(Throwable e) {
                        if(handler != null)
                            handler.sendEmptyMessage(-1);
                    }
                    @Override
                    public void onNext(final ResponseBody body) {
                        try {
                            if(handler != null){
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String s = null;
                                        try {
                                            s = body.string();
                                            JSONObject json = new JSONObject(s);
                                            Message msg = new Message();
                                            msg.what = 0;
                                            msg.obj = json;
                                            handler.sendMessage(msg);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /***
     * 通过jsonArray获取需要下载的bean数据
     * @param data
     * @return
     */
    private List<DownLoadInfoEntity> getDownloadBeanByJsonArray(JSONArray data){
        List<DownLoadInfoEntity> list = new ArrayList<>();
        try{
            for(int i = 0; i < data.length(); i ++){
                JSONObject jsonObject = data.getJSONObject(i);
                String sourceName = "sources/" + jsonObject.getString("source_name");
                String releaseDate = jsonObject.getString("release_date");
                int version = jsonObject.getInt("version");
                int downloadStatus = 0;
                //下载source的tilejson.json
                DownLoadInfoEntity beanJson = new DownLoadInfoEntity();
                beanJson.setDownLoadUrl(jsonObject.getString("tilejson_url"));
                beanJson.setTileSize("");
                beanJson.setVersion(version);
                beanJson.setReleaseDate(releaseDate);
                beanJson.setTileName("tilejson.json");
                beanJson.setSoureceName(sourceName);
                beanJson.setMd5("");
                beanJson.setDownloadStatus(downloadStatus);

                String url1 = beanJson.getDownLoadUrl();

                DownloadTarget targetJson = Aria.download(activity.getApplicationContext())
                        .load(beanJson.getDownLoadUrl())
                        .setFilePath(DownloadTileManager.PATH + beanJson.getSoureceName() + "/" + url1.substring(url1.lastIndexOf("/")));
                beanJson.setCurrprogress(targetJson != null ? targetJson.getCurrentProgress() : 0);
                beanJson.setStatu(targetJson != null ? targetJson.getTaskState() : 0);

                list.add(beanJson);
//                //保存到数据库
//                beanJson.sugerSave();
                JSONArray jsonArray = jsonObject.getJSONArray("package_list");
                //忽略过滤
                if(jsonArray != null && jsonArray.length() > 0){//下载source的 mbtiles
                for(int c = 0; c < jsonArray.length(); c ++){
                    JSONObject json = jsonArray.getJSONObject(c);
                    DownLoadInfoEntity bean = new DownLoadInfoEntity();
                    bean.setDownLoadUrl(json.getString("url"));
                    bean.setTileSize(json.getString("size"));
                    bean.setVersion(version);
                    bean.setReleaseDate(releaseDate);
                    bean.setTileName(json.getString("name") + ".mbtiles");
                    bean.setSoureceName(sourceName);
                    bean.setMd5(json.getString("md5"));
                    bean.setDownloadStatus(downloadStatus);

                    String url2 = beanJson.getDownLoadUrl();

                    DownloadTarget target = Aria.download(activity.getApplicationContext())
                            .load(bean.getDownLoadUrl())
                            .setFilePath(DownloadTileManager.PATH + beanJson.getSoureceName() + "/" + url1.substring(url1.lastIndexOf("/")));
                    bean.setCurrprogress(target != null ? target.getCurrentProgress() : 0);
                    bean.setStatu(target != null ? target.getTaskState() : 0);

                    list.add(bean);
//                    //保存到数据库
//                    bean.sugerSave();
                    }
                }
            }
            return list;
        } catch (Exception e){
            list.clear();
            return list;
        }
    }

    public List<DownLoadInfoEntity> getBaseMapData(JSONObject json){

        try {
            JSONArray data = json.getJSONArray("file");
            String sprite = json.getString("sprite");
            JSONArray fonts = json.getJSONArray("fonts");

            List<DownLoadInfoEntity> list = new ArrayList<>();
            List<DownLoadInfoEntity> list1 = getDownloadBeanBySpriteJson(getStyleNameByStyleUrl(mMapboxMap.getStyleUrl()), sprite);
            List<DownLoadInfoEntity> list2 = getDownloadBeanByFontJson(getStyleNameByStyleUrl(mMapboxMap.getStyleUrl()), fonts);
            List<DownLoadInfoEntity> list3 = getDownloadBeanByJsonArray(data);

            if(list1 != null && list1.size() > 0)
                list.addAll(list1);
            if(list2 != null && list2.size() > 0)
                list.addAll(list2);
            if(list3 != null && list3.size() > 0)
                list.addAll(list3);
            return list;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }


    private List<DownLoadInfoEntity> getDownloadBeanBySpriteJson(String styleName, String sprite) {

        if(null == mMapboxMap){
            return null ;
        }

        if(sprite != null){
            List<DownLoadInfoEntity> list = null;
            String pathAs = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mapboom/data/styles/" + styleName + "/";

            File files = new File(pathAs);
            if(!files.exists()){
                files.mkdir();
            }

            File fileStyle = new File(pathAs + "style.json");
            if(!fileStyle.exists()){
                DownLoadInfoEntity bean = new DownLoadInfoEntity();
                bean.setDownLoadUrl(sprite);
                bean.setTileSize("128kb");
                bean.setVersion(1);
                bean.setReleaseDate("-1");
                bean.setTileName(styleName + "_sprite.zip");
                bean.setSoureceName("styles/" + styleName);
                bean.setMd5("");
                bean.setDownloadStatus(0);

                if(list == null)
                    list = new ArrayList<>();
                list.add(bean);
            }
            return list;
        } else {
            return null;
        }
    }


    private List<DownLoadInfoEntity> getDownloadBeanByFontJson(String styleName,JSONArray fonts) {
        if(fonts != null && fonts.length() > 0) {
            List<DownLoadInfoEntity> list = null;
            String pathAs = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mapboom/data/fonts/";

            File files = new File(pathAs);
            if(!files.exists()){
                files.mkdir();
            }

            for(int i = 0; i < fonts.length(); i ++){
                try {
                    JSONObject jsonObject = fonts.getJSONObject(i);

                    String url = jsonObject.getString("url");
                    String name = jsonObject.getString("name");
                    String size = jsonObject.getString("size");
                    File file = new File(pathAs + name + ".zip");
                    if(!file.exists()){
                        DownLoadInfoEntity bean = new DownLoadInfoEntity();
                        bean.setDownLoadUrl(url);
                        bean.setTileSize(size);
                        bean.setVersion(1);
                        bean.setReleaseDate("-1");
                        bean.setTileName(name + ".zip");
                        bean.setSoureceName("fonts");
                        bean.setMd5("");
                        bean.setDownloadStatus(0);
                        bean.setStatu(0);

                        if(list == null)
                            list = new ArrayList<>();
                        list.add(bean);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            return list;
        } else {
            return null;
        }
    }



    /***
     * 根据完整的styleUrl获取当前样式名称
     * @param styleUrl 完整styleUrl路径：http://202.107.245.40:10430/v1.0/styles/global/style.json
     * @return 样式名称：global
     */
    private String getStyleNameByStyleUrl(String styleUrl){
        try{
            int last1 = styleUrl.lastIndexOf("/");
            int last2 = styleUrl.lastIndexOf("/", last1 - 1);
            String ss = styleUrl.substring(last2 + 1, last1);
            return ss;
        } catch (Exception e){
            return "";
        }
    }

    //继续未完成的下载
    public void continueDownload(){
        List<DownloadEntity> list = Aria.download(activity).getAllNotCompletTask();
        if(list != null && list.size() > 0) {
            for (DownloadEntity entity : list) {
                Aria.download(activity)
                        .load(entity.getUrl())
//                        .setFilePath(entity.getStr())
                        .start();
                entity.setState(4);
            }
        }
    }

    public boolean isContinueDownload() {
        return isContinueDownload;
    }

    public void setContinueDownload(boolean continueDownload) {
        isContinueDownload = continueDownload;
        //继续未完成的下载。。。
        if(mMapboxMap != null){
            continueDownload();
        }
    }

    public TileGridBean DownloadEntity2TileGridBean(DownloadEntity downloadEntity){
        String url = downloadEntity.getUrl();
        int gridNum = url.lastIndexOf("/");
        String gridStr = url.substring(gridNum + 1);
        if(gridStr.contains(".mbtiles")){
            String[] split = gridStr.replace(".mbtiles", "").split("-");
            if(split != null && split.length == 5){
                int x = Integer.valueOf(split[3]);
                int y = Integer.valueOf(split[4]);
                int z = Integer.valueOf(split[2]);
                return new TileGridBean(x, y, z);
            }
        }
        return null;
    }

    public String subStringLastButOne(String url){
        int indexLast = url.lastIndexOf("/");
        int indexLast2 = url.lastIndexOf("/", indexLast - 1);
        int indexLast3 = url.lastIndexOf("/", indexLast2 - 1);
        return url.substring(indexLast3);
    }

    /**
     *  @return 是否修改成功
     */
    public boolean renameFileName(String oldPath, String newPath) {
        //1.判断参数阈值
        if (oldPath == null || newPath == null) {
            return false;
        }
        File oldFile = new File(oldPath);
        if(oldFile.exists()){
            return oldFile.renameTo(new File(newPath));
        }
        return false;
    }

    /**
     * 解压文件
     * @param folderPath  解压到的目标目录
     * @param zipPath     压缩文件目录
     * @param mContext
     */
    public boolean unZip(String folderPath,String zipPath,Context mContext) {
        File FILE = new File(zipPath);
        try {
            upZipFile(FILE, folderPath);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        Toast.makeText(mContext,"解压完成",Toast.LENGTH_SHORT).show();
        return true;
    }

    /**
     * 解压缩
     * 将zipFile文件解压到folderPath目录下.
     * @param zipFile      zip文件
     * @param folderPath   解压到的地址
     * @throws IOException
     */
    private void upZipFile(File zipFile, String folderPath) throws IOException {
        ZipFile zfile = new ZipFile(zipFile);
        Enumeration zList = zfile.entries();
        ZipEntry ze = null;
        byte[] buf = new byte[1024];
        while (zList.hasMoreElements()) {
            ze = (ZipEntry) zList.nextElement();
            if (ze.isDirectory()) {
                String dirstr = folderPath + ze.getName();
                dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
                File f = new File(dirstr);
                f.mkdir();
                continue;
            }
//            upZipFileName = ze.getName();
            OutputStream os = new BufferedOutputStream(new FileOutputStream(getRealFileName(folderPath, ze.getName())));
            InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
            int readLen = 0;
            while ((readLen = is.read(buf, 0, 1024)) != -1) {
                os.write(buf, 0, readLen);
            }
            is.close();
            os.close();
        }
        zfile.close();
    }

    /**
     * 给定根目录，返回一个相对路径所对应的实际文件名.
     * @param baseDir         指定根目录
     * @param absFileName     相对路径名，来自于ZipEntry中的name
     * @return java.io.File   实际的文件
     */
    public File getRealFileName(String baseDir, String absFileName) {
        String[] dirs = absFileName.split("/");
        File ret = new File(baseDir);
        String substr = null;
        if (dirs.length >= 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                substr = dirs[i];
                try {
                    substr = new String(substr.getBytes("8859_1"), "GB2312");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                ret = new File(ret, substr);

            }
            if (!ret.exists())
                ret.mkdirs();
            substr = dirs[dirs.length - 1];
            try {
                substr = new String(substr.getBytes("8859_1"), "GB2312");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            ret = new File(ret, substr);
            return ret;
        }
        return ret;
    }

    public List<DownLoadCallBack> downLoadCallBacks = new ArrayList<>();

    public void addDownLoadCallBack(DownLoadCallBack downLoadCallBack){
        if(downLoadCallBack != null)
            downLoadCallBacks.add(downLoadCallBack);
    }

    public void removeDownLoadCallBack(DownLoadCallBack downLoadCallBack){
        if(downLoadCallBack != null)
            downLoadCallBacks.remove(downLoadCallBack);
    }

    public DownLoadCallBack downLoadCallBack = new DownLoadCallBack() {
        @Override
        public void onPre(DownloadTask task) {
            if(downLoadCallBacks != null && downLoadCallBacks.size() > 0){
                for(DownLoadCallBack callBack : downLoadCallBacks){
                    callBack.onPre(task);
                }
            }
        }

        @Override
        public void taskStart(DownloadTask task) {
            if(downLoadCallBacks != null && downLoadCallBacks.size() > 0){
                for(DownLoadCallBack callBack : downLoadCallBacks){
                    callBack.taskStart(task);
                }
            }
        }

        @Override
        public void taskResume(DownloadTask task) {
            if(downLoadCallBacks != null && downLoadCallBacks.size() > 0){
                for(DownLoadCallBack callBack : downLoadCallBacks){
                    callBack.taskResume(task);
                }
            }
        }

        @Override
        public void taskStop(DownloadTask task) {
            if(downLoadCallBacks != null && downLoadCallBacks.size() > 0){
                for(DownLoadCallBack callBack : downLoadCallBacks){
                    callBack.taskStop(task);
                }
            }
        }

        @Override
        public void taskCancel(DownloadTask task) {
            if(downLoadCallBacks != null && downLoadCallBacks.size() > 0){
                for(DownLoadCallBack callBack : downLoadCallBacks){
                    callBack.taskCancel(task);
                }
            }
        }

        @Override
        public void taskFail(DownloadTask task) {
            if(downLoadCallBacks != null && downLoadCallBacks.size() > 0){
                for(DownLoadCallBack callBack : downLoadCallBacks){
                    callBack.taskFail(task);
                }
            }
        }

        @Override
        public void taskComplete(DownloadTask task) {
            if(downLoadCallBacks != null && downLoadCallBacks.size() > 0){
                for(DownLoadCallBack callBack : downLoadCallBacks){
                    callBack.taskComplete(task);
                }
            }

            DownloadEntity infoEntity = task.getDownloadEntity();
            String url = infoEntity.getUrl();
            //下载完成 更新本地文件名称
            String downloadingPath = infoEntity.getDownloadPath();
            String destpath = infoEntity.getDownloadPath().replace(".std", "");
            renameFileName(downloadingPath,destpath);


//            String downloadingPath = PATH  + infoEntity.getSoureceName()+ "/"+url.substring(url.lastIndexOf("/") + 1)+ ".std";
//            String destpath = PATH  + infoEntity.getSoureceName()+ "/"+url.substring(url.lastIndexOf("/") + 1);

            if(destpath.endsWith("sprite.zip")){
                String soureceName = getStyleNameByStyleUrl(infoEntity.getDownloadPath());
                boolean unZip = unZip(PATH + "styles" + "/" + soureceName, destpath, activity);
                if(unZip){
                    new File(destpath).delete();
                }
            }else if(destpath.endsWith(".zip")){
                boolean unZip = unZip(PATH + "fonts/", destpath, activity);
                if(unZip){
                    new File(destpath).delete();
                }
            }
        }

        @Override
        public void taskRunning(DownloadTask task) {
            if(downLoadCallBacks != null && downLoadCallBacks.size() > 0){
                for(DownLoadCallBack callBack : downLoadCallBacks){
                    callBack.taskRunning(task);
                }
            }
        }
    };

    public DownLoadCallBack getDownLoadCallBack() {
        return downLoadCallBack;
    }

    public void setDownLoadCallBack(DownLoadCallBack downLoadCallBack) {
        this.downLoadCallBack = downLoadCallBack;
    }

    public interface DownLoadCallBack{
        public void onPre(DownloadTask task);

        public void taskStart(DownloadTask task);

        public void taskResume(DownloadTask task);

        public void taskStop(DownloadTask task);

        public void taskCancel(DownloadTask task);

        public void taskFail(DownloadTask task);

        public void taskComplete(DownloadTask task);

        public void taskRunning(DownloadTask task);
    }

}
