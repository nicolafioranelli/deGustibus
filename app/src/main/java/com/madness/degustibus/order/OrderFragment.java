package com.madness.degustibus.order;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.madness.degustibus.R;
import com.madness.degustibus.notifications.NotificationsFragment;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class OrderFragment extends Fragment{

    ArrayList<MenuClass> dishList = new ArrayList<>();
    DatabaseReference databaseRef;
    private RecyclerView recyclerView;
    private MenuDataAdapter mAdapter;
    private Button confirm_btn;
    private Fragment fragment;
    HashMap<String,String> order=new HashMap<>();

    public OrderFragment() {
        // Required empty public constructor
        fakeConstructor();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_order, container, false);
        getActivity().setTitle("New order");

        databaseRef = FirebaseDatabase.getInstance().getReference();

        confirm_btn = rootView.findViewById(R.id.complete_order_btn);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        mAdapter = new MenuDataAdapter(dishList);

        /* Here is checked if there are elements to be displayed, in case nothing can be shown an
        icon is set as visible and the other elements of the fragment are set invisible.

        if (mAdapter.getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);

            LinearLayout linearLayout = rootView.findViewById(R.id.emptyLayout);
            linearLayout.setVisibility(View.VISIBLE);
            LinearLayout linearLayout1 = rootView.findViewById(R.id.fabLayout);
            linearLayout1.setVisibility(View.INVISIBLE);
        } else {
            LinearLayout linearLayout = rootView.findViewById(R.id.emptyLayout);
            linearLayout.setVisibility(View.INVISIBLE);
            LinearLayout linearLayout1 = rootView.findViewById(R.id.fabLayout);
            linearLayout1.setVisibility(View.VISIBLE);

            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(mAdapter);
            LinearLayoutManager manager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(manager);
        }
        */
        recyclerView.setAdapter(mAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);

        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    fragment = null;
                    Class fragmentClass;
                    fragmentClass = CompletedOrderFragment.class;
                    fragment = (Fragment) fragmentClass.newInstance();
                    int i=0;
                    for(MenuClass dish: mAdapter.getList()){

                        if(dish.quantity != "0"){
                            databaseRef.child("prova2").setValue("prova2");
                            order.put("dishname",dish.title);
                            order.put("price",dish.price);
                            order.put("quantity",dish.quantity);
                            databaseRef.child("customers/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/cart").push().setValue(order);
                        }
                        else{
                            databaseRef.child("prova3").setValue("prova2");
                        }
                    }
                } catch (Exception e) {
                    Log.e("MAD", "editProfileClick: ", e);
                }

                ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContent, fragment, "Complete order")
                        .addToBackStack("Home")
                        .commit();
            }
        });
        return rootView;
    }

    /* Here is set the content to be shown, this method will be removed from the following lab */
    private void fakeConstructor() {

        MenuClass dish1 = new MenuClass("Pizza Margherita", "Base impasto integrale, pomodoro, mozzarella, basilico", "8,60 €", "0", null);
        this.dishList.add(dish1);
        dish1 = new MenuClass("Pizza patatine", "Base impasto integrale, pomodoro, mozzarella, basilico", "8,60 €", "0", null);
        this.dishList.add(dish1);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setMenuDataAdapter();
    }

    /* Here is set the Adapter */
    private void setMenuDataAdapter() {
        mAdapter = new MenuDataAdapter(dishList);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            dishList = savedInstanceState.getParcelableArrayList("Menu");
            setMenuDataAdapter();
            recyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        /* Checks if the fragment actually loaded is the home fragment, in case no disable the saving operation */
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.flContent);
        if( fragment instanceof OrderFragment) {
            View rootView = getLayoutInflater().inflate(R.layout.fragment_order, (ViewGroup) getView().getParent(), false);
            recyclerView = rootView.findViewById(R.id.recyclerView);
            if (recyclerView.getVisibility() == View.VISIBLE) {
                outState.putParcelableArrayList("Menu", new ArrayList<>(mAdapter.getList()));
            }
        }
        String piatto = "0";
        for(MenuClass dish: dishList){
            outState.putString(piatto,dish.quantity);
            // Toast.makeText(getContext(), getResources().getString(Integer.valueOf(dish.quantity)), Toast.LENGTH_SHORT).show();
             Toast.makeText(getContext(),dish.quantity,Toast.LENGTH_SHORT).show();
            piatto = String.valueOf(Integer.valueOf(piatto)+1);

        }

    }
    private void loadBundle(Bundle bundle) {
        String piatto = "0";
        for(MenuClass dish: dishList){
            dish.setQuantity(bundle.getString(piatto));
            piatto = String.valueOf(Integer.valueOf(piatto)+1);
        }
    }

    /* Populates the menu with the notification button */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /* Add action to be performed once the item on the toolbar is clicked */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_notifications) {
            Fragment fragment = null;
            Class fragmentClass;
            try {
                fragmentClass = NotificationsFragment.class;
                fragment = (Fragment) fragmentClass.newInstance();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment, "Notifications").addToBackStack("HOME").commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
            DrawerLayout drawer = getActivity().findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }

        return super.onOptionsItemSelected(item);
    }

}
