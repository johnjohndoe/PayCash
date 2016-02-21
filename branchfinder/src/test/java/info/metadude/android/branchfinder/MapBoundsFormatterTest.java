package info.metadude.android.branchfinder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import info.metadude.android.branchfinder.models.BoundingBox;
import info.metadude.android.branchfinder.models.GeoPoint;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnit4.class)
public class MapBoundsFormatterTest {

    @Test
    public void getFormattedStringWithBoundingBox() {
        GeoPoint southWest = new GeoPoint(52.48675300749431, 13.35877465576175);
        GeoPoint northEast = new GeoPoint(52.54420821064123, 13.444605344238312);
        BoundingBox boundingBox = new BoundingBox(southWest, northEast);
        String expectedMapBounds = "((52.48675300749431,13.35877465576175),(52.54420821064123,13.444605344238312))";
        assertThat(MapBoundsFormatter.getFormattedString(boundingBox))
                .isEqualTo(expectedMapBounds);
    }

}
