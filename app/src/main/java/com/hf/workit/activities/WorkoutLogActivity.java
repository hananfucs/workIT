package com.hf.workit.activities;

import android.app.Activity;
import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.hf.workit.R;
import com.hf.workit.components.IPlan;
import com.hf.workit.components.LogManager;
import com.hf.workit.components.PlanManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hanan on 07/06/16.
 */
public class WorkoutLogActivity extends Activity implements ExpandableListView.OnChildClickListener{

    private static final String TIMES_PER_WEEK = "Average Times per week: ";
    private static final String AVERAGE_LENGTH = "Average Length: ";
    private static final String COMPLETED = "Average % Completed: ";


    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    ArrayList<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout_log);

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);
        expListView.setOnChildClickListener(this);
        getActionBar().setTitle("LOG");
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
            planDetails.add(TIMES_PER_WEEK + LogManager.getPlanAveragePerWeek(plan.planId()));
            planDetails.add(AVERAGE_LENGTH + LogManager.getPlanAverageLength(plan.planId()));
            planDetails.add(COMPLETED + LogManager.getPlanAverageCompleted(plan.planId()));
            listDataChild.put(plan.getTitle(), planDetails); // Header, Child data
        }

    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Log.d("HHH", "groupPosition: " + groupPosition + " childPosition: " + childPosition + " id: " + id);

        Intent intent = new Intent(this, PlanExecutionSummaryActivity.class);
        intent.putExtra(IPlan.PLAN_NAME, listDataHeader.get(groupPosition));
        startActivity(intent);
        return false;
    }
}

