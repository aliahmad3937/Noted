package com.cc.tbd.datasource.pref

import android.content.Context
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class PrefManagerImp(context: Context) : PrefManager {
    private val dataStore = context.dataStore

    override fun isAnalyzingCompleted(): Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[IS_ANALYZING_COMPLETED_KEY] ?: false
        }

    override fun isImagesAnalyzingCompleted(): Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[IS_IMAGES_ANALYZING_COMPLETED_KEY] ?: false
        }

    override fun isVideosAnalyzingCompleted(): Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[IS_VIDEOS_ANALYZING_COMPLETED_KEY] ?: false
        }

    override fun isUserFirstTime(): Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[IS_FIRST_TIME_KEY] ?: true
        }

    override fun lastItemDate(): Flow<Long> = dataStore.data
        .map { preferences ->
            preferences[LAST_ITEM_DATE_KEY] ?: 0L
        }

    override suspend fun updateAnalyzingValue(isCompleted: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_ANALYZING_COMPLETED_KEY] = isCompleted
        }
    }

    override suspend fun updateImagesAnalyzingValue(isCompleted: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_IMAGES_ANALYZING_COMPLETED_KEY] = isCompleted
        }
    }

    override suspend fun updateVideosAnalyzingValue(isCompleted: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_VIDEOS_ANALYZING_COMPLETED_KEY] = isCompleted
        }
    }

    override suspend fun updateIsFirstTimeValue(isFirstTime: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_FIRST_TIME_KEY] = isFirstTime
        }
    }

    override suspend fun updateLastItemDateValue(lastItemDateNewValue: Long) {
        dataStore.edit { preferences ->
            preferences[LAST_ITEM_DATE_KEY] = lastItemDateNewValue
        }
    }
}