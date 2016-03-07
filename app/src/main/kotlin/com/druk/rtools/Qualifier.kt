/*
 * Copyright (C) 2015 Andriy Druk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.druk.rtools

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.util.DisplayMetrics
import android.view.View

enum class Qualifier {

    MMC(R.string.mcc, R.string.mcc_description, 1, R.string.mcc_caution),
    MNC(R.string.mnc, R.string.mcc_description, 1, R.string.mcc_caution),
    LOCALE(R.string.locale, R.string.locale_description, 1, R.string.locale_caution),
    LD(R.string.ld, R.string.ld_description, 17, -1, R.string.ld_note),
    SW(R.string.sw, R.string.sw_description, 13),
    AW(R.string.aw, R.string.aw_description, 13, R.string.orientation_caution),
    AH(R.string.ah, R.string.ah_description, 13, R.string.orientation_caution),
    SIZE(R.string.size, R.string.size_description, 4, R.string.size_caution, R.string.size_note),
    ASPECT(R.string.aspect, R.string.aspect_description, 4),
    ROUND(R.string.round, R.string.round_description, 23),
    ORIENTATION(R.string.orientation, R.string.orientation_description, 1, R.string.orientation_caution),
    UI_MODE(R.string.ui_mode, R.string.ui_mode_description, 8, R.string.ui_mode_caution),
    NIGHT_MODE(R.string.night_mode, R.string.night_mode_description, 8, R.string.night_mode_caution),
    DPI(R.string.dpi, R.string.dpi_description, 1, -1, R.string.dpi_note),
    TOUCHSCREEN_TYPE(R.string.touchscreen_type, R.string.touchscreen_type_description, 1),
    KEYBOARD(R.string.keyboard, R.string.keyboard_description, 1, R.string.keyboard_caution),
    TEXT_INPUT(R.string.text_input, R.string.text_input_description, 1),
    NAVIGATION(R.string.navigation, R.string.navigation_description, 1, R.string.navigation_caution),
    NON_TOUCH_NAVIGATION(R.string.non_touch_navigation, R.string.non_touch_navigation_description, 1),
    API_LEVEL(R.string.api_level, R.string.api_level_description, 1);

    var nameResource: Int = 0
    var descriptionResource: Int = 0
    var cautionResource = -1
    var noteResource = -1
    var minApiLevel = 1

    internal constructor(name: Int, resource: Int, minApiLevel: Int) {
        this.nameResource = name
        this.descriptionResource = resource
        this.minApiLevel = minApiLevel
    }

    internal constructor(name: Int, resource: Int, minApiLevel: Int, cautionResource: Int) {
        this.nameResource = name
        this.descriptionResource = resource
        this.minApiLevel = minApiLevel
        this.cautionResource = cautionResource
    }

    internal constructor(name: Int, resource: Int, minApiLevel: Int, cautionResource: Int, noteResource: Int) {
        this.nameResource = name
        this.descriptionResource = resource
        this.minApiLevel = minApiLevel
        this.cautionResource = cautionResource
        this.noteResource = noteResource
    }

    fun getValue(ctx: Context): String? {
        val config = ctx.resources.configuration
        when (this) {
            MMC -> return if (config.mcc > 0) "mcc" + config.mcc else null
            MNC -> return if (config.mnc > 0) "mnc" + config.mnc else null
            LOCALE -> return config.locale.language + "-r" + config.locale.country
            LD -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                return if (config.layoutDirection == View.LAYOUT_DIRECTION_LTR) "ldltr" else "ldrtl"
            }
            SW -> if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR1) {
                return "sw" + config.smallestScreenWidthDp + "dp"
            }
            AW -> if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR1) {
                return "w" + config.screenWidthDp + "dp"
            }
            AH -> if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR1) {
                return "h" + config.screenHeightDp + "dp"
            }
            SIZE -> {
                val screenLayoutSizeMask = config.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK
                when (screenLayoutSizeMask) {
                    Configuration.SCREENLAYOUT_SIZE_SMALL -> return "small"
                    Configuration.SCREENLAYOUT_SIZE_NORMAL -> return "normal"
                    Configuration.SCREENLAYOUT_SIZE_LARGE -> return "large"
                    Configuration.SCREENLAYOUT_SIZE_XLARGE -> return "xlarge"
                    Configuration.SCREENLAYOUT_SIZE_UNDEFINED -> return null
                    else -> return null
                }
            }
            ASPECT -> {
                val screenLayoutLongMask = config.screenLayout and Configuration.SCREENLAYOUT_LONG_MASK
                when (screenLayoutLongMask) {
                    Configuration.SCREENLAYOUT_LONG_YES -> return "long"
                    Configuration.SCREENLAYOUT_LONG_NO -> return "notlong"
                    Configuration.SCREENLAYOUT_LONG_UNDEFINED -> return null
                    else -> return null
                }
            }
            ROUND -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    return if (config.isScreenRound) "round" else "notround";
                }
                else{
                    return null;
                }
            }
            ORIENTATION -> {
                if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) return "land"
                if (config.orientation == Configuration.ORIENTATION_PORTRAIT) return "port"
                return null
            }
            UI_MODE -> if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
                val uiModeTypeMask = config.uiMode and Configuration.UI_MODE_TYPE_MASK
                when (uiModeTypeMask) {
                    Configuration.UI_MODE_TYPE_APPLIANCE -> return "appliance"
                    Configuration.UI_MODE_TYPE_CAR -> return "car"
                    Configuration.UI_MODE_TYPE_DESK -> return "desk"
                    Configuration.UI_MODE_TYPE_TELEVISION -> return "television"
                    Configuration.UI_MODE_TYPE_WATCH -> return "watch"
                    Configuration.UI_MODE_TYPE_NORMAL -> return null
                    Configuration.UI_MODE_TYPE_UNDEFINED -> return null
                    else -> return null
                }
            }
            NIGHT_MODE -> if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
                val uiModeNightMask = config.uiMode and Configuration.UI_MODE_NIGHT_MASK
                when (uiModeNightMask) {
                    Configuration.UI_MODE_NIGHT_YES -> return "night"
                    Configuration.UI_MODE_NIGHT_NO -> return "notnight"
                    Configuration.UI_MODE_NIGHT_UNDEFINED -> return null
                    else -> return null
                }
            }
            DPI -> {
                val densityDpi = (ctx.resources.displayMetrics.density * DisplayMetrics.DENSITY_DEFAULT).toInt()
                if (densityDpi >= DisplayMetrics.DENSITY_XXXHIGH) return "xxxhdpi"
                if (densityDpi >= DisplayMetrics.DENSITY_XXHIGH) return "xxhdpi"
                if (densityDpi >= DisplayMetrics.DENSITY_XHIGH) return "xhdpi"
                if (densityDpi >= DisplayMetrics.DENSITY_HIGH) return "hdpi"
                if (densityDpi >= DisplayMetrics.DENSITY_MEDIUM) return "mdpi"
                return "ldpi"
            }
            TOUCHSCREEN_TYPE -> {
                val touchscreen = config.touchscreen
                when (touchscreen) {
                    Configuration.TOUCHSCREEN_FINGER -> return "finger"
                    Configuration.TOUCHSCREEN_NOTOUCH -> return "notouch"
                    Configuration.TOUCHSCREEN_UNDEFINED -> return null
                    else -> return null
                }
            }
            KEYBOARD -> return ctx.resources.getString(R.string.keyboard_available)
            TEXT_INPUT -> {
                val keyboard = config.keyboard
                when (keyboard) {
                    Configuration.KEYBOARD_NOKEYS -> return "nokeys"
                    Configuration.KEYBOARD_QWERTY -> return "qwerty"
                    Configuration.KEYBOARD_12KEY -> return "12key"
                    else -> return null
                }
            }
            NAVIGATION -> {
                val navHidden = config.navigationHidden
                when (navHidden) {
                    Configuration.NAVIGATIONHIDDEN_YES -> return "navexposed"
                    Configuration.NAVIGATIONHIDDEN_NO -> return "navhidden"
                    Configuration.NAVIGATIONHIDDEN_UNDEFINED -> return null
                    else -> return null
                }
            }
            NON_TOUCH_NAVIGATION -> {
                val navigation = config.navigation
                when (navigation) {
                    Configuration.NAVIGATION_NONAV -> return "nonav"
                    Configuration.NAVIGATION_DPAD -> return "dpad"
                    Configuration.NAVIGATION_TRACKBALL -> return "trackball"
                    Configuration.NAVIGATION_WHEEL -> return "wheel"
                    Configuration.NAVIGATION_UNDEFINED -> return null
                    else -> return null
                }
            }
            API_LEVEL -> return "v" + Build.VERSION.SDK_INT
            else -> return null
        }
        return null
    }

    companion object {

        fun getQualifier(ordinal: Int): Qualifier {
            if (ordinal < 0 || ordinal >= values().size) {
                throw IllegalArgumentException("Wrong ordinal " + ordinal)
            }
            return values()[ordinal]
        }
    }

}
