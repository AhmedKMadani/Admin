package com.nymeria.admin.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.nymeria.admin.R;
import com.nymeria.admin.Utiliti.ConnectivityReceiver;
import com.nymeria.admin.activity.AddingActivity;
import com.nymeria.admin.activity.MainActivity;
import com.nymeria.admin.activity.TrackBookingActivity;
import com.nymeria.admin.adapter.ServiceUserAdapter;
import com.nymeria.admin.helper.SQLiteHandler;
import com.nymeria.admin.model.ServiceUser;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalFloat;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;

import static com.android.volley.VolleyLog.TAG;


public class AddServiceFragment extends Fragment implements ServiceUserAdapter.ServiceUserAdapterListener{


    private RecyclerView recyclerView;
    private MaterialRippleLayout lyt_add;

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
    ProgressDialog pgetDialog = null;

    String title_ar_ ;
    String title_en_ ;

    String deatils_ar_ ;
    String deatils_en_ ;
    String price_ ;
    String TAG_re = "Response";

    CardView cardservices ;
    private SQLiteHandler db;
    int user_cid ;
    private Double _fees = 0D;

    SoapObject resultString_service;
    SoapPrimitive resultString;
    private List<ServiceUser> serviceusersList;
    private ServiceUserAdapter mAdapter;

    public AddServiceFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

         View view =inflater.inflate( R.layout.fragment_add_service, container, false);
        lyt_add = (MaterialRippleLayout) view.findViewById ( R.id.lyt_add );

        title_ar = (EditText) view.findViewById ( R.id.title_ar );
        layout_title_ar = (TextInputLayout) view.findViewById ( R.id.layout_title_ar );

        title_en = (EditText) view.findViewById ( R.id.title_en );
        layout_title_en = (TextInputLayout) view.findViewById ( R.id.layout_title_en );

        details_ar = (EditText) view.findViewById ( R.id.deatils_ar );
        layout_details_ar = (TextInputLayout) view.findViewById ( R.id.layout_details_ar );

        details_en = (EditText) view.findViewById ( R.id.deatils_en );
        layout_details_en = (TextInputLayout) view.findViewById ( R.id.layout_details_en );

        price = (EditText) view.findViewById ( R.id.price );
        layout_price = (TextInputLayout) view.findViewById ( R.id.layout_price );

        title_ar.addTextChangedListener ( new CheckoutTextWatcher ( title_ar ) );
        title_en.addTextChangedListener ( new CheckoutTextWatcher ( title_en ) );

        details_ar.addTextChangedListener ( new CheckoutTextWatcher ( details_ar ) );
        details_en.addTextChangedListener ( new CheckoutTextWatcher ( details_en ) );

        price.addTextChangedListener ( new CheckoutTextWatcher ( price ) );


        db = new SQLiteHandler (  getActivity () );
        user_cid = Integer.parseInt(db.getUserDetails ().get ("cid"));


        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        serviceusersList = new ArrayList<> ();
        mAdapter = new ServiceUserAdapter (getActivity (),serviceusersList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager (getActivity ());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator ());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager ( new LinearLayoutManager ( getActivity () ));

        recyclerView.setAdapter(mAdapter);

        cardservices = (CardView) view.findViewById ( R.id.cardservices );
        cardservices.setVisibility ( View.GONE );

        progressDialog = new ProgressDialog ( getActivity () );
        progressDialog.setCancelable ( false );
        progressDialog.setTitle ( R.string.add_services);
        progressDialog.setMessage ( getString ( R.string.title_please_wait ) );

        pgetDialog = new ProgressDialog ( getActivity () );
        pgetDialog.setCancelable ( false );
        pgetDialog.setTitle ( R.string.title_please_wait );
        pgetDialog.setMessage ( getString ( R.string.title_getting_services) );

