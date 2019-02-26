package com.mapscloud.download.tools;

/**
 * 地图瓦片
 */
public class MapTile {

    /**
     * 请求成功ID
     */
    public static final int MAPTILE_SUCCESS_ID = 0;
    /**
     * 请求失败ID
     */
    public static final int MAPTILE_FAIL_ID = MAPTILE_SUCCESS_ID + 1;

    // This class must be immutable because it's used as the key in the cache
    // hash map
    // (ie all the fields are final).
    private final int x;
    private final int y;
    private final int zoomLevel;

    /**
     * 创建瓦片
     * 
     * @param zoomLevel
     *            缩放级别
     * @param tileX
     *            列号
     * @param tileY
     *            行号
     */
    public MapTile(final int zoomLevel, final int tileX, final int tileY) {
        this.zoomLevel = zoomLevel;
        this.x = tileX;
        this.y = tileY;
    }

    /**
     * 获取缩放级别
     * 
     * @return 缩放级别
     */
    public int getZoomLevel() {
        return zoomLevel;
    }

    /**
     * 获取列号
     * 
     * @return 列号
     */
    public int getX() {
        return x;
    }

    /**
     * 获取行号
     * 
     * @return 行号
     */
    public int getY() {
        return y;
    }

    /**
     * 获取关键字
     * 
     * @return 关键字
     */
    public String getKeyString() {
        return String.format("%d-%d-%d", zoomLevel, x, y);
    }

    @Override
    public String toString() {
        return "tile:" + zoomLevel + "-" + x + "-" + y + " ";
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof MapTile))
            return false;
        final MapTile rhs = (MapTile) obj;
        return zoomLevel == rhs.zoomLevel && x == rhs.x && y == rhs.y;
    }

    @Override
    public int hashCode() {
        int code = 17;
        code *= 37 + zoomLevel;
        code *= 37 + x;
        code *= 37 + y;
        return code;
    }
}
