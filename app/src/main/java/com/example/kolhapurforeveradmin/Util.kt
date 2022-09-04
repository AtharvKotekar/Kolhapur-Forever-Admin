package com.example.kolhapurforeveradmin

import android.text.Editable
import java.util.*

fun getRandomString(length: Int) : String {
    val ALLOWED_CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    val random = Random()
    val sb = StringBuilder(length)
    for (i in 0 until length)
        sb.append(ALLOWED_CHARACTERS[random.nextInt(ALLOWED_CHARACTERS.length)])
    return sb.toString()
}

fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)