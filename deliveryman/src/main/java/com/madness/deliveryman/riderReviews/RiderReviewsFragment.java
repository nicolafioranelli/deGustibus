package com.madness.deliveryman.riderReviews;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madness.deliveryman.R;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RiderReviewsFragment extends Fragment {

    /* Database references */
    private DatabaseReference databaseReference;
    private DatabaseReference reviewReference;

    /* Value Event Listeners */
    private ValueEventListener reviewListener;

    /* Firebase */
    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private LinearLayout empty;
    private RecyclerView recyclerView;
    private View progressBar;

    private LinearLayoutManager linearLayoutManager;
    private RiderReviewsAdapter adapter;
    private RiderReviewsComparable review;
    private String sortBy="NULL";
    private View view;

    public RiderReviewsFragment() {
        // Required empty public constructor
    }

    /* In the onCreate method all variables containing useful information are set in a way that other
     * methods can use them once called.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
    }

    /* Menu inflater for toolbar (adds elements inserted in res/menu/menu_sort.xml) */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_sort, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /* The onCreateView allows to inflate the view of the fragment, in particular here are load information
     * from Firebase related to the reviews.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_rider_reviews, container, false);
        getActivity().setTitle(getResources().getString(R.string.reviews));

        empty=rootView.findViewById(R.id.emptyLayout);
        progressBar = rootView.findViewById(R.id.progress_horizontal);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        view = rootView;
        loadAdapter();
        return rootView;
    }

    /* Click listener to correctly handle actions related to toolbar items */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sort) {
            showSortDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //show an AlertDialog in order to choose how to sort the reviews
    private void showSortDialog() {
        String byName=getResources().getText(R.string.by_name).toString();
        String byRating=getResources().getText(R.string.by_rating).toString();
        String byDate=getResources().getText(R.string.by_date).toString();
        String[] options={byName, byRating, byDate};
        AlertDialog.Builder builder= new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getText(R.string.sort_by).toString());
        builder.setIcon(R.drawable.ic_action_sort);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0){
                    sortBy="byName";
                    loadAdapter();
                }
                if(which==1){
                    sortBy="byRating";
                    loadAdapter();
                }
                if(which==2){
                    sortBy="byDate";
                    loadAdapter();
                }

            }
        });
        builder.create().show();
    }


    @Override
    public void onDetach() {
        super.onDetach();
        try {
            reviewReference.removeEventListener(reviewListener);
        } catch (Exception e) {
            Log.e("MAD", "onDetach: ", e);
        }
    }


    /* This method allows to download data from Firebase through different calls to Event Listeners
     * at the end the custom Adapter is populated and are present also some methods for data change/delete.
     */
    private void loadAdapter() {
        progressBar.setVisibility(View.VISIBLE);
        /* Get all the current Rider's reviews */
        getReviews(new GetReviewsCallback() {
            /* Save the reviews in a List of RiderReviewsComparable type */
            List<RiderReviewsComparable> list = new ArrayList<>();

            @Override
            public void onCallback(RiderReviewsComparable review) {
                boolean exists = false;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getKey().equals(review.getKey())) {
                        exists = true;
                        list.set(i, review);
                    }
                }
                if(exists) {
                    adapter.updateData(list);
                } else {
                    //if the retrieved review is not already in the list, it is added
                    list.add(review);
                    //Sort the reviews, if asked
                    if(sortBy.compareTo("byName")==0){
                        Collections.sort(list, new Comparator<RiderReviewsComparable>() {
                            public int compare(RiderReviewsComparable obj1, RiderReviewsComparable obj2) {
                                // ## Order By Name
                                return obj1.getName().compareTo((obj2.getName()));
                            }
                        });
                    }
                    if(sortBy.compareTo("byRating")==0){
                        Collections.sort(list, new Comparator<RiderReviewsComparable>() {
                            public int compare(RiderReviewsComparable obj1, RiderReviewsComparable obj2) {
                                // ## Order By Rating
                                return obj2.getRating().compareTo((obj1.getRating()));
                            }
                        });
                    }
                    if(sortBy.compareTo("byDate")==0){
                        Collections.sort(list, new Comparator<RiderReviewsComparable>() {
                            public int compare(RiderReviewsComparable obj1, RiderReviewsComparable obj2) {
                                // ## Order By Date
                                String date1=obj1.getDate().substring(6)+ // get yyyy
                                        obj1.getDate().substring(3,5)+ // get MM
                                        obj1.getDate().substring(0,2); // get dd
                                String date2=obj2.getDate().substring(6)+ // get yyyy
                                        obj2.getDate().substring(3,5)+ // get MM
                                        obj2.getDate().substring(0,2); // get dd
                                return date1.compareTo(date2);
                            }
                        });
                    }
                    /* Set the adapter and show the recycler view while make invisible the progress bar */
                    linearLayoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(linearLayoutManager);
                    adapter = new RiderReviewsAdapter(getContext(), view, list);
                    recyclerView.setAdapter(adapter);
                    view.findViewById(R.id.progress_horizontal).setVisibility(View.GONE);
                }

            }
        });
    }

    /* This method retrieves data about the reviews */
    private void retrieveData(String key, final DataRetrieveCallback callback) {
        final String userKey=key;
        reviewReference = databaseReference.child("ratings").child("riders").child(user.getUid()).child(key);
        reviewListener = reviewReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> user = (HashMap<String, Object>) dataSnapshot.getValue();
                user.put("key",userKey);
                callback.onCallback(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /* This method retrieves all the rider's reviews*/
    private void getReviews(final GetReviewsCallback callback) {
        final DatabaseReference progressRef = databaseReference.child("ratings").child("riders");
        progressRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //check out all riders who received reviews
                    for(DataSnapshot dSnapshot : dataSnapshot.getChildren()) {
                        //if there is a child who's key is current rider's key
                        if(dSnapshot.getKey().compareTo(user.getUid())==0){
                            //Set the "No reviews available" Layout to INVISIBLE
                            empty.setVisibility(View.GONE);
                            progressBar.setVisibility(View.VISIBLE);
                            progressRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    //for each review of the current rider
                                    for(DataSnapshot dSnapshot : dataSnapshot.getChildren()) {
                                        retrieveData(dSnapshot.getKey(), new DataRetrieveCallback() {
                                            @Override
                                            public void onCallback(Map user) {
                                                /* This method retrieves the information of the reviews and will add them to the item to be passed to the adapter */
                                                review = null;
                                                review = new RiderReviewsComparable();
                                                review.setName(user.get("name").toString());
                                                review.setDate(user.get("date").toString());
                                                review.setRating(user.get("value").toString());
                                                review.setComment(user.get("comment").toString());
                                                review.setKey(user.get("key").toString());
                                                callback.onCallback(review);
                                            }
                                        });
                                    }
                                    view.findViewById(R.id.progress_horizontal).setVisibility(View.GONE);
                                    view.findViewById(R.id.recyclerView).setVisibility(View.VISIBLE








                                    );
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // A review cannot be deleted
                                }
                            });
                        }
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // A rider cannot be deleted
            }
        });
    }

    /* Interfaces for callbacks */


    public interface DataRetrieveCallback {
        void onCallback(Map user);
    }

    public interface GetReviewsCallback {
        void onCallback(RiderReviewsComparable rider);
    }
}
