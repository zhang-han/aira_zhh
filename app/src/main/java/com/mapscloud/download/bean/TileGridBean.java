package com.mapscloud.download.bean;

/**
 * Created by mapscloud8 on 2018/12/10.
 */

public class TileGridBean {

    int x = 0;
    int y = 0;
    int z = 0;

    public TileGridBean(){}
    public TileGridBean(int[] xy, int z){
        x = xy[0];
        y = xy[1];
        this.z = z;
    }
    public TileGridBean(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public String getTileStrCode(){
        String s = "";
        if(z == 0){
            return "0-7-0-0-0";
        } else if(z == 3){
            s = "8-11-3-";
        } else if(z == 8){
            s = "12-16-8-";
        }
        return s + x + "-" + y;
    }

    public String getTileStr(){
        String s = "";
        if(z == 0){
            return "0-7-0-0-0";
        } else if(z == 3){
            s = "8-11-3-";
        } else if(z == 8){
            s = "12-16-8-";
        }
        return s + x + "-" + y + ".mbtiles";
    }

}
