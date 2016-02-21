package info.metadude.android.branchfinder;

import info.metadude.android.branchfinder.models.BoundingBox;
import info.metadude.android.branchfinder.models.GeoPoint;

public class MapBoundsFormatter {

    /**
     * Returns the formatted string representation of the given bounding box
     * suitable for being used as the query value for the "map_bounds" parameter.
     * <p/>
     * Example output: ((52.48675300749431,13.35877465576175),(52.54420821064123,13.444605344238312))
     */
    public static String getFormattedString(BoundingBox boundingBox) {
        GeoPoint southWest = boundingBox.southWest;
        GeoPoint northEast = boundingBox.northEast;
        return "((" +
                southWest.latitude +
                "," +
                southWest.longitude +
                "),(" +
                northEast.latitude +
                "," +
                northEast.longitude +
                "))";
    }

}
