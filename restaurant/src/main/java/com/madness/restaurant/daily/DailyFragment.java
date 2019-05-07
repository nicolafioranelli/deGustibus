package com.madness.restaurant.daily;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.madness.restaurant.GlideApp;
import com.madness.restaurant.R;
import com.madness.restaurant.swipe.SwipeController;
import com.madness.restaurant.swipe.SwipeControllerActions;

/**
 * The DailyFragment class is in charge of presenting a ListItem View where will be displayed
 * the different dishes that the Restaurateur will prepare. This is obtained by means of the
 * DailyDataAdapter. The saving functionality is not implemented since will be enlarged with the
 * usage of Firebase.
 */
public class DailyFragment extends Fragment {

    private DailyListener listener;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseDatabase db;
    private DatabaseReference databaseReference;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private SwipeController swipeController;
    private FirebaseRecyclerAdapter adapter;
    private FirebaseUser user;

    public DailyFragment() {
        // Required empty public constructor();
    }

    /* The onAttach method registers the listener */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DailyListener) {
            listener = (DailyListener) context;
        } else {
            throw new ClassCastException(context.toString() + "must implement DailyListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = this.getActivity().getSharedPreferences("DEGUSTIBUS", Context.MODE_PRIVATE);
        editor = pref.edit();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    /* During the creation of the view the title is set and layout is generated */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_dailyoffers, container, false);
        getActivity().setTitle(getResources().getString(R.string.title_Daily));

        db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference();
        recyclerView = rootView.findViewById(R.id.dishes);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        rootView.findViewById(R.id.progress_horizontal).setVisibility(View.VISIBLE);
        final Query query = databaseReference.child("offers").child(user.getUid());

        FirebaseRecyclerOptions<DailyClass> options =
                new FirebaseRecyclerOptions.Builder<DailyClass>()
                        .setQuery(query, DailyClass.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<DailyClass, DailyHolder>(options) {

            @NonNull
            @Override
            public DailyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(viewGroup.getContext()).
                        inflate(R.layout.dailyoffer_listitem, viewGroup, false);
                rootView.findViewById(R.id.progress_horizontal).setVisibility(View.GONE);
                return new DailyHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull DailyHolder holder, int position, @NonNull DailyClass model) {
                holder.dish.setText(model.getDish());
                holder.type.setText(model.getType());
                holder.avail.setText(model.getAvail());
                holder.price.setText(model.getPrice());

                GlideApp.with(holder.pic.getContext())
                        .load(model.getPic())
                        .placeholder(R.drawable.dish_image)
                        .into(holder.pic);
            }

        };

        recyclerView.setAdapter(adapter);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });

        // set swipe controller
        swipeController = new SwipeController((new SwipeControllerActions() {
            @Override
            public void onLeftClicked(int position) {
                listener.addDailyOffer(adapter.getRef(position).getKey());
                super.onLeftClicked(position);
            }

            @Override
            public void onRightClicked(int position) {
                databaseReference = db.getReference();
                Query removeQuery = databaseReference.child("offers")
                        .child(user.getUid())
                        .orderByChild("identifier")
                        .equalTo(adapter.getRef(position).getKey());

                removeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                            singleSnapshot.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                super.onRightClicked(position);
            }
        }), this.getContext());

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(recyclerView);
        return rootView;
    }

    /* Here is set the click listener on the floating button */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FloatingActionButton resFb = getActivity().findViewById(R.id.dailyFab);
        resFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.addDailyOffer("null");
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    /* Here is defined the interface for the HomeActivity in order to manage the click */
    public interface DailyListener {
        void addDailyOffer(String identifier);
    }
}