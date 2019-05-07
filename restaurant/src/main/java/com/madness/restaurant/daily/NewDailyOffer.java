package com.madness.restaurant.daily;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.madness.restaurant.BuildConfig;
import com.madness.restaurant.GlideApp;
import com.madness.restaurant.R;

import java.io.File;

/**
 * The NewDailyOffer Fragment is in charge of managing addition and editing of a new daily plate.
 * At the current moment the save function is disabled, since no integration with Firebase has been
 * implemented. The functioning is similar to the EditProfile Fragment.
 */

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
    private String id;
    private String imageUrl;

    private Uri mImageUri;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private FirebaseUser user;

    public NewDailyOffer() {
        // Required empty public constructor
    }

    /* Retrieve data and enable the toolbar */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        storageReference = FirebaseStorage.getInstance().getReference("offers");
        databaseReference = FirebaseDatabase.getInstance().getReference("offers");

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
    }

    /* Set the title and inflate view */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_new_daily_offer, container, false);
        getActivity().setTitle(getResources().getString(R.string.title_Daily));
        return rootView;
    }

    /* Retrieve elements and set the listeners */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        minusBtn = getActivity().findViewById(R.id.button_minus);
        plusBtn = getActivity().findViewById(R.id.button_plus);
        dishname = getActivity().findViewById(R.id.et_dish_name);
        desc = getActivity().findViewById(R.id.et_desc);
        avail = getActivity().findViewById(R.id.et_avail);
        price = getActivity().findViewById(R.id.et_price);
        img = getActivity().findViewById(R.id.imageviewfordish);

        view.findViewById(R.id.progress_horizontal).setVisibility(View.VISIBLE);

        /* Check if it is a new insertion or an edit one in case id equals to null is a new insertion
         * and the view is populated with the canonical strings else they are downloaded from firebase.
         */
        Bundle bundle = getArguments();
        id = bundle.getString("id");
        if (id.equals("null")) {
            dishname.setText(getResources().getString(R.string.frDaily_defName));
            desc.setText(getResources().getString(R.string.frDaily_defDesc));
            avail.setText(String.valueOf(0));
            price.setText(String.valueOf(0.00));
            img.setImageResource(R.drawable.dish_image);
            view.findViewById(R.id.progress_horizontal).setVisibility(View.GONE);
        } else {
            loadFromFirebase(bundle.getString("id"), view);
        }

        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avail = getActivity().findViewById(R.id.et_avail);
                String num = avail.getText().toString();
                int n = Integer.parseInt(num);
                if (n > 0) {
                    n--;
                }
                avail.setText(String.valueOf(n));
            }
        });

        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avail = getActivity().findViewById(R.id.et_avail);
                String num = avail.getText().toString();
                int n = Integer.parseInt(num) + 1;
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
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void getPhoto(View v) {
        ImageView imageView = getActivity().findViewById(R.id.imageviewfordish);
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

    private String getPrefPhoto() {
        SharedPreferences pref = getActivity().getSharedPreferences("photoDish", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        return pref.getString("photoDish", null);
    }

    private void setPrefPhoto(String cameraFilePath) {
        SharedPreferences pref = getActivity().getSharedPreferences("photoDish", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("photoDish", cameraFilePath);
        editor.apply();
    }

    private void delPrefPhoto() {
        SharedPreferences pref = getActivity().getSharedPreferences("photoDish", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("photoDish");
        editor.apply();
    }

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

    private void storeOnFirebase() {
        if (id.equals("null")) {  // -- NEW ELEMENT --
            final DatabaseReference newItem = databaseReference.child(user.getUid()).push();  // generate a new key in /offers/{uID}
            final String newItemKey = newItem.getKey();
            final StorageReference fileReference = storageReference.child(user.getUid()).child(newItem.getKey());  // generate a new child in /

            // store the picture into firestore
            if (mImageUri != null) {
                fileReference.putFile(mImageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                  @Override
                                                  public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                      Log.d("MAD", "onSuccess!");
                                                      fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                          @Override
                                                          public void onSuccess(Uri uri) {
                                                              imageUrl = uri.toString();

                                                              // create a new daily class container
                                                              DailyClass dailyClass = new DailyClass(
                                                                      dishname.getText().toString(),  // dishname from textview
                                                                      desc.getText().toString(),      // desc from textview
                                                                      avail.getText().toString(),     // avail from textviev
                                                                      price.getText().toString(),     // price from textview
                                                                      imageUrl,       // url from previous storage on firestore
                                                                      user.getUid(),                  // TODO REMOVE IT
                                                                      newItemKey                // unique key already generated with `push()`
                                                              );

                                                              newItem.setValue(dailyClass).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                  @Override
                                                                  public void onSuccess(Void aVoid) {
                                                                      Log.d("MAD", "Success!");
                                                                  }
                                                              });

                                                          }
                                                      });
                                                  }
                                              }
                        );

            } else {
                // create a new daily class container
                DailyClass dailyClass = new DailyClass(
                        dishname.getText().toString(),  // dishname from textview
                        desc.getText().toString(),      // desc from textview
                        avail.getText().toString(),     // avail from textviev
                        price.getText().toString(),     // price from textview
                        imageUrl,       // url from previous storage on firestore
                        user.getUid(),                  // TODO REMOVE IT
                        newItemKey                // unique key already generated with `push()`
                );

                newItem.setValue(dailyClass).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("MAD", "Success!");
                    }
                });
            }
        } else {  // -- UPDATE --
            final DatabaseReference updateItem = databaseReference.child(user.getUid()).child(id);
            final StorageReference fileReference = storageReference.child(user.getUid()).child(id);  // generate a new child in /

            // store the picture into firestore
            if (mImageUri != null) {
                fileReference.putFile(mImageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                  @Override
                                                  public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                      Log.d("MAD", "onSuccess!");
                                                      fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                          @Override
                                                          public void onSuccess(Uri uri) {
                                                              imageUrl = uri.toString();

                                                              // create a new daily class container
                                                              DailyClass dailyClass = new DailyClass(
                                                                      dishname.getText().toString(),  // dishname from textview
                                                                      desc.getText().toString(),      // desc from textview
                                                                      avail.getText().toString(),     // avail from textviev
                                                                      price.getText().toString(),     // price from textview
                                                                      imageUrl,       // url from previous storage on firestore
                                                                      user.getUid(),                  // TODO REMOVE IT
                                                                      id
                                                              );

                                                              updateItem.setValue(dailyClass).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                  @Override
                                                                  public void onSuccess(Void aVoid) {
                                                                      Log.d("MAD", "Success!");
                                                                  }
                                                              });

                                                          }
                                                      });
                                                  }
                                              }
                        );
            } else {
                // create a new daily class container
                DailyClass dailyClass = new DailyClass(
                        dishname.getText().toString(),  // dishname from textview
                        desc.getText().toString(),      // desc from textview
                        avail.getText().toString(),     // avail from textviev
                        price.getText().toString(),     // price from textview
                        imageUrl,       // url from previous storage on firestore
                        user.getUid(),                  // TODO REMOVE IT
                        id
                );

                updateItem.setValue(dailyClass).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("MAD", "Success!");
                    }
                });
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit) {
            storeOnFirebase();
            Toast.makeText(getContext(), getResources().getString(R.string.saved), Toast.LENGTH_SHORT).show();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.popBackStackImmediate("DAILY", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadFromFirebase(String id, final View view) {
        Query query = databaseReference.child(user.getUid()).orderByKey().equalTo(id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        DailyClass d = snapshot.getValue(DailyClass.class);
                        dishname.setText(d.getDish());
                        desc.setText(d.getType());
                        avail.setText(d.getAvail());
                        price.setText(d.getPrice());
                        GlideApp.with(getContext())
                                .load(d.getPic())
                                .placeholder(R.drawable.dish_image)
                                .into(img);
                    }
                }
                view.findViewById(R.id.progress_horizontal).setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MAD", "onCancelled: ", databaseError.toException());
            }
        });
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

}
