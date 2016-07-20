package com.hf.workit.components;

import android.app.Application;

import com.hf.workit.BuildConfig;

import java.io.File;

/**
 * Created by hanan on 05/06/16.
 */
public class App extends Application {
    private static final boolean TEST = false;

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

        File picsFolder = new File(Constatnts.PICTURES_DIR);
        boolean success = true;
        if (!picsFolder.exists()) {
            success = picsFolder.mkdir();
        }
        if (success) {
            // Do something on success
        }

        if(TEST && BuildConfig.DEBUG) {

            AppTester.createPlans();
            AppTester.addWorkoutsToLog();
            AppTester.addCardioExercises();
        }
    }
}
