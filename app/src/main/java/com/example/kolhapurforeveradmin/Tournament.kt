package com.example.kolhapurforeveradmin

data class Tournament(
    val tournamentId:String = "",
    val tournamentName:String = "",
    val tournamentLogo:String = "",
    val teams:ArrayList<Team> = ArrayList()
)