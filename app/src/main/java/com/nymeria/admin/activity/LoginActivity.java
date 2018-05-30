
package com.nymeria.admin.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;


import com.nymeria.admin.R;
import com.nymeria.admin.Utiliti.ConnectivityReceiver;
import com.nymeria.admin.helper.SQLiteHandler;
import com.nymeria.admin.helper.SessionManager;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


public class LoginActivity extends Activity  {
    private static final String TAG = LoginActivity.class.getSimpleName ();

    private ProgressDialog pDialog = null;
    private SessionManager session;
    private SQLiteHandler db;
    ProgressDialog progressDialog = null;

    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;

    private EditText EditTextEmail;
    private EditText EditTextPassword;

    private AppCompatButton appCompatButtonLogin;
    private NestedScrollView nestedScrollView;

    String email;
    String password;

    String TAG_re = "Response";
    SoapObject resultString;
    String user_stat;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_login );


        db = new SQLiteHandler (  this );
        session = new SessionManager ( this);

        if (session.isLoggedIn ()) {

            Log.e ( " :", String.valueOf ( session.isLoggedIn () ) );

            Intent intent = new Intent ( LoginActivity.this, MainActivity.class );
            startActivity ( intent );
            finish ();

        }


        nestedScrollView = (NestedScrollView) findViewById ( R.id.nestedScrollView );
        textInputLayoutEmail = (TextInputLayout) findViewById ( R.id.textInputLayoutEmail );
        textInputLayoutPassword = (TextInputLayout) findViewById ( R.id.textInputLayoutPassword );

        EditTextEmail = (EditText) findViewById ( R.id.textInputEditTextEmail );
        EditTextPassword = (EditText) findViewById ( R.id.textInputEditTextPassword );

        EditTextEmail.addTextChangedListener ( new  CheckoutTextWatcher ( EditTextEmail ) );
        EditTextPassword.addTextChangedListener ( new  CheckoutTextWatcher ( EditTextPassword ) );
        appCompatButtonLogin = (AppCompatButton) findViewById ( R.id.appCompatButtonLogin );


        pDialog = new ProgressDialog ( this );
        pDialog.setProgressStyle ( ProgressDialog.STYLE_SPINNER );
        pDialog.setTitle ( R.string.title_please_wait );
        pDialog.setMessage ( getResources ().getString ( R.string.title_getting_data ) );
        pDialog.setIndeterminate ( false );
        pDialog.setCancelable ( false );


        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(R.string.title_please_wait);
        progressDialog.setMessage(getString(R.string.title_getting_data));


        appCompatButtonLogin.setOnClickListener ( new View.OnClickListener () {

            public void onClick(View view) {
                submit();

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
                case R.id.textInputEditTextEmail:
                    validateEmail();
                    break;
                case R.id.textInputEditTextPassword:
                    validatePassword();
                    break;


            }
        }
    }


    private boolean validateEmail() {
        String str = EditTextEmail.getText().toString().trim();
        if (str.isEmpty()) {
            textInputLayoutEmail.setError(getString(R.string.invalid_name));
            requestFocus(EditTextEmail);
            return false;
        } else {
            textInputLayoutEmail.setErrorEnabled(false);
        }
        return true;
    }


    private boolean validatePassword() {
        String str = EditTextPassword.getText().toString().trim();
        if (str.isEmpty()) {
            textInputLayoutPassword.setError(getString(R.string.invalid_name));
            requestFocus(EditTextPassword);
            return false;
        } else {
            textInputLayoutPassword.setErrorEnabled(false);
        }
        return true;
    }



    private void requestFocus(View view) {
        if (view.requestFocus()) {
            this.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    private void submit() {
        if (!validateEmail ()) {
            Snackbar.make ( this.findViewById ( android.R.id.content ), R.string.invalid_username, Snackbar.LENGTH_SHORT ).show ();
            return;

        }if (!validatePassword ()) {
            Snackbar.make ( this.findViewById ( android.R.id.content ), R.string.invalid_pass, Snackbar.LENGTH_SHORT ).show ();
            return;

        }else {

            email = EditTextEmail.getText ().toString ();
            password = EditTextPassword.getText ().toString ();

            hideKeyboard ();
            new Login ().execute ();

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

    private class Login extends AsyncTask <Void, Void, SoapObject> {
        @Override
        protected void onPreExecute() {

            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setTitle(R.string.title_please_wait);
            progressDialog.setMessage(getString(R.string.title_getting_data));
            progressDialog.show();

            Log.i ( TAG, "onPreExecute" );
        }

        @Override
        protected SoapObject doInBackground(Void... params) {
            Log.i ( TAG, "doInBackground" );

            String SOAP_ACTION = "http://farhatty.sd/login";
            String METHOD_NAME = "login";
            String NAMESPACE = "http://farhatty.sd/";
            String URL = "http://farhatty.sd/WebService.asmx";

            SoapObject Request = new SoapObject ( NAMESPACE, METHOD_NAME );
            Request.addProperty ( "Passwd", password );
            Request.addProperty ( "UserName", email );

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

                resultString = (SoapObject) soapEnvelope.getResponse ();
                Log.d ( "TAG","Login :" +resultString);

            } catch (Exception e) {
                e.printStackTrace ();
                return null;
            }
            for (int i = 0; i < resultString.getPropertyCount (); i++) {

                PropertyInfo pi = new PropertyInfo ();
                resultString.getPropertyInfo ( i, pi );
                Object property = resultString.getProperty ( i );
                if (pi.name.equals ( "loginclass" ) && property instanceof SoapObject) {
                    SoapObject transDetail = (SoapObject) property;

                    String user_id = ((String) transDetail.getProperty ( "userID" ).toString ());
                    String user_name = (String) transDetail.getProperty ( "userName" ).toString ();
                    String user_cid = (String) transDetail.getProperty ( "HallID" ).toString ();
                    String Company_Name = (String) transDetail.getProperty ( "Company_Name" ).toString ();
                    user_stat = (String) transDetail.getProperty ( "Stutas" ).toString ();
                    String Email = (String) transDetail.getProperty ( "Email" ).toString ();
                    String CategoryID = (String) transDetail.getProperty ( "CategoryID" ).toString ();
                    String phone1 = (String) transDetail.getProperty ( "Phone" ).toString ();
                    String phone2 = (String) transDetail.getProperty ( "Phone_2" ).toString ();
                    String supername = (String) transDetail.getProperty ( "Supervisor_Name" ).toString ();
                    String loc = (String) transDetail.getProperty ( "Address" ).toString ();
                    String date = (String) transDetail.getProperty ( "Date" ).toString ();
                    String stats = (String) transDetail.getProperty ( "Stutas" ).toString ();

                    db.addUser(user_name, user_cid, Email,phone1,phone2,supername,stats,loc,CategoryID,Company_Name, user_id, date);
                    Log.d ( "TAG","Login Parameters:" +user_id + "" +user_name);

                }
            }
            return Request;
        }

        @Override
        protected void onPostExecute(SoapObject response) {
            super.onPostExecute ( response );

        if (response == null){

                dialogFailedRetry ();
                progressDialog.dismiss ();

            }else {
                session.setLogin(true);
                progressDialog.dismiss ();
                Intent i = new Intent ( LoginActivity.this, MainActivity.class );
                startActivity ( i );
                finish ();
            }

            }
        }


    public void dialogFailedRetry() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_DayNight_Dialog);
        builder.setTitle(R.string.failed);
        builder.setMessage(getString(R.string.failed_getting));
        builder.setPositiveButton(R.string.TRY_AGAIN, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new Login ().execute ();
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


    public void dialogNotActive() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.not_active);
        builder.setMessage(getString(R.string.user_not_active));

        builder.setNegativeButton(R.string.Close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish ();
            }
        });
        builder.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed ();
        finish ();
    }

}



