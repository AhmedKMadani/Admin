package com.nymeria.admin.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nymeria.admin.R;
import com.nymeria.admin.activity.AddBookingActivity;
import com.nymeria.admin.activity.AddLocationActivity;
import com.nymeria.admin.activity.AddingActivity;
import com.nymeria.admin.activity.EditActivity;
import com.nymeria.admin.activity.GetBookingActivity;
import com.nymeria.admin.activity.TrackBookingActivity;
import com.nymeria.admin.model.service;

import java.util.List;

/**
 * Created by AhmedKamal on 8/23/2017.
 */
public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.MyViewHolder> {

    private Context mContext;
    private List<service> serviceList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count;
        public ImageView thumbnail, overflow;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            int position = getAdapterPosition();
        }
    }


    public ServiceAdapter(Context mContext, List<service> serviceList) {
        this.mContext = mContext;
        this.serviceList = serviceList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate( R.layout.service_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final service service = serviceList.get(position);
        holder.title.setText(service.getName());
        // loading album cover using Glide library
        Glide.with(mContext).load(service.getThumbnail()).into(holder.thumbnail);


        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(view.getContext(), String.valueOf(position),Toast.LENGTH_SHORT).show();

                 switch (position) {

                     case 0:

                         Intent A = new Intent(view.getContext(), AddingActivity.class);
                         view.getContext().startActivity(A);

                         break;


                     case 1:

                         Intent E = new Intent(view.getContext(), EditActivity.class);
                         view.getContext().startActivity(E);


                         break;


                     case 2:
                         Intent G = new Intent(view.getContext(), GetBookingActivity.class);
                         view.getContext().startActivity(G);

                         break;

                     case 3:
                         Intent B = new Intent(view.getContext(), AddBookingActivity.class);
                         view.getContext().startActivity(B);

                         break;


                     case 4:

                         Intent L = new Intent(view.getContext(), AddLocationActivity.class);
                         view.getContext().startActivity(L);

                         break;


                     case 5:

                         Intent T = new Intent(view.getContext(), TrackBookingActivity.class);
                         view.getContext().startActivity(T);

                         break;


                     case 6:

                         break;


                     case 7:

                         break;
                 }
            }
        });




    }
    @Override
    public int getItemCount () {
        return serviceList.size();
    }
}
