package com.hf.workit.components;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by hanan on 12/09/15.
 */
public interface IExercise extends Serializable {
    String EXERCISE_ID = "exercise_id";

    String getTitle();
    String getExercise();
    String getWeight();
    int getId();
    int getSets();
    JSONObject toJson();

    String getExercise2();
    String getWeight2();

    ArrayList<String> getPhotos();
}
