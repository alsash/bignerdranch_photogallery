package com.bignerdranch.android.bignerdranch_photogallery.utils;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

public class QueryPreferences {
    private static final String PREF_SEARCH_QUERY = "SearchQuery";

    @Nullable
    public static String getSortedQuery(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_SEARCH_QUERY, null);
    }

    public static void setSortedQuery(Context context, String query) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_SEARCH_QUERY, query)
                .apply();
    }
}
