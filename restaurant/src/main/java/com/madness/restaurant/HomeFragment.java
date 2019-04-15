package com.madness.restaurant;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    private HomeListener listener;

    public interface HomeListener {
        public void setHomeBarTitle(String title);
    }
    public HomeFragment() {
        // Required empty public constructor
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof HomeListener) {
            listener = (HomeListener) context;
        } else {
            throw new ClassCastException(context.toString() + "must implement HomeListner");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        listener.setHomeBarTitle( getResources().getString(R.string.menu_home));
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

}
