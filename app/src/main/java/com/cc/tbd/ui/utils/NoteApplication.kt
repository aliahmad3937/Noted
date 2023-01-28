package com.cc.tbd.ui.utils

import android.app.Application

class NoteApplication : Application() {

    override fun onCreate() {
        super.onCreate()
    }

    companion object{
        var isTextScan = false
    }

}