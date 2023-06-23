package com.example.map_tracking;

import static android.service.controls.ControlsProviderService.TAG;
import static android.view.FrameMetrics.ANIMATION_DURATION;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, RoutingListener {

    //google map object
    private GoogleMap mMap;

    //current and destination location objects
    Location myLocation = null;
    Location destinationLocation = null;
    protected LatLng start = null;
    protected LatLng end = null;
    protected LatLng startlatilongval = null;
    protected LatLng endlatilongval = null;
    LocationRequest locationRequest;
    //to get location permissions.
    private final static int LOCATION_REQUEST_CODE = 23;
    Marker userLocationMarker;
    private Location previousLocation;
    boolean locationPermission = false;
    FusedLocationProviderClient fusedLocationProviderClient;
    //polyline object
    private List<Polyline> polylines = null;
    ArrayList latlngarray;
    Button myButton;
    DatabaseReference latlngdatabase;
    protected LatLng startLatLng = null;
    protected LatLng endLatLng = null;
    protected LatLng updatesLatLng = null;
    LocationInfo latlngvalue;
    Circle userLocationAccuracyCircle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        //request location permission.
        requestPermision();
        latlngdatabase = FirebaseDatabase.getInstance().getReference("LocationInfo");
        myButton =findViewById(R.id.myButton);
        latlngarray = new ArrayList();
        //init google map fragment to show map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent c = new Intent(MapsActivity.this, MapsActivity2.class);
                startActivity(c);
            }
        });
        Toast.makeText(this, "maps1", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        previousLocation = null;
        if (locationPermission) {

        }
        Toast.makeText(MapsActivity.this, "on map ready", Toast.LENGTH_SHORT).show();

        // Add the code snippet here
        // Define a method to pass the values to Findroutes()


// Inside your existing code
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                latlngdatabase.child("Deliveryperson1").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Retrieve values from the snapshot
                        String latLngString = snapshot.child("latlngupdates").getValue(String.class);
                        String startlatLngString = snapshot.child("startpoint").getValue(String.class);
                        String endlatLngString = snapshot.child("destinationpoint").getValue(String.class);
                        String bearingVal = snapshot.child("bearing").getValue(String.class);
                        setUserLocationMarker(latLngString, bearingVal);

                        if (startlatLngString != null && endlatLngString != null) {
                            // Parse the latitude and longitude values
                            String[] startpointlatLngValues = startlatLngString.split(",");
                            String[] endpointlatLngValues = endlatLngString.split(",");
                            String[] updatespointlatLngString = latLngString.split(",");
                            if (startpointlatLngValues.length == 2 && endpointlatLngValues.length == 2) {
                                double latitude = Double.parseDouble(startpointlatLngValues[0]);
                                double longitude = Double.parseDouble(startpointlatLngValues[1]);

                                double latitude1 = Double.parseDouble(endpointlatLngValues[0]);
                                double longitude1 = Double.parseDouble(endpointlatLngValues[1]);


                                double latitude2 = Double.parseDouble(updatespointlatLngString[0]);
                                double longitude2 = Double.parseDouble(updatespointlatLngString[1]);
                                // Create LatLng objects
                                startLatLng = new LatLng(latitude, longitude);
                                endLatLng = new LatLng(latitude1, longitude1);
                                updatesLatLng = new LatLng(latitude2,longitude2);
                                //passValuesToFindRoutes(startLatLng, endLatLng,updatesLatLng);
                                Findroutes(updatesLatLng,endLatLng);

                                // Pass the values to the method outside of onDataChange()
                               // Toast.makeText(MapsActivity.this, "updated latlng "+updatesLatLng.toString(), Toast.LENGTH_SHORT).show();


                            }
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                handler.postDelayed(this, 500);
            }
        }, 500);



    }

    private void passValuesToFindRoutes(LatLng startLatLng,LatLng endLatLng,LatLng updateslatlng) {
       //Toast.makeText(this, "check: "+updateslatlng.toString()+" end. "+endLatLng.toString(), Toast.LENGTH_SHORT).show();
        Findroutes(updateslatlng, endLatLng);
        markerstartend(startLatLng, endLatLng);
    }

    private void requestPermision() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_REQUEST_CODE);
        } else {
            locationPermission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //if permission granted.
                    locationPermission = true;


                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }


        private void setUserLocationMarker(String latLngString, String bearingVal) {

            if (latLngString != null) {
                String[] latLngValues = latLngString.split(",");
                if (latLngValues.length == 2) {
                    double latitude = Double.parseDouble(latLngValues[0]);
                    double longitude = Double.parseDouble(latLngValues[1]);



                    Location updatedLocation = new Location("");
                    updatedLocation.setLatitude(latitude);
                    updatedLocation.setLongitude(longitude);

                    LatLng latLng = new LatLng(updatedLocation.getLatitude(), updatedLocation.getLongitude());

                    if (userLocationMarker == null) {
                        // Create a new marker
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.redcar1));
                        markerOptions.anchor((float) 0.5, (float) 0.5);
                        userLocationMarker = mMap.addMarker(markerOptions);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                    } else {
                        // Use the previously created marker
                        userLocationMarker.setPosition(latLng);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                    }

                    // Rotate the marker based on bearing value
                    if (bearingVal != null) {
                        float bearing = Float.parseFloat(bearingVal);
                        if (userLocationMarker != null) {
                            userLocationMarker.setRotation(bearing);
                        }
                    }

                    if (userLocationAccuracyCircle == null) {
                        CircleOptions circleOptions = new CircleOptions();
                        circleOptions.center(latLng);
                        circleOptions.strokeWidth(4);
                        circleOptions.strokeColor(Color.argb(255, 255, 0, 0));
                        circleOptions.fillColor(Color.argb(32, 255, 0, 0));
                        circleOptions.radius(updatedLocation.getAccuracy());
                        userLocationAccuracyCircle = mMap.addCircle(circleOptions);
                    } else {
                        userLocationAccuracyCircle.setCenter(latLng);
                        userLocationAccuracyCircle.setRadius(updatedLocation.getAccuracy());
                    }
                }
            }
            //start =new LatLng(11.039389246920685, 76.97952005368357);
            //end = new LatLng(11.04828942895949, 76.97322062669703);
            //get destination location when user click on map
            //mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            //   @Override
            //  public void onMapClick(LatLng latLng) {

            //      end=latLng;

            //      mMap.clear();

            //       start=new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
            //start route finding

            //Findroutes(start,end);
        }

    public void markerstartend(LatLng polylineStartLatLng,LatLng polylineEndLatLng){
        if(polylines!=null) {
            polylines.clear();
        }
        PolylineOptions polyOptions = new PolylineOptions();
        //Add Marker on route starting position
        MarkerOptions startMarker = new MarkerOptions();
        startMarker.position(polylineStartLatLng);
        startMarker.title("My Location");
        mMap.addMarker(startMarker);

        //Add Marker on route ending position
        MarkerOptions endMarker = new MarkerOptions();
        endMarker.position(polylineEndLatLng);
        endMarker.title("Destination");
        mMap.addMarker(endMarker);
    }
        // function to find Routes.
    public void Findroutes(LatLng Start, LatLng End)
    {
        if(Start==null || End==null) {
            Toast.makeText(MapsActivity.this,"Unable to get location", Toast.LENGTH_LONG).show();
        }
        else
        {
           Toast.makeText(this, "startupdates= "+Start, Toast.LENGTH_SHORT).show();

            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(Start, End)
                    .key("AIzaSyBoTdErOd_1lQGFxgkjIroIJeUOhY76_gE")  //also define your api key here.
                    .build();
            routing.execute();
        }
    }

    //Routing call back functions.
    @Override
    public void onRoutingFailure(RouteException e) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar snackbar= Snackbar.make(parentLayout, e.toString(), Snackbar.LENGTH_LONG);
        snackbar.show();
        Toast.makeText(this, "faaa", Toast.LENGTH_SHORT).show();
