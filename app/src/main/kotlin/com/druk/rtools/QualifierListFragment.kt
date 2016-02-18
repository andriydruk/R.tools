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
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * A list fragment representing a list of Qualifiers. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a [QualifierDetailFragment].
 *
 *
 * Activities containing this fragment MUST implement the [Callbacks]
 * interface.
 */
class QualifierListFragment : Fragment(), QualifierAdapter.OnItemSelectedListener {

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private var mCallbacks: Callbacks? = sDummyCallbacks

    interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        fun onItemSelected(id: Int)

        fun onFolderNameChanged(folderName: String)
    }

    private var mAdapter: QualifierAdapter? = null
    private var mRecyclerView: RecyclerView? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)


        // Activities containing this fragment must implement its callbacks.
        if (context !is Callbacks) {
            throw IllegalStateException("Activity must implement fragment's callbacks.")
        }
        mCallbacks = context as Callbacks?
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_qualifier_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val qualifierNames = resources.getStringArray(R.array.qualifiers)
        val checkedQualifiers: BooleanArray
        var position = 0
        // Restore the previously serialized activated item position.
        if (savedInstanceState != null && savedInstanceState.containsKey(PARAM_SELECTED_BOOLEAN_SPARSE)) {
            checkedQualifiers = savedInstanceState.getBooleanArray(PARAM_SELECTED_BOOLEAN_SPARSE)
            position = savedInstanceState.getInt(PARAM_SELECTED_ITEM, 0)
        } else {
            checkedQualifiers = BooleanArray(qualifierNames.size)
        }

        mAdapter = QualifierAdapter(context, checkedQualifiers, this)
        mAdapter!!.selectedPosition = position
        mAdapter!!.setHasStableIds(true)
        mRecyclerView = view!!.findViewById(R.id.recycler_view) as RecyclerView
        mRecyclerView!!.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mRecyclerView!!.adapter = mAdapter
    }

    override fun onResume() {
        super.onResume()
        onSelectedQualifiersChanged(mAdapter!!.selectedQualifiers)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState!!.putBooleanArray(PARAM_SELECTED_BOOLEAN_SPARSE, mAdapter!!.selectedItemArray)
        outState.putInt(PARAM_SELECTED_ITEM, mAdapter!!.selectedPosition)
        super.onSaveInstanceState(outState)
    }

    override fun onDetach() {
        super.onDetach()
        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks
    }

    override fun onItemSelected(view: View): Int {
        val position = mRecyclerView!!.layoutManager.getPosition(view)
        val qualifier = mAdapter!!.getItem(position)
        mCallbacks!!.onItemSelected(qualifier.ordinal)
        return position
    }

    override fun onSelectedQualifiersChanged(qualifiers: Collection<Qualifier>) {
        mCallbacks!!.onFolderNameChanged(getResourceFolderName(context, qualifiers))
    }

    companion object {

        private val PARAM_SELECTED_BOOLEAN_SPARSE = "com.druk.rtools.PARAM_SELECTED_BOOLEAN_SPARSE"
        private val PARAM_SELECTED_ITEM = "com.druk.rtools.PARAM_SELECTED_ITEM"

        private val sDummyCallbacks = object : Callbacks {

            override fun onItemSelected(id: Int) {
            }

            override fun onFolderNameChanged(id: String) {
            }
        }

        private fun getResourceFolderName(ctx: Context, qualifiers: Collection<Qualifier>): String {
            val result = StringBuilder()
            for (qualifier in qualifiers) {
                result.append(qualifier.getValue(ctx)).append("-")
            }
            if (result.length > 0) {
                result.deleteCharAt(result.length - 1)
            }
            return result.toString()
        }
    }
}
