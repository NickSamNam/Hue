package com.nicknam.hue.util

import org.json.JSONArray
import org.json.JSONException

/**
 * Created by snick on 17-11-2017.
 */
/**
 * @throws NumberFormatException
 */
fun JSONArray.asIntList(): List<Int> {
    val l = ArrayList<Int>(this.length())
    for (i in 0 until this.length()) {
        try {
            l[i] = this[i] as? Int ?: (this.getString(i)).toInt()
        } catch (e: JSONException) {
            throw NumberFormatException()
        }
    }
    return l
}

fun JSONArray.asFloatList(): List<Float> {
    val l = ArrayList<Float>(this.length())
    for (i in 0 until this.length()) {
        try {
            l[i] = this[i] as? Float ?: (this.getString(i)).toFloat()
        } catch (e: JSONException) {
            throw NumberFormatException()
        }
    }
    return l
}