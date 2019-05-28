package com.madness.restaurant.insights;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madness.restaurant.R;
import com.madness.restaurant.notifications.NotificationsFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class InsightsFragment extends Fragment {

    private PieChart chart;

    protected final String[] parties = new String[] {
            "Party A", "Party B", "Party C", "Party D", "Party E", "Party F", "Party G", "Party H",
            "Party I", "Party J", "Party K", "Party L", "Party M", "Party N", "Party O", "Party P",
            "Party Q", "Party R", "Party S", "Party T", "Party U", "Party V", "Party W", "Party X",
            "Party Y", "Party Z"
    };

    public InsightsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and add the title
        final View rootView = inflater.inflate(R.layout.fragment_insights, container, false);
        getActivity().setTitle(getString(R.string.menu_insights));


        halfPieConfigurations(rootView);

        return rootView;
    }

    private void halfPieConfigurations(View rootView){
        chart = rootView.findViewById(R.id.pieChart);
        chart.setBackgroundColor(Color.TRANSPARENT);
        //moveOffScreen();
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        /*chart.setCenterTextTypeface(tfLight);*/           // font settings
        chart.setCenterText(generateCenterSpannableText()); // text in the middle of the chart
        chart.setDrawHoleEnabled(true);                     // whole in the center
        chart.setHoleColor(Color.TRANSPARENT);                    // whole color
        chart.setTransparentCircleColor(Color.WHITE);       // inner circle
        chart.setTransparentCircleAlpha(110);
        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);
        chart.setDrawCenterText(true);                      // draw the text
        chart.setRotationEnabled(false);                    // disable chart rotation
        chart.setHighlightPerTapEnabled(true);              // clickable
        chart.setMaxAngle(180f);                            // HALF CHART
        chart.setRotationAngle(180f);
        chart.setCenterTextOffset(0, -20);

        setHalfPieData(4, 100);                 // load data

        chart.getLegend().setEnabled(false);
        chart.animateY(1400, Easing.EaseInOutQuad); // startup animation

        /*Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // entry label styling
        chart.setEntryLabelColor(Color.WHITE);
        //chart.setEntryLabelTypeface(tfRegular);
        chart.setEntryLabelTextSize(12f);*/
    }

    /**
     * Set the data to display in the half-piechart
     * @param count number of partitions
     * @param range total percentage
     */
    private void setHalfPieData(int count, float range) {

        ArrayList<PieEntry> values = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            values.add(new PieEntry((float) ((Math.random() * range) + range / 5), parties[i % parties.length]));
        }

        PieDataSet dataSet = new PieDataSet(values, "Election Results");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        chart.setData(data);

        chart.invalidate();
    }

    /**
     * generate the text inside the half-piechart
     * @return
     */
    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("deGustibus\ndeveloped by MADness");
        s.setSpan(new RelativeSizeSpan(1.9f), 0, 10, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 11, s.length() - 8, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 11, s.length() - 8, 0);
        s.setSpan(new RelativeSizeSpan(.8f), 11, s.length() - 8, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 7, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 7, s.length(), 0);
        return s;
    }

    /**
     * Place the half-piechart in the correct position
     */
    private void moveOffScreen() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int height = displayMetrics.heightPixels;

        int offset = (int)(height * 0.75); /* percent to move */

        ConstraintLayout.LayoutParams rlParams =
                (ConstraintLayout.LayoutParams) chart.getLayoutParams();
        rlParams.setMargins(0, 0, 0, -offset);
        chart.setLayoutParams(rlParams);

    }

}
