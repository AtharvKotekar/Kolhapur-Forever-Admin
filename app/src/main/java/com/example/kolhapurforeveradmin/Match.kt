package com.example.kolhapurforeveradmin

data class Match(
    val tournamentId:String = "",
    val tournamentName:String = "",
    val matchId:String = "",
    val team1Id:String = "",
    val team2Id:String = "",
    val team1Name:String = "",
    val team2Name:String = "",
    val team1Logo:String = "",
    val team2Logo:String = "",
    val sponserId:String = "",
    val team1Score:Int = 0,
    val team2Score:Int = 0,
    val startTime:String = "",
    val goals:ArrayList<Goals> = ArrayList(),
    val startTimestamp: Long = 0L
)