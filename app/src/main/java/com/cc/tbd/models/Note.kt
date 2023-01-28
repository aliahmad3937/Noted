package com.cc.tbd.models

import android.graphics.Bitmap
import android.net.Uri
import com.google.firebase.Timestamp

data class Note(
    var id: String? = null,
    var title: String? = null,
    var sub_title: String? = null,
    var timestamp: Timestamp? = null, // latest note timestamp
    var sub_notes: ArrayList<SaveNotes>? = null
)
