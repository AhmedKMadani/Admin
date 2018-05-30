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
import com.nymeria.admin.adapter.ServiceUserAdapter;
import com.nymeria.admin.helper.SQLiteHandler;
import com.nymeria.admin.model.ServiceUser;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalFloat;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.List;

/**
 * Created by user on 4/3/2018.
 */

public class ViewServicesActivity  extends AppCompatActivity {

    String view_serv_title_ar ;
    String view_serv_title_ene ;
    String view_serv_deatils_ar ;
    String view_serv_deatils_en ;
    String  view_serv_price_ ;
    String  view_serv_id_ ;

    private RecyclerView recyclerView;
    private MaterialRippleLayout lyt_edit;
    private MaterialRippleLayout lyt_delet;

    private TextInputLayout layout_title_ar;
    private EditText title_ar;

    private TextInputLayout layout_title_en;
    private EditText title_en;

    private TextInputLayout layout_details_ar;
    private EditText details_ar;

    private TextInputLayout layout_details_en;
    private EditText details_en;

    private TextInputLayout layout_price;
    private EditText price;

    ProgressDialog progressDialog = null;

    String title_ar_ ;
    String title_en_ ;

    String deatils_ar_ ;
    String deatils_en_ ;
    String price_ ;
    String TAG_re = "Response";

    CardView cardservices ;
    private SQLiteHandler db;
    int user_cid;
    int serv_id;
    private Double _fees = 0D;

