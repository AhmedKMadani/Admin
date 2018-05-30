package com.nymeria.admin.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.nymeria.admin.R;
import com.nymeria.admin.Utiliti.ConnectivityReceiver;
import com.nymeria.admin.Utiliti.Utility;
import com.nymeria.admin.activity.AddLocationActivity;
import com.nymeria.admin.activity.AddingActivity;
import com.nymeria.admin.activity.MainActivity;
import com.nymeria.admin.helper.SQLiteHandler;
import com.nymeria.admin.helper.UploadImageApacheHttp;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalDate;
import org.ksoap2.serialization.MarshalFloat;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.android.volley.VolleyLog.TAG;


public class AddImageFragment extends Fragment{


    private AppCompatButton appCompatButtonpick;
    private SQLiteHandler db;
    int user_cid;
    String CatId;
    SoapPrimitive resultString;
    ImageView imageper;
    private MaterialRippleLayout lyt_add_perview;
    private MaterialRippleLayout lyt_add_gallery;
    int PICK_IMAGE_REQUEST = 111;
    private static final int SELECT_PICTURE = 100;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String userChoosenTask;
    private NestedScrollView nestedScrollView;
    Bitmap bitmap=null;
    ProgressDialog progressDialog = null;
    String image_name;
    Uri imageUri;
    String filename;
    String uplaodphp = "/upload.php";

    String dir_perview;
    String dir_gallery;
    public static final String UPLOAD_URL_Perview = "http://farhatty.com/Uploads/";
    public static final String UPLOAD_URL_Gallery = "http://farhatty.com/Uploads/";

    public AddImageFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate( R.layout.fragment_add_images, container, false);
        hideKeyboard();

        appCompatButtonpick = (AppCompatButton) view.findViewById ( R.id.appCompatButtonpick );
        imageper = (ImageView) view.findViewById ( R.id.thumbnail );


        lyt_add_gallery = (MaterialRippleLayout) view.findViewById ( R.id.lyt_add_gallery );
        nestedScrollView = (NestedScrollView) view.findViewById ( R.id.nestedScrollView );

        progressDialog = new ProgressDialog ( getActivity ());
        progressDialog.setCancelable ( false );
        progressDialog.setTitle ( R.string.title_please_wait );
        progressDialog.setMessage ( getString ( R.string.title_edit) );

        db = new SQLiteHandler (  getActivity () );
        user_cid = Integer.parseInt(db.getUserDetails ().get ("cid"));
        CatId = db.getUserDetails ().get ( "CatID" );

        if(CatId.equals("2")) {

            dir_perview = "HallShow";
            dir_gallery = "Halls";

        }else if(CatId.equals("1002")) {

            dir_perview = "SingerShow";
            dir_gallery = "Singer";

        }else if(CatId.equals("1003")) {

            dir_perview = "HotelsShow";
            dir_gallery = "Hotels";

        }else if(CatId.equals("1004")) {

            dir_perview = "PhotographyShow";
            dir_gallery = "Photography";

        }else if(CatId.equals("1005")) {

            dir_perview = "ChefShow";
            dir_gallery = "Chef";

        }else if(CatId.equals("1006")) {

            dir_perview = "CarsShow";
            dir_gallery = "Cars";

        }else if(CatId.equals("1007")) {

            dir_perview = "CoiffeurShow";
            dir_gallery = "Coiffeur";
        }

        appCompatButtonpick.setOnClickListener ( new View.OnClickListener () {

            public void onClick(View view) {
                selectImage();
            }



        } );


        lyt_add_gallery.setOnClickListener ( new View.OnClickListener () {

            public void onClick(View view) {
                SubmitData();
            }



        } );

        checkConnection ();

