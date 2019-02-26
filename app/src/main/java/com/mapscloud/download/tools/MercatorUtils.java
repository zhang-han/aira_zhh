package com.mapscloud.download.tools;

import android.graphics.Point;
import android.graphics.Rect;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.vividsolutions.jts.geom.Coordinate;


/**
 * 
 * @deprecated Use {@link Mercator} and {@link MercatorDroid} instead. This
 *             class will be deleted in future build.
 * 
 */
public class MercatorUtils {

    public static double metersPerInchOnZoomLevel0 = 156543.034;

    /**
     * constants of sphere mercator
     */
    public static final double LATITUDE_MIN = -85.05112878;
    public static final double LATITUDE_MAX = 85.05112878;
    public static final double LONGITUDE_MIN = -180;
    public static final double LONGITUDE_MAX = 180;
    public static final int MAP_TILE_SIZE = 256;
    /**
     * The circumference of the earth at the equator in meters.
     */
    public static final double EARTH_CIRCUMFERENCE = 40075016.686;

    private static final double E6 = 1E6;
    private static final double RADIUS = 6378137d;// 地球半径

    /**
     * 根据给定缩放级别，计算相应1个像素对应Mercator坐标上的米
     * 
     * @param zoomlevel
     * @return
     */
    public static double metersPerPixel(int zoomlevel) {
        return metersPerInchOnZoomLevel0 / Math.pow(2, zoomlevel);
    }

    /**
     * 根据X、Z索引找到某切片西侧经度坐标
     * 
     * @param x
     * @param z
     * @return
     */
    public static double tile2Longitude(int x, int z) {
        return x / Math.pow(2.0, z) * 360.0 - 180;
    }

    /**
     * 根据y、Z索引找到某切片北侧纬度坐标
     * 
     * @param y
     * @param z
     * @return
     */
    public static double tile2Latitude(int y, int z) {
        double n = Math.PI - (2.0 * Math.PI * y) / Math.pow(2.0, z);
        return Math.toDegrees(Math.atan(Math.sinh(n)));
    }

    /**
     * 
     * @param tileX
     * @param tileY
     * @param zoom
     * @return double latlon[], latlon[0]是 lat维度；latlon[1]是 lon经度
     * @author brian
     */
    public static double[] tileXY2LatLon(final int tileX, final int tileY,
            final int zoom) {
        double latlon[] = new double[2];
        latlon[0] = tile2Latitude(tileY, zoom);
        latlon[1] = tile2Longitude(tileX, zoom);
        return latlon;
    }

    /**
     * 根据经度坐标和zoom level找到该点所在相应切片索引号X
     * 
     * @param lon
     * @param zoom
     * @return
     */
    public static int getTileXNumber(final double lon, final int zoom) {
        int xtile = (int) Math.floor((lon + 180) / 360 * (1 << zoom));
        return xtile;
    }

    /**
     * 根据纬度坐标和zoom level找到该点所在相应切片索引号Y
     * 
     * @param lat
     * @param zoom
     * @return
     */
    public static int getTileYNumber(final double lat, final int zoom) {
        int ytile = (int) Math
                .floor((1 - Math.log(Math.tan(Math.toRadians(lat)) + 1
                        / Math.cos(Math.toRadians(lat)))
                        / Math.PI)
                        / 2 * (1 << zoom));
        return ytile;
    }

    /**
     * @param lon
     * @param lat
     * @param zoom
     * @return int xy[], xy[0]是 TileX；xy[1]是 TileY；
     * @author brian
     * @deprecated
     */
    public static int[] latlon2TileXY(final double lat, final double lon,
            final int zoom) {
        int xy[] = new int[2];
        xy[0] = getTileXNumber(lon, zoom);
        xy[1] = getTileYNumber(lat, zoom);
        return xy;
    }

