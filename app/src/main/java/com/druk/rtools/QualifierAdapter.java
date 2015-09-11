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

import com.druk.rtools.databinding.QualifierItemBinding;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class QualifierAdapter extends RecyclerView.Adapter<QualifierAdapter.ViewHolder> implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private final boolean[] checkedItems;
    private final Qualifier[] qualifiers;
    private final OnItemSelectedListener mListener;
    private final int mBackground;

    public QualifierAdapter(Context context, boolean[] checkedItems, OnItemSelectedListener listener) {
        this.qualifiers = Qualifier.values();
        this.checkedItems = checkedItems;
        this.mListener = listener;

        TypedValue mTypedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View root = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.qualifier_item, viewGroup, false);
        root.setBackgroundResource(mBackground);
        root.setOnClickListener(this);
        return new ViewHolder(root);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Qualifier qualifier = getItem(i);
        viewHolder.mBinding.setQualifier(qualifier);
        viewHolder.mBinding.resSwitch.setOnCheckedChangeListener(null);
        viewHolder.mBinding.resSwitch.setChecked(checkedItems[i]);
        viewHolder.mBinding.resSwitch.setOnCheckedChangeListener(this);
        viewHolder.mBinding.resSwitch.setTag(i);
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
        mListener.onItemSelected(v);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Integer position = (Integer) buttonView.getTag();
        checkedItems[position] = isChecked;
        mListener.onSelectedQualifiersChanged(getSelectedQualifiers());
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

        private QualifierItemBinding mBinding;

        public ViewHolder(View itemView) {
            super(itemView);
            mBinding = QualifierItemBinding.bind(itemView);
            mBinding.setContext(itemView.getContext());
        }
    }

    public interface OnItemSelectedListener{
        int onItemSelected(View view);
        void onSelectedQualifiersChanged(Collection<Qualifier> qualifiers);
    }

}