        return view;

    }



    Handler handler = handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.i("Response", "Handler " + msg.what);
            if(msg.what == 1){
                progressDialog.dismiss ();
                dialogSucess ();
                Log.i("Response", "Handler " + msg.what);
            }else{
                dialogFailedRetry ();
                progressDialog.dismiss ();
                Log.i("Response", "Handler " + msg.what);            }
        }

    };





    private void uploadImageGallery() {
        CheckImage();
        UploadImageApacheHttp uploadTask = new UploadImageApacheHttp();
        uploadTask.doFileUpload(UPLOAD_URL_Gallery+dir_gallery+uplaodphp, filename , bitmap, handler);

    }


    private void SubmitData(){

        if (db.getUserDetails ().get ( "cid" ).equals ( "0" )) {

            dialogHallIsnotthere ();

        }else {

            uploadImageGallery ();
        }


    }


    private void CheckImage() {
        if(imageper!=null){

            new UplaodGallery().execute (  );

        }else {

            Snackbar.make ( nestedScrollView , R.string.add_image_please, Snackbar.LENGTH_SHORT ).show ();

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {

                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = { getResources ().getString ( R.string.take_photo ), getResources ().getString ( R.string.chosse ),
                getResources ().getString ( R.string.CANCEL ) };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity (),R.style.Theme_AppCompat_DayNight);
        builder.setTitle(R.string.add_image);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(getActivity ());

                if (items[item].equals(getResources ().getString ( R.string.take_photo ))) {
                    userChoosenTask ="Take Photo";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals(getResources ().getString ( R.string.chosse ))) {
                    userChoosenTask ="Choose from Library";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals( getResources ().getString ( R.string.CANCEL ))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    private void cameraIntent()
    {
        Intent intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        bitmap = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File( Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");


        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        imageper.setImageBitmap(bitmap);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        if (data != null) {
            try {
                imageUri = data.getData();
                bitmap = MediaStore.Images.Media.getBitmap(getActivity ().getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        image_name = String.valueOf ( imageUri );
        filename=image_name.substring(image_name.lastIndexOf("/")+1);
        Log.d ( "Convert :" , ""+image_name+ "" + filename);
        imageper.setImageBitmap(bitmap);
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



    public void dialogFailedRetry() {
        progressDialog.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity ());
        builder.setTitle(R.string.failed);
        builder.setMessage(getString(R.string.failed_checkout));
        builder.setPositiveButton(R.string.TRY_AGAIN, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                uploadImageGallery ();
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




    private class UplaodGallery extends AsyncTask<Void, Void, SoapObject> {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog ( getActivity ());
            progressDialog.setCancelable ( false );
            progressDialog.setTitle ( R.string.title_please_wait );
            progressDialog.setMessage ( getString ( R.string.title_edit) );
            progressDialog.show ();
            Log.i ( TAG, "onPreExecute" );
        }

        @Override
        protected SoapObject doInBackground(Void... params) {
            Log.i ( TAG, "doInBackground" );

            String SOAP_ACTION = "http://farhatty.sd/InsertImage";
            String METHOD_NAME = "InsertImage";
            String NAMESPACE = "http://farhatty.sd/";
            String URL = "http://farhatty.sd/WebService.asmx";

            SoapObject Request = new SoapObject ( NAMESPACE, METHOD_NAME );
            Request.addProperty ( "Hall_ID", user_cid );
            Request.addProperty ( "image", "../Uploads/"+dir_gallery+"/"+filename+".jpg");

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
                Log.d ( "TAG","Add Image :" +resultString);

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
                Log.d ( "Resposne Add Image:", response.toString () );

            }else {


            }

        }
    }


    public void dialogSucess() {
        final AlertDialog.Builder builder = new AlertDialog.Builder ( getActivity (), R.style.Theme_AppCompat_DayNight_Dialog );
        builder.setTitle ( R.string.congratulation );
        builder.setMessage ( getString ( R.string.data_submitted_image ) );
        builder.setPositiveButton  ( R.string.YES, new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss ();

            }
        } );

        builder.setNegativeButton ( R.string.NO, new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity ( new Intent ( getActivity (), MainActivity.class ) );
            }
        } );
        builder.show ();
    }


    private void hideKeyboard() {
        View view = getActivity ().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity ().getSystemService( Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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


