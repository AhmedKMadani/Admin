package com.nymeria.admin.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
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
import com.nymeria.admin.Utiliti.GPSTracker;
import com.nymeria.admin.fragment.AddDataFragment;
import com.nymeria.admin.helper.SQLiteHandler;
import com.nymeria.admin.helper.SessionManager;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Created by user on 3/15/2018.
 */

public class AddLocationActivity  extends AppCompatActivity implements  LocationListener {
    private static final String TAG = AddLocationActivity.class.getSimpleName ();

    GPSTracker gps;


    private ProgressDialog pDialog = null;
    private SessionManager session;
    private SQLiteHandler db;
    ProgressDialog progressDialog = null;

    private TextInputLayout textInputLayoutTitle_en;
    private TextInputLayout textInputLayoutTitle_ar;

    private EditText EditTextTitle_en;
    private EditText EditTextTitle_ar;


    private TextInputLayout textInputLayoutdescription_en;
    private TextInputLayout textInputLayoutdescription_ar;

    private EditText EditTextdescription_en;
    private EditText EditTextdescription_ar;


    private TextInputLayout textInputLayoutlat;
    private TextInputLayout textInputLayoutlong;

    private EditText EditTextlat;
    private EditText EditTextlong;


    private AppCompatButton appCompatButtonLogin;
    private NestedScrollView nestedScrollView;

    String email;
    String password;

    String TAG_re = "Response";
    SoapPrimitive resultString;

    double latitude ;
    double longitude ;

    String title_ar;
    String title_en;

    String descp_ar;
    String descp_en;

    String lat;
    String lon;

