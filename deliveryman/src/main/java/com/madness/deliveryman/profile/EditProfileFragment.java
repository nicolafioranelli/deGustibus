package com.madness.deliveryman.profile;

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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.madness.deliveryman.BuildConfig;
import com.madness.deliveryman.GlideApp;
import com.madness.deliveryman.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class EditProfileFragment extends Fragment {

    private EditText fullname;
    private EditText email;
    private EditText desc;
    private EditText phone;
    private ImageView img;
    private RadioGroup vehicles;
    private RadioButton button;

    private String cameraFilePath;
    private SharedPreferences pref;
    private String vehicle;

    private Uri mImageUri = null;
    private String selector;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    /* The method allows to retrieve the shared preferences and to let the toolbar be available */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = this.getActivity().getSharedPreferences("Profile", Context.MODE_PRIVATE);
        setHasOptionsMenu(true);
    }

    /* This method simply sets the title on the toolbar */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and add the title
        View rootView = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        rootView.findViewById(R.id.progress_horizontal).setVisibility(View.VISIBLE);
        getActivity().setTitle(getString(R.string.title_Edit));
        return rootView;
    }

    /* The method retrieves all the elements in the view and populates them with the values
     * available in the bundle or in the shared preferences.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fullname = getView().findViewById(R.id.et_edit_fullName);
        email = getView().findViewById(R.id.et_edit_email);
        desc = getView().findViewById(R.id.et_edit_desc);
        phone = getView().findViewById(R.id.et_edit_phone);
        img = getView().findViewById(R.id.imageview);

        loadFromFirebase();

        vehicles = getView().findViewById(R.id.rg_edit_vehicle);
        vehicles.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                button = getView().findViewById(checkedId);
                switch (button.getId()) {
                    case R.id.rb_edit_bike: {
                        vehicle = "bike";
                    }
                    break;
                    case R.id.rb_edit_car: {
                        vehicle = "car";
                    }
                    break;
                    case R.id.rb_edit_motorbike: {
                        vehicle = "motorbike";
                    }
                    break;
                }
            }
        });

        getPhoto(getView());
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
            storeOnFirebase();
            delPrefPhoto();

            /* Handle save option and go back */
            Toast.makeText(getContext(), getResources().getString(R.string.saved), Toast.LENGTH_SHORT).show();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.popBackStackImmediate("PROFILE", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String getPrefPhoto() {
        SharedPreferences pref = getActivity().getSharedPreferences("photo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        return pref.getString("photo", null);
    }

    /* Methods (getters, setters and delete) to retrieve temporary photo uri. */
    private void setPrefPhoto(String cameraFilePath) {
        SharedPreferences pref = getActivity().getSharedPreferences("photo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("photo", cameraFilePath);
        editor.commit();
    }

    private void delPrefPhoto() {
        SharedPreferences pref = getActivity().getSharedPreferences("photo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("photo");
        editor.apply();
    }

    /* This method is used to retrieve the photo via camera or gallery and it is the same
     * of the previous laboratory.
     */
    public void getPhoto(View v) {
        img.setOnClickListener(new View.OnClickListener() {
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
     * the database under the child "riders" with the uid of the current authenticated user.
     */
    private void storeOnFirebase() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        DatabaseReference newItem = FirebaseDatabase.getInstance().getReference().child(user.getUid()).push();
        final StorageReference fileReference = FirebaseStorage.getInstance().getReference().child(user.getUid()).child(newItem.getKey());

        final Map<String, Object> map = new HashMap<>();
        map.put("name", fullname.getText().toString());
        map.put("email", email.getText().toString());
        map.put("desc", desc.getText().toString());
        map.put("phone", phone.getText().toString());
        map.put("vehicle", this.vehicle);

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
                                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                        mDatabase.child("riders").child(user.getUid()).updateChildren(map);

                                    }
                                });
                            }
                        });
            } catch (Exception e) {
                Log.e("MAD", "storeOnFirebase - Exception converting bitmap: ", e);
            }
        } else {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("riders").child(user.getUid()).updateChildren(map);
        }
    }

    private void loadFromFirebase() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("riders").child(user.getUid());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> user = (HashMap<String, Object>) dataSnapshot.getValue();

                /* Load items of the view */
                fullname.setText(user.get("name").toString());
                email.setText(user.get("email").toString());
                desc.setText(user.get("desc").toString());
                phone.setText(user.get("phone").toString());

                String pic = null;
                if (user.get("photo") != null) {
                    pic = user.get("photo").toString();
                }
                /* Glide */
                GlideApp.with(getContext())
                        .load(pic)
                        .placeholder(R.drawable.user_profile)
                        .into(img);

                selector = user.get("vehicle").toString();
                switch (selector) {
                    case "bike": {
                        button = getView().findViewById(R.id.rb_edit_bike);
                        button.toggle();
                    }
                    break;
                    case "car": {
                        button = getView().findViewById(R.id.rb_edit_car);
                        button.toggle();
                    }
                    break;
                    case "motorbike": {
                        button = getView().findViewById(R.id.rb_edit_motorbike);
                        button.toggle();
                    }
                    break;
                }
                getView().findViewById(R.id.progress_horizontal).setVisibility(View.GONE);
                getView().findViewById(R.id.layout).setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
