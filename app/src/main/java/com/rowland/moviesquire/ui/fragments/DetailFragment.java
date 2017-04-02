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

package com.rowland.moviesquire.ui.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.rowland.moviesquire.BuildConfig;
import com.rowland.moviesquire.R;
import com.rowland.moviesquire.data.loaders.ModelLoader;
import com.rowland.moviesquire.data.repository.MovieRepository;
import com.rowland.moviesquire.rest.enums.EBaseImageSize;
import com.rowland.moviesquire.rest.enums.EBaseURlTypes;
import com.rowland.moviesquire.rest.models.Movie;
import com.rowland.moviesquire.rest.models.Review;
import com.rowland.moviesquire.rest.models.Trailer;
import com.rowland.moviesquire.rest.services.ReviewIntentService;
import com.rowland.moviesquire.rest.services.TrailerIntentService;
import com.rowland.moviesquire.ui.activities.BaseToolBarActivity;
import com.rowland.moviesquire.ui.activities.DetailActivity;
import com.rowland.moviesquire.ui.adapters.ReviewAdapter;
import com.rowland.moviesquire.ui.adapters.TrailerAdapter;
import com.rowland.moviesquire.ui.widgets.WrappingLinearLayoutManager;
import com.rowland.moviesquire.utilities.Utilities;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Display Movie Detail
 */
public class DetailFragment extends Fragment {

