package info.metadude.android.paycash.map;

import android.graphics.Color;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import info.metadude.android.branchfinder.models.Branch;

public class BranchRenderer {

    protected final GoogleMap mMap;

    protected final BitmapDescriptor mMarkerIcon;

    protected List<Marker> mBranchMarkers;

    public BranchRenderer(@NonNull GoogleMap mMap, int markerColor) {
        this.mMap = mMap;
        float[] hsv = new float[3];
        Color.colorToHSV(markerColor, hsv);
        mMarkerIcon = BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }

    public void renderBranches(@NonNull List<Branch> mBranches) {
        clearAllBranches();
        mBranchMarkers = new ArrayList<>(mBranches.size());
        MarkerOptions markerOptions;
        for (Branch branch : mBranches) {
            LatLng position = new LatLng(branch.getLatitude(), branch.getLongitude());
            markerOptions = new MarkerOptions()
                    .position(position)
                    .title(branch.getTitle())
                    .icon(mMarkerIcon)
                    .snippet(branch.getStreetNr());
            Marker marker = mMap.addMarker(markerOptions);
            mBranchMarkers.add(marker);
        }
    }

    private void clearAllBranches() {
        if (mBranchMarkers != null && !mBranchMarkers.isEmpty()) {
            for (Marker branchMarker : mBranchMarkers) {
                branchMarker.remove();
            }
            mBranchMarkers.clear();
        }
    }

}
