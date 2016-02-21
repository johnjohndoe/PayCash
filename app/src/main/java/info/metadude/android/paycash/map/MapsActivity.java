package info.metadude.android.paycash.map;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;

import java.util.List;

import info.metadude.android.branchfinder.ApiModule;
import info.metadude.android.branchfinder.BranchFinderService;
import info.metadude.android.branchfinder.MapBoundsFormatter;
import info.metadude.android.branchfinder.models.BoundingBox;
import info.metadude.android.branchfinder.models.Branch;
import info.metadude.android.branchfinder.models.GeoPoint;
import info.metadude.android.paycash.BuildConfig;
import info.metadude.android.paycash.R;
import retrofit.ErrorHandler;
import retrofit.RetrofitError;

public class MapsActivity extends AppCompatActivity implements
        GoogleMap.OnCameraChangeListener,
        GoogleMap.OnMarkerClickListener,
        OnMapReadyCallback {

    protected static final LatLng DEFAULT_LOCATION = new LatLng(
            BuildConfig.DEFAULT_LOCATION_LATITUDE,
            BuildConfig.DEFAULT_LOCATION_LONGITUDE);

    protected static final float DEFAULT_ZOOM_LEVEL = BuildConfig.DEFAULT_ZOOM_LEVEL;

    protected static final String BUNDLE_KEY_LAST_KNOWN_LOCATION =
            BuildConfig.APPLICATION_ID + ".LAST_KNOWN_LOCATION";

    protected List<Branch> mBranches;

    protected BranchRenderer mBranchRenderer;

    protected BranchFinderService mBranchFinderService;

    protected DirectionsRenderer mDirectionsRenderer;

    protected volatile LatLng mLastKnownLocation;

    protected View mLayout;

    protected LocationListener mLocationListener;

    protected GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mLayout = findViewById(R.id.activity_maps_layout);
        mBranchFinderService = ApiModule.provideBranchFinderService(
                BuildConfig.API_BASE_URL, new BranchFinderErrorHandler());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        int markerColor = ContextCompat.getColor(this, R.color.colorPrimary);
        mBranchRenderer = new BranchRenderer(googleMap, markerColor);

        int lineColor = ContextCompat.getColor(this, R.color.colorAccent);
        mDirectionsRenderer = new DirectionsRenderer(googleMap, lineColor);

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setAllGesturesEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.setOnCameraChangeListener(this);
        mMap.setOnMarkerClickListener(this);
        requestLocationUpdates();

        if (mLastKnownLocation == null) {
            centerAtLocation(DEFAULT_LOCATION);
        } else {
            centerAtLocation(mLastKnownLocation);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            outState.putParcelable(BUNDLE_KEY_LAST_KNOWN_LOCATION, mLastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(BUNDLE_KEY_LAST_KNOWN_LOCATION);
        }
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        fetchBranches();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker != null) {
            showBranchDetails(marker);
            return true;
        }
        return false;
    }

    private void centerAtLocation(@NonNull LatLng location) {
        if (mMap != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, DEFAULT_ZOOM_LEVEL));
        }
    }

    private void fetchBranches() {
        if (mMap != null) {
            if (mMap.getCameraPosition().zoom < DEFAULT_ZOOM_LEVEL) {
                return;
            }
            LatLngBounds latLngBounds = mMap.getProjection().getVisibleRegion().latLngBounds;
            GeoPoint southWest = new GeoPoint(
                    latLngBounds.southwest.latitude, latLngBounds.southwest.longitude);
            GeoPoint northEast = new GeoPoint(
                    latLngBounds.northeast.latitude, latLngBounds.northeast.longitude);
            BoundingBox boundingBox = new BoundingBox(southWest, northEast);
            final String mapBounds = MapBoundsFormatter.getFormattedString(boundingBox);
            new Thread() {
                @Override
                public void run() {
                    try {
                        mBranches = mBranchFinderService.getBranches(mapBounds);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Thread() {
                        @Override
                        public void run() {
                            renderBranches();
                        }
                    });
                }
            }.start();
        }
    }

    public void fetchDirections(@NonNull LatLng origin, @NonNull LatLng destination) {
        final GeoApiContext context = new GeoApiContext()
                .setApiKey(BuildConfig.GEO_API_KEY);
        final String destinationString = destination.latitude + ", " + destination.longitude;
        final String originString = origin.latitude + ", " + origin.longitude;
        new Thread() {
            @Override
            public void run() {
                DirectionsApiRequest request = DirectionsApi.getDirections(
                        context, originString, destinationString);
                request.setCallback(new PendingResult.Callback<DirectionsResult>() {
                    @Override
                    public void onResult(DirectionsResult result) {
                        for (DirectionsRoute route : result.routes) {
                            for (DirectionsLeg leg : route.legs) {
                                DirectionsStep[] steps = leg.steps;
                                if (steps != null) {
                                    final List<LatLng> polyLinePoints =
                                            DirectionsHelper.getPolyLinePoints(steps);
                                    if (polyLinePoints != null) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                renderDirections(polyLinePoints);
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        String message = getString(R.string.snack_bar_action_message_directions_request_failure);
                        String actionTitle = getString(R.string.snack_bar_action_title_dismiss);
                        showSnackBarMessage(message, actionTitle);
                        Log.e(getClass().getName(), message);
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }

    private void renderBranches() {
        if (mBranchRenderer != null) {
            mBranchRenderer.renderBranches(mBranches);
        }
    }

    private void renderDirections(@NonNull List<LatLng> polyLinePoints) {
        if (mDirectionsRenderer != null) {
            mDirectionsRenderer.renderDirections(polyLinePoints);
        }
    }

    private void requestLocationUpdates() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                if (location == null) {
                    return;
                }
                boolean updatePosition = false;
                if (mLastKnownLocation == null) {
                    updatePosition = true;
                }
                mLastKnownLocation = new LatLng(location.getLatitude(), location.getLongitude());
                if (updatePosition) {
                    centerAtLocation(mLastKnownLocation);
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                // Nothing to do here
            }

            public void onProviderEnabled(String provider) {
                // Nothing to do here
            }

            public void onProviderDisabled(String provider) {
                String message = getString(R.string.snack_bar_action_message_unknown_location);
                String actionTitle = getString(R.string.snack_bar_action_title_dismiss);
                showSnackBarMessage(message, actionTitle);
                // Nothing to do here
            }
        };
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 0, 5, mLocationListener);
    }

    private void showBranchDetails(@NonNull final Marker marker) {
        if (mLastKnownLocation == null) {
            String actionTitle = getString(R.string.snack_bar_action_title_dismiss);
            showSnackBarMessage(marker.getTitle(), actionTitle);
        } else {
            String actionTitle = getString(R.string.snack_bar_action_title_directions);
            showBranchDetailsWithDirections(marker, actionTitle);
        }
    }

    private void showBranchDetailsWithDirections(@NonNull final Marker marker,
                                                 @NonNull String actionTitle) {
        Snackbar.make(mLayout, marker.getTitle(), Snackbar.LENGTH_INDEFINITE)
                .setAction(actionTitle, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LatLng position = marker.getPosition();
                        if (mLastKnownLocation != null && position != null) {
                            fetchDirections(mLastKnownLocation, position);
                        }
                    }
                })
                .show();
    }

    private void showSnackBarMessage(@NonNull String message, @NonNull String actionTitle) {
        Snackbar.make(mLayout, message, Snackbar.LENGTH_INDEFINITE)
                .setAction(actionTitle, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Nothing to do here
                    }
                })
                .show();
    }

    protected class BranchFinderErrorHandler implements ErrorHandler {
        @Override
        public Throwable handleError(RetrofitError cause) {
            if (cause != null) {
                int httpStatusCode = cause.getResponse().getStatus();
                String message = getString(R.string.snack_bar_action_error_message, httpStatusCode);
                String actionTitle = getString(R.string.snack_bar_action_title_dismiss);
                showSnackBarMessage(message, actionTitle);
            }
            return null;
        }
    }

}
