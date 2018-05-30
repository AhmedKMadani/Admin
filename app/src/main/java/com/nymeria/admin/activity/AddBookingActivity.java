package com.nymeria.admin.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.nymeria.admin.R;
import com.nymeria.admin.fragment.AddDataFragment;
import com.nymeria.admin.helper.SQLiteHandler;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalDate;
import org.ksoap2.serialization.MarshalFloat;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class AddBookingActivity extends AppCompatActivity {

    private View parent_view;
    private ImageButton bt_date_res;
    private TextView date_res;

    private MaterialRippleLayout lyt_add_cart;
    private TextInputLayout buyer_name_lyt;
    private EditText buyer_name;
    private String name_order;

    private DatePickerDialog datePickerDialog;
    private Long date_res_millis = 0L;
    ProgressDialog progressDialog = null;
    ProgressDialog progress  = null;

    String random_number;
    String Date_response;
    String TAG_re = "Response";
    int user_cid;
    SoapPrimitive resultString;

    int upperBound = 10000000;
    int lowerBound = 99999999;
    private SQLiteHandler db;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_checkout );
        db = new SQLiteHandler ( getApplicationContext () );

        toolbar = (Toolbar) findViewById ( R.id.toolbar );
        setSupportActionBar ( toolbar );
        getSupportActionBar ().setTitle ( R.string.service_text_add_booking );

        user_cid = Integer.parseInt ( db.getUserDetails ().get ( "cid" ) );
        iniComponent ();

    }


    private void iniComponent() {

        parent_view = findViewById ( android.R.id.content );
        lyt_add_cart = (MaterialRippleLayout) findViewById ( R.id.lyt_add_cart );
        buyer_name = (EditText) findViewById ( R.id.buyer_name );

        buyer_name.addTextChangedListener ( new CheckoutTextWatcher ( buyer_name ) );
        buyer_name_lyt = (TextInputLayout) findViewById ( R.id.buyer_name_lyt );
        bt_date_res = (ImageButton) findViewById ( R.id.bt_date_res );
        date_res = (TextView) findViewById ( R.id.date_res );

        progressDialog = new ProgressDialog ( this );
        progressDialog.setCancelable ( false );
        progressDialog.setTitle ( R.string.title_please_wait );
        progressDialog.setMessage ( getString ( R.string.content_submit ) );

        bt_date_res.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                dialogDatePicker ();
            }
        } );

        lyt_add_cart.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {

                new Handler ().postDelayed ( new Runnable () {
                    @Override
                    public void run() {
                        submitForm ();
                    }
                }, 1000 );
            }
        } );
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId ();
        if (item_id == android.R.id.home) {
            super.onBackPressed ();
        }
        return super.onOptionsItemSelected ( item );
    }

    @Override
    protected void onResume() {
        super.onResume ();
        displayData ();
    }


    private void displayData() {
        getBookingNumber ();
    }

    public static int randInt(int min, int max) {

        Random rand = new Random ();
        int randomNum = rand.nextInt ( (max - min) + 1 ) + min;
        return randomNum;

    }


    private void getBookingNumber() {

        int booking = randInt ( upperBound, lowerBound );
        String random_numbe = String.valueOf ( booking );
        random_number = ("B-" + random_numbe);

    }

    private void dialogDatePicker() {
        Calendar cur_calender = Calendar.getInstance ();
        datePickerDialog = new DatePickerDialog ( this, R.style.DatePickerTheme, new DatePickerDialog.OnDateSetListener () {
            @Override
            public void onDateSet(DatePicker datePicker, int _year, int _month, int _day) {
                Calendar calendar = Calendar.getInstance ();
                calendar.set ( Calendar.YEAR, _year );
                calendar.set ( Calendar.MONTH, _month );
                calendar.set ( Calendar.DAY_OF_MONTH, _day );
                date_res_millis = calendar.getTimeInMillis ();
                new CheckDate ().execute ();
                date_res.setText ( getFormattedDateSimple ( date_res_millis ) );
                datePickerDialog.dismiss ();
            }
        }, cur_calender.get ( Calendar.YEAR ), cur_calender.get ( Calendar.MONTH ), cur_calender.get ( Calendar.DAY_OF_MONTH ) );

        datePickerDialog.setCancelable ( true );
        datePickerDialog.getDatePicker ().setMinDate ( System.currentTimeMillis () - 1000 );
        datePickerDialog.show ();
    }

    private static String getFormattedDateSimple(Long dateTime) {
        SimpleDateFormat newFormat = new SimpleDateFormat ( "MMMM dd, yyyy");
        return newFormat.format ( new Date ( dateTime ) );
    }


    private static String getFormattedDateSimplePosting(Long dateTime) {
        SimpleDateFormat newFormat = new SimpleDateFormat ( "yyyy-MM-dd" ,Locale.US);
        return newFormat.format ( new Date ( dateTime ) );
    }


    private class DoBooking extends AsyncTask <Void, Void, SoapObject> {
        @Override
        protected void onPreExecute() {
            Log.i ( TAG_re, "onPreExecute" );
        }

        @Override
        protected SoapObject doInBackground(Void... params) {
            Log.i ( TAG_re, "doInBackground" );

            String SOAP_ACTION = "http://farhatty.sd/Booking";
            String METHOD_NAME = "Booking";
            String NAMESPACE = "http://farhatty.sd/";
            String URL = "http://farhatty.sd/WebService.asmx";

            SoapObject Request = new SoapObject ( NAMESPACE, METHOD_NAME );

            Request.addProperty ( "ID", user_cid );
            Request.addProperty ( "Code", random_number );
            Request.addProperty ( "name", name_order );
            Request.addProperty ( "date", getFormattedDateSimplePosting ( date_res_millis ) );

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
                Log.d ( "Response :", String.valueOf ( resultStringBooking ) );

            } catch (Exception e) {
                e.printStackTrace ();
                return null;
            }
            return Request;
        }

        @Override
        protected void onPostExecute(SoapObject result) {
            Log.i ( TAG_re, "onPostExecute" );

            if (result == null) {

                dialogFailedRetry ();

            } else {

                dialogSuccess ();

            }


        }

    }

    private boolean validateName() {
        String str = buyer_name.getText ().toString ().trim ();
        if (str.isEmpty ()) {
            buyer_name_lyt.setError ( getString ( R.string.invalid_name ) );
            requestFocus ( buyer_name );
            return false;
        } else {
            buyer_name_lyt.setErrorEnabled ( false );
        }
        return true;
    }


    private boolean validateResvertion() {
        return date_res_millis != 0L;
    }

    private void requestFocus(View view) {
        if (view.requestFocus ()) {
            getWindow ().setSoftInputMode ( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE );
        }
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus ();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService ( Context.INPUT_METHOD_SERVICE );
            imm.hideSoftInputFromWindow ( view.getWindowToken (), 0 );
        }
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
            switch (view.getId ()) {
                case R.id.name:
                    validateName ();
                    break;

            }
        }
    }


    private void submitForm() {

        if (!validateName ()) {
            Snackbar.make ( parent_view, R.string.invalid_name, Snackbar.LENGTH_SHORT ).show ();
            return;
        }

        if (!validateResvertion ()) {
            Snackbar.make ( parent_view, R.string.invalid_date_res, Snackbar.LENGTH_SHORT ).show ();
            return;

        }
        if (Date_response.equals ( "1" )) {
            Log.e ( TAG_re, "Check Date validation " + Date_response );
            Snackbar.make ( parent_view, R.string.invalid_date, Snackbar.LENGTH_LONG ).show ();

        } else {

            name_order = buyer_name.getText ().toString ();

            hideKeyboard ();

            if (db.getUserDetails ().get ( "cid" ).equals ( "0" )) {

                dialogHallIsnotthere();
            } else {

                dialogConfirmCheckout ();
            }

        }
    }


    private void submitOrderData() {
        new DoBooking ().execute ();
    }


    private void delaySubmitOrderData() {
        progressDialog.show ();
        new Handler ().postDelayed ( new Runnable () {
            @Override
            public void run() {
                submitOrderData ();
            }
        }, 2000 );
    }

    public void dialogConfirmCheckout() {
        AlertDialog.Builder builder = new AlertDialog.Builder ( this, R.style.Theme_AppCompat_DayNight_Dialog );
        builder.setTitle ( R.string.confirmation );
        builder.setMessage ( getString ( R.string.confirm_checkout ) );
        builder.setPositiveButton ( R.string.YES, new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                delaySubmitOrderData ();
            }
        } );
        builder.setNegativeButton ( R.string.NO, null );
        builder.show ();
    }

    public void dialogFailedRetry() {
        progressDialog.dismiss ();
        AlertDialog.Builder builder = new AlertDialog.Builder ( this, R.style.Theme_AppCompat_DayNight_Dialog );
        builder.setTitle ( R.string.failed );
        builder.setMessage ( getString ( R.string.failed_checkout ) );
        builder.setPositiveButton ( R.string.TRY_AGAIN, new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                delaySubmitOrderData ();
            }
        } );
        builder.setNegativeButton ( R.string.Close, new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity ( new Intent ( getApplicationContext (), MainActivity.class ) );
            }
        } );
        builder.show ();
    }


    public void dialogSuccess() {
        progressDialog.dismiss ();
        AlertDialog.Builder builder = new AlertDialog.Builder ( this, R.style.Theme_AppCompat_DayNight_Dialog );
        builder.setTitle ( R.string.success_checkout );
        builder.setMessage ( getString ( R.string.msg_success ) );
        builder.setNegativeButton ( R.string.Close, new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity ( new Intent ( getApplicationContext (), MainActivity.class ) );
            }
        } );
        builder.show ();
    }


    private class CheckDate extends AsyncTask <Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            progress = new ProgressDialog ( AddBookingActivity.this );
            progress.setCancelable ( false );
            progress.setTitle ( R.string.title_please_wait );
            progress.setMessage ( getString ( R.string.check_data ) );
            progress.show ();

            Log.i ( TAG_re, "onPreExecute" );
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i ( TAG_re, "doInBackground" );
            DateCheck ();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if (Date_response.equals ( "1" )) {
                progress.dismiss ();
                Log.e ( TAG_re, "Check Date validation " + Date_response );
                Snackbar.make ( parent_view, R.string.invalid_date, Snackbar.LENGTH_LONG ).show ();
            } else {
                progress.dismiss ();
                Log.e ( TAG_re, "Check Date validation " + Date_response );
                Log.i ( TAG_re, "onPostExecute" );
                Snackbar.make ( parent_view, R.string.valid_date, Snackbar.LENGTH_LONG ).show ();
            }

        }

        public void DateCheck() {
            String SOAP_ACTION = "http://farhatty.sd/CheckDate";
            String METHOD_NAME = "CheckDate";
            String NAMESPACE = "http://farhatty.sd/";
            String URL = "http://farhatty.sd/WebService.asmx";

            try {
                SoapObject Request = new SoapObject ( NAMESPACE, METHOD_NAME );

                Request.addProperty ( "Date", getFormattedDateSimplePosting ( date_res_millis ) );
                Request.addProperty ( "ID", user_cid );

                SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope ( SoapEnvelope.VER11 );
                soapEnvelope.dotNet = true;
                soapEnvelope.setOutputSoapObject ( Request );

                MarshalFloat md = new MarshalFloat ();
                MarshalDate mda = new MarshalDate ();

                md.register ( soapEnvelope );
                mda.register ( soapEnvelope );

                HttpTransportSE transport = new HttpTransportSE ( URL );

                transport.call ( SOAP_ACTION, soapEnvelope );

                resultString = (SoapPrimitive) soapEnvelope.getResponse ();
                Date_response = resultString.toString ();
                Log.e ( TAG_re, "Result Date Check : " + resultString );
            } catch (Exception ex) {
                Log.e ( TAG_re, "Error: " + ex.getMessage () );

            }
        }

    }


    public void dialogFailedRetryDate() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_DayNight_Dialog);
        builder.setTitle(R.string.failed);
        builder.setMessage(getString(R.string.failed_getting));
        builder.setPositiveButton(R.string.TRY_AGAIN, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new CheckDate ().execute ();
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
                startActivity(new Intent (AddBookingActivity.this, AddingActivity.class));
            }
        });
        builder.show();
    }

}
