package com.nymeria.admin.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nymeria.admin.R;
import com.nymeria.admin.Utiliti.ConnectivityReceiver;
import com.nymeria.admin.activity.MainActivity;
import com.nymeria.admin.adapter.GetServiceAdapter;
import com.nymeria.admin.helper.SQLiteHandler;
import com.nymeria.admin.model.GetService;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;

import static com.android.volley.VolleyLog.TAG;


public class EditServiceFragment extends Fragment implements GetServiceAdapter.GetServiceAdapterListener{

    private RecyclerView recyclerView;
    ProgressDialog progressDialog = null;

    String TAG_re = "Response";
    private ProgressBar pbar;
    CardView cardservices ;
    private SQLiteHandler db;
    int user_cid;

    SoapObject resultString_service;
    SoapPrimitive resultString;
    private List<GetService> getserviceList;
    private GetServiceAdapter mAdapter;

    public EditServiceFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate( R.layout.fragment_edit_service, container, false);

        db = new SQLiteHandler (  getActivity () );
        user_cid = Integer.parseInt(db.getUserDetails ().get ("cid"));
        pbar = (ProgressBar) view.findViewById(R.id.pbar_wedding);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        getserviceList = new ArrayList<> ();
        mAdapter = new GetServiceAdapter (getActivity (),getserviceList );

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager (getActivity ());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator ());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager ( new LinearLayoutManager ( getActivity () ));

        recyclerView.setAdapter(mAdapter);

        checkConnection ();

        return view;
    }



    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {

        if (isConnected) {

            new getService ().execute ();

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
            pbar.setVisibility(View.GONE);

        }

    }


    public void dialogFailedRetry() {
        pbar.setVisibility ( View.GONE );
        AlertDialog.Builder builder = new AlertDialog.Builder ( getActivity () , R.style.Theme_AppCompat_DayNight_Dialog);
        builder.setTitle ( R.string.failed );
        builder.setMessage ( getString ( R.string.failed_attemps) );
        builder.setPositiveButton ( R.string.TRY_AGAIN, new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new getService ().execute ();

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



    @Override
    public void onServiceSelected(GetService getService) {

    }


    private class getService extends AsyncTask <Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            pbar.setVisibility ( View.VISIBLE );
            pbar.setIndeterminate ( false );
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
            pbar.setVisibility ( View.GONE );
            mAdapter.notifyDataSetChanged ();

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
                Log.e ( TAG_re, "Response : " +  resultString_service );

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

                    String service_id = (String) transDetail.getProperty ( "ID" ).toString ();
                    String service_price = (String) transDetail.getProperty ( "Price" ).toString ();
                    String service_title_ar = (String) transDetail.getProperty ( "Title_ar" ).toString ();
                    String service_title_eng = (String) transDetail.getProperty ( "Title_en" ).toString ();
                    String service_descp_ar = (String) transDetail.getProperty ( "Details_ar" ).toString ();
                    String service_descp_eng = (String) transDetail.getProperty ( "Details_en" ).toString ();

                    GetService serv = new GetService ();

                    serv.setId ( service_id);
                    serv.setPrice ( service_price );
                    serv.setTitle_ar ( service_title_ar );
                    serv.setTitle_en ( service_title_eng );
                    serv.setDetails_ar ( service_descp_ar );
                    serv.setDetails_en ( service_descp_eng );

                    getserviceList.add ( serv );

                }
            }
            Log.i ( TAG_re, "Result  " + resultString_service );
        } catch (Exception ex) {
            Log.e ( TAG_re, "Error: " + ex.getMessage () );

        }
    }

    @Override
    public void onResume() {

        super.onResume();
    }
}
