package com.mapscloud.download.tools;

/**
 * 
 * @author chenxb
 * 
 */
public class Mercator {
    public static final double EARTH_RADIUS = 6378137.0;
    public static final double EARTH_CIRCUMFERENCE = 2 * Math.PI * EARTH_RADIUS;

    public static final double LATITUDE_MAX = 85.05112877980659;
    public static final double LATITUDE_MIN = -LATITUDE_MAX;
    public static final double LATITUDE_RANGE = LATITUDE_MAX - LATITUDE_MIN;
    public static final double LONGITUDE_MAX = 180.0;
    public static final double LONGITUDE_MIN = -LONGITUDE_MAX;
    public static final double LONGITUDE_RANGE = LONGITUDE_MAX - LONGITUDE_MIN;

    public static final int MAP_ZOOM_MIN = 0;
    public static final int MAP_ZOOM_MAX = 19;
    public static final int MAP_TILE_SIZE = 256;

    /**
     * 计算地图大小
     * 
     * @param zoom
     * @return
     */
    public static int calcMapSize(final int zoom) {
        return MAP_TILE_SIZE << clipZoom(zoom);
    }

    /**
     * 计算地面分辨率
     * 
     * @param latitude
     * @param zoom
     * @return
     */
    public static double calcGroundResolution(double latitude, final int zoom) {
        latitude = clipLatitude(latitude);
        return Math.cos(latitude * Math.PI / 180) * EARTH_CIRCUMFERENCE
                / calcMapSize(zoom);
    }

    /**
     * 计算比例尺
     * 
     * @param latitude
     * @param zoom
     * @param screenDpi
     * @return
     */
    public static double calcMapScale(final double latitude, final int zoom,
            final int screenDpi) {
        return calcGroundResolution(latitude, zoom) * screenDpi / 0.0254;
    }

    public static int calcZoom(int mapSize) {
        mapSize = clip(mapSize, 0, (MAP_TILE_SIZE << MAP_ZOOM_MAX) - 1);
        long n = mapSize / MAP_TILE_SIZE;
        double z = Math.log(n) / Math.log(2);
        return (int) (Math.floor(z + 0.5d));
    }

    /**
     * not implements
     * 
     * @param latitude
     * @param latitudeSpan
     * @return
     */
    public static int calcZoomByLatitudeSpan(double latitude,
            double latitudeSpan) {
        return 0;
    }

    /**
     * not implements
     * 
     * @param longitude
     * @param longitudeSpan
     * @return
     */
    public static int calcZoomByLongitudeSpan(double longitude,
            double longitudeSpan) {
        return 0;
    }

    public static int latitude2PixelY(double latitude, int zoom) {
        latitude = clipLatitude(latitude);
        int mapSize = calcMapSize(zoom);

        double sinLatitude = Math.sin(latitude * (Math.PI / 180));
        double y = 0.5 - Math.log((1 + sinLatitude) / (1 - sinLatitude))
                / (4 * Math.PI);

        return (int) clip(y * mapSize, 0, mapSize - 1);
    }

    public static int longitude2PixelX(double longitude, int zoom) {
        longitude = clipLongitude(longitude);
        int mapSize = calcMapSize(zoom);

        double x = (longitude + 180) / 360;

        return (int) clip(x * mapSize, 0, mapSize - 1);
    }

    public static int latitude2TileY(double latitude, int zoom) {
        int pixelY = latitude2PixelY(latitude, zoom);
        int tileY = pixelY / MAP_TILE_SIZE;
        return clipTile(tileY, zoom);
    }

    public static int longitude2TileX(double longitude, int zoom) {
        int pixelX = longitude2PixelX(longitude, zoom);
        int tileX = pixelX / MAP_TILE_SIZE;
        return clipTile(tileX, zoom);
    }

    public static double pixelY2Latitude(int pixelY, int zoom) {
        pixelY = clipPixel(pixelY, zoom);
        double y = 0.5 - (double) pixelY / calcMapSize(zoom);
        return 90 - 360 * Math.atan(Math.exp(-y * (2 * Math.PI))) / Math.PI;
    }

    public static double pixelX2Longitude(int pixelX, int zoom) {
        pixelX = clipPixel(pixelX, zoom);
        return 360 * ((double) pixelX / calcMapSize(zoom) - 0.5);
    }

    public static double tileY2Latitude(int tileY, int zoom) {
        tileY = clipTile(tileY, zoom);
        double y = 0.5 - (double) tileY / (1 << zoom);
        return 90 - 360 * Math.atan(Math.exp(-y * (2 * Math.PI))) / Math.PI;
    }

    public static double tileX2Longitude(int tileX, int zoom) {
        tileX = clipTile(tileX, zoom);
        return 360 * ((double) tileX / (1 << zoom) - 0.5);
    }