        lyt_add.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {

                submit();
            }
        } );


        checkConnection ();

        return view;
    }

    @Override
    public void onServiceUserSelected(ServiceUser serviceUser) {

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
            getActivity ().getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void hideKeyboard() {
        View view = getActivity ().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity ().getSystemService( Context.INPUT_METHOD_SERVICE);
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
                    .make(getActivity ().findViewById(android.R.id.content), R.string.no_internet_connection, Snackbar.LENGTH_LONG)
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

    private void submit() {

        if (!validateTitle_ar ()) {
            Snackbar.make ( getActivity ().findViewById ( android.R.id.content ), R.string.invalid_title_ar, Snackbar.LENGTH_SHORT ).show ();
            return;

        }if (!validateTitle_en ()) {
            Snackbar.make ( getActivity ().findViewById ( android.R.id.content ), R.string.invalid_title_en, Snackbar.LENGTH_SHORT ).show ();
            return;

        }if (!validatedeatils_ar ()) {
            Snackbar.make ( getActivity ().findViewById ( android.R.id.content ), R.string.invalid_desc_ar, Snackbar.LENGTH_SHORT ).show ();
            return;

        }if (!validatedeatils_en ()) {
            Snackbar.make ( getActivity ().findViewById ( android.R.id.content ), R.string.invalid_desc_en, Snackbar.LENGTH_SHORT ).show ();
            return;

        }if (!validateprice ()) {
            Snackbar.make(getActivity ().findViewById(android.R.id.content), R.string.invalid_price, Snackbar.LENGTH_SHORT).show();
            return;

        }else {

            title_ar_ = title_ar.getText ().toString ();
            title_en_ = title_en.getText ().toString ();
            deatils_ar_ = details_ar.getText ().toString ();
            deatils_en_ = details_en.getText ().toString ();
            price_ = price.getText ().toString ();
            _fees = Double.valueOf(price_);

            hideKeyboard ();

            if (db.getUserDetails ().get ( "cid" ).equals ( "0" )) {

                dialogHallIsnotthere ();

            } else {
                new AddService().execute (  );

            }

        }
    }


    private class AddService extends AsyncTask <Void, Void, SoapObject> {
        @Override
        protected void onPreExecute() {

            progressDialog = new ProgressDialog ( getActivity () );
            progressDialog.setCancelable ( false );
            progressDialog.setTitle ( R.string.title_please_wait );
            progressDialog.setMessage ( getString ( R.string.title_add_services) );
            progressDialog.show ();
            Log.i ( TAG_re, "onPreExecute" );
        }

        @Override
        protected SoapObject doInBackground(Void... params) {
            Log.i ( TAG_re, "doInBackground" );

            String SOAP_ACTION = "http://farhatty.sd/InsertService";
            String METHOD_NAME = "InsertService";
            String NAMESPACE = "http://farhatty.sd/";
            String URL = "http://farhatty.sd/WebService.asmx";

            SoapObject Request = new SoapObject ( NAMESPACE, METHOD_NAME );

            Request.addProperty ( "Title_en", title_en_ );
            Request.addProperty ( "Title_ar", title_ar_ );
            Request.addProperty ( "Details_en", deatils_en_ );
            Request.addProperty ( "Details_ar", deatils_ar_ );
            Request.addProperty ( "Price", _fees );
            Request.addProperty ( "Hall_ID", user_cid );

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
                Log.d ( "Add Services :", "" + resultStringBooking );
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
                dialogSucess ();
                progressDialog.dismiss ();

            }
        }
    }

        public void dialogFailedRetry() {
            AlertDialog.Builder builder = new AlertDialog.Builder ( getActivity (), R.style.Theme_AppCompat_DayNight_Dialog );
            builder.setTitle ( R.string.failed );
            builder.setMessage ( getString ( R.string.failed_attemps ) );
            builder.setPositiveButton ( R.string.TRY_AGAIN, new DialogInterface.OnClickListener () {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    new AddService ().execute ();
                }
            } );
            builder.setNegativeButton ( R.string.Close, new DialogInterface.OnClickListener () {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity ( new Intent ( getActivity (), MainActivity.class ) );
                }
            } );
            builder.show ();
        }


        private class getService extends AsyncTask <Void, Void, Void> {
            @Override
            protected void onPreExecute() {
                pgetDialog = new ProgressDialog ( getActivity () );
                pgetDialog.setCancelable ( false );
                pgetDialog.setTitle ( R.string.title_please_wait );
                pgetDialog.setMessage ( getString ( R.string.title_getting_services) );
                pgetDialog.show ();
                Log.i ( TAG, "onPreExecute" );
            }

            @Override
            protected Void doInBackground(Void... params) {
                Log.i ( TAG, "doInBackground" );
                getData ();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                Log.i ( TAG, "onPostExecute" );
                pgetDialog.dismiss ();
                mAdapter.notifyDataSetChanged ();
                cardservices.setVisibility ( View.VISIBLE );

            }

        }


        public void getData() {
            String SOAP_ACTION = "http://farhatty.sd/GetHallService";
            String METHOD_NAME = "GetHallService";
            String NAMESPACE = "http://farhatty.sd/";
            String URL = "http://farhatty.sd/WebService.asmx";

            try {
                SoapObject Request = new SoapObject ( NAMESPACE, METHOD_NAME );
                Request.addProperty ( "Hall_ID", user_cid );
                SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope ( SoapEnvelope.VER11 );
                soapEnvelope.dotNet = true;
                soapEnvelope.setOutputSoapObject ( Request );
                HttpTransportSE transport = new HttpTransportSE ( URL );

                try {

                    transport.call ( SOAP_ACTION, soapEnvelope );

                } catch (Exception e) {
                    e.printStackTrace ();
                }

                try {

                    resultString_service = (SoapObject) soapEnvelope.getResponse ();
                    Log.i ( TAG, ""+resultString_service);

                } catch (Exception e) {
                    e.printStackTrace ();
                    dialogFailedRetry ();

                }

                for (int i = 0; i < resultString_service.getPropertyCount (); i++) {

                    PropertyInfo pi = new PropertyInfo ();
                    resultString_service.getPropertyInfo ( i, pi );
                    Object property = resultString_service.getProperty ( i );
                    if (pi.name.equals ( "Hall_Services" ) && property instanceof SoapObject) {
                        SoapObject transDetail = (SoapObject) property;

                        String service_price = (String) transDetail.getProperty ( "Price" ).toString ();
                        String service_title_ar = (String) transDetail.getProperty ( "Title_ar" ).toString ();
                        String service_title_eng = (String) transDetail.getProperty ( "Title_en" ).toString ();
                        String service_descp_ar = (String) transDetail.getProperty ( "Details_ar" ).toString ();
                        String service_descp_eng = (String) transDetail.getProperty ( "Details_en" ).toString ();

                        ServiceUser serv = new ServiceUser ();

                        serv.setPrice ( service_price );
                        serv.setTitle_ar ( service_title_ar );
                        serv.setTitle_en ( service_title_eng );
                        serv.setDetails_ar ( service_descp_ar );
                        serv.setDetails_en ( service_descp_eng );

                        serviceusersList.add ( serv );

                    }
                }
                Log.i ( TAG_re, "Result  " + resultString );
            } catch (Exception ex) {
                Log.e ( TAG_re, "Error: " + ex.getMessage () );

            }
        }


    public void dialogSucess() {
        final AlertDialog.Builder builder = new AlertDialog.Builder ( getActivity (), R.style.Theme_AppCompat_DayNight_Dialog );
        builder.setTitle ( R.string.congratulation );
        builder.setMessage ( getString ( R.string.data_submitted ) );
        builder.setNegativeButton ( R.string.Close, new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss ();
                new getService ().execute ();
            }
        } );
        builder.show ();
    }

    @Override
    public void onResume() {

        super.onResume();
    }


            public void dialogHallIsnotthere() {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity (),R.style.Theme_AppCompat_DayNight_Dialog );
                builder.setTitle(R.string.sorry);
                builder.setMessage(getString(R.string.hall_no_ex));
                builder.setNegativeButton(R.string.Close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent (getActivity (), AddingActivity.class));
                    }
                });
                builder.show();
            }


        }
