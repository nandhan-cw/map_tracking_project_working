package com.example.map_tracking;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.map_tracking.databinding.ActivityOrdersMapBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class OrdersMap extends FragmentActivity implements OnMapReadyCallback {
    Location myLocation = null;
    LatLng myLatLng;
    private GoogleMap mMap;
    private ActivityOrdersMapBinding binding;
    private final static int LOCATION_REQUEST_CODE = 23;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LatLng myLocationLatLng;

    private LocationCallback locationCallback;
    boolean locationPermission = false;
    Marker userLocationMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = com.google.android.gms.location.LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY);

        binding = ActivityOrdersMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (locationPermission) {
            getMyLocation();
        }
        updateMapWithLocations();
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (locationPermission) {
            requestLocationUpdates();
        } else {
            requestLocationPermission();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        removeLocationUpdates();
    }
    private void updateMapWithLocations() {
        if (myLocationLatLng != null) {
            LatLng myLocation = myLocationLatLng;
            LatLng[] locations = {
                    // Add your locations here
                    new LatLng(11.055052492365592, 76.9941535113549),   // cms school
                    new LatLng(11.042712294929895, 76.97671756902588),  // prozone
                    new LatLng(11.044691963579623, 76.97515115903583),  // call for cake
                    new LatLng(11.039336595380634, 76.97945568067028),  // krishnagounder marraigehall
                    new LatLng(11.045729979784863, 76.9763829990555),   // surya hospital
                    new LatLng(11.04120113605103, 76.98148968251917)    // kprhall
            };

            // Calculate distances and sort the locations based on proximity to myLocation
            Arrays.sort(locations, new Comparator<LatLng>() {
                @Override
                public int compare(LatLng location1, LatLng location2) {
                    double distanceToLocation1 = calculateDistance(myLocation, location1);
                    double distanceToLocation2 = calculateDistance(myLocation, location2);
                    return Double.compare(distanceToLocation1, distanceToLocation2);
                }
            });

            // Print the sorted array in the log
            Log.d(TAG, "Sorted Locations: " + Arrays.toString(locations));

            int markerNumber = 1; // Marker number counter

            for (LatLng latLng : locations) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);

                // Create a custom bitmap for the marker icon with the marker number inside the round_red shape
                BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromBitmap(getMarkerBitmap(markerNumber));
                markerOptions.icon(markerIcon);

                mMap.addMarker(markerOptions);

                markerNumber++; // Increment the marker number
            }

            // Move the camera to the nearest location
            mMap.moveCamera(CameraUpdateFactory.newLatLng(locations[0]));
        }
    }

    private void requestLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    private void removeLocationUpdates() {
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
    }

    private Bitmap getMarkerBitmap(int markerNumber) {
        // Create a round_red shape bitmap
        Bitmap shapeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.round_red);

        // Create a mutable bitmap with the same size as the shapeBitmap
        Bitmap markerBitmap = shapeBitmap.copy(Bitmap.Config.ARGB_8888, true);

        // Create a canvas to draw on the markerBitmap
        Canvas canvas = new Canvas(markerBitmap);

        // Create a Paint object for drawing text
        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(getResources().getDimension(R.dimen.marker_text_size));
        textPaint.setTextAlign(Paint.Align.CENTER);

        // Calculate the center position to draw the marker number
        int centerX = markerBitmap.getWidth() / 2;
        int centerY = markerBitmap.getHeight() / 2;
        float textBaseline = centerY - ((textPaint.descent() + textPaint.ascent()) / 2);

        // Draw the shapeBitmap as the background
        canvas.drawBitmap(shapeBitmap, 0, 0, null);

        // Draw the marker number text
        canvas.drawText(String.valueOf(markerNumber), centerX, textBaseline, textPaint);

        return markerBitmap;
    }

    private void getMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                myLocation = location;
                myLocationLatLng = new LatLng(location.getLatitude(), location.getLongitude()); // Store the LatLng value
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                //Toast.makeText(OrdersMap.this, "Current Location: " + latLng.latitude + ", " + latLng.longitude, Toast.LENGTH_SHORT).show();
                if (userLocationMarker == null) {
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.redcar1));
                    markerOptions.rotation(location.getBearing());
                    markerOptions.anchor((float) 0.5, (float) 0.5);
                    userLocationMarker = mMap.addMarker(markerOptions);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                } else {
                    userLocationMarker.setPosition(latLng);
                    userLocationMarker.setRotation(location.getBearing());
                }
                updateMapWithLocations();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermission = true;
                    getMyLocation();
                } else {
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    private double calculateDistance(LatLng location1, LatLng location2) {
        if (location1 == null || location2 == null) {
            // Handle the case where either startPoint or endPoint is null
            return 0.0;
        }
        double lat1 = location1.latitude;
        double lon1 = location1.longitude;
        double lat2 = location2.latitude;
        double lon2 = location2.longitude;

        double earthRadius = 6371; // in kilometers
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;

        Log.d(TAG, "calculateDistance: " + distance);
        return distance;
    }
}
