package com.madness.degustibus;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * The Settings Fragment is a fragment used to display preferences of the app (here in particular are
 * present the methods for modify password, delete account and logout from app). The Fragment extends
 * the PreferenceFragmentCompat class from which inherits objects and methods (onCreatePreferences). The
 * corresponding view is in xml/preferences.xml and not in the standard layout folder.
 */

public class SettingsFragment extends PreferenceFragmentCompat {

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    Preference modify, delete, logout;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        /* Inflate the view which is in xml/ folder */
        addPreferencesFromResource(R.xml.preferences);

        /* Retrieve objects of the view */
        modify = findPreference("account1");
        delete = findPreference("account2");
        logout = findPreference("account3");

        firebaseAuth = FirebaseAuth.getInstance();

        /* Set listeners */
        modify.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                // Create and show the dialog.
                ModifyPasswordFragment newFragment = new ModifyPasswordFragment();
                newFragment.show(ft, "dialog");
                return false;
            }
        });

        delete.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), "User deleted", Toast.LENGTH_SHORT).show();
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
                return false;
            }
        });

        getActivity().setTitle(getString(R.string.title_Settings));
    }
}


