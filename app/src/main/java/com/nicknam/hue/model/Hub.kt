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
    var Lights: List<Light> = ArrayList()
        private set
    var Groups: List<Group> = ArrayList()
        private set
    private lateinit var _requestQueue: RequestQueue
    private var _ipAddress: String = "0.0.0.0"
    private var _baseUrl = "http://$_ipAddress/api"
    lateinit var Username: String

    fun init(ipAddress: String, context: Context) {
        _ipAddress = ipAddress
        _baseUrl = "http://$_ipAddress/api"
        _requestQueue = Volley.newRequestQueue(context)
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
                val lights = ArrayList<Light>()
                it.keys().forEach { l -> lights.add(Light.createFromJson(it.getJSONObject(l), l.toInt())) }
                Lights = lights
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
            val groups = ArrayList<Group>()
            it.keys().forEach { g -> groups.add(Group.createFromJson(it.getJSONObject(g), g.toInt())) }
            Groups = groups
            listener.onSuccessful()
        }, Response.ErrorListener { listener.onFailed() })
        _requestQueue.add(request)
    }

    fun commitLightState(light: Light, listener: State.CommitResultListener) {
        light.state.commit(_requestQueue, "$_baseUrl/$Username/Lights/${light.nr}/state", listener)
    }

    fun commitGroupState(group: Group, listener: State.CommitResultListener) {
        group.state.commit(_requestQueue, "$_baseUrl/$Username/Groups/${group.nr}/state", listener)
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