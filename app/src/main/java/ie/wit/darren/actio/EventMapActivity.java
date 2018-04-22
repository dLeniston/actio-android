package ie.wit.darren.actio;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;

import ie.wit.darren.actio.modules.FetchUrl;

/**
 * Created by Dazza on 27/02/2018.
 */

public class EventMapActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    //Vars
    private static final String TAG = "EventMap";
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private Boolean mLocationPermissionsGranted = true;
    public static GoogleMap eventMap;
    double lat = 0.0;
    double lng = 0.0;
    private double totalMeters = 0;
    private String event;
    private LatLng origin;
    private LatLng destination;
    //private FetchUrl fetchUrl = new FetchUrl(EventMapActivity.this);

    //Service vars
    ArrayList<LatLng> MarkerPoints;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    Marker mCurrLocationMarker;
    LatLng latLng;
    private LocationRequest mLocationRequest;
    MarkerOptions markerOptionsDest = new MarkerOptions();
    MarkerOptions markerOptionsOrigin = new MarkerOptions();

    //Widgets
    private TextView distanceLeft;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        eventMap = googleMap;

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                eventMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            eventMap.setMyLocationEnabled(true);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.map_toolbar);
        distanceLeft = (TextView) findViewById(R.id.distance_left);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Route Map");
        Intent intent = getIntent();

        event = intent.getStringExtra("event");
        String address = intent.getStringExtra("address");
        TextView routeDetails = (TextView) findViewById(R.id.route_details);
        String details = "Route to: " + event + "\n" + "at: " + address;
        routeDetails.setText(details);

        String sLat = intent.getStringExtra("lat");
        String sLon = intent.getStringExtra("lon");
        lat = Double.parseDouble(sLat);
        lng = Double.parseDouble(sLon);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getLocationPermission();
        }
        // Initializing
        MarkerPoints = new ArrayList<>();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        initMap();
    }

    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.event_map);

        mapFragment.getMapAsync(EventMapActivity.this);
    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    private String getUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }


    @Override
    public void onLocationChanged(Location location) {
        //Place current location marker
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        moveCamera(eventMap, latLng, 15f);

        //Get origin and destination
        origin = latLng;
        destination = new LatLng(lat, lng);

        float distance = getDistance(location.getLatitude(), location.getLongitude(), lat, lng);
        distanceLeft.setText(String.format("%.2f", (distance / 1000)) + "Km");

        markerSetup(origin, destination);

        // Checks, whether start and end locations are captured
        LatLng o = MarkerPoints.get(0);
        LatLng d = MarkerPoints.get(1);
        getDirections(o, d);
    }

    private void markerSetup(LatLng o, LatLng d){
        //Add new marker to the Map
        MarkerPoints.clear();
        MarkerPoints.add(o);
        MarkerPoints.add(d);
        markerOptionsDest.position(d).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        markerOptionsDest.title(event + " - Start");
        eventMap.addMarker(markerOptionsDest);
    }

    private void getDirections(LatLng o, LatLng d){
        // Start downloading json data from Google Directions API
        String url = getUrl(o, d);
        FetchUrl fetchUrl = new FetchUrl(getApplicationContext());
        fetchUrl.execute(url);
    }

    private void moveCamera(GoogleMap map, LatLng latLng, float zoom){
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private float getDistance(double lat1,double lon1,double lat2,double lon2) {
        float[] distance = new float[2];
        Location.distanceBetween( lat1, lon1,
                lat2, lon2, distance);
        return distance[0];
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult){
        Toast.makeText(getApplicationContext(), "You can't make map requests", Toast.LENGTH_SHORT).show();
    }

}

