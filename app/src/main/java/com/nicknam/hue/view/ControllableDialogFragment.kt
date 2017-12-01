package com.nicknam.hue.view

import android.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.nicknam.hue.R
import com.nicknam.hue.model.IControllable

/**
 * Created by snick on 1-12-2017.
 */
class ControllableDialogFragment: DialogFragment() {
   private lateinit var controllable: IControllable
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val v = inflater.inflate(R.layout.fragment_controllable_dialog, container, false)

        if (arguments != null) {
//            controllable = arguments.getParcelable("controllable")
        } else if (savedInstanceState != null) {
//            controllable = savedInstanceState.getParcelable("controllable")
        }

        return v
    }
}