    // The Movie ID Identifier Key
    public static final String MOVIE_KEY = "movie_key";
    // The Movie sort type identifer
    public static final String MOVIE_SORT_KEY = "movie_sort_key";
    // The movie position key
    public static final String MOVIE_POSITION_KEY = "movie_position_key";
    // Logging Identifier for class
    private final String LOG_TAG = DetailFragment.class.getSimpleName();
    // Reviews loader id
    private static final int REVIEWS_LOADER_ID = 4;
    // Trailers loader id
    private static final int TRAILERS_LOADER_ID = 5;
    // Is movie Favourite
    boolean isFavourite;
    // ButterKnife injected views
    @Nullable
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.movie_detail_backdrop_image_view)
    ImageView mBackdropMovie;
    @Bind(R.id.movie_detail_backdrop_play_image_view)
    ImageView mBackdropMoviePlay;
    @Bind(R.id.movie_statistic_favourite_text_view)
    TextView mDetailFavouriteTextView;
    @Bind(R.id.movie_title_text_view)
    TextView mDetailMovieTitle;
    @Bind(R.id.movie_title_container)
    RelativeLayout mDetailMovieTitleContainer;
    @Bind(R.id.movie_statistic_year_text_view)
    TextView mDetailMovieYear;
    @Bind(R.id.movie_statistic_rate_text_view)
    TextView mDetailMovieRate;
    @Bind(R.id.movie_statistic_popular_text_view)
    TextView mDetailMoviePopularity;
    @Bind(R.id.movie_overview_text_view)
    TextView mDetailMovieOverview;
    @Bind(R.id.favorite_fab)
    FloatingActionButton mFavoriteFab;
    @Bind(R.id.trailer_empty_text_view)
    TextView mDetailMovieEmptyTrailers;
    @Bind(R.id.review_empty_text_view)
    TextView mDetailMovieEmptyReviews;
    @Bind(R.id.trailer_progress_bar)
    ProgressBar mTrailerProgressBar;
    @Bind(R.id.review_progress_bar)
    ProgressBar mReviewProgressBar;
    @Bind(R.id.trailer_recycle_view)
    RecyclerView mTrailerRecycleView;
    @Bind(R.id.review_recycle_view)
    RecyclerView mReviewRecycleView;

    // The Movie model
    private Movie mMovie;
    // The model key
    private long id;
    // Reviews LoaderCallBack
    private LoaderManager.LoaderCallbacks mReviewLoaderCallBack;
    // Trailers LoaderCallBack
    private LoaderManager.LoaderCallbacks mTrailerLoaderCallBack;
    // A List of the reviews
    private List<Review> mReviewList;
    // A List of the trailers
    private List<Trailer> mTrailerList;
    // The Review adapter
    private ReviewAdapter mReviewAdapter;
    // The Trailer adapter
    private TrailerAdapter mTrailerAdapter;
    // Simple growth Animation
    private Animation simpleGrowAnimation;


    // Default constructor
    public DetailFragment() {

    }

    // Create a new Instance for this fragment
    public static DetailFragment newInstance(Bundle args) {
        // The DetailFragment instance
        DetailFragment fragmentInstance = new DetailFragment();
        // Check for null arguments
        if (args != null) {
            // Set fragment arguments
            fragmentInstance.setArguments(args);
        }
        // Return the fragment
        return fragmentInstance;
    }

    // Called to do initial creation of fragment
    // Initialize and set up the fragment's non-view hierarchy
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Let the fragment handle its menu items
        setHasOptionsMenu(true);
        // Check if we have any arguments
        if (getArguments() != null) {
            // Acquire the selected movie identifier
            id = getArguments().getLong(DetailFragment.MOVIE_KEY);
            // Acquire movie instance
            mMovie = new MovieRepository().getWhereId(id);
            // Check for null
            if (mMovie != null) {
                // Is movie Favourite
                isFavourite = mMovie.getIsFavourite();
                // Start services
                startReviewIntentService();
                startTrailerIntentService();
                // Initialize the review list
                mReviewList = new ArrayList<>();
                // Initialize the trailer list
                mTrailerList = new ArrayList<>();
                // Create an Animation
                simpleGrowAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.grow_bigger);
            }
        }
    }

    // Called to instantiate the fragment's view hierarchy
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        // Inflate all views
        ButterKnife.bind(this, rootView);
        // Return the view for this fragment
        return rootView;
    }

    // Called after onCreateView() is done i.e the fragment's view has been created
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Check for null
        if (mMovie != null) {
            // Initialize layout manager
            final WrappingLinearLayoutManager mVerticalLinearLayoutManger = new WrappingLinearLayoutManager(getContext());
            // Set the RecycleView's layout manager
            mReviewRecycleView.setLayoutManager(mVerticalLinearLayoutManger);
            // Set the RecycleView's size fixing
            mReviewRecycleView.setHasFixedSize(false);
            // Set the RecycleView's ItemAnimators
            mReviewRecycleView.setItemAnimator(new DefaultItemAnimator());
            // Initialize new Review adapter
            mReviewAdapter = new ReviewAdapter(mReviewList);
            // Set RecycleView's adapter
            mReviewRecycleView.setAdapter(mReviewAdapter);
            // Review LoaderCallBack implementation
            mReviewLoaderCallBack = new LoaderManager.LoaderCallbacks<List<Review>>() {
                @Override
                public Loader<List<Review>> onCreateLoader(int id, Bundle args) {
                    // Set ProgressBar refresh on
                    mReviewProgressBar.setVisibility(View.VISIBLE);
                    // Create new loader
                    ModelLoader reviewLoader = new ModelLoader<>(getActivity(), Review.class, new Select().from(Review.class).where("movie = ?", mMovie.getId()), true);
                    // Return new loader
                    return reviewLoader;
                }

                @Override
                public void onLoadFinished(Loader<List<Review>> loader, List<Review> reviewList) {
                    // Set ProgressBar refresh off
                    mReviewProgressBar.setVisibility(View.GONE);
                    // Set mReviewList
                    mReviewList = reviewList;
                    // Pass reviews list to our adapter
                    mReviewAdapter.addAll(mReviewList);
                    // Update the Empty View
                    updateReviewsEmptyView();
                    // Check whether we are in debug mode
                    if (BuildConfig.IS_DEBUG_MODE) {
                        Log.d(LOG_TAG, "Review: " + mReviewAdapter.getItemCount());
                    }
                }

                @Override
                public void onLoaderReset(Loader<List<Review>> loader) {
                    // Set ProgressBar refresh off
                    mReviewProgressBar.setVisibility(View.GONE);
                    // We reset the loader, nullify old data
                    mReviewAdapter.addAll(null);
                    mReviewList.clear();
                }
            };

            // Initialize layout manager
            final WrappingLinearLayoutManager mHorizontalLinearLayoutManger = new WrappingLinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            // Set the RecycleView's layout manager
            mTrailerRecycleView.setLayoutManager(mHorizontalLinearLayoutManger);
            // Set the RecycleView's size fixing
            mTrailerRecycleView.setHasFixedSize(false);
            // Set the RecycleView's ItemAnimators
            mTrailerRecycleView.setItemAnimator(new DefaultItemAnimator());
            // Initialize new Trailer adapter
            mTrailerAdapter = new TrailerAdapter(mTrailerList, getActivity());
            // Set RecycleView's adapter
            mTrailerRecycleView.setAdapter(mTrailerAdapter);
            // Trailer LoaderCallBack implementation
            mTrailerLoaderCallBack = new LoaderManager.LoaderCallbacks<List<Trailer>>() {
                @Override
                public Loader<List<Trailer>> onCreateLoader(int id, Bundle args) {
                    // Set ProgressBar refresh on
                    mTrailerProgressBar.setVisibility(View.VISIBLE);
                    // Create new loader
                    ModelLoader trailerLoader = new ModelLoader<>(getActivity(), Trailer.class, new Select().from(Trailer.class).where("movie = ?", mMovie.getId()), true);
                    // Return new loader
                    return trailerLoader;
                }

                @Override
                public void onLoadFinished(Loader<List<Trailer>> loader, List<Trailer> trailerList) {
                    // Set ProgressBar refresh off
                    mTrailerProgressBar.setVisibility(View.GONE);
                    // Set mTrailerList
                    mTrailerList = trailerList;
                    // Add trailers
                    mTrailerAdapter.addAll(mTrailerList);
                    // Update the Empty View
                    updateTrailersEmptyView();
                    // Check whether we are in debug mode
                    if (BuildConfig.IS_DEBUG_MODE) {
                        Log.d(LOG_TAG, "Trailer: " + mTrailerAdapter.getItemCount());
                    }
                }

                @Override
                public void onLoaderReset(Loader<List<Trailer>> loader) {
                    // Set ProgressBar refresh off
                    mTrailerProgressBar.setVisibility(View.GONE);
                    // We reset the loader, nullify old data
                    mTrailerAdapter.addAll(null);
                    mTrailerList.clear();
                }
            };


            // Bind data to views
            bindTo();
        }
    }

    // Called when the containing activity onCreate() is done, and after onCreateView() of fragment
    // Do final modification on the hierarchy e.g modify view elements and restore previous state
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Check which instance we are dealing with
        if (getActivity() instanceof DetailActivity) {
            // Set the Toolbar
            ((BaseToolBarActivity) getActivity()).setToolbar(mToolbar, true, false, R.drawable.ic_logo_48px);
            // Set Toolbar status bar transparency
            ((BaseToolBarActivity) getActivity()).setToolbarTransparent(true);
        }

        // Check for null
        if (mMovie != null) {
            // Initialize the Loader
            getActivity().getSupportLoaderManager().initLoader(REVIEWS_LOADER_ID, null, mReviewLoaderCallBack);
            getActivity().getSupportLoaderManager().initLoader(TRAILERS_LOADER_ID, null, mTrailerLoaderCallBack);
            // Create an Animation
            Animation simpleGrowAnimation = AnimationUtils.loadAnimation(mFavoriteFab.getContext(), R.anim.grow_bigger);
            // Animate the Floating action button
            mFavoriteFab.startAnimation(simpleGrowAnimation);
        }
    }

    public void onResume() {
        super.onResume();
        LoaderManager manager = getActivity().getSupportLoaderManager();

        manager.restartLoader(REVIEWS_LOADER_ID, null, mReviewLoaderCallBack);
        manager.restartLoader(TRAILERS_LOADER_ID, null, mTrailerLoaderCallBack);
    }

    // Called to create menu item
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_detail, menu);
    }

    // Do actions based on selected menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Share a movie trailer
            case R.id.action_share:
                // Check for null
                if (mMovie != null) {
                    // Create an Intent object
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    Trailer trailerShare = null;
                    try {
                        // Retrieve first trailer
                        trailerShare = mMovie.getMovieTrailers().get(0);
                        // Acquire the video url
                        String trailerUrl = String.format(EBaseURlTypes.YOUTUBE_VIDEO_URL.getUrlType(), trailerShare);
                        // Put the trailer url
                        intent.putExtra(Intent.EXTRA_TEXT, trailerUrl);
                        // Put a subject for Intent
                        intent.putExtra(Intent.EXTRA_SUBJECT, mMovie.getOriginalTitle());
                        // Start the share Intent
                        startActivity(Intent.createChooser(intent, "Share Trailer"));
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                    // Do we have any trailers?
                    if (trailerShare == null) {
                        // Inform user of unavailable trailers
                        Snackbar.make(getView(), R.string.status_no_trailers, Snackbar.LENGTH_SHORT);
                    }
                }
                return true;
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    // Bind data to the views
    private void bindTo() {
        // Build the image url
        String imageUrl = EBaseURlTypes.MOVIE_API_IMAGE_BASE_URL.getUrlType() + EBaseImageSize.IMAGE_SIZE_W500.getImageSize() + mMovie.getBackdropPath();

        Target target = new Target() {

            @Override
            public void onPrepareLoad(Drawable arg0) {
                // Show some progress
            }

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom arg1) {
                // Set background
                mBackdropMovie.setImageBitmap(bitmap);
                final Palette.PaletteAsyncListener paletteListener = new Palette.PaletteAsyncListener() {
                    public void onGenerated(Palette palette) {
                        //Default color
                        final int defaultColor = 0xEF5350;
                        // Access palette colors here
                        int mutedDarkColor = palette.getDarkMutedColor(defaultColor);
                        mDetailMovieTitleContainer.setBackgroundColor(mutedDarkColor);
                        // Get the "vibrant" color swatch based on the bitmap
                        Palette.Swatch vibrantSwatch = palette.getDarkMutedSwatch();
                        if (vibrantSwatch != null) {
                            int textColor = vibrantSwatch.getBodyTextColor();
                            // Set the title color
                            mDetailMovieTitle.setTextColor(textColor);
                        }

                        // Check for null
                        if (mBackdropMoviePlay != null) {
                            // Show play Button
                            mBackdropMoviePlay.setVisibility(View.VISIBLE);
                            // Do some Animation on play button
                            mBackdropMoviePlay.startAnimation(simpleGrowAnimation);
                        }
                    }
                };

                if (bitmap != null && !bitmap.isRecycled()) {
                    Palette.from(bitmap).generate(paletteListener);
                }

            }

            @Override
            public void onBitmapFailed(Drawable arg0) {
                // Something went wrong - Hide play button
                mBackdropMoviePlay.setVisibility(View.GONE);
            }
        };
        // Use Picasso to load the images
        Picasso.with(mBackdropMovie.getContext())
                .load(imageUrl)
                .networkPolicy(Utilities.NetworkUtility.isNetworkAvailable(mBackdropMovie.getContext()) ? NetworkPolicy.NO_CACHE : NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.ic_movie_placeholder)
                .into(target);

        // Set the title
        mDetailMovieTitle.setText(mMovie.getOriginalTitle());
        // Set the overview
        mDetailMovieOverview.setText(mMovie.getOverview());
        // Set the rating
        mDetailMovieRate.setText(String.format("%d/10", Math.round(mMovie.getVoteAverage())));
        // Set popularity
        mDetailMoviePopularity.setText(String.format("%d votes", Math.round(mMovie.getPopularity())));
        // Set the release date
        if (mMovie.getReleaseDate() != null) {
            Calendar mCalendar = Calendar.getInstance();
            mCalendar.setTime(mMovie.getReleaseDate());
            mDetailMovieYear.setText(String.valueOf(mCalendar.get(Calendar.YEAR)));
        }
        // Update FAB icon drawable
        updateFabDrawable();
    }

    // Start the review service
    private void startReviewIntentService() {
        // Create an Intent object
        Intent i = new Intent(getActivity(), ReviewIntentService.class);
        // Set any extras to pass over
        i.putExtra(ReviewIntentService.REQUEST_MOVIE_REMOTE_ID, mMovie.getId_());
        i.putExtra(ReviewIntentService.REQUEST_PAGE_NO_INT, 1);
        // Start the service
        getActivity().startService(i);
        // Check whether we are in debug mode
        if (BuildConfig.IS_DEBUG_MODE) {
            Log.d(LOG_TAG, "REVIEW SERVICE STARTED");
        }
    }

    // Start the trailer service
    private void startTrailerIntentService() {
        // Create an Intent object
        Intent i = new Intent(getActivity(), TrailerIntentService.class);
        // Set any extras to pass over
        i.putExtra(TrailerIntentService.REQUEST_MOVIE_REMOTE_ID, mMovie.getId_());
        i.putExtra(TrailerIntentService.REQUEST_PAGE_NO_INT, 1);
        // Start the service
        getActivity().startService(i);
        // Check whether we are in debug mode
        if (BuildConfig.IS_DEBUG_MODE) {
            Log.d(LOG_TAG, "TRAILER SERVICE STARTED");
        }
    }

    // Update the Review's empty view
    private void updateReviewsEmptyView() {
        // Update Reviews
        if (mReviewAdapter.getItemCount() == 0) {
            // Show Empty TextView
            mReviewRecycleView.setVisibility(View.GONE);
            mDetailMovieEmptyReviews.setVisibility(View.VISIBLE);
        } else {
            // Show RecycleView filled with movies
            mReviewRecycleView.setVisibility(View.VISIBLE);
            mDetailMovieEmptyReviews.setVisibility(View.GONE);
        }
    }

    // Update the Trailer's empty view
    private void updateTrailersEmptyView() {
        // Update Trailers
        if (mTrailerAdapter.getItemCount() == 0) {
            // Show Empty TextView
            mTrailerRecycleView.setVisibility(View.GONE);
            mDetailMovieEmptyTrailers.setVisibility(View.VISIBLE);
        } else {
            // Show RecycleView filled with movies
            mTrailerRecycleView.setVisibility(View.VISIBLE);
            mDetailMovieEmptyTrailers.setVisibility(View.GONE);
        }
    }

    // Update the Fab icon drawable
    private void updateFabDrawable() {
        // Toggle drawable
        mFavoriteFab.setImageResource(isFavourite ? R.drawable.ic_heart_full_red_48dp : R.drawable.ic_heart_full_white_48dp);
        // Toggle favourite text
        mDetailFavouriteTextView.setText(isFavourite ? "Yes" : "No");
    }

    // Attack click listener to FAB
    @OnClick(R.id.favorite_fab)
    public void onToggleFavouriteMovie() {
        // The response Animation
        Animation simpleRotateAnimation;
        if (!isFavourite) {
            // Set movie isFavourite to true
            mMovie.setIsFavourite(true);
            isFavourite = true;
            // Create an Animation
            simpleRotateAnimation = AnimationUtils.loadAnimation(mFavoriteFab.getContext(), R.anim.rotate_clockwise);
        } else {
            // Set movie isFavourite to false
            mMovie.setIsFavourite(false);
            isFavourite = false;
            // Create an Animation
            simpleRotateAnimation = AnimationUtils.loadAnimation(mFavoriteFab.getContext(), R.anim.rotate_anticlockwise);
        }
        // Save changes made on movie
        mMovie.save();
        // Update the drawable
        updateFabDrawable();
        // Animate the Floating action button
        mFavoriteFab.startAnimation(simpleRotateAnimation);
    }

}
