package com.learntodroid.postrequestwithjson;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Comment;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostActivity extends FragmentActivity implements OnMapReadyCallback
{
    public ArrayList<Float> gpsLatLong = new ArrayList<>();

    public GoogleMap gMap;

    private static final String TAG = "TAG";

    // Location stuff
    private FusedLocationProviderClient mFusedLocationClient;

    private float currentLatitude = (float) 0.0;
    private float currentLongitude = (float) 0.0;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    // Handler for autoPOST
    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable()
    {
        @Override
        public void run() {
            Log.d(TAG, "autoPOST was executed!");
            sendPost();
            // Modify the first digit, according to how many seconds we want in between our POST
            // requests
            handler.postDelayed(this, 10 * 1000);
        }
    };

    private PostRepository commentsRepository;

    EditText getIdInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if(!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            buildAlertMessageNoGPS();
        }

        Button getLocationButton = findViewById(R.id.getLocationButton);

        //Klicanje buttona preko id-ja
        Button postButton = findViewById(R.id.postButton);
        Button getButton = findViewById(R.id.getButton);
        Button clearMarkersButton = findViewById(R.id.clearMarkersButton);

        getIdInput = findViewById(R.id.idInputEditText);

        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch autoPostSwitch = findViewById(R.id.autoPostSwitch);

        // I removed all of the input fields, because they're not needed anymore
        // We automatically build our input without user input

        // Example of sent information
        // idInput = "2";
        // deviceNameInput = "Gasper Tine";
        // longitudeInput = "13.33337";
        // latitudeInput = "13.33338";

        commentsRepository = PostRepository.getInstance();

        autoPostSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // We have to modify this number as well! Change accoriding to how many seconds
                // in between our POST calls
                handler.postDelayed(runnable, 10 * 1000);

            } else {
                Log.d(TAG, "autoPOST has been stopped!");
                handler.removeCallbacks(runnable);
            }
        });

        // Create post request when we click on POST
        postButton.setOnClickListener(v -> sendPost());

        // onClick for switching between activites this one goes from POST to GET menu
        getButton.setOnClickListener(v -> openGetActivity());

        getLocationButton.setOnClickListener(v -> getLocation());

        clearMarkersButton.setOnClickListener((v -> clearMarkers()));

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Create location request
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000); //10 seconds
        locationRequest.setFastestInterval(5 * 1000); //5 seconds

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NotNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        currentLatitude = (float) location.getLatitude();
                        currentLongitude = (float) location.getLongitude();

                        if (mFusedLocationClient != null) {
                            mFusedLocationClient.removeLocationUpdates(locationCallback);
                        }
                    }
                }
            }
        };

        // We have have to call this on startup because we can't make a POST without a predefined
        // Location, I tried to do this in the function, but couldn't find the solution
        getLocation();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    private void buildAlertMessageNoGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("GPS Storitev je onemogočena, ali jo želite omogočiti?")
                .setCancelable(false)
                .setPositiveButton("Da", (dialog, id) -> startActivity(new Intent(android
                        .provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton("Ne", (dialog, id) -> dialog.cancel());
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void openGetActivity() {
        Intent intent = new Intent(this, GetActivity.class);
        startActivity(intent);
    }

    private void sendPost() {
        getLocation();

        // OBVEZNO moramo najprej iz EditText convertirati v String, ker mi podamo obliko EditText!
        String idSend = getIdInput.getText().toString();
        // We are using Build.ID as a unique identifier, I'm not sure how unique this actually is
        // We are not allowed to get IMEI this is a system limitation...
        // This is possibly something we can improve...
        String deviceNameSend = Build.MODEL;
        String latitudeSend = gpsLatLong.get(0).toString();
        String longitudeSend = gpsLatLong.get(1).toString();

        Post post = new Post(
                idSend,
                deviceNameSend,
                latitudeSend,
                longitudeSend
        );

        //Don't know exactly how to implement POST without a callback right now, so yolo
        commentsRepository.getCommentsService().sendPost(post).enqueue(new Callback<Comment>(){
            @Override
            public void onResponse(@NotNull Call<Comment> call, @NotNull Response<Comment> r) {
                Toast.makeText(getApplicationContext(), "Sending Post", Toast.LENGTH_SHORT)
                        .show();
            }
            @Override
            public void onFailure(@NotNull Call<Comment> call, @NotNull Throwable t) {
                // onFailure
            }
        });
    }

    private void getLocation() {

        if (ActivityCompat.checkSelfPermission(PostActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(PostActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(PostActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    1000);
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(PostActivity.this,
                location -> {
            if (location != null) {

                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback,
                        null);

                currentLatitude = (float) location.getLatitude();
                currentLongitude = (float) location.getLongitude();

                Log.d(TAG, "Latitude: " + currentLatitude);
                Log.d(TAG, "Longitude: " + currentLongitude);

                gpsLatLong.clear();
                gpsLatLong.add(currentLatitude);
                gpsLatLong.add(currentLongitude);

                putMarker();

                Toast.makeText(getApplicationContext(), "Latitude: " + currentLatitude
                        + " Longitude: " + currentLongitude, Toast.LENGTH_SHORT).show();

            } else {
                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback,
                        null);
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        LatLng centerOfSlovenia = new LatLng(46.056946, 14.505751);

        gMap = googleMap;
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        gMap.getUiSettings().setZoomControlsEnabled(true);
        gMap.getUiSettings().setZoomGesturesEnabled(true);
        gMap.getUiSettings().setCompassEnabled(true);
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(centerOfSlovenia, 7));
    }

    public void putMarker(){
        LatLng currentPosition = new LatLng(gpsLatLong.get(0), gpsLatLong.get(1));
        gMap.addMarker(new MarkerOptions()
                .position(currentPosition)
                .title(gpsLatLong.get(0).toString() + ", " +  gpsLatLong.get(1).toString()));
    }
    public void clearMarkers(){
        gMap.clear();
    }
}