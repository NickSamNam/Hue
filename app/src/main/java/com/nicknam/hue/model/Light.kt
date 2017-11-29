package com.nicknam.hue.model

import org.json.JSONObject

/**
 * Created by snick on 16-11-2017.
 */
class Light(override val nr: Int, override val state: State, val type: String, override val name: String, modelId: String, val swVersion: String): IControllable {
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