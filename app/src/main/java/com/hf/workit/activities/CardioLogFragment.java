package com.hf.workit.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.hf.workit.R;
import com.hf.workit.components.IExercise;
import com.hf.workit.components.IPlan;
import com.hf.workit.components.LogManager;
import com.hf.workit.components.PlanManager;
import com.hf.workit.components.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by hanan on 29/06/16.
 */
public class CardioLogFragment extends ListFragment {
    ArrayAdapter<String> adapter;
    HashMap<String, String> mExercises;
    List<String> mExercisesIds = new ArrayList<String>();
    List<String> mExercisesNames = new ArrayList<String>();
    ListView lv;

    ImageView mAddExercise;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.edit_plan_content, container, false);

        mExercises = PlanManager.getCardios();
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mExercisesNames);
        setListAdapter(adapter);
        lv = (ListView) rootView.findViewById(android.R.id.list);
//        lv.setOnItemClickListener(getActivity().);
        createList();
        mAddExercise = (ImageView)rootView.findViewById(R.id.add_exercise);
        mAddExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addExercise();
            }
        });
        return rootView;
    }

    private void addExercise() {
        Log.d("HHH", "addExercise");
        // Creating alert Dialog with one Button
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom));

        //AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();

        // Setting Dialog Title
        alertDialog.setCustomTitle(Utils.createTitleView(getActivity(), "Add Cardio Exercise"));

        // Setting Dialog Message
        alertDialog.setMessage("Exercise Name:");
        final EditText input = new EditText(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        alertDialog.setView(input);
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (addCardioExercise(input.getText().toString()))
                            createList();
                    }

                    private boolean addCardioExercise(String s) {
                        if (PlanManager.getCardios().containsValue(s)) {
                            Toast.makeText(getActivity(), "Already Have " + s, Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        PlanManager.getCardios().put(UUID.randomUUID().toString(), s);
                        PlanManager.saveDataToDisk();
                        return true;
                    }
                });
        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        dialog.cancel();
                    }
                });

        // closed
        AlertDialog dia = alertDialog.create();
        // Showing Alert Message
        dia.show();
    }

    private void createList() {
        mExercisesNames.clear();
        mExercisesIds.clear();
        mExercises = PlanManager.getCardios();

        for (Map.Entry<String, String> entry : mExercises.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            mExercisesIds.add(key);
            mExercisesNames.add(value);
        }
        Collections.sort(mExercisesNames, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return lhs.compareToIgnoreCase(rhs);
            }
        });
        adapter.notifyDataSetChanged();
    }

//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Intent intent = new Intent(getActivity(), CardioSummaryActivity.class);
////        intent.putExtra(IPlan.PLAN_NAME, mCurrentPlan);
////        intent.putExtra(IExercise.EXERCISE_ID, (int) mExercisesIds.get((int) id));
//        startActivity(intent);
//    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent(getActivity(), CardioSummaryActivity.class);
        intent.putExtra(IExercise.EXERCISE_ID, mExercisesIds.get((int)id));
        startActivity(intent);
    }
}
