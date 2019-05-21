package com.madness.restaurant.reservations;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.madness.restaurant.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NewNotificationClass {

    private Context context;
    private DatabaseReference databaseReference;

    public NewNotificationClass(Context context) {
        this.context = context;
    }

    public void acceptAndSend(final OrderData orderData) {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> updateObject = new HashMap<>();
        updateObject.put("customerAddress", orderData.getCustomerAddress());
        updateObject.put("customerID", orderData.getCustomerID());
        updateObject.put("deliveryDate", orderData.getDeliveryDate());
        updateObject.put("deliveryHour", orderData.getDeliveryHour());
        updateObject.put("deliverymanID", orderData.getRiderID());
        updateObject.put("description", orderData.getDescription());
        updateObject.put("restaurantID", orderData.getRestaurantID());
        updateObject.put("status", "incoming");
        updateObject.put("totalPrice", orderData.getTotalPrice());

        databaseReference.child("orders").child(orderData.getOrderKey()).updateChildren(updateObject).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                /* Perform notification insertion */
                newNotificationOrderComplete(orderData);
            }
        });

    }

    public void refuseAndNotify(final OrderData orderData) {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        Query refuseQuery = databaseReference.child("orders").child(orderData.getOrderKey());

        refuseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    /* Set order as refused */
                    databaseReference.child("restaurants").child(orderData.getRestaurantID()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                                objectMap.put("status", "refused");
                                databaseReference.child("orders").child(dataSnapshot.getKey()).updateChildren(objectMap);

                                /* Send notification to user */
                                final Map<String, Object> newNotification = new HashMap<String, Object>();
                                newNotification.put("type", context.getApplicationContext().getString(R.string.typeNot_refused));

                                Map<String, Object> restaurantMap = (HashMap<String, Object>) snapshot.getValue();
                                String restaurantName = restaurantMap.get("name").toString();
                                newNotification.put("description", context.getApplicationContext().getString(R.string.desc1) + restaurantName);

                                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                Date date = new Date();
                                newNotification.put("date", dateFormat.format(date));

                                databaseReference.child("notifications").child(objectMap.get("customerID").toString()).push().setValue(newNotification);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void newNotificationOrderComplete(final OrderData orderData) {
        ValueEventListener eventListener = databaseReference.child("restaurants").child(orderData.getRestaurantID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    /* Send notification to user */
                    final Map<String, Object> newNotification = new HashMap<String, Object>();
                    newNotification.put("type", context.getApplicationContext().getString(R.string.typeNot_accepted));

                    Map<String, Object> restaurantMap = (HashMap<String, Object>) snapshot.getValue();
                    String restaurantName = restaurantMap.get("name").toString();
                    newNotification.put("description", context.getApplicationContext().getString(R.string.desc3) + orderData.getOrderKey().substring(1, 6) + context.getApplicationContext().getString(R.string.desc4) + restaurantName);

                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date = new Date();
                    newNotification.put("date", dateFormat.format(date));

                    databaseReference.child("notifications").child(orderData.getCustomerID()).push().setValue(newNotification).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            /* Send notification to rider */
                            final Map<String, Object> notificationRider = new HashMap<String, Object>();
                            notificationRider.put("type", context.getApplicationContext().getString(R.string.typeNot_incoming));
                            notificationRider.put("description", context.getApplicationContext().getString(R.string.desc5) + orderData.getOrderKey().substring(1, 6) + context.getApplicationContext().getString(R.string.desc6));
                            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                            Date date = new Date();
                            notificationRider.put("date", dateFormat.format(date));

                            databaseReference.child("notifications").child(orderData.getRiderID()).push().setValue(notificationRider);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //databaseReference.child("restaurants").child(orderData.getRestaurantID()).removeEventListener(eventListener);
    }

}