    /**
     * 考虑传入经纬度点在格点上的解决方案 如经纬度点在给定zoomlevel上与格点像素坐标值相等 认为它在格点上，此时返回格点右下角切片的XY索引号
     * 
     * @param lon
     * @param lat
     * @param zoom
     * @return int xy[], xy[0]是 TileX；xy[1]是 TileY；
     * @author brian
     * @deprecated
     */
    public static int[] latlon2TileXY2(final double lat, final double lon,
            final int zoom) {
        int xy[] = new int[2];
        int pt[] = latLon2PixelXY(lat, lon, zoom);
        int mapsize = calcMapSize(zoom);
        if (pt[0] == mapsize || pt[0] == (mapsize - 1)) {
            System.out.println("已经处于该级别最右侧，右侧已无其他切片。");
        }
        if (pt[1] == mapsize || pt[1] == (mapsize - 1)) {
            System.out.println("已经处于该级别最下侧，下侧已无其他切片。");
        }
        xy[0] = pt[0] / MAP_TILE_SIZE;
        xy[1] = pt[1] / MAP_TILE_SIZE;

        double pixelX256 = (pt[0] + 1) / (MAP_TILE_SIZE * 1.0d);// 如果+1是256的倍数
        double pixelY256 = (pt[1] + 1) / (MAP_TILE_SIZE * 1.0d);

        if (pixelX256 == Math.ceil(pixelX256)) {// 恰好在格点上
            xy[0] = Double.valueOf(pixelX256).intValue();
            System.out.println("X --- 恰好在格点上,返回右侧切片索引号");
        }
        if (pixelY256 == Math.ceil(pixelY256)) {// 恰好在格点上
            xy[1] = Double.valueOf(pixelY256).intValue();
            System.out.println("Y --- 恰好在格点上，返回下方切片索引号");
        }
        return xy;
    }

    /**
     * bug-free version
     * 
     * @param lat
     * @param lon
     * @param zoom
     * @return int xy[], xy[0]是 TileX；xy[1]是 TileY；
     * @author brian
     */
    public static int[] latlon2TileXY3(final double lat, final double lon,
            final int zoom) {
        int xy[] = new int[2];

        final double x = (lon + 180) / 360;
        final double sinLatitude = Math.sin(lat * Math.PI / 180);
        final double y = 0.5 - Math.log((1 + sinLatitude) / (1 - sinLatitude))
                / (4 * Math.PI);

        final int mapSize = calcMapSize(zoom);
        double pixelx = x * mapSize; // [0, mapSize - 1]
        double pixely = y * mapSize;
        pixelx = pixelx / MAP_TILE_SIZE; // [0, 255]
        pixely = pixely / MAP_TILE_SIZE;
        xy[0] = (int) clip(pixelx + 0.5, 0, Math.pow(2, zoom));
        xy[1] = (int) clip(pixely + 0.5, 0, Math.pow(2, zoom));
        return xy;
    }

    /**
     * a copy from microsoft.mappoint.TileSystem.java
     * 
     * @param latitude
     * @param longitude
     * @param levelOfDetail
     * @return
     */
    public static int[] latLon2PixelXY(double latitude, double longitude,
            final int levelOfDetail) {
        int xy[] = new int[2];

        latitude = clip(latitude, LATITUDE_MIN, LATITUDE_MAX);
        longitude = clip(longitude, LONGITUDE_MIN, LONGITUDE_MAX);

        final double x = (longitude + 180) / 360;
        final double sinLatitude = Math.sin(latitude * Math.PI / 180);
        final double y = 0.5 - Math.log((1 + sinLatitude) / (1 - sinLatitude))
                / (4 * Math.PI);

        final int mapSize = calcMapSize(levelOfDetail);
        xy[0] = (int) clip(x * mapSize + 0.5, 0, mapSize - 1);
        xy[1] = (int) clip(y * mapSize + 0.5, 0, mapSize - 1);
        return xy;
    }

