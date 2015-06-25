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

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;


public class ResourceActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String PARAM_SELECTED_BOOLEAN_SPARSE = "com.druk.rtools.PARAM_SELECTED_BOOLEAN_SPARSE";

    private ResourceAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource);
        String[] qualifierNames = getResources().getStringArray(R.array.qualifiers);
        boolean[] checkedQualifiers;
        if (savedInstanceState != null){
            checkedQualifiers = savedInstanceState.getBooleanArray(PARAM_SELECTED_BOOLEAN_SPARSE);
        }
        else{
            checkedQualifiers = new boolean[qualifierNames.length];
        }

        mAdapter = new ResourceAdapter(qualifierNames, getConfig(), checkedQualifiers, this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        onItemSelected();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_resource, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBooleanArray(PARAM_SELECTED_BOOLEAN_SPARSE, mAdapter.checkedItems);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter = null;
    }

    @Override
    public void onClick(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (!TextUtils.isEmpty(mAdapter.getSelectedQualifierValues())) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(getString(R.string.qualifiers), ((TextView) v).getText().toString());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(ResourceActivity.this, getString(R.string.copy_toast_message), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String[] getConfig(){
        Configuration config = getResources().getConfiguration();
        String[] qualifiers = new String[getResources().getStringArray(R.array.qualifiers).length];
        int i = 0;

        //Read mcc
        qualifiers[i++] = (config.mcc > 0) ? "mcc" + config.mcc : null;

        //Read mnc
        qualifiers[i++] = (config.mnc > 0) ? "mnc" + config.mnc : null;

        //Read locale
        qualifiers[i++] = config.locale.getLanguage() + "-r" + config.locale.getCountry();

        //Read layout direction (sdk 17)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            qualifiers[i++] = (config.getLayoutDirection() == View.LAYOUT_DIRECTION_LTR) ? "ldltr" : "ldrtl";
        }
        else{
            qualifiers[i++] = null;
        }

        //Read smallest width
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR1) {
            qualifiers[i++] = "sw" + config.smallestScreenWidthDp + "dp";
        }
        else{
            qualifiers[i++] = null;
        }

        //Read width in dp
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR1) {
            qualifiers[i++] = "w" + config.screenWidthDp + "dp";
        }
        else{
            qualifiers[i++] = null;
        }

        //Read height in dp
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR1) {
            qualifiers[i++] = "h" + config.screenHeightDp + "dp";
        }
        else{
            qualifiers[i++] = null;
        }


        int screenLayoutSizeMask = config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        switch (screenLayoutSizeMask){
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                qualifiers[i++] = "small";
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                qualifiers[i++] = "normal";
                break;
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                qualifiers[i++] = "large";
                break;
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                qualifiers[i++] = "xlarge";
                break;
            case Configuration.SCREENLAYOUT_SIZE_UNDEFINED:
            default:
                qualifiers[i++] = null;
                break;
        }


        //Read screen long
        int screenLayoutLongMask = config.screenLayout & Configuration.SCREENLAYOUT_LONG_MASK;
        switch (screenLayoutLongMask){
            case Configuration.SCREENLAYOUT_LONG_YES:
                qualifiers[i++] = "long";
                break;
            case Configuration.SCREENLAYOUT_LONG_NO:
                qualifiers[i++] = "notlong";
                break;
            case Configuration.SCREENLAYOUT_LONG_UNDEFINED:
            default:
                qualifiers[i++] = null;
                break;
        }

        //Read orientation
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE){
            qualifiers[i++] = "land";
        }
        else if (config.orientation == Configuration.ORIENTATION_PORTRAIT){
            qualifiers[i++] = "port";
        }
        else {
            qualifiers[i++] = null;
        }

        //Read ui mode
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
            int uiModeTypeMask = config.uiMode & Configuration.UI_MODE_TYPE_MASK;
            switch (uiModeTypeMask){
                case Configuration.UI_MODE_TYPE_APPLIANCE:
                    qualifiers[i++] = "appliance";
                    break;
                case Configuration.UI_MODE_TYPE_CAR:
                    qualifiers[i++] = "car";
                    break;
                case Configuration.UI_MODE_TYPE_DESK:
                    qualifiers[i++] = "desk";
                    break;
                case Configuration.UI_MODE_TYPE_TELEVISION:
                    qualifiers[i++] = "television";
                    break;
                case Configuration.UI_MODE_TYPE_WATCH:
                    qualifiers[i++] = "watch";
                    break;
                case Configuration.UI_MODE_TYPE_NORMAL:
                case Configuration.UI_MODE_TYPE_UNDEFINED:
                default:
                    qualifiers[i++] = null;
                    break;
            }
        }
        else{
            qualifiers[i++] = null;
        }

        //Read night mode
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
            int uiModeNightMask = config.uiMode & Configuration.UI_MODE_NIGHT_MASK;
            switch (uiModeNightMask){
                case Configuration.UI_MODE_NIGHT_YES:
                    qualifiers[i++] = "night";
                    break;
                case Configuration.UI_MODE_NIGHT_NO:
                    qualifiers[i++] = "notnight";
                    break;
                case Configuration.UI_MODE_NIGHT_UNDEFINED:
                default:
                    qualifiers[i++] = null;
                    break;
            }
        }
        else{
            qualifiers[i++] = null;
        }

        //Read density
        int densityDpi = (int)(getResources().getDisplayMetrics().density * DisplayMetrics.DENSITY_DEFAULT);
        if (densityDpi >= DisplayMetrics.DENSITY_XXXHIGH){
            qualifiers[i++] = "xxxhdpi";
        } else if (densityDpi >= DisplayMetrics.DENSITY_XXHIGH) {
            qualifiers[i++] = "xxhdpi";
        } else if (densityDpi >= DisplayMetrics.DENSITY_XHIGH) {
            qualifiers[i++] = "xhdpi";
        } else if (densityDpi >= DisplayMetrics.DENSITY_HIGH) {
            qualifiers[i++] = "hdpi";
        } else if (densityDpi >= DisplayMetrics.DENSITY_MEDIUM) {
            qualifiers[i++] = "mdpi";
        } else {
            qualifiers[i++] = "ldpi";
        }

        //Read touchscreen type
        int touchscreen = config.touchscreen;
        switch (touchscreen){
            case Configuration.TOUCHSCREEN_FINGER:
                qualifiers[i++] = "finger";
                break;
            case Configuration.TOUCHSCREEN_NOTOUCH:
                qualifiers[i++] = "notouch";
                break;
            case Configuration.TOUCHSCREEN_UNDEFINED:
            default:
                qualifiers[i++] = null;
                break;
        }

        //Keyboard available
        qualifiers[i++] = getResources().getString(R.string.keyboard_available);

        //Read hardware keyboard style
        int keyboard = config.keyboard;
        switch (keyboard){
            case Configuration.KEYBOARD_NOKEYS:
                qualifiers[i++] = "nokeys";
                break;
            case Configuration.KEYBOARD_QWERTY:
                qualifiers[i++] = "qwerty";
                break;
            case Configuration.KEYBOARD_12KEY:
                qualifiers[i++] = "12key";
                break;
            default:
                qualifiers[i++] = null;
                break;
        }

        //Read navigation hidden
        int navHidden = config.navigationHidden;
        switch (navHidden){
            case Configuration.NAVIGATIONHIDDEN_YES:
                qualifiers[i++] = "navexposed";
                break;
            case Configuration.NAVIGATIONHIDDEN_NO:
                qualifiers[i++] = "navhidden";
                break;
            case Configuration.NAVIGATIONHIDDEN_UNDEFINED:
            default:
                qualifiers[i++] = null;
                break;
        }

        //Read navigation
        int navigation = config.navigation;
        switch (navigation){
            case Configuration.NAVIGATION_NONAV:
                qualifiers[i++] = "nonav";
                break;
            case Configuration.NAVIGATION_DPAD:
                qualifiers[i++] = "dpad";
                break;
            case Configuration.NAVIGATION_TRACKBALL:
                qualifiers[i++] = "trackball";
                break;
            case Configuration.NAVIGATION_WHEEL:
                qualifiers[i++] = "wheel";
                break;
            case Configuration.NAVIGATION_UNDEFINED:
            default:
                qualifiers[i++] = null;
                break;
        }
        qualifiers[i] = "v" + Build.VERSION.SDK_INT;
        return qualifiers;
    }

    private void onItemSelected(){
        TextView textView = (TextView) findViewById(R.id.qualifiers);
        String selectedQualifiers = mAdapter.getSelectedQualifierValues();
        if (TextUtils.isEmpty(selectedQualifiers)){
            textView.setText(R.string.no_item);
            textView.setTextColor(getResources().getColor(R.color.material_deep_teal_200));
            textView.setOnClickListener(null);
        }
        else {
            textView.setText(selectedQualifiers);
            textView.setTextColor(Color.WHITE);
            textView.setOnClickListener(this);
        }
    }

    private static class ResourceAdapter extends RecyclerView.Adapter<ResourceAdapter.ViewHolder> implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

        private String[] qualifierNames;
        private String[] qualifierValues;
        private final boolean[] checkedItems;
        private final ResourceActivity activity;

        private ResourceAdapter(String[] names, String[] values, boolean[] checkedItems, ResourceActivity activity){
            this.qualifierNames = names;
            this.qualifierValues = values;
            this.checkedItems = checkedItems;
            this.activity = activity;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View root = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.qualifier_cell, viewGroup, false);
            ViewHolder vh = new ViewHolder(root);
            vh.cell.setOnClickListener(this);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            viewHolder.name.setText(qualifierNames[i]);
            viewHolder.switchCompat.setOnCheckedChangeListener(null);
            if (qualifierValues[i] == null){
                viewHolder.value.setText("undefined");
                viewHolder.switchCompat.setEnabled(false);
            }
            else {
                viewHolder.value.setText(qualifierValues[i]);
                viewHolder.switchCompat.setEnabled(true);
            }
            viewHolder.switchCompat.setChecked(checkedItems[i]);
            viewHolder.switchCompat.setOnCheckedChangeListener(this);
            viewHolder.switchCompat.setTag(i);
            viewHolder.cell.setTag(viewHolder.switchCompat);

        }

        @Override
        public int getItemCount() {
            return qualifierValues.length;
        }

        @Override
        public void onClick(View v) {
            SwitchCompat switchCompat = (SwitchCompat) v.getTag();
            int position = (int) switchCompat.getTag();
            if (!TextUtils.isEmpty(qualifierValues[position])) {
                switchCompat.toggle();
            }
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            checkedItems[((int) buttonView.getTag())] = isChecked;
            activity.onItemSelected();
        }

        public String getSelectedQualifierValues(){
            StringBuilder result = new StringBuilder();
            for (int i=0; i < getItemCount(); i++){
                if (checkedItems[i]){
                    result.append(qualifierValues[i]).append("-");
                }
            }
            if (result.length()>0) {
                result.deleteCharAt(result.length() - 1);
            }
            return result.toString();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder{

            private TextView name;
            private TextView value;
            private CompoundButton switchCompat;
            private View cell;

            public ViewHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.qualifier_name);
                value = (TextView) itemView.findViewById(R.id.qualifier_value);
                switchCompat = (CompoundButton) itemView.findViewById(R.id.res_switch);
                cell = itemView.findViewById(R.id.cell);
            }
        }

    }


}
