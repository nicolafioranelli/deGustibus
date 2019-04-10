package com.madness.restaurant;

import android.Manifest;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.sql.Time;

public class EditActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    /* Views */
    private Toolbar toolbar;
    private EditText fullname;
    private EditText email;
    private EditText desc;
    private EditText phone;
    private EditText address;
    private TextView mondayOpen;
    private TextView mondayClose;
    private TextView tuesdayOpen;
    private TextView tuesdayClose;
    private TextView wednesdayOpen;
    private TextView wednesdayClose;
    private TextView thursdayOpen;
    private TextView thursdayClose;
    private TextView fridayOpen;
    private TextView fridayClose;
    private TextView saturdayOpen;
    private TextView saturdayClose;
    private TextView sundayOpen;
    private TextView sundayClose;
    private int weekDay;
    private int moment;
    private ImageView img;
    private String cameraFilePath;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("deGustibus");
        toolbar.setSubtitle("Restaurant");
        toolbar.setTitleTextColor(getResources().getColor(R.color.titleColor));
        setSupportActionBar(toolbar);

        pref = getSharedPreferences("DEGUSTIBUS", Context.MODE_PRIVATE);
        editor = pref.edit();

        takeTimeButtons();
        //restore the content
        if(savedInstanceState != null){
            loadBundle(savedInstanceState);
        }else{
            loadSharedPrefs();
        }
    }

    private void takeTimeButtons(){
        Button button1= (Button) findViewById(R.id.defaultOpenButton);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay=0;
                moment=0;
                DialogFragment timePicker= new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            };
        });
        Button button2= (Button) findViewById(R.id.defaultCloseButton);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay=0;
                moment=1;
                DialogFragment timePicker= new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            };
        });
        Button button3= (Button) findViewById(R.id.mondayOpenButton);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay=1;
                moment=0;
                DialogFragment timePicker= new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            };
        });
        Button button4= (Button) findViewById(R.id.mondayCloseButton);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay=1;
                moment=1;
                DialogFragment timePicker= new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            };
        });
        Button button5= (Button) findViewById(R.id.tuesayOpenButton);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay=2;
                moment=0;
                DialogFragment timePicker= new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            };
        });
        Button button6= (Button) findViewById(R.id.tuesdayCloseButton);
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay=2;
                moment=1;
                DialogFragment timePicker= new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            };
        });
        Button button7= (Button) findViewById(R.id.wednesdayOpenButton);
        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay=3;
                moment=0;
                DialogFragment timePicker= new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            };
        });
        Button button8= (Button) findViewById(R.id.wednesdayCloseButton);
        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay=3;
                moment=1;
                DialogFragment timePicker= new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            };
        });
        Button button9= (Button) findViewById(R.id.thursdayOpenButton);
        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay=4;
                moment=0;
                DialogFragment timePicker= new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            };
        });
        Button button10= (Button) findViewById(R.id.thursdayCloseButton);
        button10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay=4;
                moment=1;
                DialogFragment timePicker= new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            };
        });
        Button button11= (Button) findViewById(R.id.fridayOpenButton);
        button11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay=5;
                moment=0;
                DialogFragment timePicker= new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            };
        });
        Button button12= (Button) findViewById(R.id.fridayCloseButton);
        button12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay=5;
                moment=1;
                DialogFragment timePicker= new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            };
        });
        Button button13= (Button) findViewById(R.id.saturdayOpenButton);
        button13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay=6;
                moment=0;
                DialogFragment timePicker= new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            };
        });
        Button button14= (Button) findViewById(R.id.saturdayCloseButton);
        button14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay=6;
                moment=1;
                DialogFragment timePicker= new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            };
        });
        Button button15= (Button) findViewById(R.id.sundayOpenButton);
        button15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay=7;
                moment=0;
                DialogFragment timePicker= new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            };
        });
        Button button16= (Button) findViewById(R.id.sundayCloseButton);
        button16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay=7;
                moment=1;
                DialogFragment timePicker= new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            };
        });
    }
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mondayOpen = findViewById(R.id.et_edit_mondayOpen);
        mondayClose = findViewById(R.id.et_edit_mondayClose);
        tuesdayOpen = findViewById(R.id.et_edit_tuesdayOpen);
        tuesdayClose = findViewById(R.id.et_edit_tuesdayClose);
        wednesdayOpen = findViewById(R.id.et_edit_wednesdayOpen);
        wednesdayClose = findViewById(R.id.et_edit_wednesdayClose);
        thursdayOpen = findViewById(R.id.et_edit_thursdayOpen);
        thursdayClose = findViewById(R.id.et_edit_thursdayClose);
        fridayOpen = findViewById(R.id.et_edit_fridayOpen);
        fridayClose = findViewById(R.id.et_edit_fridayClose);
        saturdayOpen = findViewById(R.id.et_edit_saturdayOpen);
        saturdayClose = findViewById(R.id.et_edit_saturdayClose);
        sundayOpen = findViewById(R.id.et_edit_sundayOpen);
        sundayClose = findViewById(R.id.et_edit_sundayClose);
        switch (this.weekDay){
            case 0:
                if(this.moment==0) {
                    mondayOpen.setText(String.format("%02d:%02d", hourOfDay, minute));
                    tuesdayOpen.setText(String.format("%02d:%02d", hourOfDay, minute));
                    wednesdayOpen.setText(String.format("%02d:%02d", hourOfDay, minute));
                    thursdayOpen.setText(String.format("%02d:%02d", hourOfDay, minute));
                    fridayOpen.setText(String.format("%02d:%02d", hourOfDay, minute));
                    saturdayOpen.setText(String.format("%02d:%02d", hourOfDay, minute));
                    sundayOpen.setText(String.format("%02d:%02d", hourOfDay, minute));
                }
                else{
                    mondayClose.setText(String.format("%02d:%02d", hourOfDay, minute));
                    tuesdayClose.setText(String.format("%02d:%02d", hourOfDay, minute));
                    wednesdayClose.setText(String.format("%02d:%02d", hourOfDay, minute));
                    thursdayClose.setText(String.format("%02d:%02d", hourOfDay, minute));
                    fridayClose.setText(String.format("%02d:%02d", hourOfDay, minute));
                    saturdayClose.setText(String.format("%02d:%02d", hourOfDay, minute));
                    sundayClose.setText(String.format("%02d:%02d", hourOfDay, minute));
                }
                break;
            case 1:
                if(this.moment==0)
                    mondayOpen.setText(String.format("%02d:%02d", hourOfDay, minute));
                else
                    mondayClose.setText(String.format("%02d:%02d", hourOfDay, minute));
                break;
            case 2:
                if(this.moment==0)
                    tuesdayOpen.setText(String.format("%02d:%02d", hourOfDay, minute));
                else
                    tuesdayClose.setText(String.format("%02d:%02d", hourOfDay, minute));
                break;
            case 3:
                if(this.moment==0)
                    wednesdayOpen.setText(String.format("%02d:%02d", hourOfDay, minute));
                else
                    wednesdayClose.setText(String.format("%02d:%02d", hourOfDay, minute));
                break;
            case 4:
                if(this.moment==0)
                    thursdayOpen.setText(String.format("%02d:%02d", hourOfDay, minute));
                else
                    thursdayClose.setText(String.format("%02d:%02d", hourOfDay, minute));
                break;
            case 5:
                if(this.moment==0)
                    fridayOpen.setText(String.format("%02d:%02d", hourOfDay, minute));
                else
                    fridayClose.setText(String.format("%02d:%02d", hourOfDay, minute));
                break;
            case 6:
                if(this.moment==0)
                    saturdayOpen.setText(String.format("%02d:%02d", hourOfDay, minute));
                else
                    saturdayClose.setText(String.format("%02d:%02d", hourOfDay, minute));
                break;
            case 7:
                if(this.moment==0)
                    sundayOpen.setText(String.format("%02d:%02d", hourOfDay, minute));
                else
                    sundayClose.setText(String.format("%02d:%02d", hourOfDay, minute));
                break;
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        // Save away the original text, so we still have it if the activity
        // needs to be killed while paused.
        super.onSaveInstanceState(outState);

        fullname = findViewById(R.id.et_edit_fullName);
        email = findViewById(R.id.et_edit_email);
        desc = findViewById(R.id.et_edit_desc);
        phone = findViewById(R.id.et_edit_phone);
        address = findViewById(R.id.et_edit_address);
        mondayOpen = findViewById(R.id.et_edit_mondayOpen);
        mondayClose = findViewById(R.id.et_edit_mondayClose);
        tuesdayOpen = findViewById(R.id.et_edit_tuesdayOpen);
        tuesdayClose = findViewById(R.id.et_edit_tuesdayClose);
        wednesdayOpen = findViewById(R.id.et_edit_wednesdayOpen);
        wednesdayClose = findViewById(R.id.et_edit_wednesdayClose);
        thursdayOpen = findViewById(R.id.et_edit_thursdayOpen);
        thursdayClose = findViewById(R.id.et_edit_thursdayClose);
        fridayOpen = findViewById(R.id.et_edit_fridayOpen);
        fridayClose = findViewById(R.id.et_edit_fridayClose);
        saturdayOpen = findViewById(R.id.et_edit_saturdayOpen);
        saturdayClose = findViewById(R.id.et_edit_saturdayClose);
        sundayOpen = findViewById(R.id.et_edit_sundayOpen);
        sundayClose = findViewById(R.id.et_edit_sundayClose);

        img = findViewById(R.id.imageview);

        outState.putString("name",fullname.getText().toString());
        outState.putString("email",email.getText().toString());
        outState.putString("desc",desc.getText().toString());
        outState.putString("phone",phone.getText().toString());
        outState.putString("address",address.getText().toString());
        outState.putString("mondayOpen",mondayOpen.getText().toString());
        outState.putString("mondayClose",mondayClose.getText().toString());
        outState.putString("tuesdayOpen",tuesdayOpen.getText().toString());
        outState.putString("tuesdayClose",tuesdayClose.getText().toString());
        outState.putString("wednesdayOpen",wednesdayOpen.getText().toString());
        outState.putString("wednesdayClose",wednesdayClose.getText().toString());
        outState.putString("thursdayOpen",thursdayOpen.getText().toString());
        outState.putString("thursdayClose",thursdayClose.getText().toString());
        outState.putString("fridayOpen",fridayOpen.getText().toString());
        outState.putString("fridayClose",fridayClose.getText().toString());
        outState.putString("saturdayOpen",saturdayOpen.getText().toString());
        outState.putString("saturdayClose",saturdayClose.getText().toString());
        outState.putString("sundayOpen",sundayOpen.getText().toString());
        outState.putString("sundayClose",sundayClose.getText().toString());
        if(getPrefPhoto()==null) {
            outState.putString("photo", pref.getString("photo", null));
        } else {
            outState.putString("photo", getPrefPhoto());
        }
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
            address = findViewById(R.id.et_edit_address);
            mondayOpen = findViewById(R.id.et_edit_mondayOpen);
            mondayClose = findViewById(R.id.et_edit_mondayClose);
            tuesdayOpen = findViewById(R.id.et_edit_tuesdayOpen);
            tuesdayClose = findViewById(R.id.et_edit_tuesdayClose);
            wednesdayOpen = findViewById(R.id.et_edit_wednesdayOpen);
            wednesdayClose = findViewById(R.id.et_edit_wednesdayClose);
            thursdayOpen = findViewById(R.id.et_edit_thursdayOpen);
            thursdayClose = findViewById(R.id.et_edit_thursdayClose);
            fridayOpen = findViewById(R.id.et_edit_fridayOpen);
            fridayClose = findViewById(R.id.et_edit_fridayClose);
            saturdayOpen = findViewById(R.id.et_edit_saturdayOpen);
            saturdayClose = findViewById(R.id.et_edit_saturdayClose);
            sundayOpen = findViewById(R.id.et_edit_sundayOpen);
            sundayClose = findViewById(R.id.et_edit_sundayClose);

            /* Define shared preferences and insert values */
            editor.putString("name", fullname.getText().toString());
            editor.putString("email", email.getText().toString());
            editor.putString("desc", desc.getText().toString());
            editor.putString("phone", phone.getText().toString());
            editor.putString("address", address.getText().toString());
            editor.putString("mondayOpen",mondayOpen.getText().toString());
            editor.putString("mondayClose",mondayClose.getText().toString());
            editor.putString("tuesdayOpen",tuesdayOpen.getText().toString());
            editor.putString("tuesdayClose",tuesdayClose.getText().toString());
            editor.putString("wednesdayOpen",wednesdayOpen.getText().toString());
            editor.putString("wednesdayClose",wednesdayClose.getText().toString());
            editor.putString("thursdayOpen",thursdayOpen.getText().toString());
            editor.putString("thursdayClose",thursdayClose.getText().toString());
            editor.putString("fridayOpen",fridayOpen.getText().toString());
            editor.putString("fridayClose",fridayClose.getText().toString());
            editor.putString("saturdayOpen",saturdayOpen.getText().toString());
            editor.putString("saturdayClose",saturdayClose.getText().toString());
            editor.putString("sundayOpen",sundayOpen.getText().toString());
            editor.putString("sundayClose",sundayClose.getText().toString());
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
        super.onResume();
    }

    public void getPhoto(View view){

        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle(this.getString(R.string.select_action));
        String[] pictureDialogItems = {
                getResources().getText(R.string.camera).toString(), getResources().getText(R.string.gallery).toString()};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                checkCameraPermissions();
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
                    setPrefPhoto(img.toString());
                    break;
                case 1:
                    Uri selectedImage = data.getData();
                    img.setImageURI(selectedImage);
                    setPrefPhoto(selectedImage.toString());
                    break;
            }
        } else if(resultCode == Activity.RESULT_CANCELED) {
            Log.d("MAD", "onActivityResult: CANCELED");
            try{
                File photoToCancel = new File(getPrefPhoto());
                photoToCancel.delete();
            } catch (Exception e) {
                Log.e("MAD", "onActivityResult: ", e);
            }
            delPrefPhoto();
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

    private void loadSharedPrefs(){
        fullname = findViewById(R.id.et_edit_fullName);
        email = findViewById(R.id.et_edit_email);
        desc = findViewById(R.id.et_edit_desc);
        phone = findViewById(R.id.et_edit_phone);
        address = findViewById(R.id.et_edit_address);
        mondayOpen = findViewById(R.id.et_edit_mondayOpen);
        mondayClose = findViewById(R.id.et_edit_mondayClose);
        tuesdayOpen = findViewById(R.id.et_edit_tuesdayOpen);
        tuesdayClose = findViewById(R.id.et_edit_tuesdayClose);
        wednesdayOpen = findViewById(R.id.et_edit_wednesdayOpen);
        wednesdayClose = findViewById(R.id.et_edit_wednesdayClose);
        thursdayOpen = findViewById(R.id.et_edit_thursdayOpen);
        thursdayClose = findViewById(R.id.et_edit_thursdayClose);
        fridayOpen = findViewById(R.id.et_edit_fridayOpen);
        fridayClose = findViewById(R.id.et_edit_fridayClose);
        saturdayOpen = findViewById(R.id.et_edit_saturdayOpen);
        saturdayClose = findViewById(R.id.et_edit_saturdayClose);
        sundayOpen = findViewById(R.id.et_edit_sundayOpen);
        sundayClose = findViewById(R.id.et_edit_sundayClose);
        img = findViewById(R.id.imageview);

        fullname.setText(pref.getString("name", getResources().getString(R.string.fullname)));
        email.setText(pref.getString("email", getResources().getString(R.string.email)));
        desc.setText(pref.getString("desc", getResources().getString(R.string.desc)));
        phone.setText(pref.getString("phone", getResources().getString(R.string.phone)));
        address.setText(pref.getString("address", getResources().getString(R.string.address)));
        mondayOpen.setText(pref.getString("mondayOpen", getResources().getString(R.string.Opening)));
        mondayClose.setText(pref.getString("mondayClose", getResources().getString(R.string.Closing)));
        tuesdayOpen.setText(pref.getString("tuesdayOpen", getResources().getString(R.string.Opening)));
        tuesdayClose.setText(pref.getString("tuesdayClose", getResources().getString(R.string.Closing)));
        wednesdayOpen.setText(pref.getString("wednesdayOpen", getResources().getString(R.string.Opening)));
        wednesdayClose.setText(pref.getString("wednesdayClose", getResources().getString(R.string.Closing)));
        thursdayOpen.setText(pref.getString("thursdayOpen", getResources().getString(R.string.Opening)));
        thursdayClose.setText(pref.getString("thursdayClose", getResources().getString(R.string.Closing)));
        fridayOpen.setText(pref.getString("fridayOpen", getResources().getString(R.string.Opening)));
        fridayClose.setText(pref.getString("fridayClose", getResources().getString(R.string.Closing)));
        saturdayOpen.setText(pref.getString("saturdayOpen", getResources().getString(R.string.Opening)));
        saturdayClose.setText(pref.getString("saturdayClose", getResources().getString(R.string.Closing)));
        sundayOpen.setText(pref.getString("sundayOpen", getResources().getString(R.string.Opening)));
        sundayClose.setText(pref.getString("sundayClose", getResources().getString(R.string.Closing)));
        /* check if a photo is set */
        if (pref.getString("photo", null) != null) {
            img.setImageURI(Uri.parse(pref.getString("photo", null)));
        }
    }

    private void loadBundle(Bundle bundle){
        fullname = findViewById(R.id.et_edit_fullName);
        email = findViewById(R.id.et_edit_email);
        desc = findViewById(R.id.et_edit_desc);
        phone = findViewById(R.id.et_edit_phone);
        address = findViewById(R.id.et_edit_address);
        mondayOpen = findViewById(R.id.et_edit_mondayOpen);
        mondayClose = findViewById(R.id.et_edit_mondayClose);
        tuesdayOpen = findViewById(R.id.et_edit_tuesdayOpen);
        tuesdayClose = findViewById(R.id.et_edit_tuesdayClose);
        wednesdayOpen = findViewById(R.id.et_edit_wednesdayOpen);
        wednesdayClose = findViewById(R.id.et_edit_wednesdayClose);
        thursdayOpen = findViewById(R.id.et_edit_thursdayOpen);
        thursdayClose = findViewById(R.id.et_edit_thursdayClose);
        fridayOpen = findViewById(R.id.et_edit_fridayOpen);
        fridayClose = findViewById(R.id.et_edit_fridayClose);
        saturdayOpen = findViewById(R.id.et_edit_saturdayOpen);
        saturdayClose = findViewById(R.id.et_edit_saturdayClose);
        sundayOpen = findViewById(R.id.et_edit_sundayOpen);
        sundayClose = findViewById(R.id.et_edit_sundayClose);
        img = findViewById(R.id.imageview);

        fullname.setText(bundle.getString("name"));
        email.setText(bundle.getString("email"));
        desc.setText(bundle.getString("desc"));
        phone.setText(bundle.getString("phone"));
        address.setText(bundle.getString("address"));
        mondayOpen.setText(bundle.getString("mondayOpen"));
        mondayClose.setText(bundle.getString("mondayClose"));
        tuesdayOpen.setText(bundle.getString("tuesdayOpen"));
        tuesdayClose.setText(bundle.getString("tuesdayClose"));
        tuesdayClose.setText(bundle.getString("tuesdayClose"));
        wednesdayOpen.setText(bundle.getString("wednesdayOpen"));
        wednesdayClose.setText(bundle.getString("wednesdayClose"));
        thursdayOpen.setText(bundle.getString("thursdayOpen"));
        thursdayClose.setText(bundle.getString("thursdayClose"));
        fridayOpen.setText(bundle.getString("fridayOpen"));
        fridayClose.setText(bundle.getString("fridayClose"));
        saturdayOpen.setText(bundle.getString("saturdayOpen"));
        saturdayClose.setText(bundle.getString("saturdayClose"));
        sundayOpen.setText(bundle.getString("sundayOpen"));
        sundayClose.setText(bundle.getString("sundayClose"));
        if(bundle.getString("photo")!=null) {
            img.setImageURI(Uri.parse(bundle.getString("photo")));
        }
    }
}
