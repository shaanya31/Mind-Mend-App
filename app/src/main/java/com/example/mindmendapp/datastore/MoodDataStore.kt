package com.example.mindmendapp.datastore

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.edit
import com.example.mindmendapp.model.MoodEntry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// DataStore delegate (one per file)
private val Context.dataStore by preferencesDataStore(name = "mindmend_prefs")

class MoodDataStore(private val context: Context) {

    companion object {
        private val KEY_ENTRIES = stringPreferencesKey("mood_entries_json")
        private val gson = Gson()
    }

    /**
     * Flow of stored MoodEntry list (most recent first).
     */
    fun getEntriesFlow(): Flow<List<MoodEntry>> {
        return context.dataStore.data.map { prefs ->
            val json = prefs[KEY_ENTRIES] ?: "[]"
            val type = object : TypeToken<List<MoodEntry>>() {}.type
            gson.fromJson<List<MoodEntry>>(json, type) ?: emptyList()
        }
    }

    /**
     * Save a new entry (adds to front of list).
     */
    suspend fun saveEntry(entry: MoodEntry) {
        context.dataStore.edit { prefs ->
            val json = prefs[KEY_ENTRIES] ?: "[]"
            val type = object : TypeToken<MutableList<MoodEntry>>() {}.type
            val list: MutableList<MoodEntry> = gson.fromJson(json, type) ?: mutableListOf()
            list.add(0, entry) // newest first
            prefs[KEY_ENTRIES] = gson.toJson(list)
        }
    }

    /**
     * Delete an entry (matches by timestamp + mood).
     * Call from a coroutine scope: scope.launch { moodDataStore.deleteEntry(entry) }
     */
    suspend fun deleteEntry(entry: MoodEntry) {
        context.dataStore.edit { prefs ->
            val json = prefs[KEY_ENTRIES] ?: "[]"
            val type = object : TypeToken<MutableList<MoodEntry>>() {}.type
            val list: MutableList<MoodEntry> = gson.fromJson(json, type) ?: mutableListOf()
            list.removeAll { it.timestamp == entry.timestamp && it.mood == entry.mood }
            prefs[KEY_ENTRIES] = gson.toJson(list)
        }
    }
}
