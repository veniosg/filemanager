/*
 * Copyright (C) 2014 George Venios
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.veniosg.dir.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import com.veniosg.dir.IntentConstants;
import com.veniosg.dir.R;
import com.veniosg.dir.view.Themer;

/**
 * @author George Venios
 */
public class PreferenceFragment extends android.preference.PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String PREFS_MEDIASCAN = "mediascan";
    private static final String PREFS_DISPLAYHIDDENFILES = "displayhiddenfiles";
    private static final String PREFS_DEFAULTPICKFILEPATH = "defaultpickfilepath";
    private static final String PREFS_SHOWTHUMBNAILS = "showthumbnails";
    private static final String PREFS_SORTBY = "sortby";
    private static final String PREFS_ASCENDING = "ascending";
    public static final String PREFS_THEME = "themeindex";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        // Register the onSharedPreferenceChanged listener to update the SortBy ListPreference summary
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        // Set the onSharedPreferenceChanged listener summary to its initial value
        changeListPreferenceSummaryToCurrentValue((ListPreference) findPreference(PREFS_SORTBY));
        changeListPreferenceSummaryToCurrentValue((ListPreference) findPreference(PREFS_THEME));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setBackgroundResource(Themer.getThemedResourceId(getActivity(), android.R.attr.colorBackground));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case PREFS_SORTBY:
                changeListPreferenceSummaryToCurrentValue((ListPreference) findPreference(key));
                break;
            case PREFS_THEME:
                changeListPreferenceSummaryToCurrentValue((ListPreference) findPreference(key));
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(IntentConstants.ACTION_REFRESH_THEME));
                break;
       }
    }

    private void changeListPreferenceSummaryToCurrentValue(ListPreference listPref) {
        listPref.setSummary(listPref.getEntry());
    }

    public static boolean getMediaScanFromPreference(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREFS_MEDIASCAN, false);
    }

    static void setDisplayHiddenFiles(Context context, boolean enabled) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(PREFS_DISPLAYHIDDENFILES, enabled);
        editor.commit();
    }

    public static boolean getDisplayHiddenFiles(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREFS_DISPLAYHIDDENFILES, false);
    }

    static void setShowThumbnails(Context context, boolean enabled) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(PREFS_SHOWTHUMBNAILS, enabled);
        editor.commit();
    }

    public static boolean getShowThumbnails(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREFS_SHOWTHUMBNAILS, true);
    }

    public static int getSortBy(Context context) {
        /* entryValues must be a string-array while we need integers */
        return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREFS_SORTBY, "1"));
    }

    public static boolean getAscending(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREFS_ASCENDING, true);
    }

    public static void setDefaultPickFilePath(Context context, String path) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PREFS_DEFAULTPICKFILEPATH, path);
        editor.commit();
    }

    public static String getDefaultPickFilePath(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREFS_DEFAULTPICKFILEPATH,
                        Environment.getExternalStorageState().equals(
                                Environment.MEDIA_MOUNTED)
                                ? Environment.getExternalStorageDirectory().getAbsolutePath()
                                : "/"
                );
    }

    /**
     * Get the current theme as selected in preferences.
     *
     * @return The theme index as defined in Themer#Theme.
     */
    public static int getThemeIndex(Context context) {
        /* entryValues must be a string-array while we need integers */
        return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREFS_THEME, String.valueOf(Themer.Theme.DIR.ordinal())));
    }

}
