

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

package com.rowland.movies.asynctaskloaders;

import android.annotation.TargetApi;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.rowland.movies.asynctaskloaders.broadcastrecievers.BaseLoaderBroadCastReceiver;
import com.uwetrottmann.androidutils.GenericSimpleLoader;

import java.util.List;
/*ToDo: Improve Loader using tutorial below
* <a>http://www.androiddesignpatterns.com/2012/08/implementing-loaders.html</a>
* */
public abstract class BaseLoader<T> extends GenericSimpleLoader<T> {
    // An observer to listen for changes in data
    private BaseLoaderBroadCastReceiver mLoaderObserver;

    public BaseLoader(Context context) {
        super(context);
    }
    /**
     * Handles a request to start the Loader.
     * Checking for takeContentChanged seems an important step too
     * See <a>http://stackoverflow.com/a/19480980/1464571</a>
     */
    @Override
    protected void onStartLoading() {
        if (mItems != null) {
            // Deliver any previously loaded data immediately.
            deliverResult(mItems);
        }
        // Register Observer - Start watching for changes in the app data.
        if (mLoaderObserver == null) {
            mLoaderObserver = new BaseLoaderBroadCastReceiver(this);
        }
        // When the observer detects a change, it should call onContentChanged()
        // on the Loader, which will cause the next call to takeContentChanged() to return true.
        if (takeContentChanged() || mItems == null) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, force a new load.
            forceLoad();
        }
    }
    /**
     * Handles a request to completely reset the Loader.
     */
    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        // At this point we can release resources
        if (mItems != null) {
            onReleaseResources(mItems);
            mItems = null;
        }

        // Unregister Observer - Stop monitoring for changes.
        if (mLoaderObserver != null) {
            getContext().unregisterReceiver(mLoaderObserver);
            mLoaderObserver = null;
        }
    }
}
