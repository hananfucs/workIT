package com.hf.workit.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.hf.workit.R;
import com.hf.workit.components.IPlan;
import com.hf.workit.components.LogManager;
import com.hf.workit.components.PlanManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hanan on 29/06/16.
 */
public class GymLogFragment extends Fragment implements ExpandableListView.OnChildClickListener{

//    private static final String TIMES_PER_WEEK = "Average Times per week: ";
//    private static final String AVERAGE_LENGTH = "Average Length: ";
//    private static final String COMPLETED = "Average % Completed: ";


    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    ArrayList<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.workout_log);
        View rootView = inflater.inflate(R.layout.workout_log, container, false);

        // get the listview
        expListView = (ExpandableListView) rootView.findViewById(R.id.lvExp);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);
        expListView.setOnChildClickListener(this);

        return rootView;
    }

    /*
     * Preparing the list data
     */
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        ArrayList<IPlan> plans = PlanManager.getPlans();

        for (IPlan plan : plans) {
            listDataHeader.add(plan.getTitle());
            List<String> planDetails = new ArrayList<String>();
            planDetails.add(getResources().getString(R.string.avg_times_per_week) + LogManager.getPlanAveragePerWeek(plan.planId()));
            planDetails.add(getResources().getString(R.string.avg_length) + LogManager.getPlanAverageLength(plan.planId()));
            planDetails.add(getResources().getString(R.string.avg_completed) + LogManager.getPlanAverageCompleted(plan.planId()));
            listDataChild.put(plan.getTitle(), planDetails); // Header, Child data
        }

    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Intent intent = new Intent(getActivity(), PlanExecutionSummaryActivity.class);
        intent.putExtra(IPlan.PLAN_NAME, listDataHeader.get(groupPosition));
        startActivity(intent);
        return false;
    }


}
