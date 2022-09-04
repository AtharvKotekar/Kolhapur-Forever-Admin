package com.example.kolhapurforeveradmin

import java.sql.Timestamp


data class Sponser(
    var id :String = "",
    var name:String = "",
    var offer:String = "",
    var logo:String = "",
    var image:ArrayList<String> = ArrayList(),
    var address:String = "",
    var contact:String = "",
    var barcode:String = "",
    var addressLink:String = "",
    var timestamp: Long = 0L
)