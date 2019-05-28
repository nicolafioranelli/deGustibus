package com.madness.restaurant.insights;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.madness.restaurant.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class InsightsFragment extends Fragment {

    private PieChart pieChart;
    private BarChart barChart;

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
        barChartConfigurations(rootView);

        return rootView;
    }

    private void barChartConfigurations(View rootView) {
        barChart = rootView.findViewById(R.id.barChart);
        barChart.setBackgroundColor(Color.TRANSPARENT);
        //barChart.setOnChartValueSelectedListener(this);
        barChart.getDescription().setEnabled(false);        // no description
        barChart.setMaxVisibleValueCount(40);               // if more than 40 entries are displayed in the chart,
                                                            // no values will be drawn
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(false);
        barChart.setHighlightFullBarEnabled(false);
        YAxis leftAxis = barChart.getAxisLeft();            // change the position of the y-labels
        leftAxis.setAxisMinimum(0f);                        // set minimun value on y
        barChart.getAxisRight().setEnabled(false);
        barChart.getLegend().setEnabled(false);             // hide the legend

        /*XAxis xLabels = barChart.getXAxis();
        xLabels.setPosition(XAxisPosition.TOP);*/

        // chart.setDrawLegend(false);
    }

    private void halfPieConfigurations(View rootView){
        pieChart = rootView.findViewById(R.id.pieChart);
        pieChart.setBackgroundColor(Color.TRANSPARENT);
        //moveOffScreen();
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        /*pieChart.setCenterTextTypeface(tfLight);*/           // font settings
        pieChart.setCenterText(generateCenterSpannableText()); // text in the middle of the pieChart
        pieChart.setDrawHoleEnabled(true);                     // whole in the center
        pieChart.setHoleColor(Color.TRANSPARENT);                    // whole color
        pieChart.setTransparentCircleColor(Color.WHITE);       // inner circle
        pieChart.setTransparentCircleAlpha(110);
        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setDrawCenterText(true);                      // draw the text
        pieChart.setRotationEnabled(false);                    // disable pieChart rotation
        pieChart.setHighlightPerTapEnabled(true);              // clickable
        pieChart.setMaxAngle(180f);                            // HALF CHART
        pieChart.setRotationAngle(180f);
        pieChart.setCenterTextOffset(0, -20);

        setHalfPieData(4, 100);                 // load data

        pieChart.getLegend().setEnabled(false);
        pieChart.animateY(1400, Easing.EaseInOutQuad); // startup animation

        /*Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // entry label styling
        pieChart.setEntryLabelColor(Color.WHITE);
        //pieChart.setEntryLabelTypeface(tfRegular);
        pieChart.setEntryLabelTextSize(12f);*/
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
        pieChart.setData(data);

        pieChart.invalidate();
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
                (ConstraintLayout.LayoutParams) pieChart.getLayoutParams();
        rlParams.setMargins(0, 0, 0, -offset);
        pieChart.setLayoutParams(rlParams);

    }

}
