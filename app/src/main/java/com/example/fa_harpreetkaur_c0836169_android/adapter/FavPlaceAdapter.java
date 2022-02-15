package com.example.fa_harpreetkaur_c0836169_android.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fa_harpreetkaur_c0836169_android.databinding.ItemPlacesListBinding;
import com.example.fa_harpreetkaur_c0836169_android.roomdb.FavPlace;
import com.example.fa_harpreetkaur_c0836169_android.ui.AddFavPlaceActivity;

import java.util.ArrayList;
import java.util.List;

public class FavPlaceAdapter extends RecyclerView.Adapter<FavPlaceAdapter.ViewHolder> {
    List<FavPlace> favPlaceList = new ArrayList<>();
    Context context;

    public FavPlaceAdapter(List<FavPlace> favPlaceList) {
        this.favPlaceList = favPlaceList;
    }

    public FavPlaceAdapter() {
        // this.favPlaceList = favPlaceList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context= parent.getContext();
        return new ViewHolder(ItemPlacesListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.tvPlaceName.setText(favPlaceList.get(position).getName());
        holder.binding.tvDate.setText(favPlaceList.get(position).getDate());
        if (favPlaceList.get(position).getIsVisited() == 1) {
            holder.binding.tvVisited.setVisibility(View.VISIBLE);
            holder.binding.tvNotVisited.setVisibility(View.GONE);
        } else {
            holder.binding.tvVisited.setVisibility(View.GONE);
            holder.binding.tvNotVisited.setVisibility(View.VISIBLE);
        }
        holder.itemView.setOnClickListener(v -> {
            context.startActivity(new Intent(context, AddFavPlaceActivity.class).putExtra("DATA",favPlaceList.get(position)));
        });
    }

    @Override
    public int getItemCount() {
        return favPlaceList.size();
    }

    public void updateList(List<FavPlace> favPlaceList) {
        this.favPlaceList = new ArrayList<>(favPlaceList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemPlacesListBinding binding;

        public ViewHolder(ItemPlacesListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
