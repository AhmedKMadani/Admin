package com.nymeria.admin.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.nymeria.admin.R;
import com.nymeria.admin.Utiliti.ConnectivityReceiver;
import com.nymeria.admin.adapter.TrackAdapter;
import com.nymeria.admin.helper.SQLiteHandler;
import com.nymeria.admin.model.BookingTrack;


import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2/27/2018.
 */

public class TrackBookingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MaterialRippleLayout lyt_check_booking;
    private TextInputLayout check_booking_lyt;
    private EditText booking_number;
    ProgressDialog progressDialog = null;

    String BookingID ;
    SoapObject result = null;
    CardView Cardbooking ;

    private static String SOAP_ACTION = "http://farhatty.sd/Tracking";
    private static String NAMESPACE = "http://farhatty.sd/";
    private static String METHOD_NAME = "Tracking";
    private static String URL = "http://farhatty.sd/WebService.asmx";

    private List<BookingTrack> trackList;
    private TrackAdapter mAdapter;
    private Toolbar toolbar;
    private SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_track);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.service_text_tarck_booking);

        lyt_check_booking = (MaterialRippleLayout) findViewById ( R.id.lyt_check_booking );
        booking_number = (EditText) findViewById ( R.id.book_num );
        booking_number.addTextChangedListener ( new CheckoutTextWatcher ( booking_number ) );
        check_booking_lyt = (TextInputLayout) findViewById ( R.id.check_booking_lyt );

        db = new SQLiteHandler ( getApplicationContext () );
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        trackList = new ArrayList<> ();
        mAdapter = new TrackAdapter(this, trackList, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager ( new LinearLayoutManager ( this ));

        recyclerView.setAdapter(mAdapter);
        Cardbooking = (CardView) findViewById ( R.id.cardbooking );
        Cardbooking.setVisibility ( View.GONE );

        progressDialog = new ProgressDialog (TrackBookingActivity.this );
        progressDialog.setCancelable ( false );
        progressDialog.setTitle ( R.string.nav_text_track );
        progressDialog.setMessage ( getString ( R.string.title_getting_databooking ) );


        checkConnection ();

        lyt_check_booking.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {

                submit();
            }
        } );



    }



    private class CheckoutTextWatcher implements TextWatcher {
        private View view;

        private CheckoutTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.book_num:
                    validateNumber ();
                    break;

            }
        }
    }



    private boolean validateNumber() {
        String str = booking_number.getText().toString().trim();
        if (str.isEmpty()) {
            check_booking_lyt.setError(getString(R.string.invalid_booking_numbr));
            requestFocus(booking_number);
            return false;
        } else {
            check_booking_lyt.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            this.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService( Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }



    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {

        if (isConnected) {

        }else{

            Snackbar snackbar = Snackbar
                    .make(this.findViewById(android.R.id.content), R.string.no_internet_connection, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            checkConnection();
                        }
                    });


            snackbar.setActionTextColor( Color.RED);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();




        }

    }



    public class TrackBooking extends AsyncTask<String, Void, SoapObject> {
        @Override

        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog (TrackBookingActivity.this );
            progressDialog.setCancelable ( false );
            progressDialog.setTitle ( R.string.nav_text_track );
            progressDialog.setMessage ( getString ( R.string.title_getting_databooking ) );
            progressDialog.show ();
        }
        @Override
        protected SoapObject doInBackground(String... params) {
            Log.i("TaG", "doInBackground");

            String SOAP_ACTION = "http://farhatty.sd/Tracking";
            String METHOD_NAME = "Tracking";
            String NAMESPACE = "http://farhatty.sd/";
            String URL = "http://farhatty.sd/WebService.asmx";

            SoapObject Request = new SoapObject ( NAMESPACE, METHOD_NAME );
            Request.addProperty ( "BookingID", BookingID);
            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope ( SoapEnvelope.VER11 );
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject ( Request );
            HttpTransportSE transport = new HttpTransportSE ( URL );

            try {

                transport.call ( SOAP_ACTION, soapEnvelope );

            } catch (Exception e) {
                e.printStackTrace ();

                return null;

            }

            try {

                result  = (SoapObject) soapEnvelope.getResponse ();

            } catch (Exception e) {
                e.printStackTrace ();

                return null;

            }


            for (int i = 0; i < result.getPropertyCount(); i++) {

                PropertyInfo pi = new PropertyInfo ();
                result.getPropertyInfo ( i, pi );
                Object property = result.getProperty ( i );
                if (pi.name.equals ( "Track" ) && property instanceof SoapObject) {
                    SoapObject transDetail = (SoapObject) property;


                    String track_id =  ( (String) transDetail.getProperty ( "bookingID" ).toString () );
                    String track_bdate = (String) transDetail.getProperty ( "DateBooking" ).toString ();
                    String track_status = (String) transDetail.getProperty ( "Status" ).toString ();
                    String track_name = (String) transDetail.getProperty ( "Name" ).toString ();
                    String track_total = (String) transDetail.getProperty ( "Total" ).toString ();
                    String track_phone = (String) transDetail.getProperty ( "Phone" ).toString ();
                    String track_date = (String) transDetail.getProperty ( "Date" ).toString ();

                    BookingTrack tarck = new BookingTrack ();

                    tarck.setId ( track_id );
                    tarck.setName ( track_name );
                    tarck.setBookingDate (track_bdate);
                    tarck.setStatus ( track_status );
                    tarck.setTotal ( track_total );
                    tarck.setPhone ( track_phone );
                    tarck.setDate ( track_date );

                    trackList.add(tarck);

                }
            }
            return Request;
        }


        @Override
        protected void onPostExecute(SoapObject response) {
            super.onPostExecute(response);



            if(response == null){

                dialogFailedRetry ();

            }else {

                mAdapter.notifyDataSetChanged();
                Cardbooking.setVisibility ( View.VISIBLE );
                progressDialog.dismiss ();

            }


        }
    }

    private void submit() {

        if (!validateNumber()) {
            Snackbar.make(this.findViewById(android.R.id.content), R.string.invalid_booking_numbr, Snackbar.LENGTH_SHORT).show();
            return;

        }else {

            BookingID = booking_number.getText ().toString ();
            hideKeyboard ();

            if (db.getUserDetails ().get ( "cid" ).equals ( "0" )) {

                dialogHallIsnotthere ();

            }else {

                new TrackBooking ().execute (  );

            }


        }
    }


    public void dialogFailedRetry() {
        progressDialog.dismiss ();
        AlertDialog.Builder builder = new AlertDialog.Builder(this , R.style.Theme_AppCompat_DayNight_Dialog);
        builder.setTitle(R.string.failed);
        builder.setMessage(getString(R.string.failed_getting_booking));
        builder.setPositiveButton(R.string.TRY_AGAIN, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new TrackBooking ().execute (  );
            }
        });
        builder.setNegativeButton(R.string.Close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent (TrackBookingActivity.this ,MainActivity.class));
            }
        });
        builder.show();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed ();
        finish ();
    }


    public void dialogHallIsnotthere() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Theme_AppCompat_DayNight_Dialog );
        builder.setTitle(R.string.sorry);
        builder.setMessage(getString(R.string.hall_no_ex));
        builder.setNegativeButton(R.string.Close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent (TrackBookingActivity.this, AddingActivity.class));
            }
        });
        builder.show();
    }

}






