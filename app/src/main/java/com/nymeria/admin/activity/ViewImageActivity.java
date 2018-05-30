package com.nymeria.admin.activity;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.nymeria.admin.R;
import com.nymeria.admin.Utiliti.ConnectivityReceiver;
import com.nymeria.admin.Utiliti.Utility;
import com.nymeria.admin.helper.SQLiteHandler;
import com.nymeria.admin.helper.UploadImageApacheHttp;
import com.nymeria.admin.model.ServiceUser;
import com.squareup.picasso.Picasso;

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
import java.util.List;

import static com.android.volley.VolleyLog.TAG;

/**
 * Created by user on 4/3/2018.
 */

public class ViewImageActivity  extends AppCompatActivity {

    String view_image_name ;
    String view_image_id ;

    int image_id;
    private AppCompatButton appCompatButtonpick;
    private AppCompatButton appCompatButtonLogin;
    private NestedScrollView nestedScrollView;

    private SQLiteHandler db;
    int User_id;
    int CatID;
    ImageView imageper;

    Bitmap bitmap;
    ProgressDialog progressDialog = null;
    int PICK_IMAGE_REQUEST = 111;
    private static final int SELECT_PICTURE = 100;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String userChoosenTask;

    SoapObject resultString_service;
    SoapPrimitive resultString;
    private List<ServiceUser> serviceusersList;
    private Toolbar toolbar;

    private MaterialRippleLayout lyt_edit;
    private MaterialRippleLayout lyt_delet;
    private MaterialRippleLayout lyt_upload;

    String image_name;
    Uri imageUri;
    String filename;
    String uplaodphp = "/upload.php";
    String CatId;

