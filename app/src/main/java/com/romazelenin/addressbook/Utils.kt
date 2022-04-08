package com.romazelenin.addressbook

import androidx.compose.ui.text.intl.Locale
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date


fun parsingDate(date: String): String {
    val cleanDate = SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).parse(date)
    return SimpleDateFormat("d MMMM yyyy", java.util.Locale.getDefault()).format(cleanDate)
}

fun getAge(date: String): Int {
    val cleanDate = SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).parse(date)
    return ((Date().time - cleanDate.time)/(1000L*60L*60L*24L*365L)).toInt()
}