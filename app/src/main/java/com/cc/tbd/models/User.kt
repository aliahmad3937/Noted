package com.cc.tbd.models

data class User(
    var id: String = "",
    var uname: String = "",
    var email: String = "",
    var password: String = "",
    var referral: ArrayList<String>? = null
)
