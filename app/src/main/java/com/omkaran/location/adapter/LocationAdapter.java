package com.omkaran.location.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.omkaran.location.MapActivity;
import com.omkaran.location.databinding.ItemLocationBinding;
import com.omkaran.location.model.LocationEntity;
import java.util.List;
public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder>
{
    private List<LocationEntity> locationList;
    public LocationAdapter(List<LocationEntity> locationList)
    {
        this.locationList = locationList;
    }
    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType)
    {
        ItemLocationBinding binding =ItemLocationBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new LocationViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder,int position)
    {
        LocationEntity location =locationList.get(position);
        holder.binding.tvLatitude.setText("Latitude: " + location.getLatitude());
        holder.binding.tvLongitude.setText("Longitude: " + location.getLongitude());
        holder.binding.tvAccuracy.setText("Accuracy: " + location.getAccuracy() + " m");
        holder.binding.tvAddress.setText("Address: " + location.getAddress());
        holder.binding.tvDateTime.setText(location.getDateTime());
        holder.itemView.setOnClickListener(v -> {

            Intent intent =new Intent(v.getContext(),MapActivity.class);
            intent.putExtra("latitude",location.getLatitude());
            intent.putExtra("longitude",location.getLongitude());
            intent.putExtra("Address",location.getAddress());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount()
    {
        return locationList.size();
    }
    public void updateList(List<LocationEntity> newList)
    {
        locationList = newList;
        notifyDataSetChanged();
    }
    static class LocationViewHolder extends RecyclerView.ViewHolder
    {
        ItemLocationBinding binding;
        public LocationViewHolder(ItemLocationBinding binding)
        {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}