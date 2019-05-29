package com.madness.degustibus.new_reservations;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anton46.stepsview.StepsView;
import com.madness.degustibus.R;

public class DetailedResFragment extends Fragment {

    private StepsView stepsView;
    private final String[] labels = {"In attesa", "In elaborazione", "In consegna", "Consegnato"};

    public DetailedResFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_detailed_res, container, false);
        stepsView = rootView.findViewById(R.id.stepsView);
        stepsView.setCompletedPosition(2 % labels.length)
                .setLabels(labels)
                .setBarColorIndicator(
                        getContext().getResources().getColor(R.color.material_blue_grey_800))
                .setProgressColorIndicator(getContext().getResources().getColor(R.color.orange))
                .setLabelColorIndicator(getContext().getResources().getColor(R.color.orange))
                .drawView();
        return rootView;
    }

}
