package com.hf.workit.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.hf.workit.R;
import com.hf.workit.components.IPlan;
import com.hf.workit.components.PlanManager;

import java.util.ArrayList;
import java.util.List;


public class EditPlansListActivity extends ListActivity implements AdapterView.OnItemClickListener {
    ArrayAdapter<String> adapter;
    List<IPlan> mPlans = new ArrayList<IPlan>();

    ArrayList<String> mPlansNames = new ArrayList<String>();
    ListView lv;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plans_edit);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPlans = PlanManager.getPlans();

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mPlansNames);
        setListAdapter(adapter);

        refreshPlans();
        lv = (ListView)findViewById(android.R.id.list);
        lv.setOnItemClickListener(this);
        getActionBar().setTitle("Plans");
    }

    private void refreshPlans() {
        mPlansNames.clear();
        for(IPlan pl:mPlans){
            mPlansNames.add(pl.getTitle());
        }
        adapter.notifyDataSetChanged();
    }


    public void createPlan(View v){
        Intent i = new Intent(this, EditPlanDetailsActivity.class);
        startActivityForResult(i, 1);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, EditPlanDetailsActivity.class);
        intent.putExtra(IPlan.PLAN_NAME, mPlansNames.get((int) id));
        startActivity(intent);
    }


    @Override
    public void onBackPressed() {
        PlanManager.saveDataToDisk();
        finish();
    }
}
