package com.madness.deliveryman;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    Preference modify, delete, logout;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);

        modify = findPreference("account1");
        delete = findPreference("account2");
        logout = findPreference("account3");

        firebaseAuth = FirebaseAuth.getInstance();

        modify.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                // Create and show the dialog.
                ModifyPasswordFragment newFragment = new ModifyPasswordFragment ();
                newFragment.show(ft, "dialog");
                return false;
                //TODO: get result from dialog and display toast of update password
            }
        });

        delete.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                user = firebaseAuth.getCurrentUser();
                if(user!=null){
                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(getContext(),"User deleted",Toast.LENGTH_SHORT).show();
                                        //TODO: start register activity
                                        //startActivity(new Intent(getContext(),RegisterActivity.class));
                                        //finish();
                                    }
                                }
                            });
                }
                return false;
            }
        });

        logout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                firebaseAuth.signOut();
                //startActivity(new Intent(getContext(),LoginActivity.class));
                //finish();
                return false;
            }
        });
    }
}


