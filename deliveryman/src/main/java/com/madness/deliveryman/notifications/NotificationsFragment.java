package com.madness.deliveryman.notifications;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.madness.deliveryman.R;

import java.util.ArrayList;

public class NotificationsFragment extends Fragment {

    ArrayList<NotificationsClass> notificationList = new ArrayList<>();
    private RecyclerView recyclerView;
    private NotificationsDataAdapter mAdapter;

    public NotificationsFragment() {
        // Required empty public constructor
        fakeConstructor();
    }

    /* Here is set the content to be shown, this method will be removed from the following lab */
    private void fakeConstructor() {
        NotificationsClass notif1 = new NotificationsClass("Pizza Express", "Order completed! - #2537 Nicola Fioranelli - Deliveryman: #123 - Scheduled delivery: 20.45", "01/05/2019", "19.55");
        this.notificationList.add(notif1);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setNotificationsDataAdapter();
    }

    /* Here is set the Adapter */
    private void setNotificationsDataAdapter() {
        mAdapter = new NotificationsDataAdapter(notificationList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and add the title
        View rootView = inflater.inflate(R.layout.fragment_notifications, container, false);
        getActivity().setTitle(getString(R.string.title_Notifications));

        recyclerView = rootView.findViewById(R.id.recyclerView);
        mAdapter = new NotificationsDataAdapter(notificationList);

        /* Here is checked if there are elements to be displayed, in case nothing can be shown an
        icon is set as visible and the other elements of the fragment are set invisible.
         */
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
            recyclerView = rootView.findViewById(R.id.recyclerView);
            if (recyclerView.getVisibility() == View.VISIBLE) {
                outState.putParcelableArrayList("Notifications", new ArrayList<>(mAdapter.getList()));
            }
        }
    }
}
