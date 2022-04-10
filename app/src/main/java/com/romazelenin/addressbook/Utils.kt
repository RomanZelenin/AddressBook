package com.romazelenin.addressbook

import java.text.SimpleDateFormat
import java.util.Date


fun formatDate(date: String, format: String = "d MMMM yyyy"): String {
    val cleanDate = SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).parse(date)
    return SimpleDateFormat(format, java.util.Locale.getDefault()).format(cleanDate)
}

fun parsingDate(date: String): Date {
    return SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).parse(date)
}

fun getAge(date: String): Int {
    val cleanDate = SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).parse(date)
    return ((Date().time - cleanDate.time) / (1000L * 60L * 60L * 24L * 365L)).toInt()
}

fun getNowDate() = Date()

fun getNextYear(): Int = 1900 + Date().year + 1
