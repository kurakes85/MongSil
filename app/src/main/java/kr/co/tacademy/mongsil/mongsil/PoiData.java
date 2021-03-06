package kr.co.tacademy.mongsil.mongsil;

/**
 * Created by ccei on 2016-08-08.
 */
public class POIData {
    public static final int TYPE_MARK_HEADER = 0;
    public static final int TYPE_CONTENT_HEADER = 1;
    public static final int TYPE_CONTENT_ITEM = 2;

    int typeCode = 2;
    boolean isMarked = false;
    long id;
    String name;
    String noorLat;
    String noorLon;
    String upperAddrName;
    String middleAddrName;
    String lowerAddrName;

    POIData() { }

    POIData(int typeCode) {
        this.typeCode = typeCode;
    }
    POIData(long id, String name, String upperAddrName,
            String middleAddrName, String noorLat, String noorLon) {
        this.id = id;
        this.name = name;
        this.upperAddrName = upperAddrName;
        this.middleAddrName = middleAddrName;
        this.noorLat = noorLat;
        this.noorLon = noorLon;
    }
}
