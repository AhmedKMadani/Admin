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
import android.support.v7.widget.GridLayoutManager;
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
import com.nymeria.admin.adapter.GalleryAdapter;
import com.nymeria.admin.helper.SQLiteHandler;
import com.nymeria.admin.model.Image;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

/**
 * Created by user on 4/6/2018.
 */

public class EditImageFragment extends Fragment {

    private ArrayList<Image> imageslist;
    private ProgressBar pbar;
    private GalleryAdapter mAdapter;
    private RecyclerView recyclerView;
    int hall_id;
    private SQLiteHandler db;
    SoapObject result = null;
    private CoordinatorLayout coordinatorLayout;

    public EditImageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder ().permitAll ().build ();
        StrictMode.setThreadPolicy ( policy );
        View view = inflater.inflate ( R.layout.fragment_gallery, container, false );

        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id
                .coordinatorLayout);
        recyclerView = (RecyclerView) view.findViewById ( R.id.recycler_view );

        pbar = (ProgressBar) view.findViewById(R.id.pbar_wedding);
        imageslist = new ArrayList <> ();
        mAdapter = new GalleryAdapter ( getActivity (), imageslist );

        db = new SQLiteHandler ( getActivity () );
        hall_id = Integer.parseInt(db.getUserDetails ().get ( "cid" ));

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager ( getActivity (), 2 );
        recyclerView.setLayoutManager ( mLayoutManager );
        recyclerView.setItemAnimator ( new DefaultItemAnimator () );
        recyclerView.setAdapter ( mAdapter );

        checkConnection();

        return view;
    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {

        if (isConnected) {

            new getImages ().execute (  );

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


    public class getImages extends AsyncTask<String, Void, SoapObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute ();
            pbar.setVisibility ( View.VISIBLE );
            pbar.setIndeterminate ( false );
        }


        @Override
        protected SoapObject doInBackground(String... params) {
            String SOAP_ACTION = "http://farhatty.sd/Hall_Images";
            String METHOD_NAME = "Hall_Images";
            String NAMESPACE = "http://farhatty.sd/";
            String URL = "http://farhatty.sd/WebService.asmx";

            SoapObject Request = new SoapObject ( NAMESPACE, METHOD_NAME );
            Request.addProperty ( "Hall_ID", hall_id );

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
                Log.e ( "Response Images ",result.toString () );
            } catch (SoapFault soapFault) {
                soapFault.printStackTrace ();

                return null;

            }

            for (int i = 0; i < result.getPropertyCount (); i++) {

                PropertyInfo pi = new PropertyInfo ();
                result.getPropertyInfo ( i, pi );
                Object property = result.getProperty ( i );
                if (pi.name.equals ( "Hall_ImagesClass" ) && property instanceof SoapObject) {
                    SoapObject transDetail = (SoapObject) property;

                    String id = (String) transDetail.getProperty ( "ID" ).toString ();
                    String name =  (String) transDetail.getProperty ( "image" ).toString ();

                    Image image = new Image();

                    image.setId ( id );
                    image.setName (name );

                    imageslist.add ( image );

                }
            }
            return Request;
        }

        @Override
        protected void onPostExecute(SoapObject response) {
            super.onPostExecute ( response );

            if (response == null) {

                pbar.setVisibility ( View.GONE );
                dialogFailedRetry ();

            } else {

                pbar.setVisibility ( View.GONE );
                mAdapter.notifyDataSetChanged ();

            }
        }

    }

    public void dialogFailedRetry() {

        pbar.setVisibility ( View.GONE );
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity (), R.style.Theme_AppCompat_DayNight_Dialog);
        builder.setTitle(R.string.failed);
        builder.setMessage(getString(R.string.failed_attemps));
        builder.setPositiveButton(R.string.TRY_AGAIN, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new getImages ().execute (  );
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
