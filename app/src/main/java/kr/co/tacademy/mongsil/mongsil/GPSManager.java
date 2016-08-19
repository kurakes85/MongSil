package kr.co.tacademy.mongsil.mongsil;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by ccei on 2016-08-18.
 */
public class GPSManager implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    public static final String TAG = LocationProvider.class.getSimpleName();
    private static final int REQUEST_RESOLVE_ERROR = 18;

    public abstract interface LocationCallback {
        public void handleNewLocation(Location location);
    }

    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;

    boolean resolvingError = false;

    private BaseActivity activity;
    private String latitude, longitude;

    GPSManager(BaseActivity activity, LocationCallback callback) {
        this.activity = activity;
        if(!resolvingError) {
            googleApiStart();
            locationCallback = callback;
            locationRequest = locationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(10 * 1000)
                    .setFastestInterval(1 * 1000);
        }
    }

    public void googleApiStart() {
        googleApiClient = new GoogleApiClient.Builder(activity.getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void connect() {
        Log.e("GPS 커넥션 : ", "커넥션 완료");
        googleApiClient.connect();
    }

    public void GPSstop() {
        if(googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        locationCallback.handleNewLocation(location);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        int permissionCheck = ContextCompat.checkSelfPermission(
                activity.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionCheck== PackageManager.PERMISSION_DENIED){
            // 권한 없음
            Log.e("GPS 권한 체크 : ", "권한 없음");
            return;
        }

        Log.i(TAG, "Location services connected.");
        Location location =
                LocationServices.FusedLocationApi
                        .getLastLocation(googleApiClient);

        if (location == null) {
            LocationServices.FusedLocationApi
                    .requestLocationUpdates(
                            googleApiClient, locationRequest, this);

            latitude = String.valueOf(location.getLatitude());
            longitude = String.valueOf(location.getLongitude());

            Log.d("Location : Latitude", latitude);
            Log.d("Location : Longitude", longitude);
            Log.d("Location : Accuracy", String.valueOf(location.getAccuracy()));
        } else {
            locationCallback.handleNewLocation(location);
        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if(resolvingError) {
            return;
       } else if (connectionResult.hasResolution()){
            try {
                resolvingError = true;
                connectionResult.startResolutionForResult(activity, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                googleApiClient.connect();
            }
        } else {
            Log.e("구글 API 연결 실패", ": " + connectionResult.getErrorCode());
            showErrorDialog();
            resolvingError = true;
        }
    }

    private void showErrorDialog() {
        activity.getSupportFragmentManager().beginTransaction()
                .add(MiddleAloneDialogFragment.newInstance(80),
                        "google_api_connection_fail").commit();
    }

}