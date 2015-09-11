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

import com.druk.rtools.databinding.FragmentQualifierDetailBinding;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * A fragment representing a single Qualifier detail screen.
 * This fragment is either contained in a {@link ResourceActivity}
 * in two-pane mode (on tablets) or a {@link QualifierDetailActivity}
 * on handsets.
 */
public class QualifierDetailFragment extends Fragment {

    public static final String ARG_ITEM_ID = "item_id";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public QualifierDetailFragment() {
    }

    public static QualifierDetailFragment newInstance(int id){
        QualifierDetailFragment fragment = new QualifierDetailFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(QualifierDetailFragment.ARG_ITEM_ID, id);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final FragmentQualifierDetailBinding binding = FragmentQualifierDetailBinding.inflate(inflater, container, false);
        int id = getArguments().getInt(ARG_ITEM_ID);
        binding.setQualifier(Qualifier.getQualifier(id));
        binding.setContext(getContext());
        setDummyView(binding.caution, binding.cautionDummy);
        setDummyView(binding.note, binding.noteDummy);
        return binding.getRoot();
    }

    private void setDummyView(final View origin, final View dummy){
        Utils.getViewSize(origin, new Runnable() {
            @Override
            public void run() {
                dummy.getLayoutParams().height = origin.getHeight();
                dummy.requestLayout();
            }
        });
        dummy.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                origin.dispatchTouchEvent(event);
                return true;
            }
        });
    }
}
