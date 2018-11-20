package com.hellmund.meetingtalkdetector.util

import android.content.Context
import android.preference.PreferenceManager

object ServerUtils {

    private const val KEY_SERVER_ADDRESS = "server_address"

    fun getServerAddress(context: Context): String {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPrefs.getString(KEY_SERVER_ADDRESS, "")
    }

    fun setServerAddress(context: Context, address: String) {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPrefs.edit().putString(KEY_SERVER_ADDRESS, address).apply()
    }

}
