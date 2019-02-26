package com.mapscloud.download.bean;

/**
 * Created by mapscloud8 on 2018/12/11.
 */

public class TileGridPolyBean {

    private TileGridBean bean;
    private double[] latlng1;
    private double[] latlng2;
    private double[] latlng3;
    private double[] latlng4;

    public TileGridPolyBean(){}
    public TileGridPolyBean(TileGridBean bean, double[] latlng1, double[] latlng2, double[] latlng3, double[] latlng4){
        this.bean = bean;
        this.latlng1 = latlng1;
        this.latlng2 = latlng2;
        this.latlng3 = latlng3;
        this.latlng4 = latlng4;
    }

    public TileGridBean getBean() {
        return bean;
    }

    public void setBean(TileGridBean bean) {
        this.bean = bean;
    }

    public double[] getLatlng1() {
        return latlng1;
    }

    public void setLatlng1(double[] latlng1) {
        this.latlng1 = latlng1;
    }

    public double[] getLatlng2() {
        return latlng2;
    }

    public void setLatlng2(double[] latlng2) {
        this.latlng2 = latlng2;
    }

    public double[] getLatlng3() {
        return latlng3;
    }

    public void setLatlng3(double[] latlng3) {
        this.latlng3 = latlng3;
    }

    public double[] getLatlng4() {
        return latlng4;
    }

    public void setLatlng4(double[] latlng4) {
        this.latlng4 = latlng4;
    }
}
