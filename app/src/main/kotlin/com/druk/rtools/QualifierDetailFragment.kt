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
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_qualifier_detail.view.*

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
            root.qualifier_value.text = qualifier.getValue(activity)
            root.qualifier_value.setTextColor(resources.getColor(R.color.text_medium))
        } else {
            root.qualifier_value.setText(R.string.undefined)
            root.qualifier_value.setTextColor(resources.getColor(R.color.text_super_light))
        }
        root.min_api_level.text = qualifier.minApiLevel.toString() + " API"

        root.caution.setText(if (qualifier.cautionResource < 0) qualifier.nameResource else qualifier.cautionResource)
        root.caution.visibility = if (qualifier.cautionResource < 0) View.GONE else View.VISIBLE
        root.caution_dummy.visibility = if (qualifier.cautionResource < 0) View.GONE else View.VISIBLE
        root.caution.movementMethod = LinkMovementMethod.getInstance()

        root.note.setText(if (qualifier.noteResource < 0) qualifier.nameResource else qualifier.noteResource)
        root.note.visibility = if (qualifier.noteResource < 0) View.GONE else View.VISIBLE
        root.note_dummy.visibility = if (qualifier.noteResource < 0) View.GONE else View.VISIBLE
        root.note.movementMethod = LinkMovementMethod.getInstance()

        root.qualifier_detail.setText(qualifier.descriptionResource)

        setDummyView(root.caution, root.caution_dummy)
        setDummyView(root.note, root.note_dummy)
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
