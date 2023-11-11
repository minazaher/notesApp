package com.example.notesapp.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.notesapp.Activity.GetLocationActivity;
import com.example.notesapp.R;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

public class MapFragment extends Fragment {
    OnLocationChangedListener locationChangedListener;
    FloatingActionButton getMyLocation;
    GoogleMap myGoogleMap;
    public static String NOTE_LOCATION = "";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public interface OnLocationChangedListener {
        public void onLocationChanged(LatLng latLng);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);


        assert supportMapFragment != null;
        getMyLocation = (FloatingActionButton) view.findViewById(R.id.btn_getMyLocation);
        getMyLocation.setOnClickListener(view1 -> {setMarkerOnLocation(myGoogleMap, GetLocationActivity.locationLatlng);});

        supportMapFragment.getMapAsync(googleMap -> {

            googleMap.setOnMapLoadedCallback(() -> {
                myGoogleMap = googleMap;
                setMarkerOnLocation(googleMap,GetLocationActivity.locationLatlng);
            });

            googleMap.setOnMapClickListener(latLng -> {
                setMarkerOnLocation(googleMap,latLng);
                locationChangedListener.onLocationChanged(latLng);});

            googleMap.setIndoorEnabled(true);
       });
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        locationChangedListener = (OnLocationChangedListener) context;
    }

    private void setMarkerOnLocation(GoogleMap googleMap, LatLng latLng){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        markerOptions.title("Selected location!");
        googleMap.clear();
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerOptions.getPosition(), 20));
        googleMap.addMarker(markerOptions);
    }

}