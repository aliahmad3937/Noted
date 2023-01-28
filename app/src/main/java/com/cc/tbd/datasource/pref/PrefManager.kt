package com.cc.tbd.datasource.pref

import kotlinx.coroutines.flow.Flow

internal interface PrefManager {
    fun isAnalyzingCompleted(): Flow<Boolean>
    fun isImagesAnalyzingCompleted(): Flow<Boolean>
    fun isVideosAnalyzingCompleted(): Flow<Boolean>
    fun isUserFirstTime(): Flow<Boolean>
    fun lastItemDate(): Flow<Long>
    suspend fun updateAnalyzingValue(isCompleted: Boolean)
    suspend fun updateImagesAnalyzingValue(isCompleted: Boolean)
    suspend fun updateVideosAnalyzingValue(isCompleted: Boolean)
    suspend fun updateIsFirstTimeValue(isFirstTime: Boolean)
    suspend fun updateLastItemDateValue(lastItemDateNewValue: Long)
}