package com.hf.workit.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import com.hf.workit.R;
import com.hf.workit.components.IExercise;
import com.hf.workit.components.IPlan;
import com.hf.workit.components.LogManager;
import com.hf.workit.components.PlanManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hanan on 08/06/16.
 */
public class PlanExercisesSummaryActivity extends Activity implements ExpandableListView.OnChildClickListener {

    private static final String CURRENT_WEIGHT = "Current Weight: ";
    private static final String COMPLETED = "Average % Completed: ";


    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    ArrayList<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    private String planID;
    private ArrayList<String> exercisesIDs = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plan_exerxises_summary);

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lv_exp_exercises);

        // preparing list data
        prepareListData(getIntent().getStringExtra(IPlan.PLAN_NAME));
        planID = getIntent().getStringExtra(IPlan.PLAN_NAME);

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);
        expListView.setOnChildClickListener(this);
        getActionBar().setTitle("LOG");

        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(this);
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
     * Preparing the list data
     */
    private void prepareListData(String planName) {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        ArrayList<IExercise> planExercises = PlanManager.getExecisesForPlan(planName);

        for (IExercise exercise : planExercises) {
            exercisesIDs.add(String.valueOf(exercise.getId()));
            listDataHeader.add(exercise.getTitle() + " - " + exercise.getExercise());
            List<String> exerciseDetails = new ArrayList<String>();
            exerciseDetails.add(CURRENT_WEIGHT + exercise.getWeight());
            exerciseDetails.add(COMPLETED + LogManager.getExerciseAverageCompletion(String.valueOf(exercise.getId())));
            listDataChild.put(exercise.getTitle() + " - " + exercise.getExercise(), exerciseDetails); // Header, Child data
        }

    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Intent intent = new Intent(this, ExerciseExecutionSummaryActivity.class);
        intent.putExtra(IPlan.PLAN_NAME, planID);
        intent.putExtra(IExercise.EXERCISE_ID, exercisesIDs.get(groupPosition));
        startActivity(intent);
        return false;
    }
}
