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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collection;

/**
 * A list fragment representing a list of Qualifiers. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link QualifierDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class QualifierListFragment extends Fragment implements QualifierAdapter.OnItemSelectedListener {

    private static final String PARAM_SELECTED_BOOLEAN_SPARSE = "com.druk.rtools.PARAM_SELECTED_BOOLEAN_SPARSE";
    private static final String PARAM_SELECTED_ITEM = "com.druk.rtools.PARAM_SELECTED_ITEM";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        void onItemSelected(int id);

        void onFolderNameChanged(String folderName);
    }

    private static Callbacks sDummyCallbacks = new Callbacks() {

        @Override
        public void onItemSelected(int id) {}

        @Override
        public void onFolderNameChanged(String id) {}
    };

    private QualifierAdapter mAdapter;
    private RecyclerView mRecyclerView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Activities containing this fragment must implement its callbacks.
        if (!(context instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_qualifier_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String[] qualifierNames = getResources().getStringArray(R.array.qualifiers);
        boolean[] checkedQualifiers;
        int position = 0;
        // Restore the previously serialized activated item position.
        if (savedInstanceState != null && savedInstanceState.containsKey(PARAM_SELECTED_BOOLEAN_SPARSE)) {
            checkedQualifiers = savedInstanceState.getBooleanArray(PARAM_SELECTED_BOOLEAN_SPARSE);
            position = savedInstanceState.getInt(PARAM_SELECTED_ITEM, 0);
        } else {
            checkedQualifiers = new boolean[qualifierNames.length];
        }

        mAdapter = new QualifierAdapter(getContext(), checkedQualifiers, this);
        mAdapter.setSelectedPosition(position);
        mAdapter.setHasStableIds(true);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        onSelectedQualifiersChanged(mAdapter.getSelectedQualifiers());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBooleanArray(PARAM_SELECTED_BOOLEAN_SPARSE, mAdapter.getSelectedItemArray());
        outState.putInt(PARAM_SELECTED_ITEM, mAdapter.getSelectedPosition());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public int onItemSelected(View view) {
        int position = mRecyclerView.getLayoutManager().getPosition(view);
        Qualifier qualifier = mAdapter.getItem(position);
        mCallbacks.onItemSelected(qualifier.ordinal());
        return position;
    }

    @Override
    public void onSelectedQualifiersChanged(Collection<Qualifier> selectedQualifiers) {
        mCallbacks.onFolderNameChanged(getResourceFolderName(getContext(), selectedQualifiers));
    }

    private static String getResourceFolderName(Context ctx, Collection<Qualifier> qualifiers) {
        StringBuilder result = new StringBuilder();
        for (Qualifier qualifier : qualifiers) {
            result.append(qualifier.getValue(ctx)).append("-");
        }
        if (result.length() > 0) {
            result.deleteCharAt(result.length() - 1);
        }
        return result.toString();
    }
}
