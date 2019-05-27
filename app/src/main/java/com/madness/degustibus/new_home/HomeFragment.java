package com.madness.degustibus.new_home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.algolia.instantsearch.core.helpers.Searcher;
import com.algolia.instantsearch.core.model.AlgoliaResultsListener;
import com.algolia.instantsearch.core.model.SearchResults;
import com.algolia.instantsearch.ui.helpers.InstantSearch;
import com.algolia.instantsearch.ui.utils.ItemClickSupport;
import com.algolia.instantsearch.ui.views.Hits;
import com.algolia.instantsearch.ui.views.SearchBox;
import com.madness.degustibus.R;
import com.madness.degustibus.order.OrderFragment;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeFragment extends Fragment {

    public static final String ALGOLIA_INDEX_NAME = "rest_HOME";
    private static final String ALGOLIA_APP_ID = "LRBUKD1XJR";
    private static final String ALGOLIA_API_KEY = "d1909b402e103014c844a891abb4bb4a";
    private Searcher searcher;
    private FilterResultsFragment filterResultsFragment;
    private SearchView searchBox;
    private Hits hits;
    private HomeInterface homeInterface;

    public HomeFragment() {
        // Required empty public constructor
    }

    /* The onAttach method registers the newOrderInterface */
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
        View rootView = inflater.inflate(R.layout.fragment_home2, container, false);
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

        searcher.registerResultListener(new AlgoliaResultsListener() {
            @Override
            public void onResults(@NonNull SearchResults results, boolean isLoadingMore) {
                System.out.println(results.toString());

            }
        });

        hits.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, int position, View v) {
                JSONObject hit = hits.get(position);
                String restaurant = hit.toString();
                homeInterface.viewRestaurantDetails(restaurant);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        searcher.destroy();
    }

    public interface HomeInterface {
        void viewRestaurantDetails(String restaurant);
    }
}
