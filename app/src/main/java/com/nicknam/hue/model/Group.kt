package com.nicknam.hue.model

import com.nicknam.hue.util.asIntList
import org.json.JSONObject

/**
 * Created by snick on 16-11-2017.
 */
class Group(val nr: Int, val name: String, val lights: List<Int>, val type: Type, val action: State) {
    companion object {
        fun createFromJson(src: JSONObject, nr: Int): Group = Group(
                nr = nr,
                name = src.getString("name"),
                lights = src.getJSONArray("Lights").asIntList(),
                type = Type.valueOf(if (src.getString("type") == "0") "O" else src.getString("type")),
                action = State.createFromJson(src.getJSONObject("action"))
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