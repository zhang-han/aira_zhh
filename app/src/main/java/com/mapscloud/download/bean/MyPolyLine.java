package com.mapscloud.download.bean;

import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;

/**
 * Created by mapscloud8 on 2018/7/26.
 */

public class MyPolyLine {

    private PolylineOptions polyLine;
    private MarkerOptions startMark;
    private MarkerOptions endMark;

    public PolylineOptions getPolyLine() {
        return polyLine;
    }

    public void setPolyLine(PolylineOptions polyLine) {
        this.polyLine = polyLine;
    }

    public MarkerOptions getStartMark() {
        return startMark;
    }

    public void setStartMark(MarkerOptions startMark) {
        this.startMark = startMark;
    }

    public MarkerOptions getEndMark() {
        return endMark;
    }

    public void setEndMark(MarkerOptions endMark) {
        this.endMark = endMark;
    }
}
