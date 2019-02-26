package com.mapscloud.download.bean;

import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolygonOptions;

import java.util.List;

/**
 * Created by mapscloud8 on 2018/7/26.
 */

public class MyPolygon {

    private PolygonOptions polygon;
    private List<MarkerOptions> markList;

    public PolygonOptions getPolygon() {
        return polygon;
    }

    public void setPolygon(PolygonOptions polygon) {
        this.polygon = polygon;
    }

    public List<MarkerOptions> getMarkList() {
        return markList;
    }

    public void setMarkList(List<MarkerOptions> markList) {
        this.markList = markList;
    }
}
