package com.example.mindmendapp.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.mindmendapp.model.MoodEntry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "mindmend_prefs")

class MoodDataStore(private val context: Context) {

    companion object {
        private val KEY_ENTRIES = stringPreferencesKey("mood_entries_json")
        private val gson = Gson()
    }

    fun getEntriesFlow(): Flow<List<MoodEntry>> {
        return context.dataStore.data.map { prefs ->
            val json = prefs[KEY_ENTRIES] ?: "[]"
            val type = object : TypeToken<List<MoodEntry>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        }
    }

    suspend fun saveEntry(entry: MoodEntry) {
        context.dataStore.edit { prefs ->
            val json = prefs[KEY_ENTRIES] ?: "[]"
            val type = object : TypeToken<List<MoodEntry>>() {}.type
            val list: MutableList<MoodEntry> =
                gson.fromJson(json, type) ?: mutableListOf()

            list.add(0, entry) // newest entry first
            prefs[KEY_ENTRIES] = gson.toJson(list)
        }
    }
}