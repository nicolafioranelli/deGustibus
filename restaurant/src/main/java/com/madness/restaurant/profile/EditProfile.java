package com.madness.restaurant.profile;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.madness.restaurant.BuildConfig;
import com.madness.restaurant.GlideApp;
import com.madness.restaurant.R;
import com.madness.restaurant.home.HomeFragment;
import com.madness.restaurant.picker.TimePickerFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * EditProfile Fragment class is used to manage Restaurateur profile changes. In particular here
 * are defined the methods to pick a picture (through the camera or gallery) and methods to store
 * the file and the other inserted data.
 */

public class EditProfile extends Fragment {

    private EditText fullname;
    private EditText email;
    private EditText desc;
    private EditText phone;
    private AutoCompleteTextView autocompleteView;
    private Switch aSwitch;
    private TextView defaultOpen;
    private TextView defaultClose;
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
    private Uri mImageUri;
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private ValueEventListener listener;
    private DatabaseReference referenceListener;
    private Client client;
    private Index index;

    public EditProfile() {
        // Required empty public constructor
    }

    /* The method allows to retrieve the shared preferences and to let the toolbar be available */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        client = new Client("LRBUKD1XJR", "d796532dfd54cafdf4587b412ad560f8");
        index = client.getIndex("rest_HOME");

