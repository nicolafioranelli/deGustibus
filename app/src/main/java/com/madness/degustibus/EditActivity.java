package com.madness.degustibus;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class EditActivity extends AppCompatActivity {

    /* Views */
    private Toolbar toolbar;
    private EditText fullname;
    private EditText email;
    private EditText desc;
    private EditText phone;
    private ImageView img;
    private String cameraFilePath;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    int a = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("deGustibus");
        toolbar.setTitleTextColor(getResources().getColor(R.color.titleColor));
        setSupportActionBar(toolbar);

        pref = getSharedPreferences("DEGUSTIBUS", Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    /* Menu inflater for toolbar (adds elements inserted in res/menu/main_menu.xml */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    /* Click listener to correctly handle actions related to toolbar items */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_edit) {
            fullname = findViewById(R.id.et_edit_fullName);
            email = findViewById(R.id.et_edit_email);
            desc = findViewById(R.id.et_edit_desc);
            phone = findViewById(R.id.et_edit_phone);

            /* Define shared preferences and insert values */
            editor.putString("name", fullname.getText().toString());
            editor.putString("email", email.getText().toString());
            editor.putString("desc", desc.getText().toString());
            editor.putString("phone", phone.getText().toString());
            if (getPrefPhoto()!=null) {
                editor.putString("photo", getPrefPhoto());
            }
            editor.apply();
            delPrefPhoto();

            Toast.makeText(EditActivity.this, getResources().getText(R.string.saved).toString(), Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        fullname = findViewById(R.id.et_edit_fullName);
        email = findViewById(R.id.et_edit_email);
        desc = findViewById(R.id.et_edit_desc);
        phone = findViewById(R.id.et_edit_phone);
        img = findViewById(R.id.imageview);

        fullname.setText(pref.getString("name", null));
        email.setText(pref.getString("email", null));
        desc.setText(pref.getString("desc", null));
        phone.setText(pref.getString("phone", null));
        /* check if a photo is set */
        if(getPrefPhoto() == null) {
            if (pref.getString("photo", null) != null) {
                img.setImageURI(Uri.parse(pref.getString("photo", null)));
            }
        }
        super.onResume();
    }

    public void getPhoto(View view){
        checkCameraPermissions();
        checkGalleryPermissions();
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                getResources().getText(R.string.camera).toString(), getResources().getText(R.string.gallery).toString()};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                /* Define image file where the camera will put the taken picture */
                                File image;
                                /* Get the directory to store image */
                                File storageDir = getApplicationContext().getFilesDir();
                                try {
                                    image = File.createTempFile(
                                            "img",
                                            ".jpg",
                                            storageDir
                                    );
                                    cameraFilePath = "file://" + image.getAbsolutePath();

                                    /* Set the Uri here before starting camera */
                                    setPrefPhoto(cameraFilePath);

                                    /* Start Intent for camera */
                                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", image));
                                    startActivityForResult(intent, 0);
                                } catch (Exception e) {
                                    Log.e("MAD", "getPhoto: ", e);
                                }

                                break;
                            case 1:
                                //Create an Intent with action as ACTION_PICK
                                Intent intent=new Intent(Intent.ACTION_PICK);
                                // Sets the type as image/*. This ensures only components of type image are selected
                                intent.setType("image/*");
                                //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
                                String[] mimeTypes = {"image/jpeg", "image/png"};
                                intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
                                // Launching the Intent
                                startActivityForResult(intent, 1);
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void onActivityResult(int requestCode,int resultCode,Intent data){
        // Result code is RESULT_OK only if the user captures an Image
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 0:
                    Uri photo = Uri.parse(getPrefPhoto());
                    img = findViewById(R.id.imageview);
                    img.setImageURI(photo);

                    break;
                case 1:
                    Uri selectedImage = data.getData();
                    img.setImageURI(selectedImage);
                    setPrefPhoto(selectedImage.toString());
                    break;
            }
        } else if(resultCode == Activity.RESULT_CANCELED) {
            Log.d("MAD", "onActivityResult: CANCELED");
            delPrefPhoto();
            //img.setImageURI(null);
        }
    }

    /* Methods (getters, setters and delete) to retrieve temporary photo uri. */
    private void setPrefPhoto(String cameraFilePath) {
        SharedPreferences pref = getSharedPreferences("photo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("photo", cameraFilePath);
        editor.commit();
    }

    private String getPrefPhoto() {
        SharedPreferences pref = getSharedPreferences("photo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        return pref.getString("photo", null);
    }

    private void delPrefPhoto() {
        SharedPreferences pref = getSharedPreferences("photo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("photo");
        editor.apply();
    }

    /* Methods for permissions */
    private void checkGalleryPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            Log.d("MAD", "onCreate: permission granted" );
        }
    }

    private void checkCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        } else {
            Log.d("MAD", "onCreate: permission granted" );
        }
    }
}
