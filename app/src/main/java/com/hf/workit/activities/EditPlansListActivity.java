package com.hf.workit.activities;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.text.InputType;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.hf.workit.R;
import com.hf.workit.components.IPlan;
import com.hf.workit.components.PlanManager;
import com.hf.workit.components.Utils;

import org.apache.http.client.UserTokenHandler;

import java.util.ArrayList;
import java.util.List;


public class EditPlansListActivity extends ListActivity implements AdapterView.OnItemClickListener {
    ArrayAdapter<String> adapter;
    List<IPlan> mPlans = new ArrayList<IPlan>();

    ArrayList<String> mPlansNames = new ArrayList<String>();
    ListView lv;

    private Context mContext;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plans_edit);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        mContext = this;
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
        new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom))
                .setCustomTitle(Utils.createTitleView(this, "Create New Plan"))
                .setMessage("Create a new plan or insert share code?")
                .setPositiveButton("Create New", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        createNewPlan();
                    }
                })
                .setNegativeButton("Insert Share Code", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        importPlan();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void importPlan() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        builder.setTitle("Title");
        final Context context = this;
        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String code = input.getText().toString();
                Utils.getSharedPlan(code, refreshHandler, context);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void createNewPlan() {
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

    private Handler refreshHandler = new Handler() {
        @Override
    public void handleMessage(Message msg) {
            if (msg.what == Utils.ERR_NO_INTERNET)
                Utils.popToast(mContext, "No Internet Connection", Toast.LENGTH_LONG);
            else if (msg.what == Utils.ERR_ELSE)
                Utils.popToast(mContext, "OOPS! Something went wrong", Toast.LENGTH_LONG);
            else
                onResume();
        }
    };
}
