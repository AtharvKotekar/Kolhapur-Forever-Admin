package com.example.kolhapurforeveradmin

import java.sql.Timestamp

data class Compitition(
    val id:String = "",
    val name:String = "",
    val startDate:String = "",
    val endDate:String = "",
    val firstPrice:String = "",
    val secondPrice:String = "",
    val thirdPrice:String = "",
    val type:String = "",
    val timestamp: Long = 0L
)
