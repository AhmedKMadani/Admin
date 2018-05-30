package com.nymeria.admin.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nymeria.admin.R;
import com.nymeria.admin.activity.ViewServicesActivity;
import com.nymeria.admin.model.GetService;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2/13/2018.
 */

public class GetServiceAdapter extends RecyclerView.Adapter<GetServiceAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<GetService> getserviceList;
    private List<GetService> getserviceListFiltered;
    private GetServiceAdapterListener listener;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView service, price;
        public RelativeLayout relativeLayout;

        public MyViewHolder(View view) {
            super(view);
            service = (TextView) view.findViewById( R.id.service);
            price = (TextView) view.findViewById(R.id.price);
            relativeLayout = (RelativeLayout) view.findViewById ( R.id.view_foreground );

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }



    public GetServiceAdapter(Context context, List<GetService> getserviceList) {
        this.context = context;
        this.listener = listener;
        this.getserviceList = getserviceList;
        this.getserviceListFiltered = getserviceList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_services, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        final int pos = position;

        holder.service.setText(getserviceListFiltered.get(position).getTitle_ar ());
        holder.price.setText(getserviceListFiltered.get(position).getPrice()+ " " + context.getResources ().getString ( R.string.currency ));

        holder.relativeLayout.setOnClickListener ( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(view.getContext (),ViewServicesActivity.class);

                i.putExtra("serv_title_ar", getserviceListFiltered.get(position).getTitle_ar ());
                i.putExtra("serv_title_en",getserviceListFiltered.get(position).getTitle_en ());
                i.putExtra("serv_deatils_ar", getserviceListFiltered.get(position).getDetails_ar ());
                i.putExtra("serv_deatils_en", getserviceListFiltered.get(position).getDetails_en ());
                i.putExtra("serv_price_",getserviceListFiltered.get(position).getPrice ());
                i.putExtra("serv_id_",getserviceListFiltered.get(position).getId ());

                view.getContext ().startActivity(i);

            }
            });

    }


    @Override
    public int getItemCount() {
        return getserviceListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    getserviceListFiltered = getserviceList;
                } else {
                    List<GetService> filteredList = new ArrayList<> ();
                    for (GetService row : getserviceList) {

                        if (row.getTitle_ar ().toLowerCase().contains(charString.toLowerCase())  ) {
                            filteredList.add(row);
                        }
                    }

                    getserviceListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = getserviceListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                getserviceListFiltered = (ArrayList<GetService>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface GetServiceAdapterListener {
        void onServiceSelected(GetService getService);
    }

    public List<GetService> getServicelist() {
        return getserviceListFiltered;
    }
}

