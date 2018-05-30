package com.nymeria.admin.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nymeria.admin.R;
import com.nymeria.admin.Utiliti.ConnectivityReceiver;
import com.nymeria.admin.Utiliti.MyDividerItemDecoration;
import com.nymeria.admin.activity.MainActivity;
import com.nymeria.admin.adapter.GetHallsAdapter;
import com.nymeria.admin.helper.SQLiteHandler;
import com.nymeria.admin.model.GetHalls;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 4/3/2018.
 */

public class EditDataFragment extends Fragment implements GetHallsAdapter.GetHallsAdapterListener  {

    private RecyclerView recyclerView;
    private List<GetHalls> getHallsList;
    private GetHallsAdapter mAdapter;
    private SearchView searchView;
    private ProgressBar pbar;
    private CoordinatorLayout coordinatorLayout;

    SoapObject result = null;
    int User_id;
    private SQLiteHandler db;


    public EditDataFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        View view = inflater.inflate ( R.layout.fragment_edit_data, container, false );

        db = new SQLiteHandler ( getActivity () );
        User_id = Integer.parseInt(db.getUserDetails ().get ( "uid" ));

        pbar = (ProgressBar) view.findViewById(R.id.pbar_wedding);
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id
                .coordinatorLayout);


        recyclerView = (RecyclerView) view.findViewById( R.id.recycler_view);
        getHallsList = new ArrayList<> ();
        mAdapter = new GetHallsAdapter(getActivity (), getHallsList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager (getActivity ());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator ());
        recyclerView.addItemDecoration(new MyDividerItemDecoration (getActivity (), DividerItemDecoration.VERTICAL, 36));
        recyclerView.setAdapter(mAdapter);

        checkConnection();

        return view;
    }


    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {

        if (isConnected) {

          new getHalls ().execute (  );

        }else{

            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, R.string.no_internet_connection, Snackbar.LENGTH_LONG)
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


    public class getHalls extends AsyncTask<String, Void, SoapObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute ();
            pbar.setVisibility ( View.VISIBLE );
            pbar.setIndeterminate ( false );
        }

        @Override
        protected SoapObject doInBackground(String... params) {
            String SOAP_ACTION = "http://farhatty.sd/GetHall";
            String METHOD_NAME = "GetHall";
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
                if (pi.name.equals ( "GetHallClass" ) && property instanceof SoapObject) {
                    SoapObject transDetail = (SoapObject) property;

                    String id = (String) transDetail.getProperty ( "ID" ).toString ();
                    String name_ar =  (String) transDetail.getProperty ( "Name_ar" ).toString ();
                    String name_en = (String) transDetail.getProperty ( "Name_en" ).toString ();
                    String des_ar = (String)  transDetail.getProperty ( "Des_ar" ).toString ();
                    String des_en = (String) transDetail.getProperty ( "Des_en" ).toString ();
                    String price = (String) transDetail.getProperty ( "Price" ).toString ();
                    String size = (String) transDetail.getProperty ( "Size" ).toString ();
                    String image = (String) transDetail.getProperty ( "Image" ).toString ();
                    String loc_ar = (String)  transDetail.getProperty ( "Des_ar" ).toString ();
                    String loc_en = (String) transDetail.getProperty ( "Des_en" ).toString ();

                    GetHalls gethalls = new GetHalls ();

                    gethalls.setId ( id );
                    gethalls.setName_ar ( name_ar );
                    gethalls.setName_en ( name_en );
                    gethalls.setDes_ar ( des_ar );
                    gethalls.setDes_en ( des_en );
                    gethalls.setPrice ( price );
                    gethalls.setSize ( size );
                    gethalls.setImage ( image );
                    gethalls.setLoc_ar ( loc_ar );
                    gethalls.setLoc_en ( loc_en );

                    getHallsList.add ( gethalls );

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
    public void onGethallSelected(GetHalls getHalls) {

    }


    public void dialogFailedRetry() {
        pbar.setVisibility ( View.GONE );
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity (),R.style.Theme_AppCompat_DayNight_Dialog);
        builder.setTitle(R.string.failed);
        builder.setMessage(getString(R.string.failed_getting));
        builder.setPositiveButton(R.string.TRY_AGAIN, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new getHalls ().execute (  );
            }
        });
        builder.setNegativeButton(R.string.Close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent (getActivity (), MainActivity.class));
            }
        });
        builder.show();
    }

    @Override
    public void onResume() {

        super.onResume();
    }
}

