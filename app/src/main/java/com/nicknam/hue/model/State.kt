package com.nicknam.hue.model

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.nicknam.hue.util.JsonObjectArrayRequest
import com.nicknam.hue.util.asFloatList
import org.json.JSONObject

/**
 * Created by snick on 17-11-2017.
 */

class State(on: Boolean, bri: Int, hue: Int, sat: Int, xy: List<Float>, ct: Int, alert: Alert, effect: Effect, val colorMode: ColorMode, private val reachable: Boolean) {
    private val _updateRequest: JSONObject = JSONObject()

    private var _isOn: Boolean = on
    var On: Boolean
        get() = synchronized(this) { _isOn }
        set(value) {
            synchronized(this) { _updateRequest.put("on", value) }
        }

    private var _brightness: Int = bri
    var Brightness: Int
        get() = synchronized(this) { _brightness }
        set(value) {
            synchronized(this) { _updateRequest.put("bri", value) }
        }

    private var _hue: Int = hue
    var Hue: Int
        get() = synchronized(this) { _hue }
        set(value) {
            synchronized(this) { _updateRequest.put("hue", value) }
        }

    private var _saturation: Int = sat
    var Saturation: Int
        get() = synchronized(this) { _saturation }
        set(value) {
            synchronized(this) { _updateRequest.put("sat", value) }
        }

    private var _xy: List<Float> = xy
    var XY: List<Float>
        get() = synchronized(this) { _xy }
        set(value) {
            synchronized(this) { _updateRequest.put("xy", value) }
        }

    private var _colorTemperature: Int = ct
    var ColorTemperature: Int
        get() = synchronized(this) { _colorTemperature }
        set(value) {
            synchronized(this) { _updateRequest.put("ct", value) }
        }

    private var _alertType: Alert = alert
    var AlertType: Alert
        get() = synchronized(this) { _alertType }
        set(value) {
            synchronized(this) { _updateRequest.put("alert", value) }
        }

    private var _effectType: Effect = effect
    var EffectType: Effect
        get() = synchronized(this) { _effectType }
        set(value) {
            synchronized(this) { _updateRequest.put("effect", value) }
        }

    internal fun commit(requestQueue: RequestQueue, url: String, listener: CommitResultListener) {
        synchronized(_updateRequest) {
            val request = JsonObjectArrayRequest(Request.Method.PUT, url, _updateRequest, Response.Listener { it ->
                val r = it.toString()
                val keyIt = _updateRequest.keys()
                keyIt.forEach { k -> if (r.contains(k)) keyIt.remove() }
                if (!_updateRequest.keys().hasNext())
                    listener.onSuccess()
                else
                    listener.onFailed()
            }, Response.ErrorListener { listener.onFailed() })
            requestQueue.add(request)
        }
    }

    interface CommitResultListener {
        fun onSuccess()
        fun onFailed()
    }

    companion object {
        fun createFromJson(src: JSONObject): State = State(
                on = src.getBoolean("on"),
                bri = src.getInt("bri"),
                hue = src.getInt("hue"),
                sat = src.getInt("sat"),
                xy = src.getJSONArray("xy").asFloatList(),
                ct = src.getInt("ct"),
                alert = Alert.valueOf(src.getString("alert")),
                effect = Effect.valueOf(src.getString("effect")),
                colorMode = ColorMode.valueOf(src.getString("colormode")),
                reachable = true
        )
    }

    enum class Alert {
        none,
        select,
        lselect
    }

    enum class Effect {
        none,
        colorloop
    }

    enum class ColorMode {
        hs,
        xy,
        ct
    }
}