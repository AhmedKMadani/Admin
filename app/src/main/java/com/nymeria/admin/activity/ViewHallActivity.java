package com.nymeria.admin.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.nymeria.admin.R;
import com.nymeria.admin.Utiliti.ConnectivityReceiver;
import com.nymeria.admin.Utiliti.Utility;
import com.nymeria.admin.adapter.ServiceUserAdapter;
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

public class ViewHallActivity  extends AppCompatActivity {

    String view_hall_name_ar ;
    String view_hall_name_en ;
    String view_hall_deatils_ar ;
    String view_hall_deatils_en ;
    String  view_hall_loc_ar;
    String  view_hall_loc_en ;
    String  view_hall_price_ ;
    String  view_hall_size_ ;
    String  view_hall_image_ ;
    String  view_hall_id_ ;


    int hall_id;
    private Double _fees = 0D;

    private TextInputLayout textInputLayoutName_en;
    private TextInputLayout textInputLayoutName_ar;

    private EditText EditTextName_en;
    private EditText EditTextName_ar;


    private TextInputLayout textInputLayoutdescription_en;
    private TextInputLayout textInputLayoutdescription_ar;

    private EditText EditTextdescription_en;
    private EditText EditTextdescription_ar;


    private TextInputLayout textInputLayoutloc_ar;
    private TextInputLayout textInputLayoutloc_en;

    private EditText EditTextloaction_ar;
    private EditText EditTextlocation_en;

    private TextInputLayout textInputLayoutsize;
    private TextInputLayout textInputLayoutprice;

    private EditText EditTextsize;
    private EditText EditTextprice;

    private AppCompatButton appCompatButtonpick;
    private AppCompatButton appCompatButtonLogin;
    private NestedScrollView nestedScrollView;
    private SQLiteHandler db;
    int User_id;
    int CatID;
    ImageView imageper;

    String TextName_ar;
    String TextName_en;
    String Textdescp_ar;
    String Textdescp_en;
    String Textlocation_ar;
    String Textlocation_en;
    String Textsize;
    String Textprice;
    Bitmap bitmap;

    ProgressDialog progressDialog = null;
    ProgressDialog progressD = null;
    int PICK_IMAGE_REQUEST = 111;
    private static final int SELECT_PICTURE = 100;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String userChoosenTask;

    SoapObject resultString_service;
    SoapPrimitive resultString;
    private List<ServiceUser> serviceusersList;
    private ServiceUserAdapter mAdapter;
    private Toolbar toolbar;

    private MaterialRippleLayout lyt_edit;
    private MaterialRippleLayout lyt_delet;

    String image_name;
    Uri imageUri;
    String filename;
    String uploadname ;
    String uplaodphp = "/upload.php";

