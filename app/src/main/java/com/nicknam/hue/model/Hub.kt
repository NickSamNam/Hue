package com.nicknam.hue.model

import android.content.Context
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.nicknam.hue.util.JsonObjectArrayRequest
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by snick on 15-11-2017.
 */
class Hub private constructor(ipAddress: String, context: Context) {
    var Lights: List<Light> = ArrayList()
        private set
    var Groups: List<Group> = ArrayList()
        private set
    private val _requestQueue = Volley.newRequestQueue(context)
    private val _baseUrl = "https://$ipAddress/api"
    private lateinit var _username: String

    fun createUser(devicetype: String, listener: CreateUserListener) {
        val body = JSONObject()
                .put("devicetype", devicetype)
        val request = JsonObjectArrayRequest(Request.Method.POST, _baseUrl, body, Response.Listener {
            try {
                val username = it.getJSONObject(0).getJSONObject("success").getString("username")
                _username = username
                listener.onSuccessful(username)
            } catch (e: JSONException) {
                listener.onFailed()
            }
        }, Response.ErrorListener { listener.onFailed() })
        _requestQueue.add(request)
    }

    fun updateAllLights(listener: RequestListener) {
        val request = JsonObjectRequest(Request.Method.GET, "$_baseUrl/$_username/Lights", null, Response.Listener { it ->
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
        val request = JsonObjectRequest(Request.Method.GET, "$_baseUrl/$_username/Groups", null, Response.Listener { it ->
            val groups = ArrayList<Group>()
            it.keys().forEach { g -> groups.add(Group.createFromJson(it.getJSONObject(g), g.toInt())) }
            Groups = groups
            listener.onSuccessful()
        }, Response.ErrorListener { listener.onFailed() })
        _requestQueue.add(request)
    }

    fun commitLightState(light: Light, listener: State.CommitResultListener) {
        light.state.commit(_requestQueue, "$_baseUrl/$_username/Lights/${light.nr}/state", listener)
    }

    fun commitGroupState(group: Group, listener: State.CommitResultListener) {
        group.action.commit(_requestQueue, "$_baseUrl/$_username/Groups/${group.nr}/action", listener)
    }

    companion object {
        @Volatile private var INSTANCE: Hub? = null

        fun getInstance(ipAddress: String, context: Context): Hub =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: Hub(ipAddress, context).also { INSTANCE = it }
                }
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