package com.madness.degustibus.home;

import android.content.Context;
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

import com.algolia.instantsearch.core.helpers.Searcher;
import com.algolia.instantsearch.core.model.FacetStat;
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
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
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
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            View customView = inflater.inflate(R.layout.popup_filter,null);
            final PopupWindow window = new PopupWindow(
                    customView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );

            FrameLayout frameLayout = getActivity().findViewById(R.id.frame);
            window.showAtLocation(frameLayout, Gravity.CENTER,0,0);

            CheckBox checkBox = customView.findViewById(R.id.checkBox);

            FacetStat facetStat = searcher.getFacetStat("id");
            System.out.println(facetStat);

            List<String> facet = searcher.getFacetRefinements("id");
            System.out.println(facet);
            if(facet!=null) {
                if(facet.size()!=0){
                    checkBox.setChecked(true);
                } else {
                    checkBox.setChecked(false);
                }
            } else {
                checkBox.setChecked(false);
            }

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        searcher.addFacetRefinement("id", "VSrLj8XzxRboYed89O5XYAQl1Ll1").search();
                        window.dismiss();
                    } else {
                        searcher.removeFacetRefinement("id", "VSrLj8XzxRboYed89O5XYAQl1Ll1").search();
                        window.dismiss();
                    }
                }
            });


            //searcher.addFacetRefinement("id", "VSrLj8XzxRboYed89O5XYAQl1Ll1").search();
/*
            final boolean willDisplay = !filterResultsWindow.isShowing();
            if (willDisplay) {
                filterResultsWindow.showAsDropDown(buttonFilter);
            } else {
                filterResultsWindow.dismiss();
            }*/
            //toggleArrow(buttonFilter, willDisplay);

            /*Client client = new Client("LRBUKD1XJR", "d1909b402e103014c844a891abb4bb4a");
            Index index = client.getIndex("rest_HOME");

            //index.search(new Query("").setFilters(""), new RequestOptions());
            index.searchAsync(new Query("").setFilters("id:VSrLj8XzxRboYed89O5XYAQl1Ll1"), new CompletionHandler() {
                @Override
                public void requestCompleted(JSONObject content, AlgoliaException error) {
                    // [...]
                    System.out.println(content);
                    SearchResultsJsonParser resultsJsonParser = new SearchResultsJsonParser();
                    List<HomeModel> results = resultsJsonParser.parseResults(content);
                    /*
                    moviesListAdapter.clear();
                    moviesListAdapter.addAll(results);
                    moviesListAdapter.notifyDataSetChanged();
                }
            });*/

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
