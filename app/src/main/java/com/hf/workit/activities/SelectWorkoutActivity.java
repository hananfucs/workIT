package com.hf.workit.activities;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.hf.workit.R;
import com.hf.workit.components.IPlan;
import com.hf.workit.components.LogManager;
import com.hf.workit.components.PlanManager;
import com.hf.workit.components.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hanan on 01/06/16.
 */
public class SelectWorkoutActivity extends ListActivity implements AdapterView.OnItemClickListener {
    ArrayAdapter<String> adapter;
    List<IPlan> mPlans = new ArrayList<IPlan>();

    ArrayList<String> mPlansNames = new ArrayList<String>();
    ListView lv;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_workout);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPlans = PlanManager.getPlans();

        adapter = new ArrayAdapter<String>(this, R.layout.listview_reg, mPlansNames);
        setListAdapter(adapter);

        refreshPlans();
        lv = (ListView)findViewById(android.R.id.list);
        lv.setOnItemClickListener(this);
        getActionBar().setTitle("Start");
    }

    private void refreshPlans() {
        mPlansNames.clear();
        for(IPlan pl:mPlans){
            mPlansNames.add(pl.getTitle());
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, WorkoutActivity.class);
        intent.putExtra(IPlan.PLAN_NAME, mPlansNames.get((int) id));
        if (!manageActiveWorkout(mPlansNames.get((int)id), intent, id))
            return;
        startWorkoutExecuteActivity(intent, id);
    }

    private void startWorkoutExecuteActivity(Intent intent, long id) {
        LogManager.startWorkout();

        if (!PlanManager.getPlan(mPlansNames.get((int) id)).planId().equals(LogManager.getCurrentPlanID())) {
            LogManager.resetExerciseExecution();
        }
        LogManager.setCurrentPlanID(PlanManager.getPlan(mPlansNames.get((int) id)).planId());
        startActivity(intent);
    }

    private boolean manageActiveWorkout(String workoutName, final Intent intent, final long id) {
        if (!LogManager.isWorkoutInProgress())
            return true;
        String workoutId = PlanManager.getPlan(workoutName).planId();
        if (workoutId.equals(LogManager.getCurrentPlanID()))
            return true;

        String currentWorkoutName = LogManager.getCurrentPlanName();
        new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom))
                .setCustomTitle(Utils.createTitleView(this, "Start Another Workout"))
                .setMessage("Are you sure you want to end workout " + currentWorkoutName + " and start " + workoutName + "?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        LogManager.getExerciseStateList().clear();
                        startWorkoutExecuteActivity(intent, id);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(android.R.drawable.ic_input_get)
                .show();

        return false;
    }

}
