package com.hellmund.meetingtalkdetector.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import org.jetbrains.anko.defaultSharedPreferences

class RecordingsStore(private val sharedPrefs: SharedPreferences) {

    // Using SharedPreferences due to time constraints; ideally, we'd use a proper database

    private val gson = Gson()

    fun getAll(): List<Recording> {
        return getAll(sharedPrefs, KEY_RECORDINGS)
    }

    private fun getAll(sharedPrefs: SharedPreferences, key: String): MutableList<Recording> {
        val stringSets = sharedPrefs.getStringSet(key, emptySet<String>())
        return stringSets
            .map { gson.fromJson(it, Recording::class.java) }
            .toMutableList()
    }

    fun store(recording: Recording) {
        transaction {
            add(recording)
        }
    }

    fun delete(recording: Recording) {
        transaction {
            remove(recording)
        }
    }

    private fun transaction(block: MutableList<Recording>.() -> Unit) {
        val recordings = getAll(sharedPrefs, KEY_RECORDINGS)
        recordings.block()
        val newStrings = recordings.map { gson.toJson(it) }
        sharedPrefs.edit().putStringSet(KEY_RECORDINGS, newStrings.toSet()).apply()
    }

    companion object {

        private const val KEY_RECORDINGS = "recordings"

        private lateinit var INSTANCE: RecordingsStore

        fun getInstance(context: Context): RecordingsStore {
            if (this::INSTANCE::isInitialized.get()) {
                INSTANCE = RecordingsStore(context.defaultSharedPreferences)
            }

            return INSTANCE
        }

    }

}
