package com.nymeria.admin.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.nymeria.admin.R;
import com.nymeria.admin.model.ServiceUser;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2/13/2018.
 */

public class ServiceUserAdapter extends RecyclerView.Adapter<ServiceUserAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<ServiceUser> serviceuserList;
    private List<ServiceUser> serviceuserListFiltered;
    private ServiceUserAdapterListener listener;




    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView service, price,descp;


        public MyViewHolder(View view) {
            super(view);
            service = (TextView) view.findViewById( R.id.service);
            price = (TextView) view.findViewById(R.id.price);



            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                   // listener.onServiceHallSelected(servicehallsListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }



    public ServiceUserAdapter(Context context, List<ServiceUser> serviceuserList) {
        this.context = context;
        this.listener = listener;
        this.serviceuserList = serviceuserList;
        this.serviceuserListFiltered = serviceuserList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_services, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        /**
         final ServiceHalls servicehalls = servicehallsListFiltered.get(position);
         holder.service.setText(servicehalls.getTitle_ar ());
         holder.price.setText( servicehalls.getPrice()+ " " + context.getResources ().getString ( R.string.currency ));
         holder.descp.setText(servicehalls.getDetails_ar (  ));
         **/
        final int pos = position;

        holder.service.setText(serviceuserListFiltered.get(position).getTitle_ar ());

        holder.price.setText(serviceuserListFiltered.get(position).getPrice()+ " " + context.getResources ().getString ( R.string.currency ));

    }


    @Override
    public int getItemCount() {
        return serviceuserListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    serviceuserListFiltered = serviceuserList;
                } else {
                    List<ServiceUser> filteredList = new ArrayList<> ();
                    for (ServiceUser row : serviceuserList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getTitle_ar ().toLowerCase().contains(charString.toLowerCase())  ) {
                            filteredList.add(row);
                        }
                    }

                    serviceuserListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = serviceuserListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                serviceuserListFiltered = (ArrayList<ServiceUser>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ServiceUserAdapterListener {
        void onServiceUserSelected(ServiceUser serviceUser);
    }

    public List<ServiceUser> getServiceuserlist() {
        return serviceuserListFiltered;
    }
}

