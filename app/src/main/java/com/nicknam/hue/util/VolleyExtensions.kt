package com.nicknam.hue.util

import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by snick on 16-11-2017.
 */

/**
 * Creates a new request with Json Object as sending type and Json Array as receiving type.
 * @param method the HTTP method to use
 * @param url URL to fetch the JSON from
 * @param jsonRequest A JSONObject to post with the request.
 * @param listener Listener to receive the JSON response
 * @param errorListener Error listener, or null to ignore errors.
 */
class JsonObjectArrayRequest(method: Int, url: String, private val jsonRequest: JSONObject, listener: Response.Listener<JSONArray>, errorListener: Response.ErrorListener?) : StringRequest(method, url, Response.Listener<String>({ it ->
    listener.onResponse(try {
        JSONArray(it)
    } catch (e: JSONException) {
        null
    })
}), errorListener) {
    override fun getBody(): ByteArray = jsonRequest.toString().toByteArray()

    override fun getBodyContentType(): String = "application/json; charset=utf-8"
}