    /**
     * 经纬度坐标转指定级别下的像素坐标
     * 
     * @param latitude
     *            纬度
     * @param longitude
     *            经度
     * @param levelOfDetail
     *            作坊级别
     * @return 坐标数组
     */
    public static double[] latLon2PixelXY2(double latitude, double longitude,
            final int levelOfDetail) {
        double xy[] = new double[2];

        latitude = clip(latitude, LATITUDE_MIN, LATITUDE_MAX);
        longitude = clip(longitude, LONGITUDE_MIN, LONGITUDE_MAX);

        final double x = (longitude + 180) / 360;
        final double sinLatitude = Math.sin(latitude * Math.PI / 180);
        final double y = 0.5 - Math.log((1 + sinLatitude) / (1 - sinLatitude))
                / (4 * Math.PI);

        final int mapSize = calcMapSize(levelOfDetail);
        xy[0] = clip(x * mapSize /* + 0.5 */, 0, mapSize - 1);
        xy[1] = clip(y * mapSize /* + 0.5 */, 0, mapSize - 1);
        return xy;
    }

    public static double clip(final double n, final double minValue,
            final double maxValue) {
        return Math.min(Math.max(n, minValue), maxValue);
    }

    /**
     * a copy from microsoft.mappoint.TileSystem.java
     * 
     * @param levelOfDetail
     * @return
     */
    public static int calcMapSize(final int levelOfDetail) {
        return MAP_TILE_SIZE << levelOfDetail;
    }

    public static LatLngBounds tile2BoundingBox(final int x, final int y,
                                                final int zoom) {
        double maxLatitude = tile2Latitude(y, zoom);
        double minLatitude = tile2Latitude(y + 1, zoom);
        double minLongitude = tile2Longitude(x, zoom);
        double maxLongitude = tile2Longitude(x + 1, zoom);
//        BoundingBoxE6 bb = new BoundingBoxE6(maxLatitude, maxLongitude,
//                minLatitude, minLongitude);// north, east, south, west
        return LatLngBounds.from(maxLatitude, maxLongitude, minLatitude, minLongitude); //北东南西;
    }

    /**
     * 获取某一切片对应的区域范围BoundingBoxE6
     * 
     * @param mapTile
     * @return
     */
    public static LatLngBounds tile2BoundingBox(MapTile mapTile) {
        return tile2BoundingBox(mapTile.getX(), mapTile.getY(),
                mapTile.getZoomLevel());
    }

    public static void getUpperLeftLowerRight(int level, LatLngBounds inBoxE6,
            Rect outTileRect) {
        
        outTileRect.set(getTileXNumber(inBoxE6.getLonWest(), level),
                getTileYNumber(inBoxE6.getLatNorth(), level),
                getTileXNumber(inBoxE6.getLonEast(), level),
                getTileYNumber(inBoxE6.getLatSouth(), level));
    }

    public static void getUpperLeftLowerRight(int level, Rect inScreenRect,
            Rect outTileRect) {
        Point upperLeft = new Point();
        Point lowerRight = new Point();
        TileSystem.PixelXYToTileXY(inScreenRect.left, inScreenRect.top,
                upperLeft);
        upperLeft.offset(-1, -1);
        TileSystem.PixelXYToTileXY(inScreenRect.right, inScreenRect.bottom,
                lowerRight);
        outTileRect.set(upperLeft.x, upperLeft.y, lowerRight.x, lowerRight.y);
    }

