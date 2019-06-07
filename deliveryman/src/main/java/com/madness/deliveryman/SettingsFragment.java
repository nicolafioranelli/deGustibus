package com.madness.deliveryman;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
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
    private SharedPreferences pref;

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
                modifyDialog();
                return false;
            }
        });

        delete.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                deleteDialog();
                return false;
            }
        });

        logout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                pref = getActivity().getSharedPreferences("Profile", Context.MODE_PRIVATE);
                pref.edit().clear().apply();
                firebaseAuth.signOut();
                return false;
            }
        });

        getActivity().setTitle(getString(R.string.title_Settings));
    }

    private void modifyDialog() {
        /* Create dialog and populate it with editText */
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.modPassword));

        /* Linear layout for the Dialog */
        LinearLayout layout = new LinearLayout(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(lp);
        layout.setOrientation(LinearLayout.VERTICAL);

        /* Input edit text for the first password */
        final EditText input1 = new EditText(getContext());
        input1.setHint(getString(R.string.passwordHint));
        input1.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD);
        LinearLayout.LayoutParams etLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        etLp.setMargins(4, 16, 4, 4);
        input1.setLayoutParams(etLp);
        layout.addView(input1);

        /* Input edit text for the confirm password */
        final EditText input2 = new EditText(getContext());
        input2.setHint(getString(R.string.passwordRepHint));
        input2.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input2.setLayoutParams(etLp);
        layout.addView(input2);

        /* Set the view and add the buttons */
        builder.setView(layout);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        /* Create the dialog and show it */
        final AlertDialog dialog = builder.create();
        dialog.show();

        /* Override of the on click listeners in order to manage update and password check */
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean closeDialog = false;

                String firstPassword = input1.getText().toString();
                String secondPassword = input2.getText().toString();
                if (firstPassword.isEmpty()) {
                    Toast.makeText(getContext(), getString(R.string.err_passMiss), Toast.LENGTH_SHORT).show();
                } else if (firstPassword.length() < 6) {
                    Toast.makeText(getContext(), getString(R.string.err_passShort), Toast.LENGTH_SHORT).show();
                } else if (secondPassword.isEmpty()) {
                    Toast.makeText(getContext(), getString(R.string.err_passMiss), Toast.LENGTH_SHORT).show();
                } else if (firstPassword.equals(secondPassword)) {
                    update(secondPassword);
                    closeDialog = true;
                } else {
                    Toast.makeText(getContext(), getString(R.string.err_passSame), Toast.LENGTH_LONG).show();
                }

                if (closeDialog)
                    dialog.dismiss();
                //else dialog stays open.
            }
        });
    }

    private void update(String password) {
        /* Get current user and update the password */
        user = firebaseAuth.getCurrentUser();
        if (user != null) {
            user.updatePassword(password)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), getString(R.string.psw_changed), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getContext(), getString(R.string.err_gen), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void deleteDialog() {
        /* Create dialog and populate it with editText */
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.deleteTitle));
        builder.setMessage(getString(R.string.deleteDesc));
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), getString(R.string.deleteCompleted), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        /* Create the dialog and show it */
        final AlertDialog dialog = builder.create();
        dialog.show();
    }
}



