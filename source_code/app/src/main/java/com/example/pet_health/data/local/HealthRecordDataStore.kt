package com.example.pet_health.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "health_records")

class HealthRecordDataStore(private val context: Context) {

    private val RECORDS_KEY = stringSetPreferencesKey("records")

    // Flow đọc danh sách record
    val getRecords: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[RECORDS_KEY] ?: emptySet()
    }

    // Lưu record mới
    suspend fun saveRecord(record: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[RECORDS_KEY] ?: emptySet()
            prefs[RECORDS_KEY] = current + record
        }
    }
}
