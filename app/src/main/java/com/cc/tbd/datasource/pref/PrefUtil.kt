package com.cc.tbd.datasource.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

internal const val DATA_STORE_NAME = "tbd_data_store"
internal val IS_ANALYZING_COMPLETED_KEY = booleanPreferencesKey("is_analyzing_completed")
internal val IS_IMAGES_ANALYZING_COMPLETED_KEY = booleanPreferencesKey("is_images_analyzing_completed")
internal val IS_VIDEOS_ANALYZING_COMPLETED_KEY = booleanPreferencesKey("is_videos_analyzing_completed")
internal val IS_FIRST_TIME_KEY = booleanPreferencesKey("is_first_time")
internal val LAST_ITEM_DATE_KEY = longPreferencesKey("last_item_date")

internal val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATA_STORE_NAME)