    String dir_perview;
    String dir_gallery;
    public static final String UPLOAD_URL_Perview = "http://farhatty.com/Uploads/";
    public static final String UPLOAD_URL_Gallery = "http://farhatty.com/Uploads/";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.fragment_view_hall );


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.service_text_edit_hall);

        db = new SQLiteHandler ( this );
        User_id = Integer.parseInt(db.getUserDetails ().get ( "uid" ));
        CatID = Integer.parseInt(db.getUserDetails ().get ( "CatID" ));


        progressDialog = new ProgressDialog ( this);
        progressDialog.setCancelable ( false );
        progressDialog.setTitle ( R.string.title_please_wait );
        progressDialog.setMessage ( getString ( R.string.title_edit) );


        progressD = new ProgressDialog ( this);
        progressD.setCancelable ( false );
        progressD.setTitle ( R.string.title_please_wait );
        progressD.setMessage ( getString ( R.string.title_edit) );

        appCompatButtonpick = (AppCompatButton) findViewById ( R.id.appCompatButtonpick );
        imageper = (ImageView) findViewById ( R.id.thumbnail );

        view_hall_name_ar = getIntent ().getStringExtra ( "hall_name_ar" );
        view_hall_name_en = getIntent ().getStringExtra ( "hall_name_en" );
        view_hall_deatils_ar = getIntent ().getStringExtra ( "hall_deatils_ar" );
        view_hall_deatils_en = getIntent ().getStringExtra ( "hall_deatils_en" );
        view_hall_loc_ar = getIntent ().getStringExtra ( "hall_loc_ar" );
        view_hall_loc_en = getIntent ().getStringExtra ( "hall_loc_en" );
        view_hall_price_ = getIntent ().getStringExtra ( "hall_price_" );
        view_hall_size_ = getIntent ().getStringExtra ( "hall_size_" );
        view_hall_image_ = getIntent ().getStringExtra ( "hall_image_" );
        view_hall_id_ = getIntent ().getStringExtra ( "hall_id_" );


        hall_id = Integer.parseInt ( view_hall_id_ );

        textInputLayoutName_ar = (TextInputLayout) findViewById ( R.id.textInputLayoutName_ar );
        textInputLayoutName_en = (TextInputLayout) findViewById ( R.id.textInputLayoutName_en);

        EditTextName_ar = (EditText) findViewById ( R.id.textInputEditTextName_ar);
        EditTextName_en = (EditText) findViewById ( R.id.textInputEditTextName_en );

        textInputLayoutdescription_ar = (TextInputLayout) findViewById ( R.id.textInputLayoutdescription_ar );
        textInputLayoutdescription_en = (TextInputLayout) findViewById ( R.id.textInputLayoutdescription_en);

        EditTextdescription_ar = (EditText) findViewById ( R.id.textInputEditTextdescription_ar);
        EditTextdescription_en = (EditText) findViewById ( R.id.textInputEditTextdescription_en );


        textInputLayoutloc_ar = (TextInputLayout) findViewById ( R.id.textInputLayoutloc_ar );
        textInputLayoutloc_en = (TextInputLayout) findViewById ( R.id.textInputLayoutLoc_en);

        EditTextloaction_ar = (EditText) findViewById ( R.id.textInputEditTextloc_ar);
        EditTextlocation_en = (EditText) findViewById ( R.id.textInputEditTextLoc_en);

        textInputLayoutsize = (TextInputLayout) findViewById ( R.id.textInputLayoutsize );
        textInputLayoutprice = (TextInputLayout) findViewById ( R.id.textInputLayoutprice);

        EditTextsize = (EditText) findViewById ( R.id.textInputEditTextsize);
        EditTextprice = (EditText) findViewById ( R.id.textInputEditTextprice);


        lyt_edit = (MaterialRippleLayout) findViewById ( R.id.lyt_edit );
        lyt_delet = (MaterialRippleLayout) findViewById ( R.id.lyt_delet );

          EditTextName_ar.setText (view_hall_name_ar);
          EditTextName_en.setText (view_hall_name_en);
          EditTextdescription_ar.setText ( Html.fromHtml (view_hall_deatils_ar));
          EditTextdescription_en.setText (Html.fromHtml (view_hall_deatils_en));
          EditTextloaction_ar.setText (Html.fromHtml (view_hall_loc_ar));
          EditTextlocation_en.setText (Html.fromHtml (view_hall_loc_en));
          EditTextsize.setText (view_hall_size_);
          EditTextprice.setText (view_hall_price_);




        Picasso.with(this).load("http://farhatty.com/" + view_hall_image_)
                .into(imageper);

        appCompatButtonpick.setOnClickListener ( new View.OnClickListener () {

            public void onClick(View view) {
                selectImage();
            }



        } );


        if(String.valueOf ( CatID ).equals("2")) {

            dir_perview = "HallShow";
            dir_gallery = "Halls";

        }else if(String.valueOf ( CatID ).equals("1002")) {

            dir_perview = "SingerShow";
            dir_gallery = "Singer";

        }else if(String.valueOf ( CatID ).equals("1003")) {

            dir_perview = "HotelsShow";
            dir_gallery = "Hotels";

        }else if(String.valueOf ( CatID ).equals("1004")) {

            dir_perview = "PhotographyShow";
            dir_gallery = "Photography";

        }else if(String.valueOf ( CatID ).equals("1005")) {

            dir_perview = "ChefShow";
            dir_gallery = "Chef";

        }else if(String.valueOf ( CatID ).equals("1006")) {

            dir_perview = "CarsShow";
            dir_gallery = "Cars";

        }else if(String.valueOf ( CatID ).equals("1007")) {

            dir_perview = "CoiffeurShow";
            dir_gallery = "Coiffeur";
        }


        if(String.valueOf ( CatID ).equals ( "2" )){

            EditTextsize.setVisibility ( View.VISIBLE );
            EditTextprice.setVisibility ( View.VISIBLE );

            textInputLayoutsize.setVisibility ( View.VISIBLE );
            textInputLayoutprice.setVisibility ( View.VISIBLE );


        }else {

            EditTextsize.setVisibility ( View.GONE );
            EditTextprice.setVisibility ( View.GONE );

            textInputLayoutsize.setVisibility ( View.GONE );
            textInputLayoutprice.setVisibility ( View.GONE );
        }

        checkConnection ();


        lyt_edit.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                uploadImagePerview ();
            }
        } );

        lyt_delet.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                submitDelete();
            }
        } );

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
                progressDialog.dismiss ();
                dialogFailedRetryEdit ();
                Log.i("Response", "Handler " + msg.what);            }
        }

    };


    private void uploadImagePerview() {

        if(filename!=null) {
          uploadname = "../Uploads/"+dir_perview+"/"+filename+".jpg";

            submitEdit ();
            Log.d ( "Dir name " , dir_perview);
            UploadImageApacheHttp uploadTask = new UploadImageApacheHttp();
            uploadTask.doFileUpload(UPLOAD_URL_Perview+dir_perview+uplaodphp, filename , bitmap, handler);

        }else {
            uploadname = view_hall_image_;
            submitOtherEdit ();
        }


    }


    private void selectImage() {
        final CharSequence[] items =  { getResources ().getString ( R.string.take_photo ), getResources ().getString ( R.string.chosse ),
                getResources ().getString ( R.string.CANCEL ) };


        AlertDialog.Builder builder = new AlertDialog.Builder(this ,R.style.Theme_AppCompat_DayNight);
        builder.setTitle(R.string.add_image);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= Utility.checkPermission(ViewHallActivity.this);

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
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        image_name = String.valueOf ( imageUri );
        filename=image_name.substring(image_name.lastIndexOf("/")+1);
        Log.d ( "Convert :" , ""+image_name+ "" + filename);
        imageper.setImageBitmap(bitmap);
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
                case R.id.textInputEditTextName_ar:
                    validateName_ar();
                    break;
                case R.id.textInputEditTextName_en:
                    validateName_en();
                    break;
                case R.id.textInputEditTextdescription_ar:
                    validatedescription_ar();
                    break;
                case R.id.textInputEditTextdescription_en:
                    validatedescription_en();
                    break;

                case R.id.textInputEditTextloc_ar:
                    validateTextloc_ar();
                    break;
                case R.id.textInputEditTextLoc_en:
                    validateTextloc_en();
                    break;


            }
        }
    }


    private boolean validateName_ar() {
        String str = EditTextName_ar.getText().toString().trim();
        if (str.isEmpty()) {
            textInputLayoutName_ar.setError(getString(R.string.invalid_name));
            requestFocus(EditTextName_ar);
            return false;
        } else {
            textInputLayoutName_ar.setErrorEnabled(false);
        }
        return true;
    }


    private boolean validateName_en() {
        String str = EditTextName_en.getText().toString().trim();
        if (str.isEmpty()) {
            textInputLayoutName_en.setError(getString(R.string.invalid_name));
            requestFocus(EditTextName_en);
            return false;
        } else {
            textInputLayoutName_en.setErrorEnabled(false);
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
            textInputLayoutdescription_en.setError(getString(R.string.invalid_locat));
            requestFocus(EditTextdescription_en);
            return false;
        } else {
            textInputLayoutdescription_en.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateTextloc_ar() {
        String str = EditTextloaction_ar.getText().toString().trim();
        if (str.isEmpty()) {
            textInputLayoutloc_ar.setError(getString(R.string.invalid_name));
            requestFocus(EditTextloaction_ar);
            return false;
        } else {
            textInputLayoutloc_ar.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateTextloc_en() {
        String str = EditTextlocation_en.getText().toString().trim();
        if (str.isEmpty()) {
            textInputLayoutloc_en.setError(getString(R.string.invalid_locat));
            requestFocus(EditTextlocation_en);
            return false;
        } else {
            textInputLayoutloc_en.setErrorEnabled(false);
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

        if (!validateName_en ()) {
            Snackbar.make (this.findViewById ( android.R.id.content ), R.string.invalid_name, Snackbar.LENGTH_SHORT ).show ();
            return;

        }
        if (!validateName_ar ()) {
            Snackbar.make (this.findViewById ( android.R.id.content ), R.string.invalid_name, Snackbar.LENGTH_SHORT ).show ();
            return;

        }
        if (!validatedescription_en ()) {
            Snackbar.make (this.findViewById ( android.R.id.content ), R.string.invalid_desc_en, Snackbar.LENGTH_SHORT ).show ();
            return;

        }
        if (!validatedescription_ar ()) {
            Snackbar.make (this.findViewById ( android.R.id.content ), R.string.invalid_desc_ar, Snackbar.LENGTH_SHORT ).show ();
            return;

        }
        if (!validateTextloc_ar ()) {
            Snackbar.make (this.findViewById ( android.R.id.content ), R.string.invalid_lat, Snackbar.LENGTH_SHORT ).show ();
            return;

        }
        if (!validateTextloc_en ()) {
            Snackbar.make (this.findViewById ( android.R.id.content ), R.string.invalid_long, Snackbar.LENGTH_SHORT ).show ();
            return;

        } else {


            TextName_ar = EditTextName_ar.getText ().toString ();
            TextName_en = EditTextName_en.getText ().toString ();
            Textdescp_ar = EditTextdescription_ar.getText ().toString ();
            Textdescp_en = EditTextdescription_en.getText ().toString ();
            Textlocation_ar = EditTextloaction_ar.getText ().toString ();
            Textlocation_en = EditTextlocation_en.getText ().toString ();
            Textsize = EditTextsize.getText ().toString ();
            Textprice = EditTextprice.getText ().toString ();

             hideKeyboard ();
             new Edithall ().execute ();

            }

        }




    private void submitOtherEdit() {

        if (!validateName_en ()) {
            Snackbar.make (this.findViewById ( android.R.id.content ), R.string.invalid_name, Snackbar.LENGTH_SHORT ).show ();
            return;

        }
        if (!validateName_ar ()) {
            Snackbar.make (this.findViewById ( android.R.id.content ), R.string.invalid_name, Snackbar.LENGTH_SHORT ).show ();
            return;

        }
        if (!validatedescription_en ()) {
            Snackbar.make (this.findViewById ( android.R.id.content ), R.string.invalid_desc_en, Snackbar.LENGTH_SHORT ).show ();
            return;

        }
        if (!validatedescription_ar ()) {
            Snackbar.make (this.findViewById ( android.R.id.content ), R.string.invalid_desc_ar, Snackbar.LENGTH_SHORT ).show ();
            return;

        }
        if (!validateTextloc_ar ()) {
            Snackbar.make (this.findViewById ( android.R.id.content ), R.string.invalid_lat, Snackbar.LENGTH_SHORT ).show ();
            return;

        }
        if (!validateTextloc_en ()) {
            Snackbar.make (this.findViewById ( android.R.id.content ), R.string.invalid_long, Snackbar.LENGTH_SHORT ).show ();
            return;

        } else {


            TextName_ar = EditTextName_ar.getText ().toString ();
            TextName_en = EditTextName_en.getText ().toString ();
            Textdescp_ar = EditTextdescription_ar.getText ().toString ();
            Textdescp_en = EditTextdescription_en.getText ().toString ();
            Textlocation_ar = EditTextloaction_ar.getText ().toString ();
            Textlocation_en = EditTextlocation_en.getText ().toString ();
            Textsize = EditTextsize.getText ().toString ();
            Textprice = EditTextprice.getText ().toString ();

            hideKeyboard ();
            new EdithallOther ().execute ();

        }

    }


    private void submitDelete() {
        if (!validateName_en ()) {
            Snackbar.make (this.findViewById ( android.R.id.content ), R.string.invalid_name, Snackbar.LENGTH_SHORT ).show ();
            return;

        }if (!validateName_ar ()) {
            Snackbar.make (this.findViewById ( android.R.id.content ), R.string.invalid_name, Snackbar.LENGTH_SHORT ).show ();
            return;

        }if (!validatedescription_en ()) {
            Snackbar.make (this.findViewById ( android.R.id.content ), R.string.invalid_desc_en, Snackbar.LENGTH_SHORT ).show ();
            return;

        }if (!validatedescription_ar ()) {
            Snackbar.make (this.findViewById ( android.R.id.content ), R.string.invalid_desc_ar, Snackbar.LENGTH_SHORT ).show ();
            return;

        }if (!validateTextloc_ar ()) {
            Snackbar.make (this.findViewById ( android.R.id.content ), R.string.invalid_lat, Snackbar.LENGTH_SHORT ).show ();
            return;

        }if (!validateTextloc_en ()) {
            Snackbar.make (this.findViewById ( android.R.id.content ), R.string.invalid_long, Snackbar.LENGTH_SHORT ).show ();
            return;

        } else {


            TextName_ar = EditTextName_ar.getText ().toString ();
            TextName_en = EditTextName_en.getText ().toString ();
            Textdescp_ar  = EditTextdescription_ar.getText ().toString ();
            Textdescp_en = EditTextdescription_en.getText ().toString ();
            Textlocation_ar = EditTextloaction_ar.getText ().toString ();
            Textlocation_en = EditTextlocation_en.getText ().toString ();
            Textsize  = EditTextsize.getText ().toString ();
            Textprice  = EditTextprice.getText ().toString ();

            hideKeyboard ();
            new Delethall ().execute ();


        }
    }


    private class Edithall extends AsyncTask<Void, Void, SoapObject> {
        @Override
        protected void onPreExecute() {
            progressDialog.show ();
            Log.i ( TAG, "onPreExecute" );
        }

        @Override
        protected SoapObject doInBackground(Void... params) {
            Log.i ( TAG, "doInBackground" );

            String SOAP_ACTION = "http://farhatty.sd/EditHall";
            String METHOD_NAME = "EditHall";
            String NAMESPACE = "http://farhatty.sd/";
            String URL = "http://farhatty.sd/WebService.asmx";

            SoapObject Request = new SoapObject ( NAMESPACE, METHOD_NAME );
            Request.addProperty ( "ID", hall_id );
            Request.addProperty ( "Name_en", TextName_en );
            Request.addProperty ( "Name_en", TextName_en );
            Request.addProperty ( "Name_en", TextName_en );
            Request.addProperty ( "Name_ar", TextName_ar );
            Request.addProperty ( "Des_en", Textdescp_en );
            Request.addProperty ( "Des_ar", Textdescp_ar );
            Request.addProperty ( "Size", Textsize );
            Request.addProperty ( "Price", Textprice );
            Request.addProperty ( "Loacation_ar", Textlocation_ar);
            Request.addProperty ( "Loacation_en", Textlocation_en );
            Request.addProperty ( "Image", uploadname);

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
                Log.d ( "TAG","Edit Hall Data :" +resultString);

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


            }else {


            }

        }
    }



    private class EdithallOther extends AsyncTask<Void, Void, SoapObject> {
        @Override
        protected void onPreExecute() {
            progressD = new ProgressDialog ( ViewHallActivity.this);
            progressD.setCancelable ( false );
            progressD.setTitle ( R.string.title_please_wait );
            progressD.setMessage ( getString ( R.string.title_edit) );
            progressD.show ();
            Log.i ( TAG, "onPreExecute" );
        }

        @Override
        protected SoapObject doInBackground(Void... params) {
            Log.i ( TAG, "doInBackground" );

            String SOAP_ACTION = "http://farhatty.sd/EditHall";
            String METHOD_NAME = "EditHall";
            String NAMESPACE = "http://farhatty.sd/";
            String URL = "http://farhatty.sd/WebService.asmx";

            SoapObject Request = new SoapObject ( NAMESPACE, METHOD_NAME );
            Request.addProperty ( "ID", hall_id );
            Request.addProperty ( "Name_en", TextName_en );
            Request.addProperty ( "Name_en", TextName_en );
            Request.addProperty ( "Name_en", TextName_en );
            Request.addProperty ( "Name_ar", TextName_ar );
            Request.addProperty ( "Des_en", Textdescp_en );
            Request.addProperty ( "Des_ar", Textdescp_ar );
            Request.addProperty ( "Size", Textsize );
            Request.addProperty ( "Price", Textprice );
            Request.addProperty ( "Loacation_ar", Textlocation_ar);
            Request.addProperty ( "Loacation_en", Textlocation_en );
            Request.addProperty ( "Image", view_hall_image_);

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
                Log.d ( "TAG","Edit Hall Data :" +resultString);

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


            }else {
                progressDialog.dismiss ();
                dialogSucess ();

            }

        }
    }

    private class Delethall extends AsyncTask<Void, Void, SoapObject> {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog ( ViewHallActivity.this);
            progressDialog.setCancelable ( false );
            progressDialog.setTitle ( R.string.title_please_wait );
            progressDialog.setMessage ( getString ( R.string.title_delete) );
            progressDialog.show ();
            Log.i ( TAG, "onPreExecute" );
        }

        @Override
        protected SoapObject doInBackground(Void... params) {
            Log.i ( TAG, "doInBackground" );

            String SOAP_ACTION = "http://farhatty.sd/DeleteHall";
            String METHOD_NAME = "DeleteHall";
            String NAMESPACE = "http://farhatty.sd/";
            String URL = "http://farhatty.sd/WebService.asmx";

            SoapObject Request = new SoapObject ( NAMESPACE, METHOD_NAME );

            Request.addProperty ( "ID", hall_id);
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
                Log.d ( "Delete Hall Data :", "" + resultStringBooking );
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

                dialogFailedRetryDelete ();
                progressDialog.dismiss ();

            } else {

                progressDialog.dismiss ();
                dialogSucess ();
            }


        }
    }



    public void dialogFailedRetryEdit() {

        AlertDialog.Builder builder = new AlertDialog.Builder ( this );
        builder.setTitle ( R.string.failed );
        builder.setMessage ( getString ( R.string.failed_attemps ) );
        builder.setPositiveButton ( R.string.TRY_AGAIN, new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new Edithall ().execute (  );
            }
        } );
        builder.setNegativeButton ( R.string.Close, new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity ( new Intent ( ViewHallActivity.this, MainActivity.class ) );
            }
        } );
        builder.show ();
    }



    public void dialogFailedRetryDelete() {

        AlertDialog.Builder builder = new AlertDialog.Builder ( this , R.style.Theme_AppCompat_DayNight_Dialog);
        builder.setTitle ( R.string.failed );
        builder.setMessage ( getString ( R.string.failed_attemps ) );
        builder.setPositiveButton ( R.string.TRY_AGAIN, new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new Delethall ().execute (  );
            }
        } );
        builder.setNegativeButton ( R.string.Close, new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity ( new Intent ( ViewHallActivity.this, MainActivity.class ) );
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
                startActivity ( new Intent ( ViewHallActivity.this, MainActivity.class ) );
            }
        } );
        builder.show ();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed ();
        finish ();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if ( progressDialog!=null && progressDialog.isShowing() ){
            progressDialog.cancel();
        }
    }
}
