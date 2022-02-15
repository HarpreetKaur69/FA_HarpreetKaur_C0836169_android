package com.example.fa_harpreetkaur_c0836169_android.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.View;
import android.widget.Toast;

import com.example.fa_harpreetkaur_c0836169_android.R;
import com.example.fa_harpreetkaur_c0836169_android.adapter.FavPlaceAdapter;
import com.example.fa_harpreetkaur_c0836169_android.databinding.ActivityMainBinding;
import com.example.fa_harpreetkaur_c0836169_android.extras.GpsTracker;
import com.example.fa_harpreetkaur_c0836169_android.roomdb.DatabaseClient;
import com.example.fa_harpreetkaur_c0836169_android.roomdb.FavPlace;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    List<FavPlace> notVisitedPlaces = new ArrayList<>();
    List<FavPlace> visitedPlaces = new ArrayList<>();
    List<String> titles = new ArrayList<String>();
    private double latitude, longitude;
    FavPlaceAdapter adapter = new FavPlaceAdapter();
    String currentLocName;
    List<FavPlace> favPlaceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setTitle("Favorite Places");
        titles.add("Visited");
        titles.add("Not Visited");
        binding.rvPLacesList.setAdapter(adapter);
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    new Handler().postDelayed(() -> {
                        searchPlaces(query);
                    }, 3000);
                } else {
                    getAllPlacesList();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.isEmpty()) {
                    new Handler().postDelayed(() -> {
                        // productList.clear();
                        searchPlaces(newText);
                    }, 3000);
                } else {

                    getAllPlacesList();
                }
                return false;
            }
        });
        getAllPlacesList();
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            } else getCurrentLocation();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        binding.viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), 0, titles));
//        binding.tabLayout.setupWithViewPager(binding.viewPager);
        binding.btnFab.setOnClickListener(v -> {
            startActivity(new Intent(this, AddFavPlaceActivity.class)
                    .putExtra("LAT", latitude)
                    .putExtra("LNG", longitude)
                    .putExtra("NAME", currentLocName)
            );
        });
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                // this method is called
                // when the item is moved.
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {


                // below line is to get the position
                // of the item at that position.

                int position = viewHolder.getAdapterPosition();
                AlertDialog.Builder buider = new AlertDialog.Builder(MainActivity.this);
                buider.setMessage("Please specify your action to perform on this item");
                buider.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deletePlace(favPlaceList.get(position));
                    }
                });
                buider.setNegativeButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        startActivity(new Intent(MainActivity.this, AddFavPlaceActivity.class).putExtra("DATA", favPlaceList.get(position)));
                    }
                });
                AlertDialog dialog = buider.create();
                dialog.show();

            }
            // at last we are adding this
            // to our recycler view.
        }).attachToRecyclerView(binding.rvPLacesList);
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        List<String> titles;

        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior, List<String> titles) {
            super(fm, behavior);
            this.titles = titles;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            if (position == 1)
                return FavPlaceFragment.newInstance((ArrayList<FavPlace>) notVisitedPlaces);
            else
                return FavPlaceFragment.newInstance((ArrayList<FavPlace>) visitedPlaces);
        }

        @Override
        public int getCount() {
            return titles.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    public void getAllPlacesList() {
        GetPlaces places = new GetPlaces();
        places.execute();
    }

    private void searchPlaces(String query) {
        if (query.isEmpty())
            getAllPlacesList();
        else {
            SearchPlaces gt = new SearchPlaces(query);
            gt.execute();
        }


    }

    public void getCurrentLocation() {
        GpsTracker gpsTracker = new GpsTracker(MainActivity.this);
        if (gpsTracker.canGetLocation()) {
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses != null && addresses.size() > 0) {
                    String cityName = addresses.get(0).getAddressLine(0);
                    currentLocName = cityName;
                    binding.tvMyLocation.setText("My Location = " + cityName);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        }
    }

    class GetPlaces extends AsyncTask<Void, Void, List<FavPlace>> {

        @Override
        protected List<FavPlace> doInBackground(Void... voids) {
            List<FavPlace> productList = DatabaseClient
                    .getInstance(MainActivity.this)
                    .getAppDatabase()
                    .placesDao()
                    .getAll();
            return productList;
        }

        @Override
        protected void onPostExecute(List<FavPlace> places) {
            super.onPostExecute(places);

            for (FavPlace place : places) {
                if (place.getIsVisited() == 1) {
                    visitedPlaces.add(place);
                } else notVisitedPlaces.add(place);
            }
//            binding.viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), 0, titles));
//            binding.tabLayout.setupWithViewPager(binding.viewPager);
            favPlaceList = places;
            adapter.updateList(places);
//            TasksAdapter adapter = new TasksAdapter(FirstActivity.this, tasks);
//            recyclerView.setAdapter(adapter);
        }
    }
    private void deletePlace(FavPlace place) {
        class DeleteTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(MainActivity.this).getAppDatabase()
                        .placesDao()
                        .delete(place);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(MainActivity.this, "Deleted", Toast.LENGTH_LONG).show();
                new MainActivity().getAllPlacesList();
            }
        }

        DeleteTask dt = new DeleteTask();
        dt.execute();
    }

    class SearchPlaces extends AsyncTask<Void, Void, List<FavPlace>> {

        private final String query;

        public SearchPlaces(String q) {

            this.query = q;
        }

        @Override
        protected List<FavPlace> doInBackground(Void... voids) {

            List<FavPlace> favPlaceList = DatabaseClient
                    .getInstance(MainActivity.this)
                    .getAppDatabase()
                    .placesDao()
                    .search(query);
            return favPlaceList;
        }

        @Override
        protected void onPostExecute(List<FavPlace> places) {
            super.onPostExecute(places);

            for (FavPlace place : places) {
                if (place.getIsVisited() == 1) {
                    visitedPlaces.add(place);
                } else notVisitedPlaces.add(place);
            }
            binding.viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), 0, titles));
            binding.tabLayout.setupWithViewPager(binding.viewPager);

        }
    }
}