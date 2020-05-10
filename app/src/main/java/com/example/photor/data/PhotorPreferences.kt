package com.example.photor.data

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager

private const val PREF_SEARCH_QUERY = "searchQuery"
private const val PREF_LAST_RESULT_ID = "lastResultId"
private const val PREF_IS_POLLING = "isPolling"

object PhotorPreferences {

    fun getStoredQuery(context: Context): String = getString(context, PREF_SEARCH_QUERY)
    fun setStoredQuery(context: Context, query: String) =
        setString(context, PREF_SEARCH_QUERY, query)

    fun getLastResultId(context: Context): String = getString(context, PREF_LAST_RESULT_ID)
    fun setLastResultId(context: Context, lastResultId: String) =
        setString(context, PREF_LAST_RESULT_ID, lastResultId)

    fun isPolling(context: Context): Boolean =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(PREF_IS_POLLING, false)

    fun setPolling(context: Context, isPolling: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putBoolean(PREF_IS_POLLING, isPolling)
            }
    }

    private fun getString(context: Context, key: String): String {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, "")!!
    }

    private fun setString(context: Context, key: String, value: String) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putString(key, value)
            }
    }
}
