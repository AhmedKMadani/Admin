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
import com.nymeria.admin.activity.ViewHallActivity;
import com.nymeria.admin.helper.SQLiteHandler;
import com.nymeria.admin.model.GetHalls;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ahmed K Madani on 16/11/17.
 */

public class GetHallsAdapter extends RecyclerView.Adapter<GetHallsAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<GetHalls> getHallsList;
    private List<GetHalls> getHallsListFiltered;
    private GetHallsAdapterListener listener;
    private SQLiteHandler db;
    int CatID;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name,price,size;
        public RelativeLayout relativeLayout;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById( R.id.name);
            price = (TextView) view.findViewById(R.id.price);
            size = (TextView) view.findViewById(R.id.size);
            relativeLayout = (RelativeLayout) view.findViewById ( R.id.view_foreground );

            db = new SQLiteHandler (context);
            CatID = Integer.parseInt ( db.getUserDetails ().get ( "CatID" ) );

            if(String.valueOf ( CatID ).equals("2")) {
               size.setVisibility (View.VISIBLE );
               price.setVisibility (View.VISIBLE );
            }else {
                size.setVisibility (View.INVISIBLE );
                price.setVisibility (View.INVISIBLE );
            }
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }


    public GetHallsAdapter(Context context, List<GetHalls> getHallsList) {
        this.context = context;
        this.listener = listener;
        this.getHallsList = getHallsList;
        this.getHallsListFiltered = getHallsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hall_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        final int pos = position;

        holder.name.setText(getHallsListFiltered.get(position).getName_ar ());
        holder.price.setText(getHallsListFiltered.get(position).getPrice()+ " " + context.getResources ().getString ( R.string.currency ));

        holder.size.setText(getHallsListFiltered.get(position).getSize ());

        holder.relativeLayout.setOnClickListener ( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(view.getContext (),ViewHallActivity.class);

                i.putExtra("hall_name_ar", getHallsListFiltered.get(position).getName_ar ());
                i.putExtra("hall_name_en",getHallsListFiltered.get(position).getName_en ());
                i.putExtra("hall_deatils_ar", getHallsListFiltered.get(position).getDes_ar ());
                i.putExtra("hall_deatils_en", getHallsListFiltered.get(position).getDes_en ());
                i.putExtra("hall_loc_ar",getHallsListFiltered.get(position).getLoc_ar ());
                i.putExtra("hall_loc_en",getHallsListFiltered.get(position).getLoc_en ());
                i.putExtra("hall_price_",getHallsListFiltered.get(position).getPrice ());
                i.putExtra("hall_size_",getHallsListFiltered.get(position).getSize ());
                i.putExtra("hall_id_",getHallsListFiltered.get(position).getId ());
                i.putExtra("hall_image_",getHallsListFiltered.get(position).getImage ());

                view.getContext ().startActivity(i);

            }
        });




    }

    @Override
    public int getItemCount() {
        return getHallsListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    getHallsListFiltered = getHallsList;
                } else {
                    List<GetHalls> filteredList = new ArrayList<>();
                    for (GetHalls row : getHallsList) {

                        if (row.getName_ar ().toLowerCase().contains(charString.toLowerCase()) || row.getPrice ().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    getHallsListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = getHallsListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                getHallsListFiltered = (ArrayList<GetHalls>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface GetHallsAdapterListener {
        void onGethallSelected(GetHalls getHalls);
    }
}