    /**
     * 根据专题地图的boundingbox，获取地图初始化中心点
     * 
     * @param productInfo
     * @param initZoomLevel
     * @return
     */
    public static LatLng getCenterPoint(LatLngBounds box, int initZoomLevel) {
        int tl_TileX = MercatorUtils.getTileXNumber(box.getLonWest(),
                initZoomLevel);
        int tl_TileY = MercatorUtils.getTileYNumber(box.getLatNorth(),
                initZoomLevel);
        int br_TileX = MercatorUtils.getTileXNumber(box.getLonEast(),
                initZoomLevel);
        int br_TileY = MercatorUtils.getTileYNumber(box.getLatSouth(),
                initZoomLevel);
        int initTileX = (tl_TileX + br_TileX) / 2;
        int initTileY = (tl_TileY + br_TileY) / 2;
        double initLat = MercatorUtils.tile2Latitude(initTileY, initZoomLevel);
        double initLon = MercatorUtils.tile2Longitude(initTileX, initZoomLevel);
        return new LatLng(initLat, initLon);
    }

    public static MapTile tileInSmallerZoomLevel(MapTile tile, int smallerZoom) {
        int zoomDiff = tile.getZoomLevel() - smallerZoom;
        if (zoomDiff > 0) {
            int x = (int) (tile.getX() / Math.pow(2, zoomDiff));
            int y = (int) (tile.getY() / Math.pow(2, zoomDiff));
            return new MapTile(smallerZoom, x, y);
        }
        return new MapTile(tile.getZoomLevel(), tile.getX(), tile.getY());
    }

    /**
     * 跟据经纬度得到Mercator平面坐标
     * 
     * @param lon
     * @param lat
     * @return
     */
    public static Coordinate getXYCoordinate(int lon, int lat) {
        double x = getXCoordinate(lon);
        double y = getYCoordinate(lat);
        return new Coordinate(x, y);
    }

    /**
     * 跟据经纬度得到Mercator平面X坐标
     * 
     * @param lon
     * @return
     */
    public static double getXCoordinate(int lon) {
        return getXCoordinate(lon / E6);
    }

    /**
     * 跟据经纬度得到Mercator平面X坐标
     * 
     * @param lon
     * @return
     */
    public static double getXCoordinate(double lon) {
        return lon * Math.PI / 180 * RADIUS;
    }

    /**
     * 跟据经纬度得到Mercator平面Y坐标
     * 
     * @param lat
     * @return
     */
    public static double getYCoordinate(int lat) {
        return getYCoordinate(lat / E6);
    }

    /**
     * 跟据经纬度得到Mercator平面Y坐标
     * 
     * @param lat
     * @return
     */
    public static double getYCoordinate(double lat) {
        return Math.log(Math.tan(Math.PI / 4 + lat * Math.PI / 360)) * RADIUS;
    }

    /**
     * 计算在指定纬度上，适合指定经线跨度距离的缩放级别
     * 
     * @param latitude
     *            维度
     * @param latSpan
     *            经线跨度
     * @return zoomLevel 缩放级别
     */
    public static int calculateZoomLevelByLatSpan(double latitude,
            double latSpan) {
        double length = Math.cos(latitude * (Math.PI / 180))
                * EARTH_CIRCUMFERENCE;
        long mapSize = (long) (length / latSpan);
        return getZoomLevel(mapSize);
    }

    /**
     * 计算在指定经度上，适合制定纬线跨度的缩放级别
     * 
     * @param longitude
     *            经度
     * @param lonSpan
     *            纬线跨度
     * @return zoomLevel 缩放级别
     */
    public static int calculateZoomLevelByLonSpan(double longitude,
            double lonSpan) {
        double length = (longitude + 180) / 360 * EARTH_CIRCUMFERENCE;
        long mapSize = (long) (length / lonSpan);
        return getZoomLevel(mapSize);
    }

    /**
     * 根据指定的地图像素数计算合适的ZoomLevel
     * 
     * @param mapSize
     *            地图像素数
     * @return
     */
    public static int getZoomLevel(long mapSize) {
        if (mapSize < 0) {
            throw new IllegalArgumentException(
                    "map size must not be negative: " + mapSize);
        }

        long n = mapSize / MAP_TILE_SIZE;
        double z = Math.log(n) / Math.log(2);
        return (int) (Math.floor(z + 0.5d));
    }
}
