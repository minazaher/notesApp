package com.example.notesapp.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notesapp.Fragments.MapFragment;
import com.example.notesapp.R;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GetLocationActivity extends AppCompatActivity implements MapFragment.OnLocationChangedListener{
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    public static LatLng locationLatlng ;
    TextView tv_location ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_location);
        Fragment fragment = new MapFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_map, fragment).commit();
        locationLatlng = getUserLocation();
        tv_location = findViewById(R.id.tv_selectedLocation);
        tv_location.setText(getAddress(getUserLocation()));


        findViewById(R.id.btn_saveLocation).setOnClickListener(view -> {
            CreateNoteActivity.selectedLocation = tv_location.getText().toString();
            onBackPressed();
        });

        findViewById(R.id.btn_cancelLocation).setOnClickListener(view -> onBackPressed());
    }

    public LatLng getUserLocation(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider = "gps";

        if (provider != null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions
                        (this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                                MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                Location location = locationManager.getLastKnownLocation(locationManager.getAllProviders().get(2));
                if (location != null){
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    return latLng;
                }
                else
                    Toast.makeText(this, "Location is not enabled", Toast.LENGTH_SHORT).show();
            }

        }
        return new LatLng(0,0);
    }

    private String getAddress(LatLng latLng){
        String address = "";
        Geocoder geocoder = new Geocoder(this,
                Locale.getDefault());
        try {
            List<Address> addresses =
                    geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() > 0) {
                String city = addresses.get(0).getLocality();
                String country = addresses.get(0).getCountryName();
                String govern = addresses.get(0).getAdminArea();
                String formattedLocation = String.format("%s, %s", city, govern);
                System.out.println(formattedLocation);
                address = formattedLocation;
            }
        } catch (IOException e) {
            address = "Unable to detect your location!";
        }
        return address;
    }

    @Override
    public void onLocationChanged(LatLng latLng) {
        tv_location.setText(getAddress(latLng));
    }
}