    String dir_perview;
    String dir_gallery;
    public static final String UPLOAD_URL_Perview = "http://farhatty.com/Uploads/";
    public static final String UPLOAD_URL_Gallery = "http://farhatty.com/Uploads/";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.image_fullscreen_preview );

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.edit_image);

        checkConnection ();

        view_image_name = getIntent ().getStringExtra ( "image_name" );
        view_image_id = getIntent ().getStringExtra ( "image_id" );

        image_id = Integer.parseInt ( view_image_id );
        imageper = (ImageView) findViewById ( R.id.image_preview );

        lyt_edit = (MaterialRippleLayout) findViewById ( R.id.lyt_edit );
        lyt_delet = (MaterialRippleLayout) findViewById ( R.id.lyt_delet );
        lyt_upload = (MaterialRippleLayout) findViewById ( R.id.lyt_upload );

        lyt_upload.setVisibility (View.GONE );
        Picasso.with(this)
                .load("http://farhatty.com/" + view_image_name)
                .into(imageper);

        db = new SQLiteHandler (  this );
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


        lyt_upload.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {
               EditImage();

            }
        } );


        lyt_edit.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                selectImage ();

            }
        } );


        lyt_delet.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                new DeletImage ().execute ();
            }
        } );



    }



    private void EditImage() {
        new  EditImage ().execute (  );
        UploadImageApacheHttp uploadTask = new UploadImageApacheHttp();
        uploadTask.doFileUpload(UPLOAD_URL_Gallery +dir_gallery+uplaodphp, filename , bitmap, handler);

    }


    Handler handler = handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.i("Response", "Handler " + msg.what);
            if(msg.what == 1){
                Log.i("Response", "Handler " + msg.what);
            }else{
                Log.i("Response", "Handler " + msg.what);            }
        }

    };


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
        final CharSequence[] items =  { getResources ().getString ( R.string.take_photo ), getResources ().getString ( R.string.chosse ),
                getResources ().getString ( R.string.CANCEL ) };


        AlertDialog.Builder builder = new AlertDialog.Builder(ViewImageActivity.this,R.style.Theme_AppCompat_DayNight);
        builder.setTitle(R.string.add_image);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(ViewImageActivity.this);

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

    private void galleryIntent()
    {
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
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

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

        lyt_upload.setVisibility (View.VISIBLE );
        lyt_edit.setVisibility (View.GONE );

        imageper.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

         if (data != null) {
            try {
                imageUri = data.getData();
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        lyt_upload.setVisibility (View.VISIBLE );
        lyt_edit.setVisibility (View.GONE );
        image_name = String.valueOf ( imageUri );
        filename=image_name.substring(image_name.lastIndexOf("/")+1);
        Log.d ( "Convert :" , ""+image_name+ "" + filename);
        imageper.setImageBitmap(bitmap);
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

    private class EditImage extends AsyncTask<Void, Void, SoapObject> {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog ( ViewImageActivity.this);
            progressDialog.setCancelable ( false );
            progressDialog.setTitle ( R.string.title_please_wait );
            progressDialog.setMessage ( getString ( R.string.title_edit) );
            progressDialog.show ();
            Log.i ( TAG, "onPreExecute" );
        }

        @Override
        protected SoapObject doInBackground(Void... params) {
            Log.i ( TAG, "doInBackground" );

            String SOAP_ACTION = "http://farhatty.sd/EditImage";
            String METHOD_NAME = "EditImage";
            String NAMESPACE = "http://farhatty.sd/";
            String URL = "http://farhatty.sd/WebService.asmx";

            SoapObject Request = new SoapObject ( NAMESPACE, METHOD_NAME );
            Request.addProperty ( "ID", image_id );
            Request.addProperty ( "image", "../Uploads/"+dir_gallery+"/"+filename+".jpg" );

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

                resultString = (SoapPrimitive) soapEnvelope.getResponse ();
                Log.d ( "TAG","Edit Image:" +resultString);

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

                Log.d ( "Resposne Edit Image:", response.toString () );
                progressDialog.dismiss ();
                dialogFailedRetryEdit ();


            }else {

                progressDialog.dismiss ();
                dialogSucess ();
            }

        }
    }



    private class DeletImage extends AsyncTask<Void, Void, SoapObject> {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog ( ViewImageActivity.this);
            progressDialog.setCancelable ( false );
            progressDialog.setTitle ( R.string.title_please_wait );
            progressDialog.setMessage ( getString ( R.string.title_delete) );
            progressDialog.show ();
            Log.i ( TAG, "onPreExecute" );
        }

        @Override
        protected SoapObject doInBackground(Void... params) {
            Log.i ( TAG, "doInBackground" );

            String SOAP_ACTION = "http://farhatty.sd/DeleteImage";
            String METHOD_NAME = "DeleteImage";
            String NAMESPACE = "http://farhatty.sd/";
            String URL = "http://farhatty.sd/WebService.asmx";

            SoapObject Request = new SoapObject ( NAMESPACE, METHOD_NAME );

            Request.addProperty ( "ID", image_id);
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
                Log.d ( "Delete Image :", "" + resultStringBooking );
            } catch (Exception e) {
                e.printStackTrace ();
                return null;
            }
            return Request;
        }

        @Override
        protected void onPostExecute(SoapObject result) {
            Log.i ( TAG, "onPostExecute" );

            if (result == null) {

                progressDialog.dismiss ();
                dialogFailedRetryDelete ();

            } else {

                progressDialog.dismiss ();
                dialogSucess ();

            }


        }
    }



    public void dialogFailedRetryEdit() {

        AlertDialog.Builder builder = new AlertDialog.Builder ( this,R.style.Theme_AppCompat_DayNight_Dialog );
        builder.setTitle ( R.string.failed );
        builder.setMessage ( getString ( R.string.failed_attemps ) );
        builder.setPositiveButton ( R.string.TRY_AGAIN, new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new EditImage ().execute (  );
            }
        } );
        builder.setNegativeButton ( R.string.Close, new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity ( new Intent ( ViewImageActivity.this, MainActivity.class ) );
            }
        } );
        builder.show ();
    }



    public void dialogFailedRetryDelete() {

        AlertDialog.Builder builder = new AlertDialog.Builder ( this,R.style.Theme_AppCompat_DayNight_Dialog );
        builder.setTitle ( R.string.failed );
        builder.setMessage ( getString ( R.string.failed_attemps ) );
        builder.setPositiveButton ( R.string.TRY_AGAIN, new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new DeletImage ().execute (  );
            }
        } );
        builder.setNegativeButton ( R.string.Close, new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity ( new Intent ( ViewImageActivity.this, MainActivity.class ) );
            }
        } );
        builder.show ();
    }

    public void dialogSucess() {
        AlertDialog.Builder builder = new AlertDialog.Builder (this, R.style.Theme_AppCompat_DayNight_Dialog );
        builder.setTitle ( R.string.congratulation );
        builder.setMessage ( getString ( R.string.data_submitted ) );
        builder.setNegativeButton ( R.string.Close, new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity ( new Intent ( ViewImageActivity.this, MainActivity.class ) );
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
