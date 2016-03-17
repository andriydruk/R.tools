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

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast

import kotlinx.android.synthetic.main.activity_main.*;

class MainActivity : AppCompatActivity(), QualifierListFragment.Callbacks, View.OnClickListener {

    private var folderName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        qualifiers.setOnClickListener(this)
        setSupportActionBar(toolbar)

        if (qualifier_detail_container != null && savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(R.id.qualifier_detail_container, QualifierDetailFragment.newInstance(Qualifier.MMC.ordinal)).commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_resource, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_license) {
            startActivity(Intent(this, LicensesActivity::class.java))
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (!TextUtils.isEmpty(folderName)) {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(getString(R.string.qualifiers), folderName)
                clipboard.primaryClip = clip
                Toast.makeText(this, getString(R.string.copy_toast_message), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onItemSelected(id: Int) {
        if (qualifier_detail_container != null) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            supportFragmentManager.beginTransaction().replace(R.id.qualifier_detail_container, QualifierDetailFragment.newInstance(id)).commit()

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            val detailIntent = Intent(this, QualifierDetailActivity::class.java)
            detailIntent.putExtra(QualifierDetailFragment.ARG_ITEM_ID, id)
            startActivity(detailIntent)
        }
    }

    override fun onFolderNameChanged(folderName: String) {
        this.folderName = folderName
        val textView = qualifiers
        if (TextUtils.isEmpty(folderName)) {
            textView.setText(R.string.no_item)
            textView.setTextColor(ContextCompat.getColor(this, R.color.material_deep_teal_200))
        } else {
            textView.text = folderName
            textView.setTextColor(Color.WHITE)
        }
    }
}
