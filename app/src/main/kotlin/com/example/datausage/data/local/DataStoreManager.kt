package com.example.datausage.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

class DataStoreManager(private val context: Context) {

    companion object {
        val KEY_THRESHOLD_MB = longPreferencesKey("threshold_mb")
        val KEY_HISTORY = stringPreferencesKey("usage_history_json") // Storing simple history as JSON string for lightness
        val KEY_LAST_RECORD_DATE = longPreferencesKey("last_record_date")
    }

    val thresholdMb: Flow<Long> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_THRESHOLD_MB] ?: 1024L // Default 1GB
        }

    val usageHistory: Flow<List<HistoryEntry>> = context.dataStore.data
        .map { preferences ->
            val jsonString = preferences[KEY_HISTORY] ?: "[]"
            parseHistory(jsonString)
        }

    suspend fun setThreshold(mb: Long) {
        context.dataStore.edit { preferences ->
            preferences[KEY_THRESHOLD_MB] = mb
        }
    }

    suspend fun addHistoryEntry(entry: HistoryEntry) {
        context.dataStore.edit { preferences ->
            val currentJson = preferences[KEY_HISTORY] ?: "[]"
            val list = parseHistory(currentJson).toMutableList()
            
            // Remove entry if exists for same day (update it)
            list.removeAll { it.dateMillis == entry.dateMillis }
            
            // Add new entry
            list.add(entry)
            
            // Keep last 30 days only
            list.sortByDescending { it.dateMillis }
            if (list.size > 30) {
                list.removeAt(list.lastIndex)
            }
            
            preferences[KEY_HISTORY] = serializeHistory(list)
            preferences[KEY_LAST_RECORD_DATE] = System.currentTimeMillis()
        }
    }

    private fun parseHistory(json: String): List<HistoryEntry> {
        val list = mutableListOf<HistoryEntry>()
        try {
            val jsonArray = JSONArray(json)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(HistoryEntry(
                    dateMillis = obj.getLong("d"),
                    mobileBytes = obj.getLong("b")
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    private fun serializeHistory(list: List<HistoryEntry>): String {
        val jsonArray = JSONArray()
        list.forEach { 
            val obj = JSONObject()
            obj.put("d", it.dateMillis)
            obj.put("b", it.mobileBytes)
            jsonArray.put(obj)
        }
        return jsonArray.toString()
    }
}
