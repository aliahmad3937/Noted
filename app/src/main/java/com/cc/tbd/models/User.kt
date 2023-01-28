package com.cc.tbd.models

import java.sql.Timestamp

data class User(
    var id: String = "",
    var uname: String = "",
    var email: String = "",
    var password: String = "",
    val referral: Referral? = null
)
data class Referral(
    var userID:String? =null,
    var time:Timestamp? = null
)
