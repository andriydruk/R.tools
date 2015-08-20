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
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collection;


public class ResourceActivity extends AppCompatActivity implements View.OnClickListener, QualifierAdapter.OnItemSelectedListener {

    private static final String PARAM_SELECTED_BOOLEAN_SPARSE = "com.druk.rtools.PARAM_SELECTED_BOOLEAN_SPARSE";

    private QualifierAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource);
        String[] qualifierNames = getResources().getStringArray(R.array.qualifiers);
        boolean[] checkedQualifiers;
        if (savedInstanceState != null) {
            checkedQualifiers = savedInstanceState.getBooleanArray(PARAM_SELECTED_BOOLEAN_SPARSE);
        } else {
            checkedQualifiers = new boolean[qualifierNames.length];
        }

        mAdapter = new QualifierAdapter(this, checkedQualifiers, this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        onItemSelected(mAdapter.getSelectedQualifiers());
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
        outState.putBooleanArray(PARAM_SELECTED_BOOLEAN_SPARSE, mAdapter.getSelectedItemArray());
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
            if (!TextUtils.isEmpty(getResourceFolderName(mAdapter.getSelectedQualifiers()))) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(getString(R.string.qualifiers), ((TextView) v).getText().toString());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(ResourceActivity.this, getString(R.string.copy_toast_message), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onItemSelected(Collection<Qualifier> selectedQualifiers) {
        TextView textView = (TextView) findViewById(R.id.qualifiers);
        String folderName = getResourceFolderName(selectedQualifiers);
        if (TextUtils.isEmpty(folderName)) {
            textView.setText(R.string.no_item);
            textView.setTextColor(getResources().getColor(R.color.material_deep_teal_200));
            textView.setOnClickListener(null);
        } else {
            textView.setText(folderName);
            textView.setTextColor(Color.WHITE);
            textView.setOnClickListener(this);
        }
    }

    public String getResourceFolderName(Collection<Qualifier> qualifiers) {
        StringBuilder result = new StringBuilder();
        for (Qualifier qualifier : qualifiers) {
            result.append(qualifier.getValue(this)).append("-");
        }
        if (result.length() > 0) {
            result.deleteCharAt(result.length() - 1);
        }
        return result.toString();
    }


}
