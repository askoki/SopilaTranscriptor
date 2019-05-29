package com.example.arcibald160.sopilatranscriptor;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity  implements OnMapReadyCallback {

    double lon, lat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        lon = getIntent().getDoubleExtra("LONGITUDE", 0.0);
        lat = getIntent().getDoubleExtra("LATITUDE", 0.0);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap == null) return;
        LatLng position = new LatLng(lat, lon);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(position);
        markerOptions.title("Sopele played here");
        googleMap.clear();
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 12.0f));
        Marker myM = googleMap.addMarker(markerOptions);
        myM.showInfoWindow();
    }
}
