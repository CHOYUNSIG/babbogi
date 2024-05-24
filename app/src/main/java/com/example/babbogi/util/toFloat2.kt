package com.example.babbogi.util

fun String.toFloat2(whenError: Float = 0.0f): Float {
    return if(this.isNotEmpty()) try {
        this.toFloat()
    } catch (e: NumberFormatException) {
        whenError
    } else { 0.0f }
}
