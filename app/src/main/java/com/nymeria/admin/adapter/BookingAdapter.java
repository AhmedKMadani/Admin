package com.nymeria.admin.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.nymeria.admin.R;
import com.nymeria.admin.model.Booking;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ahmed K Madani on 16/11/17.
 */

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<Booking> bookingsList;
    private List<Booking> bookingsListFiltered;
    private BookingAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name,code,stat;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById( R.id.name);
            code = (TextView) view.findViewById(R.id.bcode);
            stat = (TextView) view.findViewById(R.id.stat);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onBookingSelected(bookingsListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }


    public BookingAdapter(Context context, List<Booking> bookingsList, BookingAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.bookingsList = bookingsList;
        this.bookingsListFiltered = bookingsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.booking_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Booking bookings = bookingsListFiltered.get(position);
        holder.name.setText(bookings.getCSName ());
        holder.code.setText(bookings.getBcode ());
        holder.stat.setText(bookings.getState ());


    }

    @Override
    public int getItemCount() {
        return bookingsListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    bookingsListFiltered = bookingsList;
                } else {
                    List<Booking> filteredList = new ArrayList<>();
                    for (Booking row : bookingsList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getCSName().toLowerCase().contains(charString.toLowerCase()) || row.getBcode ().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    bookingsListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = bookingsListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                bookingsListFiltered = (ArrayList<Booking>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface BookingAdapterListener {
        void onBookingSelected(Booking bookings);
    }
}
