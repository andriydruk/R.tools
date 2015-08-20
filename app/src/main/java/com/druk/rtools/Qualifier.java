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

package com.druk.rtools;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;

public enum Qualifier {

    MMC(R.string.mcc), MNC(R.string.mnc), LOCALE(R.string.locale), LD(R.string.ld), SW(R.string.sw), AW(R.string.aw), AH(R.string.ah),
    SIZE(R.string.size), ASPECT(R.string.aspect), ORIENTATION(R.string.orientation), UI_MODE(R.string.ui_mode), NIGHT_MODE(R.string.night_mode),
    DPI(R.string.dpi), TOUCHSCREEN_TYPE(R.string.touchscreen_type), KEYBOARD(R.string.keyboard), TEXT_INPUT(R.string.text_input),
    NAVIGATION(R.string.navigation), NON_TOUCH_NAVIGATION(R.string.non_touch_navigation), API_LEVEL(R.string.api_level);

    public int nameResource;

    Qualifier(int name){
        this.nameResource = name;
    }

    @Nullable
    public String getValue(Context ctx){
        Configuration config = ctx.getResources().getConfiguration();
        switch (this){
            case MMC:
                return (config.mcc > 0) ? "mcc" + config.mcc : null;
            case MNC:
                return (config.mnc > 0) ? "mnc" + config.mnc : null;
            case LOCALE:
                return config.locale.getLanguage() + "-r" + config.locale.getCountry();
            case LD:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    return (config.getLayoutDirection() == View.LAYOUT_DIRECTION_LTR) ? "ldltr" : "ldrtl";
                }
                break;
            case SW:
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR1) {
                    return "sw" + config.smallestScreenWidthDp + "dp";
                }
                break;
            case AW:
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR1) {
                    return "w" + config.screenWidthDp + "dp";
                }
                break;
            case AH:
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR1) {
                    return  "h" + config.screenHeightDp + "dp";
                }
                break;
            case SIZE:
                int screenLayoutSizeMask = config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
                switch (screenLayoutSizeMask) {
                    case Configuration.SCREENLAYOUT_SIZE_SMALL: return "small";
                    case Configuration.SCREENLAYOUT_SIZE_NORMAL: return "normal";
                    case Configuration.SCREENLAYOUT_SIZE_LARGE: return  "large";
                    case Configuration.SCREENLAYOUT_SIZE_XLARGE: return "xlarge";
                    case Configuration.SCREENLAYOUT_SIZE_UNDEFINED:
                    default: return null;
                }
            case ASPECT:
                int screenLayoutLongMask = config.screenLayout & Configuration.SCREENLAYOUT_LONG_MASK;
                switch (screenLayoutLongMask) {
                    case Configuration.SCREENLAYOUT_LONG_YES: return "long";
                    case Configuration.SCREENLAYOUT_LONG_NO: return "notlong";
                    case Configuration.SCREENLAYOUT_LONG_UNDEFINED:
                    default: return null;
                }
            case ORIENTATION:
                if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) return  "land";
                if (config.orientation == Configuration.ORIENTATION_PORTRAIT) return "port";
                return null;
            case UI_MODE:
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
                    int uiModeTypeMask = config.uiMode & Configuration.UI_MODE_TYPE_MASK;
                    switch (uiModeTypeMask) {
                        case Configuration.UI_MODE_TYPE_APPLIANCE: return "appliance";
                        case Configuration.UI_MODE_TYPE_CAR: return "car";
                        case Configuration.UI_MODE_TYPE_DESK: return "desk";
                        case Configuration.UI_MODE_TYPE_TELEVISION: return "television";
                        case Configuration.UI_MODE_TYPE_WATCH: return "watch";
                        case Configuration.UI_MODE_TYPE_NORMAL:
                        case Configuration.UI_MODE_TYPE_UNDEFINED:
                        default: return null;
                    }
                }
                break;
            case NIGHT_MODE:
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
                    int uiModeNightMask = config.uiMode & Configuration.UI_MODE_NIGHT_MASK;
                    switch (uiModeNightMask) {
                        case Configuration.UI_MODE_NIGHT_YES: return "night";
                        case Configuration.UI_MODE_NIGHT_NO: return "notnight";
                        case Configuration.UI_MODE_NIGHT_UNDEFINED:
                        default: return null;
                    }
                }
                break;
            case DPI:
                int densityDpi = (int) (ctx.getResources().getDisplayMetrics().density * DisplayMetrics.DENSITY_DEFAULT);
                if (densityDpi >= DisplayMetrics.DENSITY_XXXHIGH) return "xxxhdpi";
                if (densityDpi >= DisplayMetrics.DENSITY_XXHIGH) return "xxhdpi";
                if (densityDpi >= DisplayMetrics.DENSITY_XHIGH) return "xhdpi";
                if (densityDpi >= DisplayMetrics.DENSITY_HIGH) return "hdpi";
                if (densityDpi >= DisplayMetrics.DENSITY_MEDIUM) return "mdpi";
                return  "ldpi";
            case TOUCHSCREEN_TYPE:
                int touchscreen = config.touchscreen;
                switch (touchscreen) {
                    case Configuration.TOUCHSCREEN_FINGER: return "finger";
                    case Configuration.TOUCHSCREEN_NOTOUCH: return "notouch";
                    case Configuration.TOUCHSCREEN_UNDEFINED:
                    default: return null;
                }
            case KEYBOARD:
                return ctx.getResources().getString(R.string.keyboard_available);
            case TEXT_INPUT:
                int keyboard = config.keyboard;
                switch (keyboard) {
                    case Configuration.KEYBOARD_NOKEYS: return "nokeys";
                    case Configuration.KEYBOARD_QWERTY: return "qwerty";
                    case Configuration.KEYBOARD_12KEY: return "12key";
                    default: return null;
                }
            case NAVIGATION:
                int navHidden = config.navigationHidden;
                switch (navHidden) {
                    case Configuration.NAVIGATIONHIDDEN_YES: return "navexposed";
                    case Configuration.NAVIGATIONHIDDEN_NO: return "navhidden";
                    case Configuration.NAVIGATIONHIDDEN_UNDEFINED:
                    default: return null;
                }
            case NON_TOUCH_NAVIGATION:
                int navigation = config.navigation;
                switch (navigation) {
                    case Configuration.NAVIGATION_NONAV: return "nonav";
                    case Configuration.NAVIGATION_DPAD: return "dpad";
                    case Configuration.NAVIGATION_TRACKBALL: return "trackball";
                    case Configuration.NAVIGATION_WHEEL: return "wheel";
                    case Configuration.NAVIGATION_UNDEFINED:
                    default: return null;
                }
            case API_LEVEL:
                return "v" + Build.VERSION.SDK_INT;
            default:
                return null;
        }
        return null;
    }

}
