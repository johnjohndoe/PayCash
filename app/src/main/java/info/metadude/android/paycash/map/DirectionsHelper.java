package info.metadude.android.paycash.map;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.model.DirectionsStep;

import java.util.ArrayList;
import java.util.List;

public class DirectionsHelper {

    @Nullable
    public static List<LatLng> getPolyLinePoints(@NonNull DirectionsStep[] steps) {
        int stepCount = steps.length;
        if (stepCount == 0) {
            return null;
        }
        List<LatLng> polyLinePoints = new ArrayList<>(stepCount + 1);
        for (DirectionsStep step : steps) {
            polyLinePoints.add(new LatLng(step.startLocation.lat, step.startLocation.lng));
        }
        DirectionsStep lastStep = steps[stepCount - 1];
        polyLinePoints.add(new LatLng(lastStep.endLocation.lat, lastStep.endLocation.lng));
        return polyLinePoints;
    }

}
