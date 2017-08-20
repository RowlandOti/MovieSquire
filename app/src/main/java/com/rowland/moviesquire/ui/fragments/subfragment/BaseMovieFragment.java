/*
 * Copyright 2015 Oti Rowland
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.rowland.moviesquire.ui.fragments.subfragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.rowland.moviesquire.R;
import com.rowland.moviesquire.rest.enums.ESortOrder;
import com.rowland.moviesquire.rest.models.Movie;
import com.rowland.moviesquire.rest.services.MovieIntentService;
import com.rowland.moviesquire.ui.activities.BaseToolBarActivity;
import com.rowland.moviesquire.ui.adapters.MovieAdapter;
import com.rowland.moviesquire.ui.widgets.EndlessRecyclerViewScrollListener;
import com.rowland.moviesquire.utilities.Utilities;

import java.util.List;

import butterknife.BindView;


/**
 * Created by Oti Rowland on 12/18/2015.
 */
public class BaseMovieFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    // Logging tracker for this class
    private final String LOG_TAG = BaseMovieFragment.class.getSimpleName();
    // An arrayList of the movies
    protected List<Movie> mMovieList;
    // The grid adapter
    protected MovieAdapter mMovieAdapter;
    // Sort Order for thid fragment
    protected ESortOrder mSortOrder;
    // Is it first launch of fragment?
    protected boolean isLaunch = true;
    // Page no. of request
    protected int mRequestPageNo = 1;
    // The scroll listener to load more
    private EndlessRecyclerViewScrollListener scrollListener;

    // ButterKnife injected Views
    @BindView(R.id.sw_refresh_layout)
    protected SwipeRefreshLayout mSwRefreshLayout;
    @BindView(R.id.movie_recycle_view)
    protected RecyclerView mMovieRecycleView;
    @BindView(R.id.empty_text_view_container)
    protected LinearLayout mEmptyTextViewContainer;


    // Default constructor
    public BaseMovieFragment() {

    }

    protected static BaseMovieFragment newInstance(BaseMovieFragment fragment, Bundle args) {
        // Create the new fragment instance
        BaseMovieFragment fragmentInstance = fragment;
        // Set arguments if it is not null
        if (args != null) {
            fragmentInstance.setArguments(args);
        }
        // Return the new fragment
        return fragmentInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Don't destroy fragment across orientation change
        setRetainInstance(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Configure the refresh layout look
        mSwRefreshLayout.setColorSchemeResources(R.color.apptheme_accent_teal);
        mSwRefreshLayout.setProgressViewOffset(true, 100, 400);
        // Calculate no. of columns
        int numberOfColumns = ((BaseToolBarActivity) getActivity()).calculateNoOfColumns(getActivity());
        // Create new instance of layout manager
        final StaggeredGridLayoutManager mLayoutManger = new StaggeredGridLayoutManager(numberOfColumns, StaggeredGridLayoutManager.VERTICAL);
        // Set the layout manger
        mMovieRecycleView.setLayoutManager(mLayoutManger);
        //mMovieRecycleView.setHasFixedSize(false);
        // Call is actually only necessary with custom ItemAnimators
        mMovieRecycleView.setItemAnimator(new DefaultItemAnimator());
        // Create new adapter
        mMovieAdapter = new MovieAdapter(mMovieList, getContext(), getActivity());
        // Associate RecycleView with adapter
        mMovieRecycleView.setAdapter(mMovieAdapter);
        // Set onScrollListener
        scrollListener = new EndlessRecyclerViewScrollListener(mLayoutManger) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Load next page of movies
                loadMoviesData(page);
                // Save currently loaded page
                mRequestPageNo = page;
            }
        };
        // Associate RecyclerView with the EndlessRecyclerViewScrollListener
        mMovieRecycleView.addOnScrollListener(scrollListener);
        // Set the refreshlayout's listener
        mSwRefreshLayout.setOnRefreshListener(this);
    }

    // When RefreshLayout is triggered reload the loader
    @Override
    public void onRefresh() {
        // Check if we are online
        if (Utilities.NetworkUtility.isNetworkAvailable(getContext())) {
            startMovieIntentService();
        } else {
            // Set refreshing
            mSwRefreshLayout.setRefreshing(false);
            // Tell user of no connectivity
            Snackbar.make(getView(), R.string.status_no_internet, Snackbar.LENGTH_LONG).show();

        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            // Restore last state for checked position.
            isLaunch = savedInstanceState.getBoolean("IS_LAUNCH", isLaunch);
        }
    }

    // Here you Save your data
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("IS_LAUNCH", isLaunch);
    }

    // Start the service
    protected void startMovieIntentService() {
        // Don't query internet for locally favoured movies
        if (mSortOrder != ESortOrder.FAVOURITE_DESCENDING) {
            Intent i = new Intent(getActivity(), MovieIntentService.class);
            i.putExtra(MovieIntentService.REQUEST_SORT_TYPE_STRING, mSortOrder.getSortOrder());
            i.putExtra(MovieIntentService.REQUEST_PAGE_NO_INT, mRequestPageNo);
            getActivity().startService(i);
            // Increment requestPage no.
            mRequestPageNo++;
        }
    }

    protected void loadMoviesData(int requestPageNo) {
        // Don't query internet for locally favoured movies
        if (mSortOrder != ESortOrder.FAVOURITE_DESCENDING) {
            Intent i = new Intent(getActivity(), MovieIntentService.class);
            i.putExtra(MovieIntentService.REQUEST_SORT_TYPE_STRING, mSortOrder.getSortOrder());
            i.putExtra(MovieIntentService.REQUEST_PAGE_NO_INT, mRequestPageNo);
            getActivity().startService(i);
            // Increment requestPage no.
            mRequestPageNo++;
        }
    }

    // Update the empty view
    public void updateEmptyView() {
        if (mMovieAdapter.getItemCount() == 0) {
            // Show Empty TextView
            mMovieRecycleView.setVisibility(View.GONE);
            mEmptyTextViewContainer.setVisibility(View.VISIBLE);
            Log.d(BaseMovieFragment.class.getSimpleName(), "Adapter Count: " + mMovieAdapter.getItemCount());
        } else {
            // Show RecycleView filled with movies
            mMovieRecycleView.setVisibility(View.VISIBLE);
            mEmptyTextViewContainer.setVisibility(View.GONE);
        }
    }
}
