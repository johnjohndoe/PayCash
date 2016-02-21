package info.metadude.android.paycash.map;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.model.DirectionsStep;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(JUnit4.class)
public class DirectionsHelperTest {

    @Test
    public void getPolyLinePointsWithASingleDirectionStep() {
        DirectionsStep directionsStep = new DirectionsStep();
        directionsStep.startLocation = new com.google.maps.model.LatLng(51, 13);
        directionsStep.endLocation = new com.google.maps.model.LatLng(52, 10);
        DirectionsStep[] steps = new DirectionsStep[]{
                directionsStep
        };
        List<LatLng> points = new ArrayList<>(2);
        points.add(new LatLng(51, 13));
        points.add(new LatLng(52, 10));
        assertThat(DirectionsHelper.getPolyLinePoints(steps)).isEqualTo(points);
    }

}
