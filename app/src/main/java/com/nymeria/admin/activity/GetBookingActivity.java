package com.nymeria.admin.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


import com.nymeria.admin.R;
import com.nymeria.admin.Utiliti.ConnectivityReceiver;
import com.nymeria.admin.Utiliti.MyDividerItemDecoration;
import com.nymeria.admin.adapter.BookingAdapter;
import com.nymeria.admin.helper.SQLiteHandler;
import com.nymeria.admin.model.Booking;

import java.util.ArrayList;
import java.util.List;

public class GetBookingActivity extends AppCompatActivity implements BookingAdapter.BookingAdapterListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<Booking> bookingList;
    private BookingAdapter mAdapter;
    private SearchView searchView;
    private ProgressBar pbar;
    private CoordinatorLayout coordinatorLayout;

    SoapObject result = null;
    int User_id;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getbooking);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        db = new SQLiteHandler ( getApplicationContext () );

        User_id = Integer.parseInt(db.getUserDetails ().get ( "uid" ));

        ActionBar actionBar;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pbar = (ProgressBar) findViewById(R.id.pbar_wedding);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);

        actionBar = getSupportActionBar();
        getSupportActionBar().setTitle(R.string.service_text_booking);

        recyclerView = (RecyclerView) findViewById( R.id.recycler_view);
        bookingList = new ArrayList<>();
        mAdapter = new BookingAdapter(this, bookingList, this);



        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration (this, DividerItemDecoration.VERTICAL, 36));
        recyclerView.setAdapter(mAdapter);

        checkConnection();

    }



    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {

        if (isConnected) {

            new fetchHalls().execute (  );

        }else{

            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, R.string.no_internet_connection, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            checkConnection();
                        }
                    });


            snackbar.setActionTextColor(Color.RED);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();

            pbar.setVisibility(View.GONE);


        }

    }



    public class fetchHalls extends AsyncTask<String, Void, SoapObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute ();
            pbar.setVisibility ( View.VISIBLE );
            pbar.setIndeterminate ( false );

        }


        @Override
        protected SoapObject doInBackground(String... params) {
            String SOAP_ACTION = "http://farhatty.sd/GetBooking";
            String METHOD_NAME = "GetBooking";
            String NAMESPACE = "http://farhatty.sd/";
            String URL = "http://farhatty.sd/WebService.asmx";

            SoapObject Request = new SoapObject ( NAMESPACE, METHOD_NAME );
            Request.addProperty ( "UserID", User_id );

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope ( SoapEnvelope.VER11 );
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject ( Request );


            HttpTransportSE androidHttpTransport = new HttpTransportSE ( URL );
            try {

                androidHttpTransport.call ( SOAP_ACTION, soapEnvelope );
            } catch (Exception e) {
                e.printStackTrace ();

                return null;

            }

            try {
                result = (SoapObject) soapEnvelope.getResponse ();
                Log.e ( "Response Booking ",result.toString () );
            } catch (SoapFault soapFault) {
                soapFault.printStackTrace ();

                return null;

            }

            for (int i = 0; i < result.getPropertyCount (); i++) {

                PropertyInfo pi = new PropertyInfo ();
                result.getPropertyInfo ( i, pi );
                Object property = result.getProperty ( i );
                if (pi.name.equals ( "GetBookingClass" ) && property instanceof SoapObject) {
                    SoapObject transDetail = (SoapObject) property;



                    String booking_id = (String) transDetail.getProperty ( "ID" ).toString ();
                    String booking_total =  (String) transDetail.getProperty ( "Total" ).toString ();
                    String booking_csname = (String) transDetail.getProperty ( "Customer_Name" ).toString ();
                    String booking_date = (String)  transDetail.getProperty ( "Date" ).toString ();
                    String booking_bcode = (String) transDetail.getProperty ( "B_Code" ).toString ();
                    String booking_price = (String) transDetail.getProperty ( "Price" ).toString ();
                    String booking_state = (String) transDetail.getProperty ( "Status" ).toString ();
                    String booking_hallname = (String) transDetail.getProperty ( "Hall_name" ).toString ();

                    Booking booking = new Booking ();

                    booking.setId ( booking_id );
                    booking.setCSName ( booking_csname );
                    booking.setBCode ( booking_bcode );
                    booking.setTotal ( booking_total );
                    booking.setPrice ( booking_price );
                    booking.setHallname ( booking_hallname );
                    booking.setState ( booking_state );
                    booking.setDate ( booking_date );


                    bookingList.add ( booking );

                }
            }
            return Request;
        }

        @Override
        protected void onPostExecute(SoapObject response) {
            super.onPostExecute ( response );

            if (response == null) {

                dialogFailedRetry ();

            } else {

                pbar.setVisibility ( View.GONE );
                mAdapter.notifyDataSetChanged ();

            }
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
        finish ();
    }

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);

        }
    }

    @Override
    public void onBookingSelected(Booking booking) {



        Intent i = new Intent(this,ViewBookingActivity.class);

        i.putExtra("booking_id_s", booking.getId());
        i.putExtra("booking_name_s", booking.getCSName());
        i.putExtra("booking_price_s",booking.getPrice ());
        i.putExtra("booking_total_s", booking.getTotal ());
        i.putExtra("booking_date_s", booking.getDate ());
        i.putExtra("booking_bcode_s",booking.getBcode ());
        i.putExtra("booking_stat_s",booking.getState ());
        startActivity(i);

    }


    public void dialogFailedRetry() {

        pbar.setVisibility ( View.GONE );
        AlertDialog.Builder builder = new AlertDialog.Builder(this ,R.style.Theme_AppCompat_DayNight_Dialog);
        builder.setTitle(R.string.failed);
        builder.setMessage(getString(R.string.failed_getting));
        builder.setPositiveButton(R.string.TRY_AGAIN, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new fetchHalls().execute (  );
            }
        });
        builder.setNegativeButton(R.string.Close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent (getApplicationContext(), MainActivity.class));
            }
        });
        builder.show();
    }


}
