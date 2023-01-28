package com.cc.tbd.models

import android.graphics.Bitmap
import android.net.Uri

data class SubNote(
    var audio:String? = null,
    var fileUri: Uri? = null,
    var caption:String? = null,
    var scanText:String? = null,
    var imageUri:String? = null,
    var drawBitmap: Bitmap? = null
)
