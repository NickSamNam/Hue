package com.nicknam.hue.view

import android.app.DialogFragment
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import com.madrapps.pikolo.HSLColorPicker
import com.madrapps.pikolo.listeners.OnColorSelectionListener
import com.nicknam.hue.R
import com.nicknam.hue.model.*
import com.nicknam.hue.util.fromPhilipsHSB

/**
 * Created by snick on 1-12-2017.
 */
class ControllableDialogFragment : DialogFragment(), OnColorSelectionListener, State.CommitResultListener {
    private var controllable: IControllable? = null
    private var ogColour = Color.WHITE

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val v = inflater.inflate(R.layout.fragment_controllable_dialog, container, false)

        var nr = 0
        var name = ""

        if (arguments != null) {
            nr = arguments.getInt("nr")
            name = arguments.getString("name")
        } else if (savedInstanceState != null) {
            nr = savedInstanceState.getInt("nr")
            name = savedInstanceState.getString("name")
        }

        val hub = Hub.getInstance()
        controllable = hub.Lights.find { it -> it.nr == nr && it.name == name } ?: hub.Groups.find { it -> it.nr == nr && it.name == name }

        if (controllable != null)
            ogColour = Color().fromPhilipsHSB(controllable!!.state.Hue, controllable!!.state.Saturation, controllable!!.state.Brightness)
        else
            dismiss()

        val colorPicker = v.findViewById<HSLColorPicker>(R.id.fragment_controllable_dialog_color_picker)
        colorPicker.setColor(ogColour)
        colorPicker.setColorSelectionListener(this)

        v.findViewById<Button>(R.id.fragment_controllable_dialog_btn_cancel).setOnClickListener({
            onColorSelected(ogColour)
            dismiss()
        })
        v.findViewById<Button>(R.id.fragment_controllable_dialog_btn_save).setOnClickListener({ dismiss() })

        return v
    }

    override fun onColorSelected(color: Int) {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        val hue = hsv[0] * 182.0416666666667f
        val sat = hsv[1] * 254f
        val bri = hsv[2] * 254f
        controllable?.state?.Hue = hue.toInt()
        controllable?.state?.Saturation = sat.toInt()
        controllable?.state?.Brightness = bri.toInt()
        when (controllable) {
            is Light -> Hub.getInstance().commitLightState(controllable as Light, this)
            is Group -> Hub.getInstance().commitGroupState(controllable as Group, this)
        }
    }

    override fun onColorSelectionEnd(color: Int) {
    }

    override fun onColorSelectionStart(color: Int) {
    }

    override fun onSuccess() {
    }

    override fun onFailed() {
    }

    override fun onStart() {
        super.onStart()
            dialog.window.setLayout(resources.getDimension(R.dimen.controllable_dialog_width).toInt(), resources.getDimension(R.dimen.controllable_dialog_height).toInt())
    }
}