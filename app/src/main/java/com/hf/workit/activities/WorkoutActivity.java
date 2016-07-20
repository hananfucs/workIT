package com.hf.workit.activities;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hf.workit.R;
import com.hf.workit.components.Constatnts;
import com.hf.workit.components.IExercise;
import com.hf.workit.components.IPlan;
import com.hf.workit.components.LogManager;
import com.hf.workit.components.PlanManager;
import com.hf.workit.components.Utils;


import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hanan on 01/06/16.
 */
public class WorkoutActivity extends ListActivity implements AdapterView.OnItemClickListener {
    ArrayAdapter<String> adapter;
    List<IExercise> mExercises = new ArrayList<IExercise>();
    List<Integer> mExercisesIds = new ArrayList<Integer>();
    List<Integer> mExercisesExecution = new ArrayList<Integer>();
    List<String> mExercisesNames = new ArrayList<String>();
    ListView lv;
    private String mCurrentPlan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout_main);
        Intent it = getIntent();
        mCurrentPlan = it.getStringExtra(IPlan.PLAN_NAME);
        LogManager.initExerciseExecution(new ArrayList<Integer>(), mCurrentPlan);
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

    @Override
    public void onResume() {
        super.onResume();
        mCurrentPlan = getIntent().getStringExtra(IPlan.PLAN_NAME);
        mExercises = PlanManager.getExecisesForPlan(mCurrentPlan);
        Collections.sort(mExercises, new Comparator<IExercise>() {
            @Override
            public int compare(IExercise lhs, IExercise rhs) {
                return lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
            }
        });
        lv = (ListView)findViewById(android.R.id.list);
        lv.setOnItemClickListener(this);
        createList();
        setListColors();
        getActionBar().setTitle(mCurrentPlan.toUpperCase());
    }

    private void createList() {
        mExercisesNames.clear();
        mExercisesIds.clear();
        for(IExercise ex : mExercises){
            mExercisesNames.add(ex.getTitle() + " - " + ex.getExercise());
            mExercisesIds.add(ex.getId());
            if (LogManager.getExerciseStateList().size() < mExercisesIds.size())
                LogManager.getExerciseStateList().add(0);
        }
        LogManager.initExerciseExecution(mExercisesIds, mCurrentPlan);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mExercisesNames);
        setListAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!lv.getChildAt((int) id).isEnabled())
            return;


        Intent intent = new Intent(this, ExecuteExercise.class);
        intent.putExtra(IPlan.PLAN_NAME, mCurrentPlan);
        intent.putExtra(IExercise.EXERCISE_ID, mExercisesIds.get((int) id));
        currentExerciseInt = position;
        startActivityForResult(intent, 1);

    }

    private int currentExerciseInt;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int setsDone = data.getIntExtra("completed", 0);
        int execution = data.getIntExtra("execution", 0);
        LogManager.setExerciseExecution(mExercisesIds.get(currentExerciseInt), setsDone);
        LogManager. getExerciseStateList().add(currentExerciseInt, execution);
        LogManager. getExerciseStateList().remove(currentExerciseInt + 1);
        setListColors();
    }

    private void setListColors() {
        lv.post(new Runnable() {
            @Override
            public void run() {

                for (int i = 0; i < mExercisesIds.size(); i++) {
                    int curr = LogManager.getExerciseStateList().get(i);
                    if (curr == ExecuteExercise.EXERCISE_FINISHED) {
                        lv.getChildAt(i).setBackgroundColor(0xFF0C4300);
                        lv.getChildAt(i).setEnabled(false);
                    } else if (curr == ExecuteExercise.EXERCISE_MIDWAY)
                        lv.getChildAt(i).setBackgroundColor(0xFF434200);
                    else if (curr == ExecuteExercise.EXERCISE_NOT_STARTED)
                        lv.getChildAt(i).setBackgroundColor(0x07000000);
                }
            }
        });
    }

    public void finishWorkout(View v) {
        // display workout stats
        if (!isWorkoutDone())
            askIfFinish();
        else
            displayWorkoutStatsAndFinish();
    }

    private void askIfFinish() {
        new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom))
                .setMessage(getResources().getString(R.string.end_workout_msg))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        displayWorkoutStatsAndFinish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                })
                .setIcon(android.R.drawable.btn_star)
                .setCustomTitle(Utils.createTitleView(this, getResources().getString(R.string.workout_not_complete)))
                .show();
    }

    private boolean isWorkoutDone() {
        for (Integer execution : LogManager. getExerciseStateList()) {
            if (execution != ExecuteExercise.EXERCISE_FINISHED)
                return false;
        }
        return true;
    }

    private void displayWorkoutStatsAndFinish() {
        LogManager.endWorkout();
        LogManager.logWorkout();
        new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom))
                .setCustomTitle(Utils.createTitleView(this, getResources().getString(R.string.workout_complete)))
                .setMessage(getFinishMessage())
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        LogManager.resetCurrentWorkout();
                        finish();
                    }
                })
                .setIcon(android.R.drawable.btn_star)
                .show();
    }

    private String getFinishMessage() {
        StringBuilder builder  = new StringBuilder(getResources().getString(R.string.great_job) + " \n \n");
        builder.append(getResources().getString(R.string.plan) + ": " + mCurrentPlan + "\n")
                .append(getResources().getString(R.string.date) + ": " + new SimpleDateFormat("dd-MM-yyyy").format(new Date()) + "\n")
                .append(getResources().getString(R.string.time) + ": " + LogManager.getWorkoutLength() + "\n")
                .append(getResources().getString(R.string.percentage_completed) + ": " + LogManager.getWorkoutPercentage());
        return builder.toString();
    }

    public void editPlan(View v) {
        Intent intent = new Intent (this, EditPlanDetailsActivity.class);
        intent.putExtra(IPlan.PLAN_NAME, mCurrentPlan);
        startActivity(intent);
    }
}
