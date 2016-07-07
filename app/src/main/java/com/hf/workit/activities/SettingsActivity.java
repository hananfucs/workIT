package com.hf.workit.activities;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.hf.workit.R;
import com.hf.workit.components.PlanManager;

/**
 * Created by hanan on 05/07/16.
 */
public class SettingsActivity extends ListActivity {
    ListView lv;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        SettingsListItem[] drawerItem = new SettingsListItem[3];

        drawerItem[0] = new SettingsListItem("Vibrate");
        drawerItem[1] = new SettingsListItem("Whistle");
        drawerItem[2] = new SettingsListItem("CountDown");

        DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(this, R.layout.settings_list_item, drawerItem);

        lv = (ListView) findViewById(android.R.id.list);

        lv.setAdapter(adapter);
    }



    @Override
    public void onResume() {
        super.onResume();
    }


    public class DrawerItemCustomAdapter extends ArrayAdapter<SettingsListItem> {

        Context mContext;
        int layoutResourceId;
        SettingsListItem data[] = null;

        public DrawerItemCustomAdapter(Context mContext, int layoutResourceId, SettingsListItem[] data) {

            super(mContext, layoutResourceId, data);
            this.layoutResourceId = layoutResourceId;
            this.mContext = mContext;
            this.data = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View listItem = convertView;

            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            listItem = inflater.inflate(layoutResourceId, parent, false);

            TextView textViewName = (TextView) listItem.findViewById(R.id.settings_title);

            SettingsListItem folder = data[position];

                textViewName.setText(folder.name);

            return listItem;
        }
    }

    public class SettingsListItem {

        public String name;
        public SettingsListItem(String name) {
            this.name = name;
        }
    }

    public void setEnable(View v) {
        Switch currentSwitch = (Switch)v;
        Log.d("HHH", "isEnabled: " + currentSwitch.isChecked() + " id: " + currentSwitch.getId());
    }
}
