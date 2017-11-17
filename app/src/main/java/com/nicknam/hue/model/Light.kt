package com.nicknam.hue.model

import org.json.JSONObject

/**
 * Created by snick on 16-11-2017.
 */
class Light(val nr: Int, val state: State, val type: String, val name: String, modelId: String, val swVersion: String) {
    companion object {
        fun createFromJson(src: JSONObject, nr: Int): Light = Light(
                nr = nr,
                state = State.createFromJson(src.getJSONObject("state")),
                type = src.getString("type"),
                name = src.getString("name"),
                modelId = src.getString("modelid"),
                swVersion = src.getString("swversion")
        )
    }
}