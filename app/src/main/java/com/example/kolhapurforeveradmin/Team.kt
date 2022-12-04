package com.example.kolhapurforeveradmin

data class Team (
    val teamId:String = "",
    val name:String = "",
    val logo:String = "",
    val matchPlayed:Int = 0,
    val matchWon:Int = 0,
    val matchLoss:Int = 0,
    val matchDraw:Int = 0,
    val totalPoints:Int = 0,
    val goalsGF:Int = 0,
    val goalsGA:Int = 0
)