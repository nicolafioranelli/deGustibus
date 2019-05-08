package com.madness.degustibus.notifications;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.madness.degustibus.R;
import com.madness.degustibus.order.SummaryOrderFragment;

import java.util.ArrayList;

public class NotificationsFragment extends Fragment implements NotificationsDataAdapter.ItemClickListener{

    ArrayList<NotificationsClass> notificationList = new ArrayList<>();
    private RecyclerView recyclerView;
    private NotificationsDataAdapter mAdapter;
    private  NotificationsClass notif;
    private ArrayList<NotificationsClass> notifList= new ArrayList<>();
    private Fragment fragment;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;
    private DatabaseReference databaseRef;

    public NotificationsFragment() {
        // Required empty public constructor
       // fakeConstructor();
    }

    /* Here is set the content to be shown, this method will be removed from the following lab */
   /* private void fakeConstructor() {
        NotificationsClass notif1 = new NotificationsClass("Pizza Express", "Order completed! - #2537 Nicola Fioranelli - Deliveryman: #123 - Scheduled delivery: 20.45", "01/05/2019", "19.55");
        this.notificationList.add(notif1);
    }*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setNotificationsDataAdapter();
        mAdapter = new NotificationsDataAdapter(notifList,this);


    }

    /* Here is set the Adapter */
    private void setNotificationsDataAdapter() {
        mAdapter = new NotificationsDataAdapter(notificationList,this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and add the title
        final View rootView = inflater.inflate(R.layout.fragment_notifications, container, false);
        getActivity().setTitle(getString(R.string.title_Notifications));

        //mAdapter = new NotificationsDataAdapter(notifList,this);
        recyclerView = rootView.findViewById(R.id.recyclerViewNotf);
        databaseRef = FirebaseDatabase.getInstance().getReference();
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        /* Here is checked if there are elements to be displayed, in case nothing can be shown an
        icon is set as visible and the other elements of the fragment are set invisible.
         */
        populateList();

        /*FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseRef = database.getReference("orders");
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //this method is called once with the initial value and again whenever data at this location is updated
                for(DataSnapshot dS : dataSnapshot.getChildren()){
                    notif = new NotificationsClass(dS.getValue(NotificationsClass.class).getTitle(),dS.getValue(NotificationsClass.class).getDescription(),null,null,dS.getValue(NotificationsClass.class).getPrice(),dS.getKey());
                    notifList.add(notif);
                }




                if (mAdapter.getItemCount() == 0) {
                    recyclerView.setVisibility(View.GONE);

                    LinearLayout linearLayout = rootView.findViewById(R.id.emptyLayout);
                    linearLayout.setVisibility(View.VISIBLE);
                } else {
                    LinearLayout linearLayout = rootView.findViewById(R.id.emptyLayout);
                    linearLayout.setVisibility(View.INVISIBLE);

                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setAdapter(mAdapter);
                    LinearLayoutManager manager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(manager);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            notificationList = savedInstanceState.getParcelableArrayList("Notifications");
            setNotificationsDataAdapter();
            recyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        /* Checks if the fragment actually loaded is the home fragment, in case no disable the saving operation */
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.flContent);
        if( fragment instanceof NotificationsFragment ) {
            View rootView = getLayoutInflater().inflate(R.layout.fragment_notifications, (ViewGroup) getView().getParent(), false);
            recyclerView = rootView.findViewById(R.id.recyclerViewNotf);
            if (recyclerView.getVisibility() == View.VISIBLE) {
                outState.putParcelableArrayList("Notifications", new ArrayList<>(mAdapter.getList()));
            }
        }
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
         notifList.get(clickedItemIndex);
        try {

            Bundle bundle = new Bundle();
            bundle.putString("idOrder",notifList.get(clickedItemIndex).idOrder);
            fragment = null;
            Class fragmentClass;
            fragment = (Fragment) SummaryOrderFragment.class.newInstance();
            fragment.setArguments(bundle);

            int i=0;


        } catch (Exception e) {
            Log.e("MAD", "editProfileClick: ", e);
        }

        ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction()
                .replace(R.id.flContent, fragment, " Order")
                .addToBackStack("Home")
                .commit();

    }
    void populateList (){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("orders");
        Query query = FirebaseDatabase.getInstance().getReference().child("orders");

        FirebaseRecyclerOptions<NotificationsClass> options =
                new FirebaseRecyclerOptions.Builder<NotificationsClass>()
                        .setQuery(query, new SnapshotParser<NotificationsClass>() {
                            @NonNull
                            @Override
                            public NotificationsClass parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return notif = new NotificationsClass(snapshot.getValue(NotificationsClass.class).getTitle(),snapshot.getValue(NotificationsClass.class).getDescription(),null,null,snapshot.getValue(NotificationsClass.class).getPrice(),snapshot.getKey());
                            }
                        })
                        .build();
        adapter = new FirebaseRecyclerAdapter<NotificationsClass, NotificationHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull NotificationHolder holder, final int position, @NonNull NotificationsClass model) {
                holder.title.setText(model.getTitle());
                holder.date.setText(model.getDate());
                holder.description.setText(model.getDescription());
                holder.hour.setText(model.getHour());
                holder.price.setText(model.getPrice());
            }

            @Override
            public NotificationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.notifications_listitem, parent, false);

                NotificationHolder hold = new NotificationHolder(view);
                return hold;

            }

        };

        recyclerView.setAdapter(adapter);

    }
}
