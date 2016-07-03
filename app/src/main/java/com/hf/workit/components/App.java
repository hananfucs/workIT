package com.hf.workit.components;

import android.app.Application;

/**
 * Created by hanan on 05/06/16.
 */
public class App extends Application {
    private static final boolean TEST = true;

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.initUtils(this);

        PlanManager.setContext(this.getApplicationContext());
        PlanManager.loadPlansFromFile();
        PlanManager.getAcFromFile();
        PlanManager.getCardioFromFile();

        FontsOverride.setDefaultFont(this, "DEFAULT", "work_sans-el.ttf");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "work_sans-el.ttf");
        FontsOverride.setDefaultFont(this, "SERIF", "work_sans-el.ttf");
        FontsOverride.setDefaultFont(this, "SANS_SERIF", "work_sans-el.ttf");

        if(TEST) {

            AppTester.createPlans();
            AppTester.addWorkoutsToLog();
            AppTester.addCardioExercises();
        }
    }
}
