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

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_license.*

class LicensesActivity : AppCompatActivity(), View.OnClickListener {

    private var mLayoutManager: LinearLayoutManager? = null
    private var mAdapter: OpenSourceComponentAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_license)

        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        mLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mAdapter = OpenSourceComponentAdapter(this, LICENSE_SOFTWARE, arrayOf(ANDROID_ASSETS_FILE_PATH + ANDROID_OPEN_SOURCE_PROJECT_LICENSE, ANDROID_ASSETS_FILE_PATH + ANDROID_OPEN_SOURCE_PROJECT_LICENSE, ANDROID_ASSETS_FILE_PATH + ANDROID_OPEN_SOURCE_PROJECT_LICENSE, ANDROID_ASSETS_FILE_PATH + ANDROID_SOFTWARE_DEVELOPMENT_KIT))

        recycler_view.layoutManager = mLayoutManager
        recycler_view.adapter = mAdapter
    }

    override fun onResume() {
        super.onResume()
        mAdapter!!.setListener(this)
    }

    override fun onPause() {
        super.onPause()
        mAdapter!!.setListener(null)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onClick(v: View) {
        val position = mLayoutManager!!.getPosition(v)
        val intent = Intent(v.context, HTMLViewerActivity::class.java)
        intent.data = Uri.parse(mAdapter!!.getLicensePath(position))
        intent.putExtra(Intent.EXTRA_TITLE, mAdapter!!.getComponentName(position))
        intent.addCategory(Intent.CATEGORY_DEFAULT)

        try {
            v.context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Log.e("TAG", "Failed to find viewer", e)
        }

    }

    class OpenSourceComponentAdapter constructor(context: Context, private val componentNames: Array<String>, private val licensePaths: Array<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val mBackground: Int

        private var listener: View.OnClickListener? = null

        init {
            val mTypedValue = TypedValue()
            context.theme.resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true)
            mBackground = mTypedValue.resourceId
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
            val vh = object : RecyclerView.ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.one_text_item, viewGroup, false)) {

            }
            vh.itemView.setBackgroundResource(mBackground)
            return vh
        }

        override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
            (viewHolder.itemView as AppCompatTextView).text = componentNames[i]
            viewHolder.itemView.setOnClickListener(listener)
        }

        override fun getItemCount(): Int {
            return componentNames.size
        }

        fun setListener(listener: View.OnClickListener?) {
            this.listener = listener
        }

        fun getComponentName(position: Int): String {
            return componentNames[position]
        }

        fun getLicensePath(position: Int): String {
            return licensePaths[position]
        }
    }

    companion object {

        private val LICENSE_SOFTWARE = arrayOf("Android Compatibility Library v4", "Android Compatibility Library v7", "Android Design Support Library", "Android SDK")

        private val ANDROID_ASSETS_FILE_PATH = "file:///android_asset/"
        private val ANDROID_OPEN_SOURCE_PROJECT_LICENSE = "ANDROID-OPEN-SOURCE-PROJECT-LICENSE.txt"
        private val ANDROID_SOFTWARE_DEVELOPMENT_KIT = "ANDROID-SOFTWARE-DEVELOPMENT-KIT.txt"
    }
}
