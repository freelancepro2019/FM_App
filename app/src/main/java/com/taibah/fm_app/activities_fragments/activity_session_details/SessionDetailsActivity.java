package com.taibah.fm_app.activities_fragments.activity_session_details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.taibah.fm_app.R;
import com.taibah.fm_app.activities_fragments.FragmentMapTouchListener;
import com.taibah.fm_app.databinding.ActivitySessionDetailsBinding;
import com.taibah.fm_app.interfaces.Listeners;
import com.taibah.fm_app.language.LanguageHelper;
import com.taibah.fm_app.models.SessionModel;

import io.paperdb.Paper;

public class SessionDetailsActivity extends AppCompatActivity implements Listeners.BackListener, OnMapReadyCallback{
    private ActivitySessionDetailsBinding binding;
    private GoogleMap mMap;
    private FragmentMapTouchListener fragment;
    private SessionModel sessionModel;
    private String lang;



    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_session_details);
        getDataFromIntent();
        initView();
    }

    private void initView() {
        Paper.init(this);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        binding.setBackListener(this);
        initMap();
        binding.setModel(sessionModel);


    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("data")) {
            sessionModel = (SessionModel) intent.getSerializableExtra("data");
        }

    }

    private void initMap() {

        fragment = (FragmentMapTouchListener) getSupportFragmentManager().findFragmentById(R.id.map1);
        if (fragment != null) {
            fragment.getMapAsync(this);

        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap != null) {
            mMap = googleMap;
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.maps));
            mMap.setTrafficEnabled(false);
            mMap.setBuildingsEnabled(false);
            mMap.setIndoorEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(false);
            mMap.getUiSettings().setCompassEnabled(false);
            mMap.getUiSettings().setTiltGesturesEnabled(false);


            fragment.setListener(() -> binding.scrollView.requestDisallowInterceptTouchEvent(true));

            addMarker(sessionModel.getLatitude(),sessionModel.getLongitude(),sessionModel.getAddress());


        }
    }

    private void addMarker(double lat, double lng, String title) {


        mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(title).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))).setAnchor(0.5f, 0.5f);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng),15.6f));

    }

    @Override
    public void back() {
        finish();
    }
}
