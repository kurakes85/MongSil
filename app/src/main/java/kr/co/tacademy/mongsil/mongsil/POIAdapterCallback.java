package kr.co.tacademy.mongsil.mongsil;

/**
 * Created by ccei on 2016-08-16.
 */
public interface POIAdapterCallback {
    void onMarkCallback(boolean select, POIData poiData);
    void onSelectCallback(POIData poiData);
}