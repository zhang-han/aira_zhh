package com.mapscloud.download;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadEntity;
import com.arialyy.aria.core.download.DownloadTarget;
import com.arialyy.aria.core.download.DownloadTask;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.style.sources.Source;
import com.mapscloud.download.tools.MapUtils;
import com.mapscloud.download.view.BasicDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mapscloud8 on 2018/12/10.
 */

public class TileGridActivity extends Activity {

    TileGridActivity activity;
    MapView mapView;
    MapboxMap mMapboxMap;
    LinearLayout ll_content;
    Button btn1, btn2, btn3;

    EditText et;
    TextView tv_zoom;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.activity_test_vectormap);

        ll_content = (LinearLayout) findViewById(R.id.ll_content);
        et = (EditText) findViewById(R.id.text_url);
        tv_zoom = (TextView) findViewById(R.id.tv_zoom);
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (Button) findViewById(R.id.btn3);
        btn1.setText("切换样式1");
        btn2.setText("切换样式2");
        btn3.setText("下载数据");
        btn1.setOnClickListener(listener);
        btn2.setOnClickListener(listener);
        btn3.setOnClickListener(listener);
        findViewById(R.id.btn_map).setOnClickListener(listener);
        initMap();

        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);

        initAria();
    }

    private void initMap(){
        ll_content = (LinearLayout) findViewById(R.id.ll_content);
        Mapbox.getInstance(activity, "pk.eyJ1IjoiZGFuemlzZSIsImEiOiJjamJwdmo2a2YyY20xMndxa295YXJlZWV5In0._DzAMKkWtDGUufHazS6aYQ");
        MapboxMapOptions options = new MapboxMapOptions()
                .localIdeographFontFamily("Droid Sans,Droid Sans Fallback,PingFang SC,Microsoft YaHei,微软雅黑,Arial,sans-serif,黑体")
                .apiBaseUrl("http://202.107.245.40:10410/")
                .styleUrl("http://202.107.245.40:10430/v1.0/styles/global/style.json")
//                .apiBaseUrl("http://202.107.245.46:10430/")
//                .styleUrl("http://202.107.245.46:10430/v1.0/styles/SW/style.json")
//                .styleUrl("http://202.107.245.46:10430/v1.0/styles/mapbox1_11/style.json")
                .camera(new CameraPosition.Builder()
                        .target(new LatLng(39.0, 116.0))//39.0, 116.0
                        .zoom(6)
                        .build());
        mapView = new MapView(this, options);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mapView.setLayoutParams(params);
        ll_content.addView(mapView);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                mMapboxMap = mapboxMap;
                mMapboxMap.getUiSettings().setAttributionEnabled(false);
                mapboxMap.getUiSettings().setCompassMargins(30,450,0,0);

                mMapboxMap.addOnMoveListener(new MapboxMap.OnMoveListener() {
                    @Override
                    public void onMoveBegin(@NonNull MoveGestureDetector moveGestureDetector) {

                    }

                    @Override
                    public void onMove(@NonNull MoveGestureDetector moveGestureDetector) {

                    }

                    @Override
                    public void onMoveEnd(@NonNull MoveGestureDetector moveGestureDetector) {
                        tv_zoom.setText((mMapboxMap.getCameraPosition().zoom) + "");
                    }
                });

                MapUtils.getInstance().init(mMapboxMap, mapView, activity);
                //初始化地图数据包格子下载工具
                List<String> sourceName = new ArrayList<>();
                List<Source> lis = mMapboxMap.getSources();
                for(Source source : lis){
                    if(!source.getId().equals("com.mapbox.annotations"))
                        sourceName.add(source.getId());
                }
                DownloadTileManager.getInstance().init(activity, mapView, mMapboxMap, sourceName);


            }
        });

    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id){
                case R.id.btn1:
                    et.setText("http://202.107.245.40:10430/v1.0/styles/global/style.json");
                    break;
                case R.id.btn2:
                    et.setText("http://202.107.245.40:10430/v1.0/styles/dm/style.json");
                    break;
                case R.id.btn_map:
                    if(et != null && et.getText() != null && !et.getText().toString().equals("")){
                        if(mapView != null && mMapboxMap != null){
                            mMapboxMap.clear();
                            mMapboxMap.setStyleUrl(et.getText().toString());
                            mapView.postInvalidate();
                        } else {
                            Toast.makeText(activity, "地图未加载，请配置正确的url", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(activity, "请输入正确样式文件地址", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.btn3:
                    et.setText("http://202.107.245.40:10430/v1.0/styles/Global_satelite/style.json");
                    DownloadTileManager.getInstance().showDialog(activity);
            }
        }
    };

    private void initAria(){
        Aria.download(this).register();
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<DownloadEntity> list = Aria.download(activity).getAllNotCompletTask();
                if(list != null && list.size() > 0){
                    TileGridActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TileGridActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    BasicDialog.Builder builder = new BasicDialog.Builder(activity);
                                    builder.setMessage("有未完成的地图下载，是否继续？");
                                    builder.setPositiveButton(R.string.basic_dialog_positive, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            DownloadTileManager.getInstance().setContinueDownload(true);
                                        }
                                    });
                                    builder.setNegativeButton(R.string.basic_dialog_negative, null);
                                    builder.create().show();
                                }
                            });
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Aria.download(this).unRegister();
    }

    @Download.onPre void onPre(DownloadTask task) {
        DownloadTileManager.getInstance().getDownLoadCallBack().onPre(task);
    }

    @Download.onTaskStart void taskStart(DownloadTask task) {
        DownloadTileManager.getInstance().getDownLoadCallBack().taskStart(task);
    }

    @Download.onTaskResume void taskResume(DownloadTask task) {
        DownloadTileManager.getInstance().getDownLoadCallBack().taskResume(task);
    }

    @Download.onTaskStop void taskStop(DownloadTask task) {
        DownloadTileManager.getInstance().getDownLoadCallBack().taskStop(task);
    }

    @Download.onTaskCancel void taskCancel(DownloadTask task) {
        DownloadTileManager.getInstance().getDownLoadCallBack().taskCancel(task);
    }

    @Download.onTaskFail void taskFail(DownloadTask task) {
        DownloadTileManager.getInstance().getDownLoadCallBack().taskFail(task);
    }

    @Download.onTaskComplete void taskComplete(DownloadTask task) {
        DownloadTileManager.getInstance().getDownLoadCallBack().taskComplete(task);
    }

    @Download.onTaskRunning() void taskRunning(DownloadTask task) {
        DownloadTileManager.getInstance().getDownLoadCallBack().taskRunning(task);
    }

}