    public static double deltaLatitudeByPixel(double latitude, int deltaPixel,
            int zoom) {
        int pixelY = latitude2PixelY(latitude, zoom);
        int tgtPixelY = pixelY + deltaPixel;
        int mapSize = calcMapSize(zoom);
        if (tgtPixelY > mapSize) {
            tgtPixelY = tgtPixelY % mapSize;
        }
        double tgtLatitude = pixelY2Latitude(tgtPixelY, zoom);
        return Math.abs(tgtLatitude - latitude);
    }

    public static double deltaLongitudeByPixel(double longitude,
            int deltaPixel, int zoom) {
        int pixelX = longitude2PixelX(longitude, zoom);
        int tgtPixelX = pixelX + deltaPixel;
        int mapSize = calcMapSize(zoom);
        if (tgtPixelX > mapSize) {
            tgtPixelX = tgtPixelX % mapSize;
        }
        double tgtLongitude = pixelX2Longitude(tgtPixelX, zoom);
        return Math.abs(tgtLongitude - longitude);
    }

    public static int[] getParentTile(int tileX, int tileY, int zoom,
            int zoomDiff) {
        zoom = clipZoom(zoom);
        zoomDiff = Math.abs(zoomDiff);
        int z = zoom - zoomDiff;
        if (z < MAP_ZOOM_MIN) {
            z = MAP_ZOOM_MIN;
        }
        int x = tileX / (1 << zoomDiff);
        int y = tileY / (1 << zoomDiff);
        return new int[] { z, x, y };
    }

    public static int[][][] getChildTiles(int tileX, int tileY, int zoom,
            int zoomDiff) {
        zoom = clipZoom(zoom);
        zoomDiff = Math.abs(zoomDiff);
        int z = zoom + zoomDiff;
        if (z > MAP_ZOOM_MAX) {
            z = MAP_ZOOM_MAX;
            zoomDiff = z - zoom;
        }

        int n = 1 << zoomDiff;
        int[][][] ij = new int[n][n][];
        double step = 1d / n;
        for (int i = 0; i < n; i++) {
            int y = (int) ((tileY + step * i) * n);
            for (int j = 0; j < n; j++) {
                int x = (int) ((tileX + step * j) * n);
                ij[i][j] = new int[] { z, x, y };
            }
        }
        return ij;
    }

    public static double clipLatitude(final double latitude) {
        return clip(latitude, LATITUDE_MIN, LATITUDE_MAX);
    }

    public static double clipLongitude(final double longitude) {
        return clip(longitude, LONGITUDE_MIN, LONGITUDE_MAX);
    }

    private static double clip(final double n, final double min,
            final double max) {
        return Math.min(Math.max(n, min), max);
    }

    public static int clipZoom(final int zoom) {
        return clip(zoom, MAP_ZOOM_MIN, MAP_ZOOM_MAX);
    }

    public static int clipPixel(int n, int zoom) {
        return clip(n, 0, calcMapSize(zoom) - 1);
    }

    public static int clipTile(int n, int zoom) {
        return clip(n, 0, (1 << clipZoom(zoom)) - 1);
    }

    private static int clip(final int n, final int min, final int max) {
        return Math.min(Math.max(n, min), max);
    }

    /**
     * Converts tile XY coordinates into a QuadKey at a specified level of
     * detail.
     * 
     * @param tileX
     *            Tile X coordinate
     * @param tileY
     *            Tile Y coordinate
     * @param levelOfDetail
     *            Level of detail, from 1 (lowest detail) to 23 (highest detail)
     * @return A string containing the QuadKey
     */
    public static String tileXYToQuadKey(final int tileX, final int tileY,
                                         final int zoom) {
        final StringBuilder quadKey = new StringBuilder();
        for (int i = zoom; i > 0; i--) {
            char digit = '0';
            final int mask = 1 << (i - 1);
            if ((tileX & mask) != 0) {
                digit++;
            }
            if ((tileY & mask) != 0) {
                digit++;
                digit++;
            }
            quadKey.append(digit);
        }
        return quadKey.toString();
    }

    /**
     * Converts a QuadKey into tile XY coordinates.
     * 
     * @param quadKey
     *            QuadKey of the tile
     * @param reuse
     *            An optional Point to be recycled, or null to create a new one
     *            automatically
     * @return Output parameter receiving the tile X and y coordinates
     */
    public static int[] quadKeyToTileXY(final String quadKey) {
        int tileX = 0;
        int tileY = 0;

        final int levelOfDetail = quadKey.length();
        for (int i = levelOfDetail; i > 0; i--) {
            final int mask = 1 << (i - 1);
            switch (quadKey.charAt(levelOfDetail - i)) {
            case '0':
                break;

            case '1':
                tileX |= mask;
                break;

            case '2':
                tileY |= mask;
                break;

            case '3':
                tileX |= mask;
                tileY |= mask;
                break;

            default:
                throw new IllegalArgumentException(
                        "Invalid QuadKey digit sequence.");
            }
        }
        return new int[] { tileX, tileY };
    }
}
