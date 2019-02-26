package com.mapscloud.download.tools;

import android.graphics.Point;
import android.graphics.Rect;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;

public class MercatorDroid extends Mercator {

    public static Point geo2Point(int latE6, int longE6, int zoom) {
        return geo2Point(latE6 * 1E-6, longE6 * 1E-6, zoom);
    }

    public static Point geo2Point(double latitude, double longitude, int zoom) {
        int pixelY = latitude2PixelY(latitude, zoom);
        int pixelX = longitude2PixelX(longitude, zoom);
        return new Point(pixelX, pixelY);
    }

    public static Point geoPoint2Point(LatLng gp, int zoom) {
        return gp != null ? geo2Point(gp.getLatitude(), gp.getLongitude(), zoom)
                : null;
    }

    public static LatLng point2GeoPoint(Point point, int zoom) {
        return point != null ? point2GeoPoint(point.x, point.y, zoom) : null;
    }

    public static LatLng point2GeoPoint(int pixelX, int pixelY, int zoom) {
        double latitude = pixelY2Latitude(pixelY, zoom);
        double longitude = pixelX2Longitude(pixelX, zoom);
        return new LatLng(latitude, longitude);
    }

    public static LatLngBounds tile2Bbox(int tileX, int tileY, int zoom) {
        return tile2Bbox(tileX, tileY, tileX + 1, tileY + 1, zoom);
    }

    public static LatLngBounds tile2Bbox(int left, int top, int right,
                                         int bottom, int zoom) {
        zoom = clipZoom(zoom);

        left = clipTile(left, zoom);
        top = clipTile(top, zoom);
        right = clipTile(right, zoom);
        bottom = clipTile(bottom, zoom);

        if (right == left) {
            right += 1;
        } else if (right < left) {
            int tmp = right;
            right = left;
            left = tmp;
        }

        if (bottom == top) {
            bottom += 1;
        } else if (bottom < top) {
            int tmp = bottom;
            bottom = top;
            top = tmp;
        }

        int tileNum = 1 << zoom;
        double latitudeInterval = Mercator.LATITUDE_RANGE / tileNum;
        double longitudeInterval = Mercator.LONGITUDE_RANGE / tileNum;

        double north = tileY2Latitude(top, zoom);
        double south = north + (bottom - top) * latitudeInterval;
        double west = tileX2Longitude(left, zoom);
        double east = west + (right - left) * longitudeInterval;

        return LatLngBounds.from(north, east, south, west);
    }

    public static LatLngBounds tile2Bbox(int zoom, MapTile... tiles) {
        if (null == tiles || tiles.length == 0) {
            return null;
        }

        int tileNum = 1 << zoom;

        int left = tileNum;
        int top = tileNum;
        int right = 0;
        int bottom = 0;

        for (int i = 0; i < tiles.length; i++) {
            MapTile tile = tiles[i];
            if (null != tile && tile.getZoomLevel() == zoom) {
                left = Math.min(left, tile.getX());
                top = Math.min(top, tile.getY());
                right = Math.max(right, tile.getX());
                bottom = Math.max(bottom, tile.getY());
            }
        }

        return tile2Bbox(left, top, right, bottom, zoom);
    }

    public static LatLngBounds tileRect2Bbox(Rect tileRect, int zoom) {
        return tileRect != null ? tile2Bbox(tileRect.left, tileRect.top,
                tileRect.right, tileRect.bottom, zoom) : null;
    }

    public static Rect bbox2TileRect(double north, double east, double south,
                                     double west, int zoom) {
        zoom = clipZoom(zoom);
        north = clipLatitude(north);
        east = clipLongitude(east);
        south = clipLatitude(south);
        west = clipLongitude(west);

        if (east < west) {
            double tmp = east;
            east = west;
            west = tmp;
        }

        if (north < south) {
            double tmp = north;
            north = south;
            south = tmp;
        }

        int left = longitude2TileX(west, zoom);
        int top = latitude2TileY(north, zoom);
        int right = longitude2TileX(east, zoom);
        int bottom = latitude2TileY(south, zoom);

        return new Rect(left, top, right, bottom);
    }

    public static LatLngBounds bboxOfGeoPoints(LatLng... points) {
        if (null == points || points.length > 1) {
            return null;
        }

        double minLatitude = LATITUDE_MAX;
        double maxLatitude = LATITUDE_MIN;
        double minLongitude = LONGITUDE_MAX;
        double maxLongitude = LONGITUDE_MIN;

        for (int i = 0; i < points.length; i++) {
            LatLng point = points[i];
            if (null != point) {
                minLatitude = Math.min(minLatitude, point.getLatitude());
                minLongitude = Math.min(minLongitude, point.getLongitude());
                maxLatitude = Math.max(maxLatitude, point.getLatitude());
                maxLongitude = Math.max(maxLongitude, point.getLongitude());
            }
        }

        return LatLngBounds.from(maxLatitude, maxLongitude, minLatitude,
                minLongitude);
    }
}
