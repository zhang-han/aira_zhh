package com.mapscloud.download.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolygonOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.Source;
import com.mapscloud.download.R;
import com.mapscloud.download.bean.MyPolyLine;
import com.mapscloud.download.bean.MyPolygon;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconSize;


/**
 * Created by mapscloud8 on 2018/7/26.
 * 地图工具类，使用mapbox引擎
 */

public class MapUtils {

    private static MapUtils mapUtils = new MapUtils();
    private MapUtils(){};
    public static MapUtils getInstance(){
        if(mapUtils == null)
            mapUtils = new MapUtils();
        return mapUtils;
    }

    //地图控件
    private MapboxMap mapboxMap = null;
    //地图view
    private MapView mapView = null;
    //上下文
    private Context context;

    /**
     * 初始化地图参数
     * @param mapboxMap 地图对象
     * **/
    public void init(MapboxMap mapboxMap, MapView mapView, Context context){
        this.mapboxMap = mapboxMap;
        this.mapView = mapView;
        this.context = context;
    }

    public MapboxMap getMapboxMap() {
        return mapboxMap;
    }
    public void setMapboxMap(MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
    }

    public MapView getMapView() {
        return mapView;
    }

    public void setMapView(MapView mapView) {
        this.mapView = mapView;
    }

    /**
     * 添加mark点，需要先调用init方法
     * @param context 上下文，必传字段
     * @param latLng 经纬度，必传字段
     * @param drawable 资源图片，如R.drawable.icon，非必传字段，可以传-1
     * @param title mark点的title，可不传
     * @return 返回MarkerOption对象，里面的Snippet是生成的唯一标识UUID。
     * **/
    public MarkerOptions addMark(Context context, LatLng latLng, int drawable, String title){
        if(mapboxMap != null && latLng != null && context != null) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.setPosition(latLng);
            markerOptions.setIcon(IconFactory.getInstance(context).fromResource(drawable > 0 ? drawable : R.mipmap.record_note_marker));
            markerOptions.setTitle(title.isEmpty() ? "" : title);
            markerOptions.setSnippet(getUUID());
            mapboxMap.addMarker(markerOptions);
            return markerOptions;
        }
        return null;
    }
    /**
     * 添加mark点，需要先调用init方法
     * @param context 上下文，必传字段
     * @param latLng 经纬度，必传字段
     * @param drawable 资源图片，如R.drawable.icon，非必传字段，可以传-1
     * @return 返回MarkerOption对象，里面的Snippet是生成的唯一标识UUID。
     * **/
    public MarkerOptions addMark(Context context, LatLng latLng, int drawable){
        return addMark(context, latLng, drawable, "");
    }
    /**
     * 添加mark点，需要先调用init方法
     * @param context 上下文，必传字段
     * @param latLng 经纬度，必传字段
     * @return 返回MarkerOption对象，里面的Snippet是生成的唯一标识UUID。
     * **/
    public MarkerOptions addMark(Context context, LatLng latLng){
        return addMark(context, latLng, -1, "");
    }

    /**
     * 添加多个mark点，需要先调用init方法
     * @param context 上下文，必传字段
     * @param list 经纬度集合，必传字段
     * @param drawable 资源图片，如R.drawable.icon，非必传字段，可以传-1
     * @param title mark点的title，可不传
     * @return 返回MarkerOption对象，里面的Snippet是生成的唯一标识UUID。
     * **/
    public List<MarkerOptions> addMarks(Context context, List<LatLng> list, int drawable, String title){
        if(mapboxMap != null && list != null && list.size() > 0 && context != null) {
            List<MarkerOptions> result = new ArrayList<>();
            for(LatLng latlng : list){
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.setPosition(latlng);
                markerOptions.setIcon(IconFactory.getInstance(context).fromResource(drawable > 0 ? drawable : R.mipmap.record_note_marker));
                markerOptions.setTitle(title.isEmpty() ? "" : title);
                markerOptions.setSnippet(getUUID());
                result.add(markerOptions);
            }
            mapboxMap.addMarkers(result);
            return result;
        }
        return null;
    }

    /**
     * 添加多个mark点，需要先调用init方法
     * @param context 上下文，必传字段
     * @param list 经纬度集合，必传字段
     * @param color 线的颜色，非必传字段
     * @param alpha 透明度，非必传字段
     * @param width 线的宽度，非必传字段
     * @param showStarEnd 是否显示起点终点mark点，非必传字段
     * @return 返回MyPolyLine对象，里面包含PolylineOptions对象和起点终点的mark对象
     * **/
    public MyPolyLine addLine(Context context, List<LatLng> list, int color, float alpha, int width, boolean showStarEnd){
        if(mapboxMap != null && context != null && list != null && list.size() > 1){
            MyPolyLine myPolyLine = new MyPolyLine();
            PolylineOptions polyLine = new PolylineOptions();
            polyLine.addAll(list);
            polyLine.color(color > 0 ? color : 0xff09b2ef);
            polyLine.alpha(alpha > 0 ? alpha : 1f);
            polyLine.width(width > 0 ? width : 1);
            myPolyLine.setPolyLine(polyLine);
            mapboxMap.addPolyline(polyLine);
            if(showStarEnd){
                myPolyLine.setStartMark(addMark(context, list.get(0), R.mipmap.track_start_marker));
                myPolyLine.setEndMark(addMark(context, list.get(list.size() - 1), R.mipmap.track_end_marker));
                mapboxMap.addMarker(myPolyLine.getStartMark());
                mapboxMap.addMarker(myPolyLine.getEndMark());
            }
            return myPolyLine;
        }
        return null;
    }

    /**
     * 添加线，需要先调用init方法
     * @param context 上下文，必传字段
     * @param list 经纬度集合，必传字段
     * @param color 线的颜色，非必传字段
     * @param alpha 透明度，非必传字段
     * @param width 线的宽度，非必传字段
     * @return 返回MyPolyLine对象，里面包含PolylineOptions对象和起点终点的mark对象
     * **/
    public MyPolyLine addLine(Context context, List<LatLng> list, int color, float alpha, int width){
        return addLine(context, list, color, alpha, width, false);
    }

    /**
     * 添加线，需要先调用init方法
     * @param context 上下文，必传字段
     * @param list 经纬度集合，必传字段
     * @param color 线的颜色，非必传字段
     * @param alpha 透明度，非必传字段
     * @return 返回MyPolyLine对象，里面包含PolylineOptions对象和起点终点的mark对象
     * **/
    public MyPolyLine addLine(Context context, List<LatLng> list, int color, float alpha){
        return addLine(context, list, color, alpha, -1, false);
    }

    /**
     * 添加线，需要先调用init方法
     * @param context 上下文，必传字段
     * @param list 经纬度集合，必传字段
     * @param color 线的颜色，非必传字段
     * @return 返回MyPolyLine对象，里面包含PolylineOptions对象和起点终点的mark对象
     * **/
    public MyPolyLine addLine(Context context, List<LatLng> list, int color){
        return addLine(context, list, color, -1, -1, false);
    }

    /**
     * 添加线，需要先调用init方法
     * @param context 上下文，必传字段
     * @param list 经纬度集合，必传字段
     * @return 返回MyPolyLine对象，里面包含PolylineOptions对象和起点终点的mark对象
     * **/
    public MyPolyLine addLine(Context context, List<LatLng> list){
        return addLine(context, list, -1, -1, -1, false);
    }

    /**
     * 添加面，需要先调用init方法
     * @param context 上下文，必传字段
     * @param list 经纬度集合，必传字段
     * @param strokeColor 面的描边颜色，非必传字段
     * @param fillColor 面的填充颜色，非必传字段
     * @param alpha 面的透明度，非必传字段
     * @param isShowMark 是否显示途经点的mark
     * @return 返回MyPolaygon对象，里面包含PolygonOptions对象和途径点的mark点集合
     * **/
    public MyPolygon addPolygon(Context context, List<LatLng> list, int strokeColor, int fillColor, float alpha, boolean isShowMark){
        if(mapboxMap != null && context != null && list != null && list.size() > 2) {
            MyPolygon myPolygon = new MyPolygon();
            PolygonOptions polygon = new PolygonOptions();
            polygon.addAll(list);
            polygon.strokeColor(strokeColor > 0 ? strokeColor : 0xff09b2ef);
            polygon.fillColor(fillColor > 0 ? fillColor : 0xff09b2ef);
            polygon.alpha(alpha > 0 ? alpha : 1f);
            myPolygon.setPolygon(polygon);
            if(isShowMark){
                myPolygon.setMarkList(addMarks(context, list, -1, ""));
            }
            return myPolygon;
        }
        return null;
    }
    /**
     * 添加面，需要先调用init方法
     * @param context 上下文，必传字段
     * @param list 经纬度集合，必传字段
     * @param strokeColor 面的描边颜色，非必传字段
     * @param fillColor 面的填充颜色，非必传字段
     * @param alpha 面的透明度，非必传字段
     * @return 返回MyPolaygon对象，里面包含PolygonOptions对象和途径点的mark点集合
     * **/
    public MyPolygon addPolygon(Context context, List<LatLng> list, int strokeColor, int fillColor, float alpha){
        return addPolygon(context, list, strokeColor, fillColor, alpha, false);
    }
    /**
     * 添加面，需要先调用init方法
     * @param context 上下文，必传字段
     * @param list 经纬度集合，必传字段
     * @param strokeColor 面的描边颜色，非必传字段
     * @param fillColor 面的填充颜色，非必传字段
     * @return 返回MyPolaygon对象，里面包含PolygonOptions对象和途径点的mark点集合
     * **/
    public MyPolygon addPolygon(Context context, List<LatLng> list, int strokeColor, int fillColor){
        return addPolygon(context, list, strokeColor, fillColor, -1, false);
    }
    /**
     * 添加面，需要先调用init方法
     * @param context 上下文，必传字段
     * @param list 经纬度集合，必传字段
     * @param strokeColor 面的描边颜色，非必传字段
     * @return 返回MyPolaygon对象，里面包含PolygonOptions对象和途径点的mark点集合
     * **/
    public MyPolygon addPolygon(Context context, List<LatLng> list, int strokeColor){
        return addPolygon(context, list, strokeColor, -1, -1, false);
    }
    /**
     * 添加面，需要先调用init方法
     * @param context 上下文，必传字段
     * @param list 经纬度集合，必传字段
     * @return 返回MyPolaygon对象，里面包含PolygonOptions对象和途径点的mark点集合
     * **/
    public MyPolygon addPolygon(Context context, List<LatLng> list){
        return addPolygon(context, list, -1, -1, -1, false);
    }

    /**
     * 获取当前屏幕中心点经纬度
     * @return mapbox经纬度对象
     * **/
    public LatLng getCurrentPoint(){
        if(mapboxMap != null){
            return mapboxMap.getCameraPosition().target;
        }
        return null;
    }
    /**
     * 获取当前地图等级
     * @return 当前等级
     * **/
    public double getCurrentZoom(){
        if(mapboxMap != null){
            return mapboxMap.getCameraPosition().zoom;
        }
        return -1;
    }

    /**
     * @param latLng 经纬坐标
     * @param tilt 倾斜度 （0-60） 倾斜度级别使用精确度的六位小数点
     * @param zoom 缩放控制地图的比例，并消耗0到22之间的任何值。
    缩放级别为0时，视口显示大洲和其他世界特征。
    中间值为11将显示城市级别的详细信息，
    并在更高的缩放级别下，地图将开始显示建筑物和兴趣点。
     * @param bear 轴承  方位代表相机指向的方向，并以北方顺时针方向测量。
     */
    public void jumpToCamera(LatLng latLng,
                             double tilt,double zoom,double bear){
        CameraPosition position = new CameraPosition.Builder()
                .target(latLng)
                .tilt(tilt)
                .zoom(zoom)
                .bearing(bear)
                .build();
        mapboxMap.setCameraPosition(position);
        mapView.postInvalidate();
    }

    /**
     * @param latLng 经纬坐标
     * @param zoom 缩放控制地图的比例，并消耗0到22之间的任何值。
    缩放级别为0时，视口显示大洲和其他世界特征。
    中间值为11将显示城市级别的详细信息，
    并在更高的缩放级别下，地图将开始显示建筑物和兴趣点。
     */
    public void jumpToCamera(LatLng latLng, double zoom){
        CameraPosition position = new CameraPosition.Builder()
                .target(latLng)
                .zoom(zoom)
                .build();
        mapboxMap.setCameraPosition(position);
        mapView.postInvalidate();
    }

    /**
     * @param zoom 缩放控制地图的比例，并消耗0到22之间的任何值。
    缩放级别为0时，视口显示大洲和其他世界特征。
    中间值为11将显示城市级别的详细信息，
    并在更高的缩放级别下，地图将开始显示建筑物和兴趣点。
     */
    public void setMapZoom(double zoom){
        CameraPosition position = new CameraPosition.Builder()
                .zoom(zoom)
                .build();
        mapboxMap.setCameraPosition(position);
        mapView.postInvalidate();
    }

    /**
     * @param latLng 经纬坐标
     */
    public void setMapCentre(LatLng latLng){
        CameraPosition position = new CameraPosition.Builder()
                .target(latLng)
                .build();
        mapboxMap.setCameraPosition(position);
        mapView.postInvalidate();
    }

    //获取UUID
    private String getUUID(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }


    //TODO 通过geojson方式添加点、线、面

    //保存geojson添加到地图上的点集合，保存特殊处理过后的点
    List<LatLng> geojsonPoints = new ArrayList<>();
    //点source
    String pointSourceId = "points";
    //点layer
    String pointLayerName = "layer";
    //保存geojson添加到地图上的线集合，保存特殊处理过后的点
    List<List<LatLng>> geojsonLines = new ArrayList<>();
    //线source
    String lineSourceId = "lineSource";
    //线layer
    String lineLayerName = "lineLayer";
    //保存geojson添加到地图上的面集合，保存特殊处理过后的点
    List<List<LatLng>> geojsonPolys = new ArrayList<>();
    //面source
    String polySourceId = "poly";
    //面layer
    String polyLayerName = "polyLayer";

    public void clearMap(){
        geojsonPoints.clear();
        geojsonLines.clear();
        geojsonPolys.clear();
    }

    /**
     * 通过jeojson添加点到地图上。
     * @param latLng 点的经纬度
     * @param bitmap 点的图片，如果没有可以传null
     * @param sourceId 如果没有指定的，可以传null
     * @param layerName 如果没有指定的，可以传null
     */
    public void addPointGeojson(LatLng latLng, Bitmap bitmap, String sourceId, String layerName, boolean isClear){
        if(isClear)
            geojsonPoints.clear();
        //通过geojson画点。
        geojsonPoints.add(latLng);
        addPointGeojsonToMap(geojsonPoints, bitmap, sourceId, layerName);
    }
    /**
     * 通过jeojson添加点到地图上。
     * @param list 点的经纬度集合
     * @param bitmap 点的图片，如果没有可以传null
     * @param sourceId 如果没有指定的，可以传null
     * @param layerName 如果没有指定的，可以传null
     */
    public void addPointGeojson(List<LatLng> list, Bitmap bitmap, String sourceId, String layerName, boolean isClear){
        if(isClear)
            geojsonPoints.clear();
        geojsonPoints.addAll(list);
        addPointGeojsonToMap(geojsonPoints, bitmap, sourceId, layerName);
    }
    /**
     * 将点的集合添加到地图上。
     * @param list 点的经纬度集合
     * @param bitmap 点的图片，如果没有可以传null
     * @param sourceId 如果没有指定的，可以传null
     * @param layerName 如果没有指定的，可以传null
     */
    private void addPointGeojsonToMap(List<LatLng> list, Bitmap bitmap, String sourceId, String layerName){
        String pointSourceId = this.pointSourceId;
        String pointLayerName = this.pointLayerName;
        if(sourceId != null && sourceId.length() > 1 && layerName != null && layerName.length() > 1){
            pointSourceId = sourceId;
            pointLayerName = layerName;
        }

        if(null == mapboxMap.getSource(pointSourceId)){
            List<Feature> markerCoordinates = new ArrayList<>();
            for (LatLng lag : list) {
                markerCoordinates.add(Feature.fromGeometry(Point.fromLngLat(lag.getLongitude(), lag.getLatitude()))); // Boston Common Park
            }
            //坐标点
            FeatureCollection featureCollection = FeatureCollection.fromFeatures(markerCoordinates);
            //数据来源
            GeoJsonOptions options = new GeoJsonOptions()
                    .withCluster(true)          //“集群”选项设置为true。
                    .withClusterMaxZoom(20)     // 最大缩放到集群点
                    .withClusterRadius(5);//聚合的点与点之间半径
            //添加数据源
            mapboxMap.addSource(new GeoJsonSource(pointSourceId, featureCollection, options));
            mapboxMap.addImage("test-image", bitmap != null ? bitmap : BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_marker_normal));
            SymbolLayer symbolLayer = new SymbolLayer(pointLayerName, pointSourceId);
            symbolLayer.setProperties(
                    iconImage("test-image"),
                    iconSize(1f)
            );
            mapboxMap.addLayer(symbolLayer);
        } else {
            List<Feature> markerCoordinates = new ArrayList<>();
            for (LatLng lag : list) {
                markerCoordinates.add(Feature.fromGeometry(Point.fromLngLat(lag.getLongitude(), lag.getLatitude()))); // Boston Common Park
            }
            //坐标点
            FeatureCollection featureCollection = FeatureCollection.fromFeatures(markerCoordinates);
            ((GeoJsonSource) mapboxMap.getSource(pointSourceId)).setGeoJson(featureCollection);
            mapView.postInvalidate();
        }
    }
    /**
     * 通过jeojson添加一条线线到地图上。
     * @param list 点的经纬度集合
     * @param isDispose 点的经纬度是否做过特殊处理
     * @param sourceId 如果没有指定的，可以传null
     * @param layerName 如果没有指定的，可以传null
     */
    public void addLineGeojson(List<LatLng> list, boolean isDispose, String color, String sourceId, String layerName, boolean isClear){
        if(list == null)
            return;
        if(isClear)
            geojsonLines.clear();
        //处理点
        if(!isDispose){
            geojsonLines.add(dispostPoint(list));
        } else {
            geojsonLines.add(list);
        }
        addLineGeojsonToMap(geojsonLines, color, sourceId, layerName);
    }
    /**
     * 通过jeojson添加多条线线到地图上。
     * @param list 点的经纬度集合的集合
     * @param isDispose 点的经纬度是否做过特殊处理
     * @param sourceId 如果没有指定的，可以传null
     * @param layerName 如果没有指定的，可以传null
     */
    public void addLinesGeojson(List<List<LatLng>> list, boolean isDispose, String color, String sourceId, String layerName, boolean isClear){
        if(list == null)
            return;
        if(isClear)
            geojsonLines.clear();
        if(!isDispose){
            for(List<LatLng> latlngs : list){
                geojsonLines.add(dispostPoint(latlngs));
            }
        } else {
            geojsonLines.addAll(list);
        }
        addLineGeojsonToMap(geojsonLines, color, sourceId, layerName);
    }
    /**
     * 将线的集合添加到地图上。
     * @param list 点的经纬度集合的集合
     * @param sourceId 如果没有指定的，可以传null
     * @param layerName 如果没有指定的，可以传null
     */
    public void addLineGeojsonToMap(List<List<LatLng>> list, String color, String sourceId, String layerName){
        String lineSourceId = this.lineSourceId;
        String lineLayerName = this.lineLayerName;
        if(sourceId != null && sourceId.length() > 1 && layerName != null && layerName.length() > 1){
            lineSourceId = sourceId;
            lineLayerName = layerName;
        }

        if(null == mapboxMap.getSource(lineSourceId)){
            List<Feature> lines = new ArrayList<>();
            for(List<LatLng> lis : list){
                List<Point> listLines = new ArrayList<>();
                for(LatLng latLng : lis){
                    listLines.add(Point.fromLngLat(latLng.getLongitude(), latLng.getLatitude()));
                }
                lines.add(Feature.fromGeometry(LineString.fromLngLats(listLines)));
            }
            //坐标点
            FeatureCollection featureCollection = FeatureCollection.fromFeatures(lines);

            String ss = featureCollection.toJson();
            String sss = featureCollection.toString();

            Source geoJsonSource = new GeoJsonSource(lineSourceId, featureCollection);

            //添加数据源
            mapboxMap.addSource(geoJsonSource);
            //创建覆盖层
            if(color == null)
                color = "";
            LineLayer linesLayer = new LineLayer(lineLayerName, lineSourceId);
            linesLayer.setProperties(
                    PropertyFactory.lineWidth(3f),
                    PropertyFactory.lineOpacity(0.5f),
                    PropertyFactory.lineColor(Color.parseColor(color.length() < 1 ? "#13c768" : color))
            );
            //添加覆盖层
            mapboxMap.addLayer(linesLayer);

        } else {
            List<Feature> lines = new ArrayList<>();
            for(List<LatLng> lis : list){
                List<Point> listPoints = new ArrayList<>();
                for(LatLng latLng : lis){
                    listPoints.add(Point.fromLngLat(latLng.getLongitude(), latLng.getLatitude()));
                }
                lines.add(Feature.fromGeometry(LineString.fromLngLats(listPoints)));
            }
            //坐标点
            FeatureCollection featureCollection = FeatureCollection.fromFeatures(lines);
            ((GeoJsonSource) mapboxMap.getSource(lineSourceId)).setGeoJson(featureCollection);
            mapView.postInvalidate();
        }
    }

    /**
     * 通过jeojson添加一个面到地图上。
     * @param list 点的经纬度集合
     * @param isDispose 点的经纬度是否做过特殊处理
     * @param sourceId 如果没有指定的，可以传null
     * @param layerName 如果没有指定的，可以传null
     */
    public void addPolyGeojson(List<LatLng> list, boolean isDispose, String sourceId, String layerName, boolean isClear){
        if(list == null)
            return;
        if(isClear)
            geojsonPolys.clear();
        //处理点
        if(!isDispose){
            geojsonPolys.add(dispostPoint(list));
        } else {
            geojsonPolys.add(list);
        }
        addPolyGeojsonToMap(geojsonPolys, sourceId, layerName);
    }
    /**
     * 通过jeojson添加多个面到地图上。
     * @param list 点的经纬度集合的集合
     * @param isDispose 点的经纬度是否做过特殊处理
     * @param sourceId 如果没有指定的，可以传null
     * @param layerName 如果没有指定的，可以传null
     */
    public void addPolysGeojson(List<List<LatLng>> list, boolean isDispose, String sourceId, String layerName, boolean isClear){
        if(list == null)
            return;
        if(isClear)
            geojsonPolys.clear();
        if(!isDispose){
            for(List<LatLng> latlngs : list){
                geojsonPolys.add(dispostPoint(latlngs));
            }
        } else {
            geojsonPolys.addAll(list);
        }
        addPolyGeojsonToMap(geojsonPolys, sourceId, layerName);
    }
    /**
     * 将面的集合添加到地图上。
     * @param list 点的经纬度集合的集合
     * @param sourceId 如果没有指定的，可以传null
     * @param layerName 如果没有指定的，可以传null
     */
    private void addPolyGeojsonToMap(List<List<LatLng>> list, String sourceId, String layerName){
        String polySourceId = this.polySourceId;
        String polyLayerName = this.polyLayerName;
        if(sourceId != null && sourceId.length() > 1 && layerName != null && layerName.length() > 1){
            polySourceId = sourceId;
            polyLayerName = layerName;
        }

        if(null == mapboxMap.getSource(polySourceId)){
            List<Feature> polygons = new ArrayList<>();
            for(List<LatLng> lis : list){
                List<Point> listPoly = new ArrayList<>();
                for(LatLng latLng : lis){
                    listPoly.add(Point.fromLngLat(latLng.getLongitude(), latLng.getLatitude()));
                }
                listPoly.add(Point.fromLngLat(lis.get(0).getLongitude(), lis.get(0).getLatitude()));
                polygons.add(Feature.fromGeometry(LineString.fromLngLats(listPoly)));
            }

            FeatureCollection featureCollection = FeatureCollection.fromFeatures(polygons);
            String ss = featureCollection.toJson();
            String sss = featureCollection.toString();
            mapboxMap.addSource(new GeoJsonSource(polySourceId, featureCollection));

            FillLayer fillLayer = new FillLayer(polyLayerName, polySourceId);
            fillLayer.setProperties(
                    PropertyFactory.fillOpacity(0.5f),
                    PropertyFactory.fillAntialias(true),
                    PropertyFactory.fillOutlineColor(Color.BLACK),
                    PropertyFactory.fillColor(Color.BLUE)
            );
            mapboxMap.addLayer(fillLayer);



        } else {
            List<Feature> polygons = new ArrayList<>();
            for(List<LatLng> lis : list){
                List<Point> listPoly = new ArrayList<>();
                for(LatLng latLng : lis){
                    listPoly.add(Point.fromLngLat(latLng.getLongitude(), latLng.getLatitude()));
                }
                for(int i = 0; i <= lis.size(); i ++){
                    if(i == lis.size()){
                        listPoly.add(Point.fromLngLat(lis.get(0).getLongitude(), lis.get(0).getLatitude()));
                    } else {
                        listPoly.add(Point.fromLngLat(lis.get(i).getLongitude(), lis.get(i).getLatitude()));
                    }
                }
                polygons.add(Feature.fromGeometry(LineString.fromLngLats(listPoly)));
            }
            FeatureCollection featureCollection = FeatureCollection.fromFeatures(polygons);
            ((GeoJsonSource) mapboxMap.getSource(polySourceId)).setGeoJson(featureCollection);
            mapView.postInvalidate();
        }
    }

    /***
     * 处理点集合变成经过处理的多世界的特殊点集合
     * @param list 点集合
     * @return 处理后的点集合
     */
    public List<LatLng> dispostPoint(List<LatLng> list){
        if(list == null)
            return null;
        if(list.size() == 1)
            return list;
        List<LatLng> newList = new ArrayList<>();
        for(LatLng latlng : list){
            if(newList.size() > 0){
                double pre = newList.get(newList.size() - 1).getLongitude();
                if(Math.abs(latlng.getLongitude() - pre) > 180){
                    double lon = (Math.abs(pre) + 180 - Math.abs(pre) +  180 - Math.abs(latlng.getLongitude())) * (pre > 0 ? 1 : -1);
                    latlng.setLongitude(lon);
                }
            }
            newList.add(latlng);
        }
        return newList;
    }


  /**********************************************zyl_model_map***********************************************/

    /**
     * @param mapboomMap
     * @param position
     * @param title
     */
    public Marker addMarker(MapboxMap mapboomMap, LatLng position, String title, Context context, int resId){

        IconFactory iconFactory = IconFactory.getInstance(context);
        Icon icon = iconFactory.fromResource(resId);

        Marker marker = mapboomMap.addMarker(new MarkerOptions()
                .position(position)
                .icon(icon) //自定义图标
                .title(title));
        return marker;

//        IconFactory iconFactory = IconFactory.getInstance(DownLoadTileActivity.this);
//        Icon icon = iconFactory.fromResource(R.drawable.blue_marker);

        ///点的点击监听事件
//        mapboomMap.setOnMarkerClickListener(new MapboomMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(@NonNull Marker marker) {
//                Toast.makeText(context,"",Toast.LENGTH_SHORT).show();
//                return true;
//            }
//        });
    }

    /**
     * @param mapboomMap
     * @param latLng 经纬坐标
     * @param tilt 倾斜度 （0-60） 倾斜度级别使用精确度的六位小数点
     * @param zoom 缩放控制地图的比例，并消耗0到22之间的任何值。
    缩放级别为0时，视口显示大洲和其他世界特征。
    中间值为11将显示城市级别的详细信息，
    并在更高的缩放级别下，地图将开始显示建筑物和兴趣点。
     * @param bear 轴承  方位代表相机指向的方向，并以北方顺时针方向测量。
     */
    public void jumpToCamera(MapView mapView, MapboxMap mapboomMap, LatLng latLng,
                             double tilt, double zoom, double bear){
        CameraPosition position = new CameraPosition.Builder()
                .target(latLng)
                .tilt(tilt)
                .zoom(zoom)
                .bearing(bear)
                .build();
        mapboxMap.setCameraPosition(position);
        mapView.postInvalidate();
    }

    public void refreshMap() {
        MapView mapView = getMapView();
        if(mapView != null){
            mapView.postInvalidate();
        }
    }


    /**********************************************zyl_model_map***********************************************/

}
