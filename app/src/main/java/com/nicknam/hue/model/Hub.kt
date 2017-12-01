package com.nicknam.hue.model

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.nicknam.hue.util.JsonObjectArrayRequest
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by snick on 15-11-2017.
 */
class Hub private constructor() {
    var Lights: MutableList<Light> = ArrayList()
        private set
    var Groups: MutableList<Group> = ArrayList()
        private set
    private lateinit var _requestQueue: RequestQueue
    private var _ipAddress: String = "0.0.0.0"
    private var _baseUrl = "http://$_ipAddress/api"
    lateinit var Username: String

    fun init(ipAddress: String, context: Context, listener: RequestListener) {
        _ipAddress = ipAddress
        _baseUrl = "http://$_ipAddress/api"
        _requestQueue = Volley.newRequestQueue(context)

        val request = JsonObjectRequest(Request.Method.GET, "$_baseUrl/config", null, Response.Listener {
            listener.onSuccessful()
        }, Response.ErrorListener { listener.onFailed() })
        _requestQueue.add(request)
    }

    fun createUser(devicetype: String, listener: CreateUserListener) {
        val body = JSONObject()
                .put("devicetype", devicetype)
        val request = JsonObjectArrayRequest(Request.Method.POST, _baseUrl, body, Response.Listener {
            try {
                val username = it.getJSONObject(0).getJSONObject("success").getString("username")
                Username = username
                listener.onSuccessful(username)
            } catch (e: JSONException) {
                listener.onFailed()
            }
        }, Response.ErrorListener { listener.onFailed() })
        _requestQueue.add(request)
    }

    fun updateAllLights(listener: RequestListener) {
        val request = JsonObjectRequest(Request.Method.GET, "$_baseUrl/$Username/lights", null, Response.Listener { it ->
            try {
                Lights.clear()
                it.keys().forEach { l -> Lights.add(Light.createFromJson(it.getJSONObject(l), l.toInt())) }
                listener.onSuccessful()
            } catch (e: JSONException) {
                listener.onFailed()
            } catch (e: NumberFormatException) {
                listener.onFailed()
            }
        }, Response.ErrorListener { listener.onFailed() })
        _requestQueue.add(request)
    }

    fun updateAllGroups(listener: RequestListener) {
        val request = JsonObjectRequest(Request.Method.GET, "$_baseUrl/$Username/groups", null, Response.Listener { it ->
            Groups.clear()
            it.keys().forEach { g -> Groups.add(Group.createFromJson(it.getJSONObject(g), g.toInt())) }
            listener.onSuccessful()
        }, Response.ErrorListener { listener.onFailed() })
        _requestQueue.add(request)
    }

    fun commitLightState(light: Light, listener: State.CommitResultListener) {
        light.state.commit(_requestQueue, "$_baseUrl/$Username/lights/${light.nr}/state", listener)
    }

    fun commitGroupState(group: Group, listener: State.CommitResultListener) {
        group.state.commit(_requestQueue, "$_baseUrl/$Username/groups/${group.nr}/state", listener)
    }

    companion object {
        private var INSTANCE: Hub = Hub()

        fun getInstance(): Hub = INSTANCE
    }

    interface CreateUserListener {
        fun onSuccessful(username: String)
        fun onFailed()
    }

    interface RequestListener {
        fun onSuccessful()
        fun onFailed()
    }
}