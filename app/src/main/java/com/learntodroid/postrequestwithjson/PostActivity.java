package com.learntodroid.postrequestwithjson;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Comment;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// TODO: We probably have to optimize the code a little bit...
// TODO: But I'm tired ;_;
public class PostActivity extends AppCompatActivity
{

    private static final String TAG = "TAG" ;

    // Location stuff

    private FusedLocationProviderClient mFusedLocationClient;

    private float currentLatitude = (float) 0.0;
    private float currentLongitude = (float) 0.0;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private Button getLocationButton;

    private boolean isGPS = false;

    //All of the edit text fields and buttons:
    private EditText idInput;
    private EditText deviceNameInput;
    private EditText longitudeInput;
    private EditText latitudeInput;

    // Handler za autoPOST
    // Not sure if I can move this somewhere else, probably can?
    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable()
    {
        @Override
        public void run() {
            Log.d(TAG, "autoPOST was executed!");
            sendPost();
            handler.postDelayed(this, 10000);
        }
    };

    // TODO: Automate GPS fetching
    // TODO: This means that we have to:
    // TODO: a) Get GPS Location ✔️
    // TODO: b) Send the GPS location after getting it  ❌
    // TODO: c) Automate this process with autoPOST ❌

    private PostRepository commentsRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Button getLocationButton = (Button) findViewById(R.id.getLocationButton);

        //Klicanje buttona preko id-ja
        Button postButton = (Button) findViewById(R.id.postButton);
        Button getButton = (Button) findViewById(R.id.getButton);
        Switch autoPostSwitch = (Switch) findViewById(R.id.autoPostSwitch);

        // Get field id
        idInput = (EditText) findViewById(R.id.idInputEditText);
        deviceNameInput = (EditText) findViewById(R.id.deviceNameInput);
        longitudeInput = (EditText) findViewById(R.id.longitudeInput);
        latitudeInput = (EditText) findViewById(R.id.latitudeInput);

        // idInput = "2";
        // deviceNameInput = "Gasper Tine";
        // longitudeInput = "13.33337";
        // latitudeInput = "13.33338";

        commentsRepository = PostRepository.getInstance();
        autoPostSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    handler.postDelayed(runnable, 10000);

                }else{
                    Log.d(TAG, "autoPOST has been stopped!");
                    handler.removeCallbacks(runnable);
                }
            }
        });

        // Create post request when we click on POST
        postButton.setOnClickListener(v -> sendPost());

        // onClick for switching between activites this one goes from POST to GET menu
        getButton.setOnClickListener(v -> openGetActivity());

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Create location request
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000); //10 seconds
        locationRequest.setFastestInterval(5 * 1000); //5 seconds

        // Create callback and format output

        locationCallback = new LocationCallback()
        {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        currentLatitude = (float) location.getLatitude();
                        currentLongitude = (float) location.getLongitude();

                        /*txtLongitude.setText(String.format(Locale.US, "%s", currentLongitude));
                        txtLatitude.setText(String.format(Locale.US, "%s", currentLatitude));*/

                        if (mFusedLocationClient != null) {
                            mFusedLocationClient.removeLocationUpdates(locationCallback);
                        }
                    }
                }
            }
        };

        getLocationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });
    }

    private void openGetActivity() {
        Intent intent = new Intent(this, GetActivity.class);
        startActivity(intent);
    }

    private void sendPost(){
        // OBVEZNO moramo najprej iz EditText convertirati v String, ker mi podamo obliko EditText!
        String idSend = idInput.getText().toString();
        String deviceNameSend = deviceNameInput.getText().toString();
        String latitudeSend = latitudeInput.getText().toString();
        String longitudeSend = longitudeInput.getText().toString();

        Post post = new Post(
                idSend,
                deviceNameSend,
                latitudeSend,
                longitudeSend
        );

        //Don't know exactly how to implement POST without a callback right now, so yolo
        commentsRepository.getCommentsService().createComment(post).enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(@NotNull Call<Comment> call, @NotNull Response<Comment> r) {
                //Toast.makeText(getApplicationContext(), "Comment " + r.body().getId() + " created", Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Sending Post", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(@NotNull Call<Comment> call, @NotNull Throwable t) {
                //Toast.makeText(getApplicationContext(), "Error Creating Comment: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getLocation() {


        if (ActivityCompat.checkSelfPermission(PostActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(PostActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(PostActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1000);

        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(PostActivity.this, location -> {
            if (location != null) {
                currentLatitude = (float) location.getLatitude();
                currentLongitude = (float) location.getLongitude();

                Log.d(TAG, "Latitude: " + currentLatitude);
                Log.d(TAG, "Longitude: " + currentLongitude);


            } else {
                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            }
        });
    }

}