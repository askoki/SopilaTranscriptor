package com.example.arcibald160.sopilatranscriptor.tab_fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.arcibald160.sopilatranscriptor.R;
import com.example.arcibald160.sopilatranscriptor.adapters.Tab3Adapter;


public class TabFragment3 extends Fragment {

    private RecyclerView mRecyclerViewTab3;
    private Tab3Adapter mAdapter;

    public TabFragment3() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_3, container, false);

        // Recycler view
        mRecyclerViewTab3 = (RecyclerView) view.findViewById(R.id.recycler_view_tab3);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerViewTab3.setHasFixedSize(true);

        // use a linear layout manager
        mRecyclerViewTab3.setLayoutManager(new LinearLayoutManager(getContext()));

        // specify an adapter (see also next example)
        mAdapter = new Tab3Adapter(getContext());
        mRecyclerViewTab3.setAdapter(mAdapter);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            mAdapter.refreshSheetDir();
        }
    }


}
