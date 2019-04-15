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
public class DailyFragment extends Fragment {

    private DailyListener listener;

    public interface DailyListener {
        public void setDailyBarTitle(String title);
    }

    public DailyFragment() {
        // Required empty public constructor
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof DailyListener) {
            listener = (DailyListener) context;
        } else {
            throw new ClassCastException(context.toString() + "must implement DailyListner");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        listener.setDailyBarTitle( getResources().getString(R.string.menu_daily));
        return inflater.inflate(R.layout.fragment_daily, container, false);
    }

}
