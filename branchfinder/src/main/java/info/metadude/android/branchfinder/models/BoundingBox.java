package info.metadude.android.branchfinder.models;

public class BoundingBox {

    public final GeoPoint southWest;

    public final GeoPoint northEast;

    public BoundingBox(GeoPoint southWest, GeoPoint northEast) {
        this.southWest = southWest;
        this.northEast = northEast;
    }

    @Override
    public String toString() {
        return "BoundingBox{" +
                "southWest=" + southWest +
                ", northEast=" + northEast +
                '}';
    }

}
