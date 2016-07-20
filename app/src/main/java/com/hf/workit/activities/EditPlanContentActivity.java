package com.hf.workit.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.hf.workit.R;
import com.hf.workit.components.IExercise;
import com.hf.workit.components.IPlan;
import com.hf.workit.components.PlanManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class EditPlanContentActivity extends ListActivity implements AdapterView.OnItemClickListener {
    ArrayAdapter<String> adapter;
    List<IExercise> mExercises = new ArrayList<IExercise>();
    List<Integer> mExercisesIds = new ArrayList<Integer>();
    List<String> mExercisesNames = new ArrayList<String>();
    ListView lv;
    private String mCurrentPlan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_plan_content);
        Intent it = getIntent();
        mCurrentPlan = it.getStringExtra(IPlan.PLAN_NAME);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        mExercises = PlanManager.getExecisesForPlan(mCurrentPlan);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mExercisesNames);
        setListAdapter(adapter);
        lv = (ListView)findViewById(android.R.id.list);
        lv.setOnItemClickListener(this);
        createList();
        getActionBar().setTitle(getResources().getString(R.string.exercises));
    }

    private void createList() {
        mExercisesNames.clear();
        mExercisesIds.clear();
        for(IExercise ex:mExercises){
            mExercisesNames.add(ex.getTitle() + " - " + ex.getExercise());
            mExercisesIds.add(ex.getId());
        }
        Collections.sort(mExercisesNames, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return lhs.compareToIgnoreCase(rhs);
            }
        });
        adapter.notifyDataSetChanged();
    }

    public void addExecise(View v){
        Intent intent = new Intent(this, EditExerciseActivity.class);
        intent.putExtra(IPlan.PLAN_NAME, mCurrentPlan);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void savePlanContent() {
        saveExercisesToJson();

    }

    private JSONArray saveExercisesToJson() {
        JSONArray exerciseJson = new JSONArray();
        int index = 0;
        for (IExercise exercise : mExercises) {
            try {
                exerciseJson.put(index, exercise.toJson());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return exerciseJson;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, EditExerciseActivity.class);
        intent.putExtra(IPlan.PLAN_NAME, mCurrentPlan);
        intent.putExtra(IExercise.EXERCISE_ID, (int) mExercisesIds.get((int) id));
        startActivity(intent);
    }
}
