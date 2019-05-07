package com.madness.degustibus.daily;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
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
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.madness.degustibus.BuildConfig;
import com.madness.degustibus.R;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * The NewDailyOffer Fragment is in charge of managing addition and editing of a new daily plate.
 * At the current moment the save function is disabled, since no integration with Firebase has been
 * implemented. The functioning is similar to the EditProfile Fragment.
 */

public class getOfferInfo extends Fragment {

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
    public getOfferInfo() {
        // Required empty public constructor
    }
}
