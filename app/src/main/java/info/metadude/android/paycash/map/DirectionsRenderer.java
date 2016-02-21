package info.metadude.android.paycash.map;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class DirectionsRenderer {

    protected final GoogleMap mMap;

    protected final int mLineColor;

    protected Polyline mDirectionsLine;

    public DirectionsRenderer(@NonNull GoogleMap mMap, int lineColor) {
        this.mMap = mMap;
        this.mLineColor = lineColor;
    }

    public void renderDirections(@NonNull List<LatLng> polyLinePoints) {
        clearDirections();
        PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(polyLinePoints)
                .color(mLineColor);
        mDirectionsLine = mMap.addPolyline(polylineOptions);
    }

    private void clearDirections() {
        if (mDirectionsLine != null) {
            mDirectionsLine.remove();
        }
    }

}
