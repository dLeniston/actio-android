package ie.wit.darren.actio;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Dazza on 20/03/2018.
 */

public class TrackerActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private TextView travelled;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private double totalMeters = 0;
    public String totalTime;
    public String activity;
    private Polyline polyline;
    //List<LatLng> snappedPoints = new ArrayList<>();
    final List<LatLng> polylinePoints = new ArrayList<>();
    public static GoogleMap mMap;

    TextView textView;
    FloatingActionButton pauseButton;
    ImageView icon;
    Boolean clicked = false;

    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L;

    Handler handler;

    int Seconds, Minutes, MilliSeconds;

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            mMap.setMyLocationEnabled(true);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tracker);
        icon = (ImageView) findViewById(R.id.tracking_icon);
        textView = (TextView) findViewById(R.id.textView);
        pauseButton = (FloatingActionButton) findViewById(R.id.pauseButton);
        travelled = (TextView) findViewById(R.id.distance);
        Button finishButton = (Button) findViewById(R.id.tracker_submit_button);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tracker_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Tracker");
        Intent intent = getIntent();
        activity = intent.getStringExtra("activity").toLowerCase();

        setIcon(icon, activity);

        handler = new Handler();
        buildGoogleApiClient();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.tracker_map);
        mapFragment.getMapAsync(this);


        StartTime = SystemClock.uptimeMillis();

        finishButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String s = convertDistance(totalMeters);
                Intent intent = new Intent(TrackerActivity.this, TrackingCompleteActivity.class);
                intent.putExtra("time", totalTime);
                intent.putExtra("distance", s);
                intent.putExtra("type", activity);
                stopClock();
                clicked = false; //Set clicked to false so user won't have to press start button twice
                startActivity(intent);
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                clicked ^= true;
                if (!clicked) {
                    stopClock();
                }else{
                    startClock();
                }
            }
        });
    }

    public void setIcon(ImageView image, String string){
        switch(string){
            case "running":
                image.setImageResource(R.drawable.running_icon);
                break;
            case "cycling":
                image.setImageResource(R.drawable.cycling_icon);
                break;
            case "walking":
                image.setImageResource(R.drawable.walking_icon);
                break;
            case "hiking":
                image.setImageResource(R.drawable.hiking_icon);
                break;
        }
    }

    public Runnable runnable = new Runnable() {
        public void run() {
            MillisecondTime = SystemClock.uptimeMillis() - StartTime;
            UpdateTime = TimeBuff + MillisecondTime;
            Seconds = (int) (UpdateTime / 1000);
            Minutes = Seconds / 60;
            Seconds = Seconds % 60;
            MilliSeconds = (int) (UpdateTime % 1000);
            totalTime = "" + Minutes + ":" + String.format("%02d", Seconds) + ":" + String.format("%03d", MilliSeconds);
            /*textView.setText("" + Minutes + ":"
                    + String.format("%02d", Seconds) + ":"
                    + String.format("%03d", MilliSeconds));*/
            textView.setText(totalTime);
            handler.postDelayed(this, 0);
        }

    };

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        moveCamera(mMap, latLng, 15);
        polylinePoints.add(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        polyline = mMap.addPolyline(new PolylineOptions()
                .addAll(polylinePoints)
                .color(Color.BLUE)
                .width(20));
        double distance = mLastLocation.distanceTo(location);
        mLastLocation = location;
        totalMeters += distance;
        if(distance < 4 && totalMeters != 0){
            return;
        }else {

            travelled.setText(convertDistance(totalMeters));
        }
    }

    private void moveCamera(GoogleMap map, LatLng latLng, float zoom){
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();
    }

    public void stopClock(){
            TimeBuff += MillisecondTime;
            handler.removeCallbacks(runnable);
            pauseButton.setImageResource(android.R.drawable.ic_media_play);
    }

    public void startClock(){
        StartTime = SystemClock.uptimeMillis();
        handler.postDelayed(runnable, 0);
        pauseButton.setImageResource(android.R.drawable.ic_media_pause);
    }

    public String convertDistance(double d){
        String converted = String.format("%.2f", (d/1000)) + "Km";
        return converted;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }
}

