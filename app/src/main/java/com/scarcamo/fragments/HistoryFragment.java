package com.scarcamo.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scarcamo.R;
import com.scarcamo.TabbedActivity;
import com.scarcamo.adapter.HistoryAdapter;
import com.scarcamo.custom.EqualSpacingItemDecoration;
import com.scarcamo.pojo.OnListFragmentInteractionListener;
import com.scarcamo.pojo.Result;
import com.scarcamo.sugarDB.UserData;
import com.scarcamo.utilities.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment implements OnListFragmentInteractionListener {

    private int mColumnCount = 2;
    private RecyclerView recyclerView;
    private TextView noItem;
    private ArrayList<Result> historyArrayList;

    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        View view = inflater.inflate(R.layout.fragment_history, container, false);

        // Set the adapter

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUpLayout();
    }

    private void setUpLayout() {
        recyclerView = (RecyclerView) getView().findViewById(R.id.list);
        noItem = (TextView) getView().findViewById(R.id.noItem);
        //

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), mColumnCount));
        recyclerView.addItemDecoration(new EqualSpacingItemDecoration(16, EqualSpacingItemDecoration.GRID));
        getPreviousData();

    }

    private void getPreviousData() {

        historyArrayList = new ArrayList<>();
        List<UserData> userDataList = UserData.listAll(UserData.class);
        for(UserData user: userDataList){
            Result result = new Result();
            result.setId(user.getShadeId());
            result.setColor_code(user.getColor());
            result.setModify_date(user.getDate());
            historyArrayList.add(result);
        }
        Collections.reverse(historyArrayList);
        HistoryAdapter mProductPagerAdapter = new HistoryAdapter(getActivity(), historyArrayList, HistoryFragment.this);
        recyclerView.setAdapter(mProductPagerAdapter);
    }

    @Override
    public void onListFragmentInteraction(Result item, int pos) {
        Constants.SelecteResult = item;
        Constants.isToneChanged = true;
        ((TabbedActivity)getActivity()).showProductScreen();
        getActivity().getSupportFragmentManager().popBackStack();
    }
}
