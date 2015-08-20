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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class QualifierAdapter extends RecyclerView.Adapter<QualifierAdapter.ViewHolder> implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private final boolean[] checkedItems;
    private final Qualifier[] qualifiers;
    private OnItemSelectedListener mListener;

    public QualifierAdapter(Context context, boolean[] checkedItems, OnItemSelectedListener listener) {
        this.qualifiers = Qualifier.values();
        this.checkedItems = checkedItems;
        this.mListener = listener;
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
        Qualifier qualifier = getItem(i);
        viewHolder.name.setText(qualifiers[i].nameResource);
        viewHolder.switchCompat.setOnCheckedChangeListener(null);
        String value = qualifier.getValue(viewHolder.itemView.getContext());
        if (value == null) {
            viewHolder.value.setText("undefined");
            viewHolder.switchCompat.setEnabled(false);
        } else {
            viewHolder.value.setText(value);
            viewHolder.switchCompat.setEnabled(true);
        }
        viewHolder.switchCompat.setChecked(checkedItems[i]);
        viewHolder.switchCompat.setOnCheckedChangeListener(this);
        viewHolder.switchCompat.setTag(i);
        viewHolder.cell.setTag(viewHolder.switchCompat);

    }

    @Override
    public int getItemCount() {
        return qualifiers.length;
    }

    public Qualifier getItem(int position){
        return qualifiers[position];
    }

    @Override
    public void onClick(View v) {
        SwitchCompat switchCompat = (SwitchCompat) v.getTag();
        int position = (int) switchCompat.getTag();
        if (!TextUtils.isEmpty(getItem(position).getValue(v.getContext()))) {
            switchCompat.toggle();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        checkedItems[((int) buttonView.getTag())] = isChecked;
        mListener.onItemSelected(getSelectedQualifiers());
    }

    public Collection<Qualifier> getSelectedQualifiers() {
        List<Qualifier> selectedQualifiers = new LinkedList<>();
        for (int i = 0; i < getItemCount(); i++) {
            if (checkedItems[i]) {
                selectedQualifiers.add(getItem(i));
            }
        }
        return selectedQualifiers;
    }

    public boolean[] getSelectedItemArray(){
        return checkedItems;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

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

    public interface OnItemSelectedListener{
        void onItemSelected(Collection<Qualifier> selectedQualifiers);
    }

}
