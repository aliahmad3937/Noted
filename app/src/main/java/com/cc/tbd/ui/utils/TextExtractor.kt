package com.cc.tbd.ui.utils

import android.net.Uri
import kotlinx.coroutines.flow.Flow

interface TextExtractor {

    suspend fun image(uri: Uri) : Flow<String>

}