    private LocationManager locationManager;
    private String provider;
    int User_id;
    private Toolbar toolbar;
    private MaterialRippleLayout lyt_add;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.add_location );

        db = new SQLiteHandler ( getApplicationContext () );

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.service_text_add_location);

        nestedScrollView = (NestedScrollView) findViewById ( R.id.nestedScrollView );

        textInputLayoutTitle_ar = (TextInputLayout) findViewById ( R.id.textInputLayoutTitle_ar );
        textInputLayoutTitle_en = (TextInputLayout) findViewById ( R.id.textInputLayoutTitle_en);

        EditTextTitle_ar = (EditText) findViewById ( R.id.textInputEditTextTitle_ar);
        EditTextTitle_en = (EditText) findViewById ( R.id.textInputEditTextTitle_en );

        textInputLayoutdescription_ar = (TextInputLayout) findViewById ( R.id.textInputLayoutdescription_ar );
        textInputLayoutdescription_en = (TextInputLayout) findViewById ( R.id.textInputLayoutdescription_en);

        EditTextdescription_ar = (EditText) findViewById ( R.id.textInputEditTextdescription_ar);
        EditTextdescription_en = (EditText) findViewById ( R.id.textInputEditTextdescription_en );


        textInputLayoutlat = (TextInputLayout) findViewById ( R.id.textInputLayoutLat );
        textInputLayoutlong = (TextInputLayout) findViewById ( R.id.textInputLayoutlong);

        EditTextlat = (EditText) findViewById ( R.id.textInputEditTextLat);
        EditTextlong = (EditText) findViewById ( R.id.textInputEditTextlong);

         lyt_add = (MaterialRippleLayout) findViewById ( R.id.lyt_add );

        User_id = Integer.parseInt(db.getUserDetails ().get ( "cid" ));


        Log.d ( "Response " , "User Id :" + User_id);

        checkConnection ();


        locationManager = (LocationManager) getSystemService ( Context.LOCATION_SERVICE );
        provider = locationManager.getBestProvider ( new Criteria (), false );

        if (ActivityCompat.checkSelfPermission ( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission ( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Location location = locationManager.getLastKnownLocation ( provider );

        if (location != null) {
            Log.d ( "Location ", "Loaction Done " );
        } else {
            Log.d ( "Location", "No location found" );
        }



        lyt_add.setOnClickListener ( new View.OnClickListener () {

        public void onClick(View view) {
            submit();

        }

        } );

}



    @Override
    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        EditTextlat.setText ( String.valueOf ( latitude ) );
        EditTextlong.setText ( String.valueOf ( longitude ) );

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }



    @Override
    protected void onResume() {
        super.onResume ();
        if (ActivityCompat.checkSelfPermission ( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission ( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates ( provider, 70000, 100, (android.location.LocationListener) this );
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
                case R.id.textInputEditTextTitle_ar:
                    validateTitle_ar();
                    break;
                case R.id.textInputEditTextTitle_en:
                    validateTitle_en();
                    break;
                case R.id.textInputEditTextdescription_ar:
                    validatedescription_ar();
                    break;
                case R.id.textInputEditTextdescription_en:
                    validatedescription_en();
                    break;

                case R.id.textInputEditTextLat:
                    validateTextLat();
                    break;
                case R.id.textInputEditTextlong:
                    validateTextLat();
                    break;


            }
        }
    }


    private boolean validateTitle_ar() {
        String str = EditTextTitle_ar.getText().toString().trim();
        if (str.isEmpty()) {
            textInputLayoutTitle_ar.setError(getString(R.string.invalid_name));
            requestFocus(EditTextTitle_ar);
            return false;
        } else {
            textInputLayoutTitle_ar.setErrorEnabled(false);
        }
        return true;
    }


    private boolean validateTitle_en() {
        String str = EditTextTitle_en.getText().toString().trim();
        if (str.isEmpty()) {
            textInputLayoutTitle_en.setError(getString(R.string.invalid_name));
            requestFocus(EditTextTitle_en);
            return false;
        } else {
            textInputLayoutTitle_en.setErrorEnabled(false);
        }
        return true;
    }


    private boolean validatedescription_ar() {
        String str = EditTextdescription_ar.getText().toString().trim();
        if (str.isEmpty()) {
            textInputLayoutdescription_ar.setError(getString(R.string.invalid_name));
            requestFocus(EditTextdescription_ar);
            return false;
        } else {
            textInputLayoutdescription_ar.setErrorEnabled(false);
        }
        return true;
    }


    private boolean validatedescription_en() {
        String str = EditTextdescription_en.getText().toString().trim();
        if (str.isEmpty()) {
            textInputLayoutdescription_en.setError(getString(R.string.invalid_name));
            requestFocus(EditTextdescription_en);
            return false;
        } else {
            textInputLayoutdescription_en.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateTextLat() {
        String str = EditTextlat.getText().toString().trim();
        if (str.isEmpty()) {
            textInputLayoutlat.setError(getString(R.string.invalid_name));
            requestFocus(EditTextlat);
            return false;
        } else {
            textInputLayoutlat.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateTextLong() {
        String str = EditTextlong.getText().toString().trim();
        if (str.isEmpty()) {
            textInputLayoutlong.setError(getString(R.string.invalid_name));
            requestFocus(EditTextlong);
            return false;
        } else {
            textInputLayoutlong.setErrorEnabled(false);
        }
        return true;
    }



    private void requestFocus(View view) {
        if (view.requestFocus()) {
            this.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected ();
        showSnack ( isConnected );
    }

    private void showSnack(boolean isConnected) {

        if (isConnected) {

        } else {

            Snackbar snackbar = Snackbar.
                    make ( nestedScrollView, R.string.no_internet_connection, Snackbar.LENGTH_LONG );

            View sbView = snackbar.getView ();
            TextView textView = (TextView) sbView.findViewById ( android.support.design.R.id.snackbar_text );
            textView.setTextColor ( Color.WHITE );
            snackbar.show ();


        }

    }



        private void submit() {

            if (!validateTitle_en ()) {
                Snackbar.make ( this.findViewById ( android.R.id.content ), R.string.invalid_title_en, Snackbar.LENGTH_SHORT ).show ();
                return;

            }if (!validateTitle_ar ()) {
                Snackbar.make ( this.findViewById ( android.R.id.content ), R.string.invalid_title_ar, Snackbar.LENGTH_SHORT ).show ();
                return;

            }if (!validatedescription_en ()) {
                Snackbar.make ( this.findViewById ( android.R.id.content ), R.string.invalid_desc_en, Snackbar.LENGTH_SHORT ).show ();
                return;

            }if (!validatedescription_ar ()) {
                Snackbar.make ( this.findViewById ( android.R.id.content ), R.string.invalid_desc_ar, Snackbar.LENGTH_SHORT ).show ();
                return;

            }if (!validateTextLat ()) {
                Snackbar.make ( this.findViewById ( android.R.id.content ), R.string.invalid_lat, Snackbar.LENGTH_SHORT ).show ();
                return;

            }if (!validateTextLong ()) {
                Snackbar.make ( this.findViewById ( android.R.id.content ), R.string.invalid_long, Snackbar.LENGTH_SHORT ).show ();
                return;


            }else {

                  title_ar = EditTextTitle_ar.getText ().toString ();
                  title_en = EditTextTitle_en.getText ().toString ();

                  descp_ar = EditTextdescription_ar.getText ().toString ();
                  descp_en = EditTextdescription_en.getText ().toString ();

                  lat = EditTextlat.getText ().toString ();
                  lon = EditTextlong.getText ().toString ();

                 hideKeyboard ();

                if (db.getUserDetails ().get ( "cid" ).equals ( "0" )) {

                    dialogHallIsnotthere ();

                }else {

                    new SubmitLocation ().execute ();

                }
            }
        }




    private class SubmitLocation extends AsyncTask<Void, Void, SoapObject> {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(AddLocationActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setTitle(R.string.title_please_wait);
            progressDialog.setMessage(getString(R.string.add_location));
            progressDialog.show();
            Log.i ( TAG, "onPreExecute" );
        }





        @Override
        protected SoapObject doInBackground(Void... params) {
            Log.i ( TAG, "doInBackground" );

            String SOAP_ACTION = "http://farhatty.sd/AddLocation";
            String METHOD_NAME = "AddLocation";
            String NAMESPACE = "http://farhatty.sd/";
            String URL = "http://farhatty.sd/WebService.asmx";

            SoapObject Request = new SoapObject ( NAMESPACE, METHOD_NAME );
            Request.addProperty ( "userID", User_id );
            Request.addProperty ( "title_en", title_en );
            Request.addProperty ( "title_ar", title_ar );
            Request.addProperty ( "description_ar", descp_ar );
            Request.addProperty ( "description_en", descp_en );
            Request.addProperty ( "locla", lat );
            Request.addProperty ( "loclong", lon );

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

                resultString = (SoapPrimitive) soapEnvelope.getResponse ();
                Log.d ( "TAG","Add Location :" +resultString);

            } catch (Exception e) {
                e.printStackTrace ();
                return null;
            }

            return Request;
        }

        @Override
        protected void onPostExecute(SoapObject response) {
            super.onPostExecute(response);

            if(response == null){

                Log.d ( "Resposne Add location:", response.toString () );
                dialogFailedRetry ();
                progressDialog.dismiss ();

            }else {


                progressDialog.dismiss ();
                dialogSucess ();
            }

        }
    }


    public void dialogSucess() {
        AlertDialog.Builder builder = new AlertDialog.Builder ( this, R.style.Theme_AppCompat_DayNight_Dialog );
        builder.setTitle ( R.string.congratulation );
        builder.setMessage ( getString ( R.string.data_submitted ) );
        builder.setNegativeButton ( R.string.Close, new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity ( new Intent ( AddLocationActivity.this, MainActivity.class ) );
            }
        } );
        builder.show ();
    }

    public void dialogFailedRetry() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_DayNight_Dialog );
        builder.setTitle(R.string.failed);
        builder.setMessage(getString(R.string.failed_adding_location));
        builder.setPositiveButton(R.string.TRY_AGAIN, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new SubmitLocation ().execute ();
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
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService( Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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
                startActivity(new Intent (AddLocationActivity.this, AddingActivity.class));
            }
        });
        builder.show();
    }

}




