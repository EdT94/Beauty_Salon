package com.example.zinabeautysalon.CallBacks;

import com.google.android.gms.maps.model.LatLng;

public interface MapCallBack {

    void displayLocationOnMap();

    void moveCamera(LatLng latLng);

    LatLng latLngCreator();

    void clearMap();
}