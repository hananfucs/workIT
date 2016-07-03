package com.hf.workit.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hf.workit.R;
import com.hf.workit.components.Constatnts;
import com.hf.workit.components.Utils;

public class MainActivity extends Activity {
    private ListView mDrawerList;

    private FrameLayout mainFrame;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;


    private String[] sideMenuTitles = {"Start Workout", "Workout Log", "Edit Plans"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ObjectDrawerItem[] drawerItem = new ObjectDrawerItem[4];

        drawerItem[0] = new ObjectDrawerItem(R.drawable.ic_fitness_center_white_24dp, sideMenuTitles[0]);
        drawerItem[1] = new ObjectDrawerItem(R.drawable.ic_trending_up_white_24dp, sideMenuTitles[1]);
        drawerItem[2] = new ObjectDrawerItem(R.drawable.ic_assignment_white_24dp, sideMenuTitles[2]);
        drawerItem[3] = new ObjectDrawerItem(R.drawable.ic_assignment_white_24dp, "Help");

        DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(this, R.layout.listview_item_row, drawerItem);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
//        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1, sideMenuTitles));
        mDrawerList.setAdapter(adapter);
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                android.R.drawable.ic_delete, android.R.string.paste, android.R.string.copy) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public void onResume() {
        super.onResume();
        final int abTitleId = getResources().getIdentifier("action_bar_title", "id", "android");
        findViewById(abTitleId).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!mDrawerLayout.isDrawerOpen(Gravity.LEFT))
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                else
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
//        menu.findItem(android.R.id.extractArea).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);

    }



    public void startEditPlanActivity(){
        Intent in = new Intent(this, EditPlansListActivity.class);
        startActivity(in);
    }

    public void startWorkout(View v){
        Intent in = new Intent(this, SelectWorkoutActivity.class);
        startActivity(in);
    }

    public void startLogActivity() {
//        Intent intent = new Intent(this, WorkoutLogActivity.class);
        Intent intent = new Intent(this, MainLog.class);
        startActivity(intent);
    }

    private class DrawerItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            view.setBackgroundColor(0xff0a081a);
            switch (position) {
                case 0:
                    startWorkout(null);
                    break;
                case 1:
                    startLogActivity();
                    break;
                case 2:
                    startEditPlanActivity();
                    break;
            }
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }
    }


    public void openDrawer(View v) {
//        mDrawerLayout.openDrawer(Gravity.RIGHT);
    }

    private long mLastBackPress = 0;
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - mLastBackPress < (1000 * 10))
            super.onBackPressed();
        else
            Utils.popToast(this, "Press 'Back' again to exit", Toast.LENGTH_SHORT);
        mLastBackPress = System.currentTimeMillis();
    }




    public class ObjectDrawerItem {

        public int icon;
        public String name;

        // Constructor.
        public ObjectDrawerItem(int icon, String name) {

            this.icon = icon;
            this.name = name;
        }
    }

    public class DrawerItemCustomAdapter extends ArrayAdapter<ObjectDrawerItem> {

        Context mContext;
        int layoutResourceId;
        ObjectDrawerItem data[] = null;

        public DrawerItemCustomAdapter(Context mContext, int layoutResourceId, ObjectDrawerItem[] data) {

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

            ImageView imageViewIcon = (ImageView) listItem.findViewById(R.id.imageViewIcon);
            TextView textViewName = (TextView) listItem.findViewById(R.id.textViewName);

            ObjectDrawerItem folder = data[position];


            imageViewIcon.setImageResource(folder.icon);
            textViewName.setText(folder.name);

            return listItem;
        }

    }
}
