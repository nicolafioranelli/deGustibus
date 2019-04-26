package com.madness.deliveryman;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * The ModifyPasswordFragment class extends the DialogFragment class and is used to display the edit
 * text to insert the new password.
 * In particular the update method checks also differences in the inserted passwords.
 */
public class ModifyPasswordFragment extends DialogFragment {

    EditText passwordFirst;
    EditText passwordSecond;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.fragment_modify_password, null))
                // Add action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        update();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setTitle("Modify password");

        return builder.create();
    }


    private void update() {
        /* Define authentication instance */
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        /* Retrieve the fields and check the correctness of both fields */
        passwordFirst = getDialog().findViewById(R.id.password);
        passwordSecond = getDialog().findViewById(R.id.passwordRepeat);

        String firstPassword = passwordFirst.getText().toString().trim();
        String secondPassword = passwordSecond.getText().toString().trim();

        //TODO: manage password changes, check both fields are not null, check they are equal, in case display errors and toasts

        //if(firstPassword.equals(secondPassword)) {
        /* Get current user and update the password */
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            user.updatePassword(secondPassword)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity().getApplicationContext(), "Password changed successfully!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
        //}
    }


}
