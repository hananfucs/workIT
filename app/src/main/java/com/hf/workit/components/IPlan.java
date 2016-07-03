package com.hf.workit.components;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hanan on 12/09/15.
 */
public interface IPlan extends Serializable {
    String PLAN_NAME = "plan_name";

    String planId();
    String getTitle();
    String getCoach();
    String getClub();
    int getTimes();
    String getFileName();
    long getmCreationTime();
    JSONObject getJson() throws JSONException;
    long getExpirationTime();

    ArrayList<IExercise> getExercises();
    void setExercises(ArrayList<IExercise> exercises);
}
