package com.example.map_tracking;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MovingPath extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, RoutingListener {

    //google map object
    private GoogleMap mMap;
    ArrayList latlngarray;
    //current and destination location objects
    Location myLocation = null;
    Location destinationLocation = null;
    protected LatLng start = null;
    protected LatLng end = null;
    private Polyline currentRoute;

    //to get location permissions.
    private final static int LOCATION_REQUEST_CODE = 23;
    boolean locationPermission = false;


    //polyline object
    private List<Polyline> polylines = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moving_path);
        latlngarray = new ArrayList();
        //request location permission.
        requestPermision();

        //init google map fragment to show map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
                    getMyLocation();

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    //to get user location
    private void getMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {

                myLocation=location;
                LatLng ltlng=new LatLng(location.getLatitude(),location.getLongitude());
                //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(ltlng, 16f);

               // mMap.animateCamera(cameraUpdate);
            }
        });
        double[] latitudeArray = {11.04842, 11.03901, 11.03895, 11.03901, 11.03922, 11.03926, 11.0393883, 11.03964, 11.03968, 11.03985, 11.03998, 11.04024, 11.04035, 11.04028, 11.0405, 11.0405, 11.0411467, 11.0414133, 11.0417233, 11.04174, 11.04227, 11.04245, 11.04281, 11.04337, 11.0436, 11.04384, 11.04407, 11.04424, 11.04429, 11.04497, 11.0452983, 11.04559, 11.04588, 11.04609, 11.04645, 11.04669, 11.0469, 11.04712};
        double[] longitudeArray = {76.97307, 76.97964, 76.97976, 76.97991, 76.98033, 76.9803883, 76.9805517, 76.98086, 76.98091, 76.98113, 76.9812917, 76.9816167, 76.98169, 76.98148, 76.98132, 76.98132, 76.98107, 76.9809717, 76.9808833, 76.98088, 76.98071, 76.98063, 76.98043, 76.98013, 76.98001, 76.97989, 76.97974, 76.97952, 76.97946, 76.97923, 76.9791, 76.97894, 76.97884, 76.97875, 76.97856, 76.97847, 76.97843, 76.9784183};
        end = new LatLng(11.04828942895949, 76.97322062669703);
        double value = 11.04828942895949;
        int length = latitudeArray.length;

        List<Double> endlatlist = Collections.nCopies(length, 11.04828942895949);
        List<Double> endlonlist = Collections.nCopies(length, 76.97322062669703);

        start =new LatLng(11.039389246920685, 76.97952005368357);
       // Findroutes(start,end);
        final int[] i = {0};
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(start, 16f);

        mMap.animateCamera(cameraUpdate);
        int n = latitudeArray.length;
        printrun(latitudeArray,longitudeArray,0,endlatlist,endlonlist);
    }

    private void printrun(double[] latitudeArray, double[] longitudeArray, int i, List<Double> endlatlist, List<Double> endlonlist) {

        if(i<latitudeArray.length){
            start =new LatLng(latitudeArray[i], longitudeArray[i]);
            //end=new LatLng(latitudeArray[i+1], longitudeArray[i+1]);

            if (polylines != null) {
                for (Polyline polyline : polylines) {
                    polyline.remove();
                }
                polylines.clear();
            }
          //  Toast.makeText(this, ""+latitudeArray[i]+" "+longitudeArray[i], Toast.LENGTH_SHORT).show();
            Handler handler = new Handler(Looper.getMainLooper());
            Runnable printRunnable;
            Findroutes(start, end);
            int ni = i+1;

            printRunnable = new Runnable() {
                @Override
                public void run() {
                    printrun(latitudeArray,longitudeArray,ni,endlatlist,endlonlist);
                }
            };
            handler.postDelayed(printRunnable, 1000);
        }


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(locationPermission) {
            getMyLocation();
        }

    }


    // function to find Routes.
    public void Findroutes(LatLng Start, LatLng End)
    {
        if(Start==null || End==null) {
            Toast.makeText(MovingPath.this,"Unable to get location", Toast.LENGTH_LONG).show();
        }
        else
        {
            //Toast.makeText(this, ""+Start+" end: "+End, Toast.LENGTH_SHORT).show();
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
        Toast.makeText(this, "failure", Toast.LENGTH_SHORT).show();
        Findroutes(start,end);
    }

    @Override
    public void onRoutingStart() {
       // Toast.makeText(MovingPath.this,"Finding Route...",Toast.LENGTH_LONG).show();
    }

    //If Route finding success..
    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {


        CameraUpdate center = CameraUpdateFactory.newLatLng(end);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
        if(polylines!=null) {
            polylines.clear();

            Toast.makeText(this, "poly line clea", Toast.LENGTH_SHORT).show();
        }

        if (currentRoute != null) {
            currentRoute.remove();
            currentRoute = null;
        }


        PolylineOptions polyOptions = new PolylineOptions();
        currentRoute = mMap.addPolyline(polyOptions);

        LatLng polylineStartLatLng=null;
        LatLng polylineEndLatLng=null;


        polylines = new ArrayList<>();
        //add route(s) to the map using polyline
        for (int i = 0; i <route.size(); i++) {

            if(i==shortestRouteIndex)
            {
                Toast.makeText(this, "drawing route", Toast.LENGTH_SHORT).show();

                polyOptions.color(getResources().getColor(R.color.black));
                polyOptions.width(15);
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
        Findroutes(start,end);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Findroutes(start,end);

    }
}