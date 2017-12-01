package com.nicknam.hue.view

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import com.nicknam.hue.R
import com.nicknam.hue.model.*

/**
 * Created by snick on 25-11-2017.
 */
class ControllableAdapter(private val controllables: List<IControllable>) : RecyclerView.Adapter<ControllableAdapter.StateableHolder>() {
    private lateinit var _onItemClickListener: OnItemClickListener

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        _onItemClickListener = onItemClickListener
    }

    override fun onBindViewHolder(holder: StateableHolder, position: Int) {
        val s = controllables[position]
        holder.tvName.text = s.name
        holder.switchOn.isChecked = s.state.On
        val commitResultlistener = object : State.CommitResultListener {
            override fun onSuccess() {
                // todo update colour
            }

            override fun onFailed() {
                holder.switchOn.isChecked = !holder.switchOn.isChecked
            }

        }
        holder.switchOn.setOnClickListener { controllables[position].state.On = holder.switchOn.isChecked
        when(controllables[position]) {
            is Light -> Hub.getInstance().commitLightState(controllables[position] as Light, commitResultlistener)
            is Group -> Hub.getInstance().commitGroupState(controllables[position] as Group, commitResultlistener)
        }}
        holder.root.setBackgroundColor(when (s.state.colorMode) {
            State.ColorMode.hs -> Color.HSVToColor(floatArrayOf(s.state.Hue / 182.0416666666667f, s.state.Saturation / 254f, s.state.Brightness / 254f))
            State.ColorMode.xy -> Color.WHITE
            State.ColorMode.ct -> Color.WHITE
        })
        holder.root.setOnClickListener { _onItemClickListener.onItemClick(holder.adapterPosition) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StateableHolder =
            StateableHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_controllable, parent, false))

    override fun getItemCount(): Int = controllables.size

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    class StateableHolder(viewItem: View) : RecyclerView.ViewHolder(viewItem) {
        internal val root = viewItem
        internal val tvName = viewItem.findViewById<TextView>(R.id.card_stateable_tv_name)
        internal val switchOn = viewItem.findViewById<Switch>(R.id.card_stateable_switch_on)
    }
}