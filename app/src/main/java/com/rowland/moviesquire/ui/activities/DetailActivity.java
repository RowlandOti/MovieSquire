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

package com.rowland.moviesquire.ui.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.activeandroid.query.Select;
import com.rowland.moviesquire.R;
import com.rowland.moviesquire.data.loaders.ModelLoader;
import com.rowland.moviesquire.rest.models.Movie;
import com.rowland.moviesquire.ui.adapters.SmartFragmentStatePagerAdapter;
import com.rowland.moviesquire.ui.fragments.DetailFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends BaseToolBarActivity {

    // Logging Identifier for class
    private final String LOG_TAG = DetailActivity.class.getSimpleName();
    // MOvies loader id
    private static final int DETAIL_MOVIES_LOADER_ID = 3;

    // Movies LoaderCallBack
    private LoaderManager.LoaderCallbacks<List<Movie>> mMoviesLoaderCallBack;
    // A List of the movies
    private List<Movie> mMovieList;
    // Currently selected id
    private Long mSelectedMovieId;
    // Sent movie id
    private int mSelectedMoviePosition;
    // Movie sort criterion
    private String mSortCriteria;
    // Details PagerAdapter
    DetailPagerAdapter mDetailsPagerAdapter;

    @BindView(R.id.detail_viewPager) ViewPager mDetailsViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout
        setContentView(R.layout.activity_detail);
        // Inject all the views
        ButterKnife.bind(this);

        mDetailsPagerAdapter = new DetailPagerAdapter(getSupportFragmentManager());
        mDetailsViewPager.setAdapter(mDetailsPagerAdapter);

        // Acquire the movie id sent to this activity
        mSelectedMovieId = getIntent().getLongExtra(DetailFragment.MOVIE_KEY, 0);
        // Acquire the selected movie position
        mSelectedMoviePosition = getIntent().getIntExtra(DetailFragment.MOVIE_POSITION_KEY, 0);
        // Acquire sort type
        mSortCriteria = getIntent().getStringExtra(DetailFragment.MOVIE_SORT_KEY);

        // Movie LoaderCallBack implementation
        mMoviesLoaderCallBack = new LoaderManager.LoaderCallbacks<List<Movie>>() {
            @Override
            public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
                if (mSortCriteria == null) {
                    mSortCriteria = "isPopular";
                }
                StringBuilder whereClause = new StringBuilder();
                whereClause.append(mSortCriteria).append(" = ?");

                Log.d(LOG_TAG, whereClause.toString());

                // Create new loader
                ModelLoader movieLoader = new ModelLoader<>(DetailActivity.this, Movie.class, new Select().from(Movie.class).where(whereClause.toString(), true), true);
                // Return new loader
                return movieLoader;
            }

            @Override
            public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
                // Update movies list
                mMovieList = data;
                // Notify PagerAdapter
                mDetailsPagerAdapter.notifyDataSetChanged();
                // Navigate to movie item that was sent from MainActivity
                mDetailsViewPager.setCurrentItem(mSelectedMoviePosition, true);
            }

            @Override
            public void onLoaderReset(Loader<List<Movie>> loader) {
                // Reset movies list
                mMovieList.clear();
                mDetailsPagerAdapter.notifyDataSetChanged();
            }
        };
        // Start Loader
        getSupportLoaderManager().initLoader(DETAIL_MOVIES_LOADER_ID, null, mMoviesLoaderCallBack);
    }

    private class DetailPagerAdapter extends SmartFragmentStatePagerAdapter {

        public DetailPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // Update the selected movie id
            mSelectedMovieId = mMovieList.get(position).getId_();
            // Create a Bundle object
            Bundle args = new Bundle();
            // Set arguments on Bundle
            args.putLong(DetailFragment.MOVIE_KEY, mSelectedMovieId);
            // Pass bundle to the fragment
            Fragment detailFragment = createDetailFragment(args);

            return detailFragment;
        }

        @Override
        public int getCount() {
            return (mMovieList != null) ? mMovieList.size() : 0;
        }

        // Insert the DetailFragment
        private Fragment createDetailFragment(Bundle args) {
            // Create new fragment
            DetailFragment detailFragment = DetailFragment.newInstance(args);

            return detailFragment;
        }
    }
}
