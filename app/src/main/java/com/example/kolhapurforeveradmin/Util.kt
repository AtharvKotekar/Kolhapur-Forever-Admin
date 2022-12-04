package com.example.kolhapurforeveradmin

import android.text.Editable
import java.text.SimpleDateFormat
import java.util.*

fun getRandomString(length: Int) : String {
    val ALLOWED_CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    val random = Random()
    val sb = StringBuilder(length)
    for (i in 0 until length)
        sb.append(ALLOWED_CHARACTERS[random.nextInt(ALLOWED_CHARACTERS.length)])
    return sb.toString()
}

fun convertDateToLong(date: String): Long {
    val df = SimpleDateFormat("dd.MM.yyyy, HH:mm:ss")
    return df.parse(date).time
}

fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)