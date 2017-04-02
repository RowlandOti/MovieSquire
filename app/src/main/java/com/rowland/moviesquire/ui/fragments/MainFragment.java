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

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.SlidingTabStripLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.rowland.moviesquire.R;
import com.rowland.moviesquire.objects.ListPopupMenu;
import com.rowland.moviesquire.ui.activities.MainActivity;
import com.rowland.moviesquire.ui.adapters.ListPopupWindowAdapter;
import com.rowland.moviesquire.ui.adapters.SmartNestedViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

    // Selected tab key
    public static final String SELECTED_TAB_KEY = "SELECTED_TAB";
    // The class Log identifier
    private final String LOG_TAG = MainFragment.class.getSimpleName();
    // ButterKnife injected views
    @Nullable
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.slidingTabStrips)
    SlidingTabStripLayout mSlidingTabStrips;
    @Bind(R.id.viewPager)
    ViewPager mViewPager;
    // The Subfragment titles
    private String[] TITLES = {"Popular", "Highest Rated", "Favourite"};
    // The adapter that manages the subfragments
    private SmartNestedViewPagerAdapter pagerAdapter;
    // The class selection callback
    private IMovieSelectionCallBack mMovieSelectionCallBack;
    // ListPopup max width
    private float mPopupMaxWidth;
    // The currently selected tab strip
    private int selectedTabStrip = 0;

    // Default constructor
    public MainFragment() {

    }

    // Create a new Instance for this fragment
    public static MainFragment newInstance(Bundle args) {
        // Create the new fragment instance
        MainFragment fragmentInstance = new MainFragment();
        // Set arguments if it is not null
        if (args != null) {
            fragmentInstance.setArguments(args);
        }
        // Return the new fragment
        return fragmentInstance;
    }

    //
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Let the fragment handle its menu items
        setHasOptionsMenu(true);
        // Don't destroy fragment across orientation change
        setRetainInstance(true);
        //Get the maximum width of our ListPopupWindow
        this.mPopupMaxWidth = Math.min(this.getResources().getDisplayMetrics().widthPixels / 2,
                this.getResources().getDimensionPixelSize(R.dimen.config_prefListPopupWindowWidth));
    }

    // Create the view for this fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        // Initialize the ViewPager and TabStripLayout
        ButterKnife.bind(this, rootView);
        // Return the view for this fragment
        return rootView;
    }


    // Save data for this fragment
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Acquire the currently selected tab
        selectedTabStrip = mViewPager.getCurrentItem();
        // Save the currently selected tab position
        outState.putInt(SELECTED_TAB_KEY, selectedTabStrip);
    }

    // Called after the containing activity is created
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Set the ToolBar
        ((MainActivity) getActivity()).setToolbar(mToolbar, false, false, R.drawable.ic_logo_48px);
    }

    // Called after fragment's view is created by onCreateView()
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Initialize the fragments adapter
        pagerAdapter = new SmartNestedViewPagerAdapter(getActivity().getSupportFragmentManager());
        // Set up the adapter
        mViewPager.setAdapter(pagerAdapter);
        // Set up the viewPager
        mSlidingTabStrips.setupWithViewPager(mViewPager);
        // Restore states
        if (savedInstanceState != null) {
            // Acquire previously selected tab.
            selectedTabStrip = savedInstanceState.getInt(SELECTED_TAB_KEY, selectedTabStrip);
            // Restore previously selected tab
            mViewPager.setCurrentItem(selectedTabStrip, true);
        }
    }

    // Called after fragment is attached to activity
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Ensure attached activity has implemented the callback interface.
        try {
            // Acquire the implemented callback
            mMovieSelectionCallBack = (IMovieSelectionCallBack) context;
        } catch (ClassCastException e) {
            // If not, it throws an exception
            throw new ClassCastException(context.toString() + " must implement IMovieSelectionCallBack");
        }
    }

    // Called after fragment is detached from activity
    @Override
    public void onDetach() {
        // Avoid leaking,
        mMovieSelectionCallBack = null;
        super.onDetach();
    }

    // Create  the menu items for fragment
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate new menu.
        inflater.inflate(R.menu.menu_main_fragment, menu);
    }

    // Do actions based on selected menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                return true;
            case R.id.action_overflow:
                // Works as long as list item is always visible and does not go into the menu overflow
                final View menuItemView = getActivity().findViewById(R.id.action_overflow);
                onListPopUp(menuItemView);
                return true;
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    // We need to attribute our API source
    public void onShowCreditsDialog() {
        // Acquire a Dialog builder object
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Use this to inflate custom view
        LayoutInflater factory = LayoutInflater.from(getActivity());
        // Acquire custom view
        final View view = factory.inflate(R.layout.dialog_credit, null);
        // Associate dialog with custom view
        builder.setView(view);
        // Use this button to dismiss dialog
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //
                builder.create().dismiss();
            }
        });
        // Final call that will display dialog
        builder.create().show();
    }

    public void onListPopUp(View anchor) {
        // This a sample dat to fill our ListView
        List<ListPopupMenu> menuItem = new ArrayList<>();
        menuItem.add(new ListPopupMenu(R.drawable.ic_popular_black_48dp, "Popular"));
        menuItem.add(new ListPopupMenu(R.drawable.ic_rated_black_48dp, "Highest Rated"));
        menuItem.add(new ListPopupMenu(R.drawable.ic_favourite_black_48dp, "Favourite"));
        menuItem.add(new ListPopupMenu(R.drawable.ic_overview_black_48dp, "Credits"));
        // Initialise our adapter
        ListPopupWindowAdapter mListPopUpAdapter = new ListPopupWindowAdapter(getActivity().getApplicationContext(), menuItem);
        // Initialise our ListPopupWindow instance
        final ListPopupWindow pop = new ListPopupWindow(getActivity().getApplicationContext());
        // Configure ListPopupWindow properties
        pop.setAdapter(mListPopUpAdapter);
        // Set the view below/above which ListPopupWindow dropdowns
        pop.setAnchorView(anchor);
        // Setting this enables window to be dismissed by click outside ListPopupWindow
        pop.setModal(true);
        // Set the background color
        pop.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.apptheme_transparent)));
        // Set the selector color
        pop.setListSelector(new ColorDrawable(getResources().getColor(R.color.apptheme_accent_teal)));
        // Sets the width of the ListPopupWindow
        pop.setContentWidth((int) this.mPopupMaxWidth);
        // Sets the Height of the ListPopupWindow
        pop.setHeight(ListPopupWindow.WRAP_CONTENT);
        // Set up a click listener for the ListView items
        pop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Dismiss the LisPopupWindow when a list item is clicked
                pop.dismiss();
                // The overflow menu selected
                String menuName = ((ListPopupMenu) adapterView.getItemAtPosition(position)).getName();
                // Switch to the right ViewPager element at given position
                switch (menuName) {
                    case "Popular":
                        mViewPager.setCurrentItem(0, true);
                        break;
                    case "Highest Rated":
                        mViewPager.setCurrentItem(1, true);
                        break;
                    case "Favourite":
                        mViewPager.setCurrentItem(2, true);
                        break;
                    case "Credits":
                        onShowCreditsDialog();
                        break;
                    default:
                        mViewPager.setCurrentItem(0, true);
                        break;
                }
            }
        });
        pop.show();
    }

    public String[] getTITLES() {
        return TITLES;
    }

    // A callback interface that all containing activities implement
    public interface IMovieSelectionCallBack {
        // Call this when movie is selected.
        void onMovieSelected(long movieId, int selectedPosition);
    }
}
