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
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SwitchCompat
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.TextView
import java.util.LinkedList

class QualifierAdapter(context: Context, val selectedItemArray: BooleanArray, private val mListener: QualifierAdapter.OnItemSelectedListener)
    : RecyclerView.Adapter<QualifierAdapter.ViewHolder>(), View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private val qualifiers: Array<Qualifier>
    private val mBackground: Int
    private val mSelectedBackground: Int

    var selectedPosition = 0

    init {
        this.qualifiers = Qualifier.values()

        val mTypedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true)
        mBackground = mTypedValue.resourceId

        context.theme.resolveAttribute(R.attr.selectedItemBackground, mTypedValue, true)
        mSelectedBackground = mTypedValue.resourceId
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val root = LayoutInflater.from(viewGroup.context).inflate(R.layout.qualifier_item, viewGroup, false)
        root.setOnClickListener(this)
        return ViewHolder(root)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val qualifier = getItem(i)
        viewHolder.name.setText(qualifier.nameResource)
        if (qualifier.getValue(viewHolder.name.context) != null) {
            viewHolder.value.text = qualifier.getValue(viewHolder.name.context)
            viewHolder.resSwitch.isEnabled = true
        } else {
            viewHolder.value.setText(R.string.undefined)
            viewHolder.resSwitch.isEnabled = false
        }
        viewHolder.resSwitch.setOnCheckedChangeListener(null)
        viewHolder.resSwitch.isChecked = selectedItemArray[i]
        viewHolder.resSwitch.setOnCheckedChangeListener(this)
        viewHolder.resSwitch.tag = i
        viewHolder.itemView.setBackgroundResource(if (i == selectedPosition) mSelectedBackground else mBackground)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return qualifiers.size
    }

    fun getItem(position: Int): Qualifier {
        return qualifiers[position]
    }

    override fun onClick(v: View) {
        val oldSelectedPosition = selectedPosition
        selectedPosition = mListener.onItemSelected(v)
        if (oldSelectedPosition != selectedPosition) {
            notifyDataSetChanged()
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        val position = buttonView.tag as Int
        selectedItemArray[position] = isChecked
        mListener.onSelectedQualifiersChanged(selectedQualifiers)
    }

    val selectedQualifiers: Collection<Qualifier>
        get() {
            val selectedQualifiers = LinkedList<Qualifier>()
            for (i in 0..itemCount - 1) {
                if (selectedItemArray[i]) {
                    selectedQualifiers.add(getItem(i))
                }
            }
            return selectedQualifiers
        }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name: TextView
        val value: TextView
        val resSwitch: SwitchCompat

        init {
            name = itemView.findViewById(R.id.qualifier_name) as TextView
            value = itemView.findViewById(R.id.qualifier_value) as TextView
            resSwitch = itemView.findViewById(R.id.res_switch) as SwitchCompat
        }
    }

    interface OnItemSelectedListener {
        fun onItemSelected(view: View): Int
        fun onSelectedQualifiersChanged(qualifiers: Collection<Qualifier>)
    }

}
