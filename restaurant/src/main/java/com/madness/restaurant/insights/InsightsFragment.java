package com.madness.restaurant.insights;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.StackedValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madness.restaurant.R;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class InsightsFragment extends Fragment {

    private PieChart pieChart;
    private BarChart barChart;
    private FirebaseUser user;
    private HashMap<String,HashMap<String,Object>> dishes;
    private int[] orders;
    private Long total = 0L;  // total sales;
    private ChildEventListener listenerPieChart;
    private ChildEventListener listenerBarChart;
    private ArrayList<PieEntry> pieChartValues;
    private ArrayList<BarEntry> barChartValues;

    public InsightsFragment() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        dishes = new HashMap<>();
        orders = new int[24];
        loadDataFromFirebase();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

        setBarChartData(); // load with empty data
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
        pieChart.getLegend().setEnabled(false);
        pieChart.animateY(1400, Easing.EaseInOutQuad); // startup animation
        setHalfPieData(); // load with empty data
    }

    /**
     * Set the data to display in the half-piechart
     */
    private void setHalfPieData() {

        this.pieChartValues = new ArrayList<>();

        for (Map.Entry<String, HashMap<String, Object>> dish : dishes.entrySet()) { // for each dish
            String name = "";
            Long popular = 0L;
            for (Map.Entry<String, Object> entry : dish.getValue().entrySet()) {    // for each propery
                if(entry.getKey().equals("dish")) name = entry.getValue().toString();
                if(entry.getKey().equals("popular")) popular = (Long) entry.getValue();
            }

            // add a new entry onfly if is != 0
            if(popular != 0L){
                PieEntry entry = new PieEntry(popular,name);
                this.pieChartValues.add(entry);
            }

        }

        PieDataSet dataSet = new PieDataSet(this.pieChartValues, "Dishes");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
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
     *  Set the data to display in the bar chart
     */
    private void setBarChartData() {


        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new MyValueFormatter());

        this.barChartValues = new ArrayList<>();


        /*for (int i = 0; i < 24; i++) {
            float mul = (5 + 1);
            float val1 = (float) (Math.random() * mul) + mul / 3;
            float val2 = (float) (Math.random() * mul) + mul / 3;
            float val3 = (float) (Math.random() * mul) + mul / 3;

            barChartValues.add(new BarEntry(
                    i,
                    new float[]{val1, val2, val3}));
        }*/


        for(int i=0; i < orders.length; i++){
            barChartValues.add(new BarEntry(i,orders[i]));
        }

        BarDataSet set1;

        if (barChart.getData() != null &&
                barChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) barChart.getData().getDataSetByIndex(0);
            set1.setValues(barChartValues);
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(barChartValues, "Statistics Vienna 2014");
            set1.setDrawIcons(false);
            set1.setColors(getColors());
            set1.setStackLabels(new String[]{"Births", "Divorces", "Marriages"});

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueFormatter(new StackedValueFormatter(false, "", 1));
            data.setValueTextColor(Color.WHITE);

            barChart.setData(data);
        }

        barChart.setFitBars(true);
        barChart.invalidate();
    }

    private int[] getColors() {

        // have as many colors as stack-values per entry
        int[] colors = new int[3];

        System.arraycopy(ColorTemplate.MATERIAL_COLORS, 0, colors, 0, 3);

        return colors;
    }

    /**
     * Custom format of x axis
     */
    class MyValueFormatter extends ValueFormatter{

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            if(value < 10){
                return "0" + (int)value + ":00";
            }else
                return (int)value + ":00";
        }
    }


    private void loadDataFromFirebase(){
        // get offers
        this.listenerPieChart =  FirebaseDatabase.getInstance().getReference()
            .child("offers")
            .child(user.getUid()).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    // insert a single dish
                    HashMap<String,Object> tmp = new HashMap<>();
                    String name = dataSnapshot.child("dish").getValue(String.class);
                    Long popular = dataSnapshot.child("popular").getValue(Long.class);
                    tmp.put("dish",name);
                    tmp.put("popular",popular);
                    total += popular;
                    dishes.put(dataSnapshot.getKey(),tmp);
                    setHalfPieData();

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    //remove it
                    Long prevPopular = (Long) dishes.get(dataSnapshot.getKey()).get("popular");
                    total -= prevPopular;
                    dishes.remove(dataSnapshot.getKey());
                    //insert the new one
                    HashMap<String,Object> tmp = new HashMap<>();
                    String name = dataSnapshot.child("dish").getValue(String.class);
                    Long popular = dataSnapshot.child("popular").getValue(Long.class);
                    tmp.put("dish",name);
                    tmp.put("popular",popular);
                    total += popular;
                    dishes.put(dataSnapshot.getKey(),tmp);
                    setHalfPieData();
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    //remove it
                    HashMap<String,Object> tmp = new HashMap<>();
                    Long popular = dataSnapshot.child("popular").getValue(Long.class);
                    tmp.put("popular",popular);
                    total -= popular;
                    dishes.remove(dataSnapshot.getKey());
                    setHalfPieData();
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            });


        this.listenerBarChart = FirebaseDatabase.getInstance().getReference()
                .child("orders")
                .orderByChild("restaurantID")
                .equalTo(user.getUid())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        // get time
                        String time = dataSnapshot.child("deliveryHour").getValue(String.class);
                        String index = time.substring(0,2); // select only the hour
                        orders[Integer.valueOf(index).intValue()] += 1;
                        setBarChartData();
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        // get time
                        String time = dataSnapshot.child("deliveryHour").getValue(String.class);
                        String index = time.substring(0,2); // select only the hour
                        orders[Integer.valueOf(index).intValue()] -= 1;
                        setBarChartData();
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // detach listener
        FirebaseDatabase.getInstance().getReference()
                .child("offers")
                .child(user.getUid())
                .removeEventListener(this.listenerPieChart);

        FirebaseDatabase.getInstance().getReference()
                .child("orders")
                .orderByChild("restaurantID")
                .equalTo(user.getUid())
                .removeEventListener(this.listenerBarChart);
    }
}