//        Findroutes(start,end);
    }

    @Override
    public void onRoutingStart() {
        //Toast.makeText(MapsActivity.this,"Finding Route...",Toast.LENGTH_LONG).show();
    }

    //If Route finding success..
    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {

        CameraUpdate center = CameraUpdateFactory.newLatLng(startLatLng);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
        if(polylines!=null) {
            polylines.clear();
        }
        PolylineOptions polyOptions = new PolylineOptions();
        LatLng polylineStartLatLng=null;
        LatLng polylineEndLatLng=null;


        polylines = new ArrayList<>();
        //add route(s) to the map using polyline
        for (int i = 0; i <route.size(); i++) {

            if(i==shortestRouteIndex)
            {
                polyOptions.color(getResources().getColor(R.color.purple_200));
                polyOptions.width(16);
                polyOptions.addAll(route.get(shortestRouteIndex).getPoints());
                Polyline polyline = mMap.addPolyline(polyOptions);
                polylineStartLatLng=polyline.getPoints().get(0);
                int k=polyline.getPoints().size();
                polylineEndLatLng=polyline.getPoints().get(k-1);
                polylines.add(polyline);

            }
            else {

            }

        }


    }

    @Override
    public void onRoutingCancelled() {
        Findroutes(startLatLng,endLatLng);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Findroutes(startLatLng,endLatLng);

    }
}

