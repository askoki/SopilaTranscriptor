package com.example.arcibald160.sopilatranscriptor.tab_fragments;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.arcibald160.sopilatranscriptor.R;
import com.example.arcibald160.sopilatranscriptor.adapters.Tab1Adapter;

import java.io.File;

public class TabFragment1 extends Fragment {

    private RecyclerView mRecyclerViewTab1;
    private Tab1Adapter mAdapter;
    private String path;

    public TabFragment1() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_tab_1, container, false);
        path = Environment.getExternalStorageDirectory().toString() + "/" + view.getContext().getString(R.string.rec_folder);

        // Recycler view
        mRecyclerViewTab1 = (RecyclerView) view.findViewById(R.id.recycler_view_tab1);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerViewTab1.setHasFixedSize(true);

        // use a linear layout manager
        mRecyclerViewTab1.setLayoutManager(new LinearLayoutManager(getContext()));

        File recordingsDirectory = new File(path);

        // specify an adapter (see also next example)
        mAdapter = new Tab1Adapter(recordingsDirectory.listFiles(), getContext());
        mRecyclerViewTab1.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            mAdapter.refreshRecDir();
        }
    }
}
