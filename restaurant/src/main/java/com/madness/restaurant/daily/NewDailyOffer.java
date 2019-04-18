package com.madness.restaurant.daily;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.madness.restaurant.BuildConfig;
import com.madness.restaurant.R;

import java.io.File;

public class NewDailyOffer extends Fragment {

    /* Views */
    private EditText dishname;
    private EditText desc;
    private EditText avail;
    private EditText price;
    private ImageView img;
    private Button minusBtn;
    private Button plusBtn;
    private String cameraFilePath;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private NewDailyOfferListener listener;

    public interface NewDailyOfferListener {
        public void onSubmitDish();
    }

    public NewDailyOffer() {
        // Required empty public constructor
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof NewDailyOfferListener) {
            listener = (NewDailyOfferListener) context;
        } else {
            throw new ClassCastException(context.toString() + "must implement NewDailyOfferListener");
        }
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


        pref = this.getActivity().getSharedPreferences("DEGUSTIBUS", Context.MODE_PRIVATE);
        editor = pref.edit();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.activity_add_new_daily_offer, container, false);
        getActivity().setTitle(getResources().getString(R.string.title_Daily));
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        minusBtn = (Button) getActivity().findViewById(R.id.button_minus);
        plusBtn = (Button) getActivity().findViewById(R.id.button_plus);
        dishname = getActivity().findViewById(R.id.et_dish_name);
        desc = getActivity().findViewById(R.id.et_desc);
        avail = getActivity().findViewById(R.id.et_avail);
        price = getActivity().findViewById(R.id.et_price);
        img = getActivity().findViewById(R.id.imageviewfordish);

        if(savedInstanceState != null){
            loadBundle(savedInstanceState);
        }else{
            loadSharedPrefs();
        }

        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avail = (EditText) getActivity().findViewById(R.id.et_avail);
                String num = avail.getText().toString();
                int n = Integer.parseInt(num);
                if (n>0) {
                    n--;
                }
                avail.setText(String.valueOf(n));
            }
        });
        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avail = (EditText) getActivity().findViewById(R.id.et_avail);
                String num = avail.getText().toString();
                int n = Integer.parseInt(num)+1;
                avail.setText(String.valueOf(n));
            }
        });
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPhoto(v);
            }
        });

    }

    /* Menu inflater for toolbar (adds elements inserted in res/menu/main_menu.xml) */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_edit, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }


    public void onSaveInstanceState(Bundle outState) {
        // Save away the original text, so we still have it if the activity
        // needs to be killed while paused.
        super.onSaveInstanceState(outState);

        outState.putString("dish", dishname.getText().toString());
        outState.putString("descDish", desc.getText().toString());
        outState.putString("avail",avail.getText().toString());
        outState.putString("price",price.getText().toString());
        if(getPrefPhoto()==null) {
            outState.putString("photoDish", pref.getString("photoDish", null));
        } else {
            outState.putString("photoDish", getPrefPhoto());
        }

    }

    public void getPhoto(View v){
        ImageView imageView = (ImageView) getActivity().findViewById(R.id.imageviewfordish);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getActivity());
                pictureDialog.setTitle(getActivity().getString(R.string.select_action));
                String[] pictureDialogItems = {
                        getResources().getText(R.string.camera).toString(), getResources().getText(R.string.gallery).toString()};
                pictureDialog.setItems(pictureDialogItems,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        checkCameraPermissions();


                                        break;
                                    case 1:
                                        checkGalleryPermissions();
                                        break;
                                }
                            }
                        });
                pictureDialog.show();
            };
        });
    }


    @Override
    public void onResume(){
        super.onResume();
    }

    public void onActivityResult(int requestCode,int resultCode,Intent data){
        // Result code is RESULT_OK only if the user captures an Image
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 0:
                    Uri photo = Uri.parse(getPrefPhoto());
                    //img = getView().findViewById(R.id.imageviewfordish);
                    img.setImageURI(photo);
                    setPrefPhoto(photo.toString());
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        switch (requestCode) {
            case 0:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    /* Define image file where the camera will put the taken picture */
                    File image;
                    /* Get the directory to store image */
                    File storageDir = getActivity().getApplicationContext().getFilesDir();
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
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getActivity().getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", image));
                        startActivityForResult(intent, 0);
                    } catch (Exception e) {
                        Log.e("MAD", "getPhoto: ", e);
                    }
                }
                else {
                    //do something like displaying a message that he didn`t allow the app to access gallery and you wont be able to let him select from gallery
                    Toast.makeText(getActivity(),"Access to camera denied", Toast.LENGTH_SHORT).show();
                }
                break;
            case 1:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Create an Intent with action as ACTION_PICK
                    Intent intent=new Intent(Intent.ACTION_PICK);
                    // Sets the type as image/*. This ensures only components of type image are selected
                    intent.setType("image/*");
                    //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
                    String[] mimeTypes = {"image/jpeg", "image/png"};
                    intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
                    // Launching the Intent
                    startActivityForResult(intent, 1);
                } else {
                    //do something like displaying a message that he didn`t allow the app to access gallery and you wont be able to let him select from gallery
                    Toast.makeText(getActivity(),"Access to camera denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void setPrefPhoto(String cameraFilePath) {
        SharedPreferences pref = getActivity().getSharedPreferences("photoDish", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("photoDish", cameraFilePath);
        editor.apply();
    }

    private String getPrefPhoto() {
        SharedPreferences pref = getActivity().getSharedPreferences("photoDish", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        return pref.getString("photoDish", null);
    }

    private void delPrefPhoto() {
        SharedPreferences pref = getActivity().getSharedPreferences("photoDish", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("photoDish");
        editor.apply();
    }

    private void checkCameraPermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 1);
        }
       else {
            /* Define image file where the camera will put the taken picture */
            File image;
            /* Get the directory to store image */
            File storageDir = getActivity().getApplicationContext().getFilesDir();
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
                intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getActivity().getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", image));
                startActivityForResult(intent, 0);
            } catch (Exception e) {
                Log.e("MAD", "getPhoto: ", e);
            }
            Log.d("MAD", "onCreate: permission granted" );
        }
    }

    private void checkGalleryPermissions(){
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) checkGalleryPermissions ();
        } else{
            //Create an Intent with action as ACTION_PICK
            Intent intent=new Intent(Intent.ACTION_PICK);
            // Sets the type as image/*. This ensures only components of type image are selected
            intent.setType("image/*");
            //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
            String[] mimeTypes = {"image/jpeg", "image/png"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
            // Launching the Intent
            startActivityForResult(intent, 1);
            Log.d("MAD", "onCreate: permission granted" );
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_edit) {
            /* Define shared preferences and insert values */
            editor.putString("dish", dishname.getText().toString());
            editor.putString("descDish", desc.getText().toString());
            editor.putString("avail",avail.getText().toString());
            editor.putString("price",price.getText().toString());

            if (getPrefPhoto()!=null) {
                editor.putString("photoDish", getPrefPhoto());
            }
            editor.apply();
            delPrefPhoto();

            listener.onSubmitDish();

            Toast.makeText(getContext(), getResources().getString(R.string.saved), Toast.LENGTH_SHORT).show();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.popBackStackImmediate("DAILY", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadSharedPrefs(){
        dishname.setText(pref.getString("dish", getResources().getString(R.string.dish_name)));
        desc.setText(pref.getString("descDish", getResources().getString(R.string.desc_dish)));
        avail.setText(pref.getString("avail", String.valueOf(0)));
        price.setText(pref.getString("price", String.valueOf(0.00)));
        /* check if a photo is set */
        if (pref.getString("photoDish", null) != null) {
            img.setImageURI(Uri.parse(pref.getString("photoDish", null)));
        }
    }

    private void loadBundle(Bundle bundle){
        dishname.setText(bundle.getString("dish"));
        desc.setText(bundle.getString("descDish"));
        avail.setText(bundle.getString("avail"));
        price.setText(bundle.getString("price"));
        if(bundle.getString("photoDish")!=null) {
            img.setImageURI(Uri.parse(bundle.getString("photoDish")));
        }

    }

}
