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

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.druk.rtools.Qualifier
import com.druk.rtools.R

/**
 * A fragment representing a single Qualifier detail screen.
 * This fragment is either contained in a [MainActivity]
 * in two-pane mode (on tablets) or a [QualifierDetailActivity]
 * on handsets.
 */
class QualifierDetailFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = LayoutInflater.from(container!!.context).inflate(R.layout.fragment_qualifier_detail, container, false)
        val id = arguments.getInt(ARG_ITEM_ID)
        val qualifier = Qualifier.getQualifier(id)
        if (qualifier.getValue(activity) != null) {
            (root.findViewById(R.id.qualifier_value) as TextView).text = qualifier.getValue(activity)
            (root.findViewById(R.id.qualifier_value) as TextView).setTextColor(resources.getColor(R.color.text_medium))
        } else {
            (root.findViewById(R.id.qualifier_value) as TextView).setText(R.string.undefined)
            (root.findViewById(R.id.qualifier_value) as TextView).setTextColor(resources.getColor(R.color.text_super_light))
        }
        (root.findViewById(R.id.min_api_level) as TextView).text = qualifier.minApiLevel.toString() + " API"

        (root.findViewById(R.id.caution) as TextView).setText(if (qualifier.cautionResource < 0) qualifier.nameResource else qualifier.cautionResource)
        (root.findViewById(R.id.caution) as TextView).visibility = if (qualifier.cautionResource < 0) View.GONE else View.VISIBLE
        root.findViewById(R.id.caution_dummy).visibility = if (qualifier.cautionResource < 0) View.GONE else View.VISIBLE
        Utils.setMovementMethod(root.findViewById(R.id.caution) as TextView, Utils.MOVEMENT_METHOD_LINK)

        (root.findViewById(R.id.note) as TextView).setText(if (qualifier.noteResource < 0) qualifier.nameResource else qualifier.noteResource)
        (root.findViewById(R.id.note) as TextView).visibility = if (qualifier.noteResource < 0) View.GONE else View.VISIBLE
        root.findViewById(R.id.note_dummy).visibility = if (qualifier.noteResource < 0) View.GONE else View.VISIBLE
        Utils.setMovementMethod(root.findViewById(R.id.note) as TextView, Utils.MOVEMENT_METHOD_LINK)

        (root.findViewById(R.id.qualifier_detail) as TextView).setText(qualifier.descriptionResource)

        setDummyView(root.findViewById(R.id.caution), root.findViewById(R.id.caution_dummy))
        setDummyView(root.findViewById(R.id.note), root.findViewById(R.id.note_dummy))
        return root
    }

    private fun setDummyView(origin: View, dummy: View) {
        Utils.getViewSize(origin, Runnable {
            dummy.layoutParams.height = origin.height
            dummy.requestLayout()
        })
        dummy.setOnTouchListener { v, event ->
            origin.dispatchTouchEvent(event)
            true
        }
    }

    companion object {

        val ARG_ITEM_ID = "item_id"

        fun newInstance(id: Int): QualifierDetailFragment {
            val fragment = QualifierDetailFragment()
            val arguments = Bundle()
            arguments.putInt(ARG_ITEM_ID, id)
            fragment.arguments = arguments
            return fragment
        }
    }
}
