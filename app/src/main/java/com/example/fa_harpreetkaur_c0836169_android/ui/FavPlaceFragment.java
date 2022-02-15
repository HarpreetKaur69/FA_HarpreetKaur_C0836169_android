package com.example.fa_harpreetkaur_c0836169_android.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.fa_harpreetkaur_c0836169_android.R;
import com.example.fa_harpreetkaur_c0836169_android.adapter.FavPlaceAdapter;
import com.example.fa_harpreetkaur_c0836169_android.databinding.FragmentFavPlaceBinding;
import com.example.fa_harpreetkaur_c0836169_android.roomdb.DatabaseClient;
import com.example.fa_harpreetkaur_c0836169_android.roomdb.FavPlace;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class FavPlaceFragment extends Fragment {
    private FragmentFavPlaceBinding binding;
    private ArrayList<FavPlace> favPlaces;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    public FavPlaceFragment() {
        // Required empty public constructor
    }


    public static FavPlaceFragment newInstance(ArrayList<FavPlace> favPlacesList) {
        FavPlaceFragment fragment = new FavPlaceFragment();
        Bundle args = new Bundle();
     //   args.putParcelableArrayList(ARG_PARAM1, favPlacesList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
           // favPlaces = getArguments().getParcelableArrayList(ARG_PARAM1);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFavPlaceBinding.inflate(inflater, container, false);
        FavPlaceAdapter adapter = new FavPlaceAdapter(favPlaces);
        binding.rvPlaces.setAdapter(adapter);
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
                AlertDialog.Builder buider = new AlertDialog.Builder(getContext());
                buider.setMessage("Please specify your action to perform on this item");
                buider.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    deletePlace(favPlaces.get(position));
                    }
                });
                buider.setNegativeButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        startActivity(new Intent(getContext(), AddFavPlaceActivity.class).putExtra("DATA", (Parcelable) favPlaces.get(position)));
                    }
                });
                AlertDialog dialog = buider.create();
                dialog.show();

            }
            // at last we are adding this
            // to our recycler view.
        }).attachToRecyclerView(binding.rvPlaces);
        return binding.getRoot();
    }

    private void deletePlace(FavPlace place) {
        class DeleteTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(getContext()).getAppDatabase()
                        .placesDao()
                        .delete(place);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(getContext(), "Deleted", Toast.LENGTH_LONG).show();
                new MainActivity().getAllPlacesList();
            }
        }

        DeleteTask dt = new DeleteTask();
        dt.execute();
    }


}