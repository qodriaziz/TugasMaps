package com.example.tugasmaps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

//import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;


import android.Manifest;
import android.provider.Settings;
import android.view.WindowManager;
import android.widget.SearchView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback {
    //boolean isPersmissionGranter;

    SearchView mapSearchView;

    GoogleMap myMap;

    SupportMapFragment fragmentMap;
    FusedLocationProviderClient locationProviderClient;




        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            //checkPermission();
            //getCurrentLocation();

            //imapSearch = findViewById(R.id.mapSearch);

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            fragmentMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
            locationProviderClient = (FusedLocationProviderClient) LocationServices.getFusedLocationProviderClient(this);

            Dexter.withContext(getApplicationContext()).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
                @Override
                public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                    //isPersmissionGranter = true;
                    //Toast.makeText(MainActivity.this, "Permission Granter", Toast.LENGTH_SHORT).show();
                    getCurrentLocation();
                }

                @Override
                public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                    //Intent intent = new Intent();
                    //intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    //Uri uri = Uri.fromParts("package", getPackageName(), "");
                    //intent.setData(uri);
                    //startActivity(intent);
                }

                @Override
                public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();
                }
            }).check();

            //search
            mapSearchView = findViewById(R.id.mapSearch);

            mapSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    String location = mapSearchView.getQuery().toString();
                    List<Address> addressList = null;

                    if (location != null || location.equals("")){
                        Geocoder geocoder = new Geocoder((MainActivity.this));

                        try {
                            addressList = geocoder.getFromLocationName(location, 1);
                        }catch (IOException e){
                            e.printStackTrace();
                        }

                        Address address = addressList.get(0);
                        LatLng ling = new LatLng(address.getLatitude(),address.getLongitude());
                        MarkerOptions markerOptions = new MarkerOptions().position(ling).title("Current Location");
                        myMap.addMarker(new MarkerOptions().position(ling).title(location));
                        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ling,15));

                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    return false;
                }
            });
            fragmentMap.getMapAsync(this);

        }


   
    public void onMapReady(GoogleMap googleMap){
        myMap = googleMap;
    }


    public void checkPermission() {

    }

    //{
      //      @Override
      //      public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
      //          isPersmissionGranter = true;
      //          Toast.makeText(MainActivity.this, "Permission Granter", Toast.LENGTH_SHORT).show();
      //      }

      //      @Override
      //      public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
      //          Intent intent = new Intent();
      //          intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
      //          Uri uri = Uri.fromParts("package", getPackageName(), "");
      //          intent.setData(uri);
      //          startActivity(intent);
      //      }

      //      @Override
      //      public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
      //          permissionToken.continuePermissionRequest();
      //      }
      //  }).check();
   // }

    public void getCurrentLocation() {
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
        Task<Location> task = locationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                fragmentMap.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(@NonNull GoogleMap googleMap) {
                        if(location != null){
                            LatLng lng = new LatLng(location.getLatitude(),location.getLongitude());
                            MarkerOptions markerOptions = new MarkerOptions().position(lng).title("Current Location");
                            googleMap.addMarker(markerOptions);
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lng,15));
                        }else {
                            Toast.makeText(MainActivity.this, "please on your location",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }


}