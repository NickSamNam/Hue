package com.nicknam.hue.util

import android.graphics.Color

/**
 * Created by snick on 2-12-2017.
 */

fun Color.fromPhilipsHSB(hue: Int, sat: Int, bri: Int) = Color.HSVToColor(floatArrayOf(hue / 182.0416666666667f, sat / 254f, bri / 254f))