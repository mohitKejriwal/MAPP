package com.india.android.mapp;

/**
 * Created by admin on 26-08-2018.
 */

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.MyViewHolder> implements Filterable {

    private ArrayList<LocationData> locList,filterList;
    private LocationClick listener;

    public LocationsAdapter(ArrayList<LocationData> locList,LocationClick listener) {
        this.locList = locList;
        filterList = locList;
        this.listener=listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.loc_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        LocationData data = filterList.get(position);
        holder.minutes.setText("Minutes- "+data.getMinutes());
        holder.address.setText("Address- "+data.getAddress());
        holder.date.setText("Date- "+data.getDate());
    }

    @Override
    public int getItemCount() {
        return filterList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence seq) {
                String charString = seq.toString();
                if (charString.isEmpty()) {
                    filterList=locList;
                }else{
                    ArrayList<LocationData> tempList=new ArrayList<>();
                    for (LocationData data:locList) {
                        if(data.getDate().equalsIgnoreCase(charString)){
                            tempList.add(data);
                        }
                    }
                    filterList=tempList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filterList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filterList = (ArrayList<LocationData>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView address, minutes, date;

        public MyViewHolder(View view) {
            super(view);
            address = view.findViewById(R.id.tvAddress);
            minutes =view.findViewById(R.id.tvMinutes);
            date =  view.findViewById(R.id.tvDate);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onLocationClick(filterList.get(getAdapterPosition()).getLat(),filterList.get(getAdapterPosition()).getLon());
                }
            });
        }
    }

}
