package com.cc.tbd.models



data class SaveNotes(
    var id:String? = null,
    var audio:String? = null,
    var fileUri: String? = null,
    var caption:String? = null,
    var scanText:String? = null,
    var imageUri:String? = null,
)
