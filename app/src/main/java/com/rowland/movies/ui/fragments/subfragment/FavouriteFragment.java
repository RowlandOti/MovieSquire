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

package com.rowland.movies.ui.fragments.subfragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rowland.movies.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *
 */
public class FavouriteFragment extends Fragment {
    // ButterKnife injected Views
    @Bind(R.id.sw_refresh_layout) SwipeRefreshLayout swRefreshLayout;
    @Bind(R.id.grid_recycle_view) RecyclerView recycleView;
    // Logging tracker for this class
    private final String LOG_TAG = FavouriteFragment.class.getSimpleName();

    public static FavouriteFragment newInstance(Bundle args)
    {
        FavouriteFragment fragmentInstance = new FavouriteFragment();
        if(args != null)
        {
            fragmentInstance.setArguments(args);
        }
        return fragmentInstance;

    }
    public FavouriteFragment()
    {

    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_favourite, container, false);
        // Initialize the ViewPager and TabStripLayout
        ButterKnife.bind(this, rootView);
        // Return the view for this fragment
        return rootView;
    }
}