        pref = this.getActivity().getSharedPreferences("Profile", Context.MODE_PRIVATE);
        setHasOptionsMenu(true);
    }

    /* This method simply sets the title on the toolbar */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and add the title
        final View rootView = inflater.inflate(R.layout.fragment_profile_edit, container, false);
        getActivity().setTitle(getResources().getString(R.string.title_Edit));
        findViews(rootView);

        aSwitch = rootView.findViewById(R.id.switchGenWork);
        aSwitch.setChecked(true);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rootView.findViewById(R.id.specificHours).setVisibility(View.GONE);
                    rootView.findViewById(R.id.textSpecHour).setVisibility(View.GONE);
                    rootView.findViewById(R.id.textGenHour).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.genHours).setVisibility(View.VISIBLE);
                } else {
                    rootView.findViewById(R.id.specificHours).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.textSpecHour).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.textGenHour).setVisibility(View.GONE);
                    rootView.findViewById(R.id.genHours).setVisibility(View.GONE);
                }
            }
        });

        autocompleteView.setAdapter(new PlacesAutoCompleteAdapter(getActivity(), R.layout.autocomplete_list_item));
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadFromFirebase();
        takeTimeTextViews();
        getPhoto(view);
    }

    /* Menu inflater for toolbar (adds elements inserted in res/menu/main_menu.xml) */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_edit, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /* Click listener to correctly handle actions related to toolbar items */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit) {
            if (TextUtils.isEmpty(fullname.getText()) |
                    TextUtils.isEmpty(desc.getText()) | TextUtils.isEmpty(phone.getText()) |
                    TextUtils.isEmpty(autocompleteView.getText())) {

                fullname.setError(getResources().getString(R.string.name));
                desc.setError(getResources().getString(R.string.descr));
                phone.setError(getResources().getString(R.string.phone));
                autocompleteView.setError(getResources().getString(R.string.address));
            } else {
                storeOnFirebase();
                delPrefPhoto();

                /* Handle save option and go back */
                Toast.makeText(getContext(), getResources().getString(R.string.saved), Toast.LENGTH_SHORT).show();

                if (getArguments() != null) {
                    try {
                        Fragment fragment = null;
                        Class fragmentClass;
                        fragmentClass = HomeFragment.class;
                        fragment = (Fragment) fragmentClass.newInstance();
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.flContent, fragment, "HOME").commit();
                    } catch (Exception e) {

                    }
                    return true;
                } else {
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.popBackStackImmediate("PROFILE", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    return true;
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }


    /* The method allows to set the listeners in the corresponding items in order to get the
     * time pickers once clicked.
     */
    public void takeTimeTextViews() {
        LinearLayout layout1 = getActivity().findViewById(R.id.defaultOpenLinearLayout);
        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay = 0;
                moment = 0;
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getFragmentManager(), "time picker");
            }
        });

        LinearLayout layout2 = getActivity().findViewById(R.id.defaultCloseLinearLayout);
        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay = 0;
                moment = 1;
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getFragmentManager(), "time picker");
            }
        });

        LinearLayout layout3 = getActivity().findViewById(R.id.mondayOpenLinearLayout);
        layout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay = 1;
                moment = 0;
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getFragmentManager(), "time picker");
            }
        });

        LinearLayout layout4 = getActivity().findViewById(R.id.mondayCloseLinearLayout);
        layout4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay = 1;
                moment = 1;
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getFragmentManager(), "time picker");
            }
        });

        LinearLayout layout5 = getActivity().findViewById(R.id.tuesdayOpenLinearLayout);
        layout5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay = 2;
                moment = 0;
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getFragmentManager(), "time picker");
            }
        });

        LinearLayout layout6 = getActivity().findViewById(R.id.tuesdayCloseLinearLayout);
        layout6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay = 2;
                moment = 1;
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getFragmentManager(), "time picker");
            }
        });

        LinearLayout layout7 = getActivity().findViewById(R.id.wednesdayOpenLinearLayout);
        layout7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay = 3;
                moment = 0;
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getFragmentManager(), "time picker");
            }
        });

        LinearLayout layout8 = getActivity().findViewById(R.id.wednesdayCloseLinearLayout);
        layout8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay = 3;
                moment = 1;
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getFragmentManager(), "time picker");
            }
        });

        LinearLayout layout9 = getActivity().findViewById(R.id.thursdayOpenLinearLayout);
        layout9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay = 4;
                moment = 0;
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getFragmentManager(), "time picker");
            }
        });

        LinearLayout layout10 = getActivity().findViewById(R.id.thursdayCloseLinearLayout);
        layout10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay = 4;
                moment = 1;
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getFragmentManager(), "time picker");
            }
        });

        LinearLayout layout11 = getActivity().findViewById(R.id.fridayOpenLinearLayout);
        layout11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay = 5;
                moment = 0;
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getFragmentManager(), "time picker");
            }
        });

        LinearLayout layout12 = getActivity().findViewById(R.id.fridayCloseLinearLayout);
        layout12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay = 5;
                moment = 1;
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getFragmentManager(), "time picker");
            }
        });

        LinearLayout layout13 = getActivity().findViewById(R.id.saturdayOpenLinearLayout);
        layout13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay = 6;
                moment = 0;
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getFragmentManager(), "time picker");
            }
        });

        LinearLayout layout14 = getActivity().findViewById(R.id.saturdayCloseLinearLayout);
        layout14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay = 6;
                moment = 1;
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getFragmentManager(), "time picker");
            }
        });

        LinearLayout layout15 = getActivity().findViewById(R.id.sundayOpenLinearLayout);
        layout15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay = 7;
                moment = 0;
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getFragmentManager(), "time picker");
            }
        });

        LinearLayout layout16 = getActivity().findViewById(R.id.sundayCloseLinearLayout);
        layout16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay = 7;
                moment = 1;
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getFragmentManager(), "time picker");
            }
        });
    }

    /* The method allows to set the values taken with the time pickers in the text views */
    public void setHourAndMinute(int hour, int minute) {
        switch (this.weekDay) {
            case 0:
                if (this.moment == 0) {
                    defaultOpen.setText(String.format("%02d:%02d", hour, minute));
                    mondayOpen.setText(String.format("%02d:%02d", hour, minute));
                    tuesdayOpen.setText(String.format("%02d:%02d", hour, minute));
                    wednesdayOpen.setText(String.format("%02d:%02d", hour, minute));
                    thursdayOpen.setText(String.format("%02d:%02d", hour, minute));
                    fridayOpen.setText(String.format("%02d:%02d", hour, minute));
                    saturdayOpen.setText(String.format("%02d:%02d", hour, minute));
                    sundayOpen.setText(String.format("%02d:%02d", hour, minute));
                } else {
                    defaultClose.setText(String.format("%02d:%02d", hour, minute));
                    mondayClose.setText(String.format("%02d:%02d", hour, minute));
                    tuesdayClose.setText(String.format("%02d:%02d", hour, minute));
                    wednesdayClose.setText(String.format("%02d:%02d", hour, minute));
                    thursdayClose.setText(String.format("%02d:%02d", hour, minute));
                    fridayClose.setText(String.format("%02d:%02d", hour, minute));
                    saturdayClose.setText(String.format("%02d:%02d", hour, minute));
                    sundayClose.setText(String.format("%02d:%02d", hour, minute));
                }
                break;
            case 1:
                if (this.moment == 0)
                    mondayOpen.setText(String.format("%02d:%02d", hour, minute));
                else
                    mondayClose.setText(String.format("%02d:%02d", hour, minute));
                break;
            case 2:
                if (this.moment == 0)
                    tuesdayOpen.setText(String.format("%02d:%02d", hour, minute));
                else
                    tuesdayClose.setText(String.format("%02d:%02d", hour, minute));
                break;
            case 3:
                if (this.moment == 0)
                    wednesdayOpen.setText(String.format("%02d:%02d", hour, minute));
                else
                    wednesdayClose.setText(String.format("%02d:%02d", hour, minute));
                break;
            case 4:
                if (this.moment == 0)
                    thursdayOpen.setText(String.format("%02d:%02d", hour, minute));
                else
                    thursdayClose.setText(String.format("%02d:%02d", hour, minute));
                break;
            case 5:
                if (this.moment == 0)
                    fridayOpen.setText(String.format("%02d:%02d", hour, minute));
                else
                    fridayClose.setText(String.format("%02d:%02d", hour, minute));
                break;
            case 6:
                if (this.moment == 0)
                    saturdayOpen.setText(String.format("%02d:%02d", hour, minute));
                else
                    saturdayClose.setText(String.format("%02d:%02d", hour, minute));
                break;
            case 7:
                if (this.moment == 0)
                    sundayOpen.setText(String.format("%02d:%02d", hour, minute));
                else
                    sundayClose.setText(String.format("%02d:%02d", hour, minute));
                break;
        }

    }

    /* This method is used to retrieve the photo via camera or gallery and it is the same
     * of the previous laboratory.
     */
    public void getPhoto(View v) {
        ImageView imageView = getActivity().findViewById(R.id.imageviewedit);
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
                                        checkPermissionsAndStartCamera();
                                        break;
                                    case 1:
                                        checkPermissionsAndStartGallery();
                                        break;
                                }
                            }
                        });
                pictureDialog.show();
            }
        });
    }

    /* The onActivityResult method is used to get back the picture from the camera or from the
     * gallery; in case the result is canceled (the user presses back before take the picture)
     * the temporary file is canceled.
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result code is RESULT_OK only if the user captures an Image
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 0:
                    mImageUri = Uri.parse(getPrefPhoto());
                    Glide.with(getContext()).load(mImageUri).into(img);
                    setPrefPhoto(mImageUri.toString());
                    break;
                case 1:
                    mImageUri = data.getData();
                    setPrefPhoto(mImageUri.toString());
                    Glide.with(getContext()).load(mImageUri).into(img);
                    break;
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            try {
                File photoToCancel = new File(getPrefPhoto());
                photoToCancel.delete();
            } catch (Exception e) {
                Log.e("MAD", "onActivityResult: ", e);
            }
            delPrefPhoto();
        }
    }

    private String getPrefPhoto() {
        SharedPreferences pref = getActivity().getSharedPreferences("profilePhoto", Context.MODE_PRIVATE);
        return pref.getString("profilePhoto", null);
    }

    private void setPrefPhoto(String cameraFilePath) {
        SharedPreferences pref = getActivity().getSharedPreferences("profilePhoto", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("profilePhoto", cameraFilePath);
        editor.apply();
    }

    private void delPrefPhoto() {
        SharedPreferences pref = getActivity().getSharedPreferences("profilePhoto", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("profilePhoto");
        editor.apply();
    }

    /* -- Methods for permissions --
     * These methods are invoked by the getPhoto() method and first check if there are requested permissions:
     * in case positive do their operations otherwise ask for permission. The result of such request will be
     * caught by the method onRequestPermissionsResult() that in case everything is ok will perform the requested
     * operations, otherwise will do nothing.
     */
    private void checkPermissionsAndStartGallery() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 21);
        } else {
            Log.d("MAD", "onCreate: permission granted");
            //Create an Intent with action as ACTION_PICK
            Intent intent = new Intent(Intent.ACTION_PICK);
            // Sets the type as image/*. This ensures only components of type image are selected
            intent.setType("image/*");
            //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
            String[] mimeTypes = {"image/jpeg", "image/png"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            // Launching the Intent
            startActivityForResult(intent, 1);
        }
    }

    private void checkPermissionsAndStartCamera() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            /* Check permissions, if not ask for them, the result will be catched in the method on RequestPermissionsResult */
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 20);
        } else {
            // If permissions are granted then start the camera
            /* Define image file where the camera will put the taken picture */
            File image;
            /* Get the directory to store image */
            File storageDir = getContext().getFilesDir();
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
                intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".provider", image));
                startActivityForResult(intent, 0);
            } catch (Exception e) {
                Log.e("MAD", "getPhoto: ", e);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d("MAD", "onRequestPermissionsResult: HERE");
        switch (requestCode) {
            case 20: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    /* Permission was granted! Start the camera and take the picture */
                    /* Define image file where the camera will put the taken picture */
                    File image;
                    /* Get the directory to store image */
                    File storageDir = getContext().getFilesDir();
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
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".provider", image));
                        startActivityForResult(intent, 0);
                    } catch (Exception e) {
                        Log.e("MAD", "getPhoto: ", e);
                    }
                } else {
                    /* Permission denied! Disable the functionality that depends on this permission. */
                    Toast.makeText(getContext(), getResources().getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case 21: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Create an Intent with action as ACTION_PICK
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    // Sets the type as image/*. This ensures only components of type image are selected
                    intent.setType("image/*");
                    //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
                    String[] mimeTypes = {"image/jpeg", "image/png"};
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                    // Launching the Intent
                    startActivityForResult(intent, 1);
                } else {
                    /* Permission denied! Disable the functionality that depends on this permission. */
                    Toast.makeText(getContext(), getResources().getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }

    /*
     * This method allows to save personal informations on Firebase. We need first to get the current
     * user instance, then prepare an hash map where information can be put and last insert the map into
     * the database under the child "restaurants" with the uid of the current authenticated user.
     */
    private void storeOnFirebase() {
        final Map<String, Object> map = new HashMap<>();
        map.put("name", fullname.getText().toString());
        map.put("email", email.getText().toString());
        map.put("desc", desc.getText().toString());
        map.put("phone", phone.getText().toString());
        map.put("address", autocompleteView.getText().toString());
        if (getArguments() != null) {
            map.put("rating", 0);
        }
        map.put("defaultOpen", defaultOpen.getText().toString());
        map.put("defaultClose", defaultClose.getText().toString());
        map.put("mondayOpen", mondayOpen.getText().toString());
        map.put("mondayClose", mondayClose.getText().toString());
        map.put("tuesdayOpen", tuesdayOpen.getText().toString());
        map.put("tuesdayClose", tuesdayClose.getText().toString());
        map.put("wednesdayOpen", wednesdayOpen.getText().toString());
        map.put("wednesdayClose", wednesdayClose.getText().toString());
        map.put("thursdayOpen", thursdayOpen.getText().toString());
        map.put("thursdayClose", thursdayClose.getText().toString());
        map.put("fridayOpen", fridayOpen.getText().toString());
        map.put("fridayClose", fridayClose.getText().toString());
        map.put("saturdayOpen", saturdayOpen.getText().toString());
        map.put("saturdayClose", saturdayClose.getText().toString());
        map.put("sundayOpen", sundayOpen.getText().toString());
        map.put("sundayClose", sundayClose.getText().toString());

        DatabaseReference newItem = databaseReference.child(user.getUid()).push();  // generate a new key in /offers/{uID}
        final StorageReference fileReference = FirebaseStorage.getInstance().getReference().child(user.getUid()).child(newItem.getKey());  // generate a new child in /

        // Store the picture into firestore
        if (mImageUri != null) {
            try {
                Bitmap bmp = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), mImageUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                byte[] data = baos.toByteArray();
                //uploading the image
                fileReference.putBytes(data)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                  @Override
                                                  public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                      fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                          @Override
                                                          public void onSuccess(Uri uri) {
                                                              String imageUrl = uri.toString();
                                                              map.put("photo", imageUrl);
                                                              databaseReference.child("restaurants").child(user.getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                  @Override
                                                                  public void onComplete(@NonNull Task<Void> task) {
                                                                      try {
                                                                          JSONObject object = new JSONObject()
                                                                                  .put("name", map.get("name").toString())
                                                                                  .put("desc", map.get("desc").toString())
                                                                                  .put("address", map.get("address").toString())
                                                                                  .put("rating", map.get("rating").toString())
                                                                                  .put("photo", map.get("photo").toString());

                                                                          index.addObjectAsync(object, user.getUid(), null);
                                                                      } catch (JSONException e) {

                                                                      }
                                                                  }
                                                              });

                                                          }
                                                      });
                                                  }
                                              }
                        );
            } catch (Exception e) {
                Log.e("MAD", "storeOnFirebase - Exception converting bitmap: ", e);
            }
        } else {
            databaseReference.child("restaurants").child(user.getUid()).updateChildren(map);
        }
    }

    private void loadFromFirebase() {
        referenceListener = databaseReference.child("restaurants").child(user.getUid());
        listener = referenceListener.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ProfileClass profile = dataSnapshot.getValue(ProfileClass.class);

                    fullname.setText(profile.getName());
                    email.setText(profile.getEmail());
                    desc.setText(profile.getDesc());
                    phone.setText(profile.getPhone());
                    autocompleteView.setText(profile.getAddress());
                    defaultOpen.setText(profile.getDefaultOpen());
                    defaultClose.setText(profile.getDefaultClose());
                    mondayOpen.setText(profile.getMondayOpen());
                    mondayClose.setText(profile.getMondayClose());
                    tuesdayOpen.setText(profile.getTuesdayOpen());
                    tuesdayClose.setText(profile.getTuesdayClose());
                    wednesdayOpen.setText(profile.getWednesdayOpen());
                    wednesdayClose.setText(profile.getWednesdayClose());
                    thursdayOpen.setText(profile.getThursdayOpen());
                    thursdayClose.setText(profile.getThursdayClose());
                    fridayOpen.setText(profile.getFridayOpen());
                    fridayClose.setText(profile.getFridayClose());
                    saturdayOpen.setText(profile.getSaturdayOpen());
                    saturdayClose.setText(profile.getSaturdayClose());
                    sundayOpen.setText(profile.getSundayOpen());
                    sundayClose.setText(profile.getSundayClose());

                    String pic = null;
                    if (profile.getPhoto() != null) {
                        pic = profile.getPhoto();
                    }
                    /* Glide */
                    GlideApp.with(getContext())
                            .load(pic)
                            .placeholder(R.drawable.user_profile)
                            .into(img);
                } else {
                    email.setText(user.getEmail());
                    defaultOpen.setText(getResources().getString(R.string.frProfile_defOpen));
                    defaultClose.setText(getResources().getString(R.string.frProfile_defClose));
                    mondayOpen.setText(getResources().getString(R.string.frProfile_defOpen));
                    mondayClose.setText(getResources().getString(R.string.frProfile_defClose));
                    tuesdayOpen.setText(getResources().getString(R.string.frProfile_defOpen));
                    tuesdayClose.setText(getResources().getString(R.string.frProfile_defClose));
                    wednesdayOpen.setText(getResources().getString(R.string.frProfile_defOpen));
                    wednesdayClose.setText(getResources().getString(R.string.frProfile_defClose));
                    thursdayOpen.setText(getResources().getString(R.string.frProfile_defOpen));
                    thursdayClose.setText(getResources().getString(R.string.frProfile_defClose));
                    fridayOpen.setText(getResources().getString(R.string.frProfile_defOpen));
                    fridayClose.setText(getResources().getString(R.string.frProfile_defClose));
                    saturdayOpen.setText(getResources().getString(R.string.frProfile_defOpen));
                    saturdayClose.setText(getResources().getString(R.string.frProfile_defClose));
                    sundayOpen.setText(getResources().getString(R.string.frProfile_defOpen));
                    sundayClose.setText(getResources().getString(R.string.frProfile_defClose));
                }
                getView().findViewById(R.id.progress_horizontal).setVisibility(View.GONE);
                getView().findViewById(R.id.layout).setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void findViews(View view) {
        fullname = view.findViewById(R.id.et_edit_fullName);
        email = view.findViewById(R.id.et_edit_email);
        desc = view.findViewById(R.id.et_edit_desc);
        phone = view.findViewById(R.id.et_edit_phone);
        autocompleteView = view.findViewById(R.id.autocomplete);
        defaultOpen = view.findViewById(R.id.et_edit_defaultOpen);
        defaultClose = view.findViewById(R.id.et_edit_defaultClose);
        mondayOpen = view.findViewById(R.id.et_edit_mondayOpen);
        mondayClose = view.findViewById(R.id.et_edit_mondayClose);
        tuesdayOpen = view.findViewById(R.id.et_edit_tuesdayOpen);
        tuesdayClose = view.findViewById(R.id.et_edit_tuesdayClose);
        wednesdayOpen = view.findViewById(R.id.et_edit_wednesdayOpen);
        wednesdayClose = view.findViewById(R.id.et_edit_wednesdayClose);
        thursdayOpen = view.findViewById(R.id.et_edit_thursdayOpen);
        thursdayClose = view.findViewById(R.id.et_edit_thursdayClose);
        fridayOpen = view.findViewById(R.id.et_edit_fridayOpen);
        fridayClose = view.findViewById(R.id.et_edit_fridayClose);
        saturdayOpen = view.findViewById(R.id.et_edit_saturdayOpen);
        saturdayClose = view.findViewById(R.id.et_edit_saturdayClose);
        sundayOpen = view.findViewById(R.id.et_edit_sundayOpen);
        sundayClose = view.findViewById(R.id.et_edit_sundayClose);
        img = view.findViewById(R.id.imageviewedit);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        referenceListener.removeEventListener(listener);
    }

    /* Class for the autocomplete Text view */
    class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
        ArrayList<String> resultList;
        Context mContext;
        int mResource;
        PlaceAPI mPlaceAPI = new PlaceAPI();

        public PlacesAutoCompleteAdapter(Context context, int resource) {
            super(context, resource);
            mContext = context;
            mResource = resource;
        }

        @Override
        public int getCount() {
            // Last item will be the footer
            return resultList.size();
        }

        @Override
        public String getItem(int position) {
            return resultList.get(position);
        }

        @Override
        public Filter getFilter() {
            final Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    synchronized (filterResults) {
                        if (constraint != null) {
                            resultList = mPlaceAPI.autocomplete(constraint.toString());
                            filterResults.values = resultList;
                            filterResults.count = resultList.size();
                        }
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    try {
                        if (results != null && results.count > 0) {
                            notifyDataSetChanged();
                        } else {
                            notifyDataSetInvalidated();
                        }
                    } catch (Exception e) {

                    }
                }
            };
            return filter;
        }
    }
}
