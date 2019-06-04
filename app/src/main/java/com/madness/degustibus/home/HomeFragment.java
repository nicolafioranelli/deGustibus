package com.madness.degustibus.home;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;

import com.algolia.instantsearch.core.helpers.Searcher;
import com.algolia.instantsearch.core.model.NumericRefinement;
import com.algolia.instantsearch.ui.helpers.InstantSearch;
import com.algolia.instantsearch.ui.utils.ItemClickSupport;
import com.algolia.instantsearch.ui.views.Hits;
import com.algolia.instantsearch.ui.views.SearchBox;
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

    /* Algolia */
    public static final String ALGOLIA_INDEX_NAME = "rest_HOME";
    private static final String ALGOLIA_APP_ID = "LRBUKD1XJR";
    private static final String ALGOLIA_API_KEY = "d1909b402e103014c844a891abb4bb4a";
    private Searcher searcher;
    private Hits hits;

    /* Widgets */
    private SearchView searchBox;
    private HomeInterface homeInterface;
    private PopupWindow window;
    private View customView;

    public HomeFragment() {
        // Required empty public constructor
    }

    /* Lifecycle */

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
        // Inflate layout for the popup windows used for filter results
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        customView = inflater.inflate(R.layout.popup_filter, null);
        window = new PopupWindow(
                customView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        // Set option menu in toolbar
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        getActivity().setTitle(getString(R.string.title_Home));
        hits = rootView.findViewById(R.id.hits);
        return rootView;
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
    // end Lifecycle

    /* Option menu Helpers */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu); // Inflate menu
        new InstantSearch(getActivity(), menu, R.id.action_search, searcher); // link the Searcher to the UI
        searcher.search(); // Show results for empty query (on app launch)

        // Retrieve Search Box
        final MenuItem itemSearch = menu.findItem(R.id.action_search);
        searchBox = (SearchBox) itemSearch.getActionView();
        searchBox.clearFocus();

        // Add click listener for hits retrieved (elements of the recycler view)
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
        if (item.getItemId() == R.id.action_notifications) {
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

        if (item.getItemId() == R.id.action_filter) {
            FrameLayout frameLayout = getActivity().findViewById(R.id.frame);
            // If popup windows is already displayed, remove it, else display
            if (window.isShowing()) {
                window.dismiss();
            } else {
                window.showAtLocation(frameLayout, Gravity.TOP | Gravity.RIGHT, 0, 200);

                // Retrieve checkbox and seekbar
                CheckBox checkBox = customView.findViewById(R.id.checkBox);
                SeekBar seekBar = customView.findViewById(R.id.seekbar);
                // Facet allows to understand if a filter is already set (in that case the state
                // of checkbox is marked
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

                // Retrieve preferred from shared preferences
                SharedPreferences sharedPref = getActivity().getSharedPreferences("Restaurants", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();

                final List<String> filter = new ArrayList<>();
                HashMap<String, String> map = (HashMap<String, String>) sharedPref.getAll();
                Iterator iterator = map.values().iterator();
                while (iterator.hasNext()) {
                    // for each element add a filter in order to get it displayed
                    filter.add(iterator.next().toString());
                }

                // set a change listener for checkbox in order to listen to changes, once clicked dismiss window
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            searcher.addFacetRefinement("id", filter, true).search();
                            window.dismiss();
                        } else {
                            for (int i = 0; i < filter.size(); i++) {
                                searcher.removeFacetRefinement("id", filter.get(i)).search();
                            }
                            window.dismiss();
                        }
                    }
                });

                // for ratings a numeric refinement is suggested by Algolia documentation
                final NumericRefinement currentFilter = searcher.getNumericRefinement("rating", NumericRefinement.OPERATOR_GT);

                // Check if a filter is already defined in case set the seekbar to that value
                if (currentFilter != null && currentFilter.value != 0) {
                    final int progressValue = (int) ((currentFilter.value - 0) * 5 / (5 - 0));
                    seekBar.setProgress(progressValue);
                }
                // Get last value
                final int[] lastProgressValue = {seekBar.getProgress()};
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        onUpdate(seekBar);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }

                    // Update the filter
                    private void onUpdate(final SeekBar seekBar) {
                        int newProgressValue = seekBar.getProgress(); // avoid double search on ProgressChanged + StopTrackingTouch
                        if (newProgressValue != lastProgressValue[0]) {
                            final double actualValue = 0 + newProgressValue * (5 - 0) / 5;
                            if (newProgressValue == 0) {
                                searcher.removeNumericRefinement(new NumericRefinement("rating", NumericRefinement.OPERATOR_GT, actualValue - 1))
                                        .search();
                            } else {
                                searcher.addNumericRefinement(new NumericRefinement("rating", NumericRefinement.OPERATOR_GT, actualValue - 1))
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

    // end Option menu helpers

    public interface HomeInterface {
        void viewRestaurantDetails(String restaurant, String name);
    }
}
