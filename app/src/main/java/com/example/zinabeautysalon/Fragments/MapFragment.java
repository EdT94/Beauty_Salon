package com.example.zinabeautysalon.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.zinabeautysalon.CallBacks.MapCallBack;
import com.example.zinabeautysalon.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;




public class MapFragment extends Fragment {
    private SupportMapFragment supportMapFragment;
    private static GoogleMap map;


    @SuppressLint("MissingPermission")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment, container, false);
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);

        supportMapFragment.getMapAsync(googleMap -> {
            map = googleMap;
            map.setMyLocationEnabled(false);
            map.getUiSettings().setZoomControlsEnabled(false);
            map.getUiSettings().setMyLocationButtonEnabled(true);
            mapCallBack.displayLocationOnMap();
        });
        return view;
    }


    public static MapCallBack getMapCallBack() {
        return mapCallBack;
    }

    private static MapCallBack mapCallBack = new MapCallBack() {
        @Override
        public void displayLocationOnMap() {
            moveCamera(latLngCreator());
        }

        @Override
        public void moveCamera(LatLng latLng) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            Marker marker=map.addMarker(new MarkerOptions().position(latLng).title("סלון יופי זינה"));
            marker.showInfoWindow();
        }

        @Override
        public LatLng latLngCreator() {
            return new LatLng(31.945102, 34.892560);
        }

        @Override
        public void clearMap() {
            map.clear();
        }

    };
}





