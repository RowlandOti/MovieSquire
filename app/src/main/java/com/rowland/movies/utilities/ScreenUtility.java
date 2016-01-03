package com.rowland.movies.utilities;/* Copyright 2012 Charles Harley
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
 */

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

/**
 * Provides utility methods for working with the device screen.
 */
public class ScreenUtility {

    /**
     * Converts the given device independent pixels (DIP) value into the corresponding pixels
     * value for the current screen.
     *
     * @param context Context instance
     * @param dip     The DIP value to convert
     * @return The pixels value for the current screen of the given DIP value.
     */
    public static int convertDIPToPixels(Context context, int dip) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, displayMetrics);
    }

    /**
     * Converts the given device independent pixels (DIP) value into the corresponding pixels
     * value for the current screen.
     *
     * @param context Context instance
     * @param dip     The DIP value to convert
     * @return The pixels value for the current screen of the given DIP value.
     */
    public static int convertDIPToPixels(Context context, float dip) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, displayMetrics);
    }

    /**
     * Converts the given pixels value into the corresponding device independent pixels (DIP)
     * value for the current screen.
     *
     * @param context Context instance
     * @param pixels  The pixels value to convert
     * @return The DIP value for the current screen of the given pixels value.
     */
    public static float convertPixelsToDIP(Context context, int pixels) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return pixels / (displayMetrics.densityDpi / 160f);
    }

    /**
     * Returns the current screen dimensions in device independent pixels (DIP) as a {@link Point} object where
     * {@link Point#x} is the screen width and {@link Point#y} is the screen height.
     *
     * @param context Context instance
     * @return The current screen dimensions in DIP.
     */
    public static Point getScreenDimensionsInDIP(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Configuration configuration = context.getResources().getConfiguration();
            return new Point(configuration.screenWidthDp, configuration.screenHeightDp);

        } else {
            // APIs prior to v13 gave the screen dimensions in pixels. We convert them to DIPs before returning them.
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);

            int screenWidthInDIP = (int) convertPixelsToDIP(context, displayMetrics.widthPixels);
            int screenHeightInDIP = (int) convertPixelsToDIP(context, displayMetrics.heightPixels);
            return new Point(screenWidthInDIP, screenHeightInDIP);
        }
    }

    /**
     * @param context Context instance
     * @return [true] if the device is in landscape orientation, [false] otherwise.
     */
    public static boolean isInLandscapeOrientation(Context context) {
        Configuration configuration = context.getResources().getConfiguration();
        return configuration.orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    /**
     * @param context Context instance
     * @return [true] if the device has a small screen, [false] otherwise.
     */
    public static boolean hasSmallScreen(Context context) {
        return getScreenSize(context) == Configuration.SCREENLAYOUT_SIZE_SMALL;
    }

    /**
     * @param context Context instance
     * @return [true] if the device has a normal screen, [false] otherwise.
     */
    public static boolean hasNormalScreen(Context context) {
        return getScreenSize(context) == Configuration.SCREENLAYOUT_SIZE_NORMAL;
    }

    /**
     * @param context Context instance
     * @return [true] if the device has a large screen, [false] otherwise.
     */
    public static boolean hasLargeScreen(Context context) {
        return getScreenSize(context) == Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * @param context Context instance
     * @return [true] if the device has an extra large screen, [false] otherwise.
     */
    public static boolean hasXLargeScreen(Context context) {
        return getScreenSize(context) == Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * The size of the screen, one of 4 possible values:
     * <p/>
     * <ul>
     * <li>http://developer.android.com/reference/android/content/res/Configuration.html#SCREENLAYOUT_SIZE_SMALL</li>
     * <li>http://developer.android.com/reference/android/content/res/Configuration.html#SCREENLAYOUT_SIZE_NORMAL</li>
     * <li>http://developer.android.com/reference/android/content/res/Configuration.html#SCREENLAYOUT_SIZE_LARGE</li>
     * <li>http://developer.android.com/reference/android/content/res/Configuration.html#SCREENLAYOUT_SIZE_XLARGE</li>
     * </ul>
     * <p/>
     * See http://developer.android.com/reference/android/content/res/Configuration.html#screenLayout for more details.
     *
     * @param context Context instance
     * @return The size of the screen
     */
    public static int getScreenSize(Context context) {
        Configuration configuration = context.getResources().getConfiguration();
        return configuration.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
    }

}
