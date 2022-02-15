package com.example.fa_harpreetkaur_c0836169_android.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.fa_harpreetkaur_c0836169_android.R;
import com.example.fa_harpreetkaur_c0836169_android.databinding.ActivityAddFavPlaceBinding;
import com.example.fa_harpreetkaur_c0836169_android.roomdb.DatabaseClient;
import com.example.fa_harpreetkaur_c0836169_android.roomdb.FavPlace;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddFavPlaceActivity extends AppCompatActivity implements OnMapReadyCallback {
    ActivityAddFavPlaceBinding binding;
    private GoogleMap map;
    Geocoder gc;
    Marker marker;
    Boolean hasFocus = false;
    String latitude, longitude;
    Handler searchHandler = new Handler();
    Runnable runnable;
    Boolean isFromUpdate = false;
    FavPlace place = null;
    String mylatitude="56.1304", mylongitude="106.3468";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddFavPlaceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Add your favorite place");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        gc = new Geocoder(AddFavPlaceActivity.this);
        mapFragment.getMapAsync(this);


        binding.edtName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                hasFocus = b;
            }
        });
        binding.edtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() >= 3) {
                    if (hasFocus) {
                        runnable = new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    List<Address> addresses = gc.getFromLocationName(charSequence.toString(), 2);
                                    if (addresses != null && addresses.size() > 0) {
                                        Log.e("TAG", "onTextChanged: " + addresses.get(0).getCountryName());
                                        String placeName = addresses.get(0).getAddressLine(0);
                                        binding.edtName.setText(placeName);
                                        Double lat = addresses.get(0).getLatitude();
                                        Double lng = addresses.get(0).getLongitude();
                                        latitude = lat.toString();
                                        longitude = lng.toString();
                                        setUpMap(lat, lng, placeName);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        searchHandler.postDelayed(runnable, 3000);

                    }


                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        searchHandler.removeCallbacks(runnable);
                    }
                }, 4000);

            }
        });
        binding.btnSave.setOnClickListener(v -> {
            savePlace();
        });
    }

    private void setUpMap(Double lat, Double lng, String placeName) {
        LatLng ltlng = new LatLng(lat, lng);
        if (marker != null)
            marker.setVisible(false);
        marker = map.addMarker(new MarkerOptions()
                .position(ltlng)
                .draggable(true)
                .title(placeName));
        map.moveCamera(CameraUpdateFactory.newLatLng(ltlng));

      //  Double dis= distance(Double.valueOf(latitude), Double.valueOf(longitude), Double.valueOf(mylatitude), Double.valueOf(mylongitude));
        binding.tvDistance.setVisibility(View.GONE);
        binding.btnSave.setEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menuHybrid:
                map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case R.id.menuSatellite:
                map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.menuTerrain:
                map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;

        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.maps, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        if (getIntent() != null) {
            place = (FavPlace) getIntent().getSerializableExtra("DATA");
            mylatitude = getIntent().getStringExtra("LAT");
            mylongitude = getIntent().getStringExtra("LNG");
            String placeName = getIntent().getStringExtra("NAME");
            if (mylatitude != null && mylongitude != null && placeName != null && place == null) {
                setUpMap(Double.valueOf(latitude), Double.valueOf(longitude), placeName);
            }
            if (place != null) {
                isFromUpdate = true;
                setUpMap(Double.valueOf(place.getLatitude()), Double.valueOf(place.getLongitude()), place.getName());
                binding.cbVisited.setChecked(place.getIsVisited() == 1);
                binding.edtName.setText(place.getName());
                getSupportActionBar().setTitle("Update your favorite place");
                binding.btnSave.setText("Update");
                binding.btnSave.setEnabled(true);
            }
        }
        double lat = 56.1304;
        double lng = 106.3468;
        //   if (place != null) {
        LatLng canada = new LatLng(lat, lng);
        marker = map.addMarker(new MarkerOptions()
                .position(canada)
                .draggable(true)
                .title("Canada"));

        map.moveCamera(CameraUpdateFactory.newLatLng(canada));


        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                double lat = marker.getPosition().latitude;
                double lng = marker.getPosition().longitude;
                latitude = String.valueOf(lat);
                longitude = String.valueOf(lng);
                map.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                Geocoder geocoder = new Geocoder(AddFavPlaceActivity.this, Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation(lat, lng, 1);
                    if (addresses != null && addresses.size() > 0) {
                        String cityName = addresses.get(0).getAddressLine(0);
                        setUpMap(lat, lng, cityName);
                        binding.edtName.setText(cityName);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
    }


    void savePlace() {

        Date currentDate = Calendar.getInstance().getTime();
        final String name = binding.edtName.getText().toString().trim();
        String date = new SimpleDateFormat("dd-MM-yy").format(Calendar.getInstance().getTime());
        // final String date = currentDate.getDay() + "-" + (currentDate.getMonth() + 1) + "-" + currentDate.getYear();
        final int isVisted = binding.cbVisited.isChecked() ? 1 : 0;
        final String lat = latitude;
        final String lng = longitude;

        if (name.isEmpty()) {
            binding.edtName.setError("Place name required");
            binding.edtName.requestFocus();
            return;
        }


        class SaveTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                if (isFromUpdate && place != null) {
                    FavPlace splace = new FavPlace(name, date, isVisted, latitude, longitude);
                    splace.setIsVisited(binding.cbVisited.isChecked() ? 1 : 0);
                    splace.setDate(place.getDate());
                    splace.setId(place.getId());
                    //adding to database
                    DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                            .placesDao()
                            .update(splace);

                } else {
                    FavPlace place = new FavPlace(name, date, isVisted, latitude, longitude);
                    //adding to database
                    DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                            .placesDao()
                            .insert(place);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                Toast.makeText(getApplicationContext(), "Favorite Place Added", Toast.LENGTH_LONG).show();
            }
        }

        SaveTask st = new SaveTask();
        st.execute();
    }
}