    SoapObject resultString_service;
    SoapPrimitive resultString;
    private List<ServiceUser> serviceusersList;
    private ServiceUserAdapter mAdapter;
    private Toolbar toolbar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.fragment_view_services );

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.edit_services);

        view_serv_title_ar = getIntent ().getStringExtra ( "serv_title_ar" );
        view_serv_title_ene = getIntent ().getStringExtra ( "serv_title_en" );
        view_serv_deatils_ar = getIntent ().getStringExtra ( "serv_deatils_ar" );
        view_serv_deatils_en = getIntent ().getStringExtra ( "serv_deatils_en" );
        view_serv_price_ = getIntent ().getStringExtra ( "serv_price_" );
        view_serv_id_ = getIntent ().getStringExtra ( "serv_id_" );


        lyt_edit = (MaterialRippleLayout) findViewById ( R.id.lyt_edit );
        lyt_delet = (MaterialRippleLayout) findViewById ( R.id.lyt_delet );

        title_ar = (EditText) findViewById ( R.id.title_ar );
        layout_title_ar = (TextInputLayout) findViewById ( R.id.layout_title_ar );

        title_en = (EditText) findViewById ( R.id.title_en );
        layout_title_en = (TextInputLayout) findViewById ( R.id.layout_title_en );

        details_ar = (EditText) findViewById ( R.id.deatils_ar );
        layout_details_ar = (TextInputLayout) findViewById ( R.id.layout_details_ar );

        details_en = (EditText) findViewById ( R.id.deatils_en );
        layout_details_en = (TextInputLayout) findViewById ( R.id.layout_details_en );

        price = (EditText) findViewById ( R.id.price );
        layout_price = (TextInputLayout) findViewById ( R.id.layout_price );

        
        title_ar.setText ( view_serv_title_ar );
        title_en.setText ( view_serv_title_ene );
        details_ar.setText ( view_serv_deatils_ar );
        details_en.setText ( view_serv_deatils_en );
        price.setText ( view_serv_price_ );
        
        title_ar.addTextChangedListener ( new CheckoutTextWatcher ( title_ar ) );
        title_en.addTextChangedListener ( new CheckoutTextWatcher ( title_en ) );

        details_ar.addTextChangedListener ( new CheckoutTextWatcher ( details_ar ) );
        details_en.addTextChangedListener ( new CheckoutTextWatcher ( details_en ) );

        price.addTextChangedListener ( new CheckoutTextWatcher ( price ) );


        db = new SQLiteHandler (  this );
        user_cid = Integer.parseInt(db.getUserDetails ().get ("cid"));
        serv_id = Integer.parseInt(view_serv_id_);

        progressDialog = new ProgressDialog ( this );
        progressDialog.setCancelable ( false );
        progressDialog.setTitle ( R.string.edit_services);
        progressDialog.setMessage ( getString ( R.string.title_please_wait ) );


        lyt_edit.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {

                submitEdit();
            }
        } );


        lyt_delet.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {

                submitDelete();
            }
        } );


        checkConnection ();

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
                case R.id.title_ar:
                    validateTitle_ar();
                    break;
                case R.id.title_en:
                    validateTitle_en();
                    break;
                case R.id.deatils_ar:
                    validatedeatils_ar();
                    break;
                case R.id.deatils_en:
                    validatedeatils_en();
                    break;
                case R.id.price:
                    validateprice();
                    break;

            }
        }
    }


    private boolean validateTitle_ar() {
        String str = title_ar.getText().toString().trim();
        if (str.isEmpty()) {
            layout_title_ar.setError(getString(R.string.invalid_name));
            requestFocus(title_ar);
            return false;
        } else {
            layout_title_ar.setErrorEnabled(false);
        }
        return true;
    }


    private boolean validateTitle_en() {
        String str = title_en.getText().toString().trim();
        if (str.isEmpty()) {
            layout_title_en.setError(getString(R.string.invalid_name));
            requestFocus(title_en);
            return false;
        } else {
            layout_title_en.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatedeatils_ar() {
        String str = details_ar.getText().toString().trim();
        if (str.isEmpty()) {
            layout_details_ar.setError(getString(R.string.invalid_name));
            requestFocus(details_ar);
            return false;
        } else {
            layout_details_ar.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatedeatils_en() {
        String str = details_en.getText().toString().trim();
        if (str.isEmpty()) {
            layout_details_en.setError(getString(R.string.invalid_name));
            requestFocus(details_en);
            return false;
        } else {
            layout_details_en.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateprice() {
        String str = price.getText().toString().trim();
        if (str.isEmpty()) {
            layout_price.setError(getString(R.string.invalid_name));
            requestFocus(price);
            return false;
        } else {
            layout_price.setErrorEnabled(false);
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

    private void submitEdit() {

        if (!validateTitle_ar ()) {
            Snackbar.make ( this.findViewById ( android.R.id.content ), R.string.invalid_title_ar, Snackbar.LENGTH_SHORT ).show ();
            return;

        }if (!validateTitle_en ()) {
            Snackbar.make ( this.findViewById ( android.R.id.content ), R.string.invalid_title_en, Snackbar.LENGTH_SHORT ).show ();
            return;

        }if (!validatedeatils_ar ()) {
            Snackbar.make ( this.findViewById ( android.R.id.content ), R.string.invalid_desc_ar, Snackbar.LENGTH_SHORT ).show ();
            return;

        }if (!validatedeatils_en ()) {
            Snackbar.make ( this.findViewById ( android.R.id.content ), R.string.invalid_desc_en, Snackbar.LENGTH_SHORT ).show ();
            return;

        }if (!validateprice ()) {
            Snackbar.make(this.findViewById(android.R.id.content), R.string.invalid_price, Snackbar.LENGTH_SHORT).show();
            return;

        }else {

            title_ar_ = title_ar.getText ().toString ();
            title_en_ = title_en.getText ().toString ();
            deatils_ar_ = details_ar.getText ().toString ();
            deatils_en_ = details_en.getText ().toString ();
            price_ = price.getText ().toString ();
            _fees = Double.valueOf(price_);

            hideKeyboard ();
            new EditService ().execute (  );

        }
    }


    private void submitDelete() {

        if (!validateTitle_ar ()) {
            Snackbar.make ( this.findViewById ( android.R.id.content ), R.string.invalid_title_ar, Snackbar.LENGTH_SHORT ).show ();
            return;

        }if (!validateTitle_en ()) {
            Snackbar.make ( this.findViewById ( android.R.id.content ), R.string.invalid_title_en, Snackbar.LENGTH_SHORT ).show ();
            return;

        }if (!validatedeatils_ar ()) {
            Snackbar.make ( this.findViewById ( android.R.id.content ), R.string.invalid_desc_ar, Snackbar.LENGTH_SHORT ).show ();
            return;

        }if (!validatedeatils_en ()) {
            Snackbar.make ( this.findViewById ( android.R.id.content ), R.string.invalid_desc_en, Snackbar.LENGTH_SHORT ).show ();
            return;

        }if (!validateprice ()) {
            Snackbar.make(this.findViewById(android.R.id.content), R.string.invalid_price, Snackbar.LENGTH_SHORT).show();
            return;

        }else {

            title_ar_ = title_ar.getText ().toString ();
            title_en_ = title_en.getText ().toString ();
            deatils_ar_ = details_ar.getText ().toString ();
            deatils_en_ = details_en.getText ().toString ();
            price_ = price.getText ().toString ();
            _fees = Double.valueOf(price_);

            hideKeyboard ();
            new DeletService ().execute (  );

        }
    }



    private class EditService extends AsyncTask<Void, Void, SoapObject> {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog ( ViewServicesActivity.this);
            progressDialog.setCancelable ( false );
            progressDialog.setTitle ( R.string.title_please_wait );
            progressDialog.setMessage ( getString ( R.string.title_edit_services) );
            progressDialog.show ();
            Log.i ( TAG_re, "onPreExecute" );
        }

        @Override
        protected SoapObject doInBackground(Void... params) {
            Log.i ( TAG_re, "doInBackground" );

            String SOAP_ACTION = "http://farhatty.sd/EditService";
            String METHOD_NAME = "EditService";
            String NAMESPACE = "http://farhatty.sd/";
            String URL = "http://farhatty.sd/WebService.asmx";

            SoapObject Request = new SoapObject ( NAMESPACE, METHOD_NAME );

            Request.addProperty ( "ID", serv_id);
            Request.addProperty ( "Title_en", title_en_ );
            Request.addProperty ( "Title_ar", title_ar_ );
            Request.addProperty ( "Details_en", deatils_en_ );
            Request.addProperty ( "Details_ar", deatils_ar_ );
            Request.addProperty ( "Price", _fees );


            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope ( SoapEnvelope.VER11 );
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject ( Request );

            MarshalFloat md = new MarshalFloat ();
            md.register ( soapEnvelope );


            HttpTransportSE transport = new HttpTransportSE ( URL );

            try {

                transport.call ( SOAP_ACTION, soapEnvelope );
            } catch (Exception e) {
                e.printStackTrace ();
                return null;
            }

            try {
                SoapPrimitive resultStringBooking = (SoapPrimitive) soapEnvelope.getResponse ();
                Log.d ( "Edit Services :", "" + resultStringBooking );
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
                progressDialog.dismiss ();

            } else {

                progressDialog.dismiss ();
                dialogSucess ();

            }


        }
    }





    private class DeletService extends AsyncTask<Void, Void, SoapObject> {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog ( ViewServicesActivity.this);
            progressDialog.setCancelable ( false );
            progressDialog.setTitle ( R.string.title_please_wait );
            progressDialog.setMessage ( getString ( R.string.title_delete_services) );
            progressDialog.show ();
            Log.i ( TAG_re, "onPreExecute" );
        }

        @Override
        protected SoapObject doInBackground(Void... params) {
            Log.i ( TAG_re, "doInBackground" );

            String SOAP_ACTION = "http://farhatty.sd/DeleteService";
            String METHOD_NAME = "DeleteService";
            String NAMESPACE = "http://farhatty.sd/";
            String URL = "http://farhatty.sd/WebService.asmx";

            SoapObject Request = new SoapObject ( NAMESPACE, METHOD_NAME );

            Request.addProperty ( "ID", serv_id);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope ( SoapEnvelope.VER11 );
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject ( Request );

            MarshalFloat md = new MarshalFloat ();
            md.register ( soapEnvelope );


            HttpTransportSE transport = new HttpTransportSE ( URL );

            try {

                transport.call ( SOAP_ACTION, soapEnvelope );
            } catch (Exception e) {
                e.printStackTrace ();
                return null;
            }

            try {
                SoapPrimitive resultStringBooking = (SoapPrimitive) soapEnvelope.getResponse ();
                Log.d ( "Delete Services :", "" + resultStringBooking );
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

                progressDialog.dismiss ();
                dialogFailedRetry ();

            } else {

                progressDialog.dismiss ();
                dialogSucess ();
            }


        }
    }

    public void dialogFailedRetry() {

        AlertDialog.Builder builder = new AlertDialog.Builder ( this , R.style.Theme_AppCompat_DayNight_Dialog);
        builder.setTitle ( R.string.failed );
        builder.setMessage ( getString ( R.string.failed_attemps) );
        builder.setPositiveButton ( R.string.TRY_AGAIN, new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new EditService ().execute ();
            }
        } );
        builder.setNegativeButton ( R.string.Close, new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity ( new Intent ( ViewServicesActivity.this, MainActivity.class ) );
            }
        } );
        builder.show ();
    }


    public void dialogSucess() {
        AlertDialog.Builder builder = new AlertDialog.Builder ( this, R.style.Theme_AppCompat_DayNight_Dialog );
        builder.setTitle ( R.string.congratulation );
        builder.setMessage ( getString ( R.string.data_submitted ) );
        builder.setNegativeButton ( R.string.Close, new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity ( new Intent ( ViewServicesActivity.this, MainActivity.class ) );
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
