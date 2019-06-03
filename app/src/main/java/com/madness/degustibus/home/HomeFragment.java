package com.madness.degustibus.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;

import com.algolia.instantsearch.core.helpers.Searcher;
import com.algolia.instantsearch.core.model.FacetStat;
import com.algolia.instantsearch.core.model.NumericRefinement;
import com.algolia.instantsearch.ui.helpers.InstantSearch;
import com.algolia.instantsearch.ui.utils.ItemClickSupport;
import com.algolia.instantsearch.ui.views.Hits;
import com.algolia.instantsearch.ui.views.SearchBox;
import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.algolia.search.saas.RequestOptions;
import com.madness.degustibus.R;
import com.madness.degustibus.notifications.NotificationsFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class HomeFragment extends Fragment {

    public static final String ALGOLIA_INDEX_NAME = "rest_HOME";
    private static final String ALGOLIA_APP_ID = "LRBUKD1XJR";
    private static final String ALGOLIA_API_KEY = "d1909b402e103014c844a891abb4bb4a";
    private Searcher searcher;
    private SearchView searchBox;
    private Hits hits;
    private HomeInterface homeInterface;
    private PopupWindow window;
    private View customView;

    public HomeFragment() {
        // Required empty public constructor
    }

    /* The onAttach method registers the HomeInterface */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HomeInterface) {
            homeInterface = (HomeInterface) context;
        } else {
            throw new ClassCastException(context.toString() + "must implement HomeInterface");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searcher = Searcher.create(ALGOLIA_APP_ID, ALGOLIA_API_KEY, ALGOLIA_INDEX_NAME);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        customView = inflater.inflate(R.layout.popup_filter,null);
        window = new PopupWindow(
                customView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        getActivity().setTitle("deGustibus"); // TODO: strings
        hits = rootView.findViewById(R.id.hits);
        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);

        new InstantSearch(getActivity(), menu, R.id.action_search, searcher); // link the Searcher to the UI
        searcher.search(); // Show results for empty query (on app launch) / voice query (from intent)

        final MenuItem itemSearch = menu.findItem(R.id.action_search);
        searchBox = (SearchBox) itemSearch.getActionView();
        //itemSearch.expandActionView(); //open SearchBar on startup
        searchBox.clearFocus();

        hits.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, int position, View v) {
                JSONObject hit = hits.get(position);
                String restaurant = hit.toString();
                try {
                    homeInterface.viewRestaurantDetails(restaurant, hits.get(position).getString("name"));
                } catch (JSONException e) {

                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_notifications) {
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

        if(item.getItemId() == R.id.action_filter) {
            FrameLayout frameLayout = getActivity().findViewById(R.id.frame);
            if(window.isShowing()) {
                window.dismiss();
            } else {
                window.showAtLocation(frameLayout, Gravity.TOP | Gravity.RIGHT, 0, 200);

                CheckBox checkBox = customView.findViewById(R.id.checkBox);
                SeekBar seekBar = customView.findViewById(R.id.seekbar);
                List<String> facet = searcher.getFacetRefinements("id");
                if (facet != null) {
                    if (facet.size() != 0) {
                        checkBox.setChecked(true);
                    } else {
                        checkBox.setChecked(false);
                    }
                } else {
                    checkBox.setChecked(false);
                }

                SharedPreferences sharedPref = getActivity().getSharedPreferences("Restaurants", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();

                final List<String> filter = new ArrayList<>();
                HashMap<String, String> map = (HashMap<String, String>) sharedPref.getAll();
                Iterator iterator = map.values().iterator();
                while (iterator.hasNext()) {
                    //System.out.println(iterator.next().toString());
                    filter.add(iterator.next().toString());
                }
                //System.out.println(sharedPref.getAll());

                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            searcher.addFacetRefinement("id", filter, true).search();
                            window.dismiss();
                        } else {
                            for(int i = 0; i<filter.size(); i++) {
                                searcher.removeFacetRefinement("id", filter.get(i)).search();
                            }
                            window.dismiss();
                        }
                    }
                });

                //searcher.addFacetRefinement("rating", String.valueOf(1)).search();
                final NumericRefinement currentFilter = searcher.getNumericRefinement("rating", NumericRefinement.OPERATOR_GT);

                if (currentFilter != null && currentFilter.value != 0) {
                    final int progressValue = (int) ((currentFilter.value - 0) * 5 / (5 - 0));
                    seekBar.setProgress(progressValue);
                }
                final int[] lastProgressValue = {seekBar.getProgress()};
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        System.out.println(progress);
                        onUpdate(seekBar);
                        //searcher.addFacetRefinement("rating", String.valueOf(progress)).search();
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }

                    private void onUpdate(final SeekBar seekBar) {
                        int newProgressValue = seekBar.getProgress(); // avoid double search on ProgressChanged + StopTrackingTouch
                        System.out.println(newProgressValue);
                        if (newProgressValue != lastProgressValue[0]) {
                            final double actualValue = 0 + newProgressValue * (5 - 0) / 5;
                            if (newProgressValue == 0) {
                                searcher.removeNumericRefinement(new NumericRefinement("rating", NumericRefinement.OPERATOR_GT, actualValue-1))
                                        .search();
                            } else {
                                searcher.addNumericRefinement(new NumericRefinement("rating", NumericRefinement.OPERATOR_GT, actualValue-1))
                                        .search();
                            }
                        }
                        lastProgressValue[0] = newProgressValue;
                    }
                });
            }
        }
        return true;
    }

    @Override
    public void onStop() {
        super.onStop();
        searcher.destroy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        searcher.destroy();
    }

    public interface HomeInterface {
        void viewRestaurantDetails(String restaurant, String name);
    }
}
