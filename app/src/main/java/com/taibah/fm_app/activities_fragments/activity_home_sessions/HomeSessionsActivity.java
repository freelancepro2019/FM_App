package com.taibah.fm_app.activities_fragments.activity_home_sessions;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.taibah.fm_app.R;
import com.taibah.fm_app.activities_fragments.FragmentMapTouchListener;
import com.taibah.fm_app.adapters.ServiceAdapter;
import com.taibah.fm_app.databinding.ActivityHomeSessionsBinding;
import com.taibah.fm_app.databinding.DialogAlertBinding;
import com.taibah.fm_app.interfaces.Listeners;
import com.taibah.fm_app.language.LanguageHelper;
import com.taibah.fm_app.models.HomeSessionModel;
import com.taibah.fm_app.models.PlaceGeocodeData;
import com.taibah.fm_app.models.PlaceMapDetailsData;
import com.taibah.fm_app.models.SessionModel;
import com.taibah.fm_app.models.UserModel;
import com.taibah.fm_app.preferences.Preferences;
import com.taibah.fm_app.remote.Api;
import com.taibah.fm_app.share.Common;
import com.taibah.fm_app.tags.Tags;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeSessionsActivity extends AppCompatActivity implements Listeners.BackListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, Listeners.HomeSessionListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private ActivityHomeSessionsBinding binding;
    private String lang;
    private List<HomeSessionModel.Service> serviceList;
    private DatePickerDialog datePickerDialog;
    private ServiceAdapter adapter;
    private TimePickerDialog timePickerDialog;
    private GoogleMap mMap;
    private FragmentMapTouchListener fragment;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private final String fineLocPerm = Manifest.permission.ACCESS_FINE_LOCATION;
    private final int loc_req = 1225;
    private double lat, lng;
    private Marker marker;
    private final float zoom = 15.6f;
    private HomeSessionModel homeSessionModel;
    private Preferences preferences;
    private UserModel userModel;
    private DatabaseReference dRef;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home_sessions);
        initView();

    }

    private void initView() {
        dRef = FirebaseDatabase.getInstance().getReference(Tags.DATABASE_NAME).child(Tags.TABLE_SESSIONS);
        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(this);
        homeSessionModel = new HomeSessionModel();
        serviceList = new ArrayList<>();
        Paper.init(this);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        binding.setBackListener(this);
        binding.setModel(homeSessionModel);
        binding.setHomeSessionListener(this);
        binding.scrollView.getParent().requestChildFocus(binding.scrollView, binding.scrollView);
        adapter = new ServiceAdapter(this, serviceList);
        binding.spinner.setAdapter(adapter);
        CreateDatePickerDialog();
        createTimePickerDialog();
        initMap();
        addService();

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i == 0) {
                    binding.tvCost.setText("0.0");
                    homeSessionModel.setService("");
                    homeSessionModel.setService_name("");
                    binding.setModel(homeSessionModel);

                } else {
                    HomeSessionModel.Service service = serviceList.get(i);
                    homeSessionModel.setService(String.valueOf(service.getId()));
                    homeSessionModel.setCost(service.getCost());
                    homeSessionModel.setService_name(service.getServiceName());

                    binding.tvCost.setText(service.getCost());

                    binding.setModel(homeSessionModel);


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        binding.tvChangeTime.setOnClickListener(view -> {
            try {
                timePickerDialog.show(getFragmentManager(), "");

            } catch (Exception e) {
            }
        });

        binding.imageSearch.setOnClickListener(view -> {
            String address = binding.edtAddress.getText().toString().trim();
            if (!address.isEmpty()) {
                Common.CloseKeyBoard(this, binding.edtAddress);
                binding.edtAddress.setError(null);
                Search(address);
            } else {
                binding.edtAddress.setError(getString(R.string.field_req));

            }
        });

        binding.tvChange.setOnClickListener(view -> {
            try {
                datePickerDialog.show(getFragmentManager(), "");
            } catch (Exception e) {
            }
        });


    }

    private void addService() {

        HomeSessionModel.Service service1 = new HomeSessionModel.Service(0, getString(R.string.ch_ser), "0");
        HomeSessionModel.Service service2 = new HomeSessionModel.Service(1, "Service1", "100");
        HomeSessionModel.Service service3 = new HomeSessionModel.Service(2, "Service2", "200");
        HomeSessionModel.Service service4 = new HomeSessionModel.Service(3, "Service3", "300");
        HomeSessionModel.Service service5 = new HomeSessionModel.Service(4, "Service4", "400");
        HomeSessionModel.Service service6 = new HomeSessionModel.Service(5, "Service5", "500");

        serviceList.add(service1);
        serviceList.add(service2);
        serviceList.add(service3);
        serviceList.add(service4);
        serviceList.add(service5);
        serviceList.add(service6);

        adapter.notifyDataSetChanged();

    }

    private void CreateDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.dismissOnPause(true);
        datePickerDialog.setAccentColor(ActivityCompat.getColor(this, R.color.colorPrimary));
        datePickerDialog.setCancelColor(ActivityCompat.getColor(this, R.color.gray4));
        datePickerDialog.setOkColor(ActivityCompat.getColor(this, R.color.colorPrimary));
        datePickerDialog.setOkText(getString(R.string.ch));
        datePickerDialog.setCancelText(getString(R.string.cancel));
        datePickerDialog.setLocale(new Locale(lang));
        datePickerDialog.setVersion(DatePickerDialog.Version.VERSION_2);
        datePickerDialog.setMinDate(calendar);

    }

    private void createTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        timePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
        timePickerDialog.dismissOnPause(true);
        timePickerDialog.setAccentColor(ActivityCompat.getColor(this, R.color.colorPrimary));
        timePickerDialog.setCancelColor(ActivityCompat.getColor(this, R.color.gray4));
        timePickerDialog.setOkColor(ActivityCompat.getColor(this, R.color.colorPrimary));
        timePickerDialog.setOkText(getString(R.string.select));
        timePickerDialog.setCancelText(getString(R.string.cancel));
        timePickerDialog.setLocale(new Locale(lang));
        timePickerDialog.setVersion(TimePickerDialog.Version.VERSION_2);


    }

    @Override
    public void checkHomeSessionData(HomeSessionModel homeSessionModel)
    {
        if (homeSessionModel.isDataValid(this)) {
            if (userModel != null) {
                send(homeSessionModel);

            } else {
                Common.CreateDialogAlert(this, getString(R.string.please_sign_in_or_sign_up));
            }
        }
    }

    private void send(HomeSessionModel model)
    {

        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.show();
        String id = dRef.child(userModel.getId()).push().getKey();
        String create_at = new SimpleDateFormat("dd/MMM/yyyy hh:mm aa",Locale.ENGLISH).format(new Date(Calendar.getInstance().getTimeInMillis()));
        SessionModel sessionModel = new SessionModel(id,userModel.getName(), userModel.getPhone(), model.getAddress(), model.getLat(), model.getLng(), model.getService(), model.getCost(),model.getService_name(), model.getDate(), model.getTime(), model.getAge(), model.getDetails(), false,create_at);


        dRef.child(userModel.getId()).child(id)
                .setValue(sessionModel)
                .addOnCompleteListener(task -> {
                    dialog.dismiss();
                    if (task.isSuccessful()) {
                        createDialogAlert();
                    }
                }).addOnFailureListener(e -> {
            dialog.dismiss();
            if (e.getMessage() != null) {
                Toast.makeText(this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initMap()
    {

        fragment = (FragmentMapTouchListener) getSupportFragmentManager().findFragmentById(R.id.map);
        if (fragment != null) {
            fragment.getMapAsync(this);

        }

    }

    private void createDialogAlert()
    {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .create();

        DialogAlertBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_alert, null, false);

        binding.tvMsg.setText(getString(R.string.suc));
        binding.btnCancel.setOnClickListener(v -> {

                    dialog.dismiss();
                    finish();
                }

        );
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_window_bg);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setView(binding.getRoot());
        dialog.show();
    }

    private void CheckPermission() {
        if (ActivityCompat.checkSelfPermission(this, fineLocPerm) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{fineLocPerm}, loc_req);
        } else {

            initGoogleApi();
        }
    }

    private void initGoogleApi() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap != null) {
            mMap = googleMap;
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.maps));
            mMap.setTrafficEnabled(false);
            mMap.setBuildingsEnabled(false);
            mMap.setIndoorEnabled(true);
            CheckPermission();

            mMap.setOnMapClickListener(latLng -> {
                if (marker != null) {
                    marker.setPosition(latLng);
                    lat = latLng.latitude;
                    lng = latLng.longitude;
                    homeSessionModel.setLat(lat);
                    homeSessionModel.setLng(lng);
                    binding.setModel(homeSessionModel);
                    getGeoData(lat, lng);
                }


            });

            fragment.setListener(() -> binding.scrollView.requestDisallowInterceptTouchEvent(true));

        }
    }

    private void getGeoData(double lat, double lng) {


        String location = lat + "," + lng;
        Api.getService("https://maps.googleapis.com/maps/api/")
                .getGeoData(location, lang, getString(R.string.map_api_key))
                .enqueue(new Callback<PlaceGeocodeData>() {
                    @Override
                    public void onResponse(Call<PlaceGeocodeData> call, Response<PlaceGeocodeData> response) {
                        if (response.isSuccessful() && response.body() != null) {


                            if (response.body().getResults().size() > 0) {
                                String address = response.body().getResults().get(0).getFormatted_address().replace("Unnamed Road,", "");

                                binding.edtAddress.setText(address);


                            }
                        } else {

                            try {
                                Log.e("error_code", response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }


                    }

                    @Override
                    public void onFailure(Call<PlaceGeocodeData> call, Throwable t) {
                        try {


                            // Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {

                        }
                    }
                });
    }

    private void Search(String query) {

        String fields = "id,place_id,name,geometry,formatted_address";
        Api.getService("https://maps.googleapis.com/maps/api/")
                .searchOnMap("textquery", query, fields, lang, getString(R.string.map_api_key))
                .enqueue(new Callback<PlaceMapDetailsData>() {
                    @Override
                    public void onResponse(Call<PlaceMapDetailsData> call, Response<PlaceMapDetailsData> response) {
                        if (response.isSuccessful() && response.body() != null) {


                            if (response.body().getCandidates().size() > 0) {
                                String address = response.body().getCandidates().get(0).getFormatted_address().replace("Unnamed Road,", "");
                                lat = response.body().getCandidates().get(0).getGeometry().getLocation().getLat();
                                lng = response.body().getCandidates().get(0).getGeometry().getLocation().getLng();
                                binding.edtAddress.setText(address);
                                homeSessionModel.setAddress(address);

                                binding.setModel(homeSessionModel);
                                AddMarker(response.body().getCandidates().get(0).getGeometry().getLocation().getLat(), response.body().getCandidates().get(0).getGeometry().getLocation().getLng());
                            }
                        } else {


                            try {
                                Log.e("error_code", response.code() + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }


                    }

                    @Override
                    public void onFailure(Call<PlaceMapDetailsData> call, Throwable t) {
                        try {


                            Log.e("Error", t.getMessage());
                        } catch (Exception e) {

                        }
                    }
                });
    }

    private void AddMarker(double lat, double lng) {

        this.lat = lat;
        this.lng = lng;


        if (marker == null) {
            marker = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), zoom));

        } else {
            marker.setPosition(new LatLng(lat, lng));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), zoom));


        }


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        initLocationRequest();
    }

    private void initLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setFastestInterval(1000);
        locationRequest.setInterval(60000);
        LocationSettingsRequest.Builder request = new LocationSettingsRequest.Builder();
        request.addLocationRequest(locationRequest);
        request.setAlwaysShow(false);


        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, request.build());
        result.setResultCallback(locationSettingsResult -> {
            Status status = locationSettingsResult.getStatus();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    startLocationUpdate();
                    break;

                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        status.startResolutionForResult(HomeSessionsActivity.this, 100);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                    break;

            }
        });

    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdate() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                onLocationChanged(locationResult.getLastLocation());
            }
        };
        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();
        homeSessionModel.setLat(lat);
        homeSessionModel.setLng(lng);
        binding.setModel(homeSessionModel);
        AddMarker(lat, lng);
        getGeoData(location.getLatitude(), location.getLongitude());

        if (googleApiClient != null) {
            LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
            googleApiClient.disconnect();
            googleApiClient = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == loc_req) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initGoogleApi();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            startLocationUpdate();
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH);
        String date = dateFormat.format(new Date(calendar.getTimeInMillis()));

        binding.tvDate.setText(date);
        homeSessionModel.setDate(date);
        binding.setModel(homeSessionModel);

    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);

        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
        String t = dateFormat.format(new Date(calendar.getTimeInMillis()));
        binding.tvTime.setText(t);

        homeSessionModel.setTime(t);
        binding.setModel(homeSessionModel);
    }

    @Override
    public void back() {
        finish();
    }


}
