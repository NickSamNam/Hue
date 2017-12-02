package com.nicknam.hue.model

import com.nicknam.hue.util.asIntList
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by snick on 16-11-2017.
 */
class Group(override val nr: Int, override val name: String, val lights: List<Int>, val type: Type, override val state: State): IControllable {

    companion object {
        fun createFromJson(src: JSONObject, nr: Int): Group = Group(
                nr = nr,
                name = src.getString("name"),
                lights = src.getJSONArray("lights").asIntList(),
                type = try {Type.valueOf(if (src.getString("type") == "0") "O" else src.getString("type"))} catch (e: JSONException) {Type.O},
                state = State.createFromJson(src.getJSONObject("action"))
        )
    }

    enum class Type {
        O,
        Luminaire,
        Lightsource,
        LightGroup,
        Room
    }

}