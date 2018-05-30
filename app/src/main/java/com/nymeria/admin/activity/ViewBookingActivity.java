package com.nymeria.admin.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.nymeria.admin.R;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalDate;
import org.ksoap2.serialization.MarshalFloat;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Created by user on 3/27/2018.
 */

public class ViewBookingActivity extends AppCompatActivity {


    String view_booking_id;
    String view_booking_name;
    String view_booking_price;
    String view_booking_total;
    String view_booking_date;
    String view_booking_bcode;
    String view_booking_stat;

    TextView BookingNumberTextView;
    TextView NameTextView;
    TextView priceTextView;
    TextView totalTextView;
    TextView BookingDateTextView;
    TextView EventDateTextView;
    TextView StatDateTextView;
    private MaterialRippleLayout lyt_add_cart;
    private Long date_res_millis = 0L;
    String TAG_re = "Response";
    int booking_id;
    ProgressDialog progressDialog = null;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.view_booking );


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.view_booking);


        view_booking_id = getIntent ().getStringExtra ( "booking_id_s" );
        view_booking_name = getIntent ().getStringExtra ( "booking_name_s" );
        view_booking_price = getIntent ().getStringExtra ( "booking_price_s" );
        view_booking_total = getIntent ().getStringExtra ( "booking_total_s" );
        view_booking_date = getIntent ().getStringExtra ( "booking_date_s" );
        view_booking_bcode = getIntent ().getStringExtra ( "booking_bcode_s" );
        view_booking_stat = getIntent ().getStringExtra ( "booking_stat_s" );


         booking_id = Integer.valueOf ( view_booking_id );

        renderViewInfo();

    }


    private void renderViewInfo() {

        BookingNumberTextView = (TextView) findViewById(R.id.booking_number);
        NameTextView = (TextView) findViewById(R.id.buyer_name);
        priceTextView = (TextView) findViewById(R.id.price);
        totalTextView = (TextView) findViewById(R.id.total);
        BookingDateTextView = (TextView) findViewById(R.id.booking_date);
        EventDateTextView = (TextView) findViewById(R.id.event_date);
        StatDateTextView = (TextView) findViewById(R.id.stats);



        if (view_booking_name != null){

            NameTextView.setText ( view_booking_name );
            NameTextView.setVisibility ( View.VISIBLE );


        }else {

            NameTextView.setVisibility(View.GONE);
        }

        if (view_booking_bcode != null) {

            BookingNumberTextView.setText(view_booking_bcode);
            BookingNumberTextView.setVisibility(View.VISIBLE);

        } else{

            BookingNumberTextView.setVisibility(View.GONE);
        }
        if(view_booking_price!=null) {

            priceTextView.setText(view_booking_price + " " + getResources ().getString ( R.string.currency ));
            priceTextView.setVisibility(View.VISIBLE);

        }else{

            priceTextView.setVisibility(View.GONE);
        }

        if(view_booking_total!=null) {

            totalTextView.setText(view_booking_total + " " + getResources ().getString ( R.string.currency ));
            totalTextView.setVisibility(View.VISIBLE);

        }else{

            totalTextView.setVisibility(View.GONE);
        }


        if(view_booking_date!=null) {


             BookingDateTextView.setText(view_booking_date );
            BookingDateTextView.setVisibility(View.VISIBLE);

        }else {

            BookingDateTextView.setVisibility(View.GONE);
        }

        if(view_booking_stat!=null) {

            StatDateTextView.setText(view_booking_stat);
            StatDateTextView.setVisibility(View.VISIBLE);

        }else {

            StatDateTextView.setVisibility(View.GONE);
        }


        lyt_add_cart = (MaterialRippleLayout) findViewById(R.id.lyt_add_cart);

        lyt_add_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new ChangeStatus ().execute (  );

            }
        });
    }

    private class ChangeStatus extends AsyncTask<Void, Void, SoapObject> {
        @Override
        protected void onPreExecute() {
            Log.i(TAG_re, "onPreExecute");

            progressDialog = new ProgressDialog (ViewBookingActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setTitle(R.string.title_please_wait);
            progressDialog.setMessage(getString(R.string.title_getting_databooking));
            progressDialog.show();
        }

        @Override
        protected SoapObject doInBackground(Void... params) {
            Log.i ( TAG_re, "doInBackground" );

            String SOAP_ACTION = "http://farhatty.sd/ChangeStatusUser";
            String METHOD_NAME = "ChangeStatusUser";
            String NAMESPACE = "http://farhatty.sd/";
            String URL = "http://farhatty.sd/WebService.asmx";

            SoapObject Request = new SoapObject ( NAMESPACE, METHOD_NAME );


            Request.addProperty ( "bookingID", booking_id );

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope ( SoapEnvelope.VER11 );
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject ( Request );

            MarshalFloat md = new MarshalFloat ();
            MarshalDate mda = new MarshalDate ();

            md.register ( soapEnvelope );
            mda.register ( soapEnvelope );

            HttpTransportSE transport = new HttpTransportSE ( URL );

            try {

                transport.call ( SOAP_ACTION, soapEnvelope );
            } catch (Exception e) {
                e.printStackTrace ();
                return null;
            }

            try {
                SoapPrimitive resultStringBooking = (SoapPrimitive) soapEnvelope.getResponse ();
                Log.d ( "Response :", String.valueOf (resultStringBooking ));

            } catch (Exception e) {
                e.printStackTrace ();
                return null;
            }
            return Request;
        }

        @Override
        protected void onPostExecute(SoapObject result) {
            Log.i(TAG_re, "onPostExecute");

            if(result == null){

                dialogFailedRetry ();
                progressDialog.dismiss ();
            }else {

                progressDialog.dismiss ();
                dialogSucess ();

            }


        }

    }

    public void dialogFailedRetry() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this , R.style.Theme_AppCompat_DayNight_Dialog);
        builder.setTitle(R.string.failed);
        builder.setMessage(getString(R.string.failed_getting_booking));
        builder.setPositiveButton(R.string.TRY_AGAIN, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new ChangeStatus ().execute ();
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

    public void dialogSucess() {
        AlertDialog.Builder builder = new AlertDialog.Builder ( this, R.style.Theme_AppCompat_DayNight_Dialog );
        builder.setTitle ( R.string.congratulation );
        builder.setMessage ( getString ( R.string.data_submitted ) );
        builder.setNegativeButton ( R.string.Close, new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity ( new Intent ( ViewBookingActivity.this, GetBookingActivity.class ) );
            }
        } );
        builder.show ();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed ();
        finish ();
    }

}
