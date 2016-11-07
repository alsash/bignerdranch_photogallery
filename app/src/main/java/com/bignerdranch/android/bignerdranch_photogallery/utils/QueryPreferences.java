package com.bignerdranch.android.bignerdranch_photogallery.utils;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

public class QueryPreferences {
    private static final String PREF_SEARCH_QUERY = "SearchQuery";
    private static final String PREF_LAST_RESULT_ID = "LastResultId";

    @Nullable
    public static String getSortedQuery(Context context) {
        return getStringPref(context, PREF_SEARCH_QUERY);
    }

    public static void setSortedQuery(Context context, String query) {
        setStringPref(context, PREF_SEARCH_QUERY, query);
    }

    @Nullable
    public static String getLastResultId(Context context) {
        return getStringPref(context, PREF_LAST_RESULT_ID);
    }

    public static void setLastResultId(Context context, String lastResultId) {
        setStringPref(context, PREF_LAST_RESULT_ID, lastResultId);
    }

    @Nullable
    private static String getStringPref(Context context, String prefName) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(prefName, null);
    }

    private static void setStringPref(Context context, String prefName, String prefValue) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(prefName, prefValue)
                .apply();
    }
}
