package com.hf.workit.components;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by hanan on 01/04/16.
 */
public class PlanManager {
    private static HashMap <String, IPlan> sPlans = new HashMap<String, IPlan>();

    private static HashMap<String, String> sCardios = new HashMap<String, String>();
    private static Context sContext;

    private static ArrayList<String> acTrainer = new ArrayList<String>();
    private static ArrayList<String> acClub = new ArrayList<String>();
    private static ArrayList<String> acMuscle = new ArrayList<String>();
    private static ArrayList<String> acExName = new ArrayList<String>();
    private static ArrayList<String> acExDescription = new ArrayList<String>();

    public static void setContext(Context context) {
        sContext = context;
    }

    public static ArrayList<IPlan> getPlans() {
        Iterator it = sPlans.entrySet().iterator();
        ArrayList<IPlan> ret = new ArrayList<IPlan>();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            ret.add((IPlan)pair.getValue());
        }
        return ret;
    }

    public static IPlan getPlan (String name) {
        return sPlans.get(name);
    }

    public static void addPlan(String name, IPlan plan) {
        sPlans.put(name, plan);
    }

    public static ArrayList<IExercise> getExecisesForPlan(String planName) {
        if (!sPlans.containsKey(planName))
            return new ArrayList<IExercise>();
        return sPlans.get(planName).getExercises();
    }

    public static boolean addExerciseToPlan(String planName, IExercise exercise) {
        if (!sPlans.containsKey(planName))
            return false;
        ArrayList<IExercise> exercises = sPlans.get(planName).getExercises();
        IExercise toRemove = null;
        for (IExercise ex : exercises) {
            if (ex.getId() == exercise.getId()) {
                toRemove = ex;
                break;
            }
        }
        exercises.remove(toRemove);
        exercises.add(exercise);
        sPlans.get(planName).setExercises(exercises);
        return true;
    }

    public static boolean setExercisesInPlan(String planName, ArrayList<IExercise> exercises) {
        if (!planName.contains(planName))
            return false;
        sPlans.get(planName).setExercises(exercises);
        return true;
    }

    public static void removePlan(String planName) {
        sPlans.remove(planName);
    }

    public static void saveDataToDisk() {
        JSONArray plans = getAllPlansJsonArray();
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(sContext.openFileOutput(Constatnts.PLANS_FILE_NAME, Context.MODE_PRIVATE));
            outputStreamWriter.write(plans.toString());
            outputStreamWriter.close();
            writeAutoCompleteToFile();
            writeCardiosToFile();
        }
        catch (IOException e) {
            Log.e("HHH", "File write failed: " + e.toString());
        }
    }

    public static void writeCardiosToFile() throws IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(sContext.openFileOutput(Constatnts.CARDIO_FILE_NAME, Context.MODE_PRIVATE)));

        Iterator it = sCardios.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            bw.write((String)pair.getKey() + ";" + (String)pair.getValue());
            bw.newLine();
        }

        bw.close();
    }

    private static void writeAutoCompleteToFile() throws IOException {

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(sContext.openFileOutput(Constatnts.AC_FILE_NAME, Context.MODE_PRIVATE)));

        writeAcSection(Constatnts.AC_TRAINER, acTrainer, bw);
        writeAcSection(Constatnts.AC_CLUB, acClub, bw);
        writeAcSection(Constatnts.AC_MUSCLE, acMuscle, bw);
        writeAcSection(Constatnts.AC_NAME, acExName, bw);
        writeAcSection(Constatnts.AC_DESCRIPTION, acExDescription, bw);
        bw.close();
    }

    private static void writeAcSection(String title, ArrayList<String> list, BufferedWriter bw) throws IOException {
        bw.write(Constatnts.AC_SEPARATOR + ";" + title);
        bw.newLine();
        for (String str : list) {
            bw.write(str);
            bw.newLine();
        }
    }

    public static IExercise getExerciseFromPlan(String planName, int exerciseID) {
        IPlan plan = getPlan(planName);
        for (IExercise exercise : plan.getExercises()) {
            if (exercise.getId() == exerciseID)
                return  exercise;
        }
        return null;
    }

    private static JSONArray getAllPlansJsonArray() {
        JSONArray mainArray = new JSONArray();
        Iterator it = sPlans.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            try {
                mainArray.put(getPlanJson((String) pair.getKey()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return mainArray;
    }

    public static JSONObject getPlanJson(String plan) throws JSONException {
        JSONObject planJson;
        planJson = getPlan(plan).getJson();
        planJson.put(Constatnts.PlanJson.EXERCISES, getExerciseListJsonArray(plan));
        return planJson;
    }

    private static JSONArray getExerciseListJsonArray(String plan) {
        JSONArray array = new JSONArray();

        ArrayList<IExercise> exercises = getExecisesForPlan(plan);
        for (IExercise ex : exercises) {
            array.put(ex.toJson());
        }
        return array;
    }

    public static void loadPlansFromFile() {
        String loaded = readFromFile();
        JSONArray plans = null;

        try {
            plans = new JSONArray(loaded);

        if (plans == null)
            return;

        for (int i = 0; i < plans.length(); i++) {
            JSONObject planJson = plans.getJSONObject(i);
            IPlan plan = createPlanFromJson(planJson);
            addPlan(plan.getTitle(), plan);
        }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static IPlan createPlanFromJson(JSONObject planJson) throws JSONException {
        String planType = planJson.getString(Constatnts.PlanJson.TYPE);
        IPlan retPlan = null;
        if (planType.equals("ab")) {
            retPlan = new ABPlan(planJson.getString(Constatnts.PlanJson.NAME), planJson.getString(Constatnts.PlanJson.TRAINER),
                    planJson.getString(Constatnts.PlanJson.CLUB), planJson.getInt(Constatnts.PlanJson.TIMES_PER_WEEK),
                    getExercisesListFromJsonArray(planJson.getJSONArray(Constatnts.PlanJson.EXERCISES)),
                    null, false, planJson.getLong(Constatnts.PlanJson.CREATION_TIME));
        } else if (planType.equals("single")) {
            retPlan = new SinglePlan(planJson.getString(Constatnts.PlanJson.ID), planJson.getString(Constatnts.PlanJson.NAME), planJson.getString(Constatnts.PlanJson.TRAINER),
                    planJson.getString(Constatnts.PlanJson.CLUB), planJson.getInt(Constatnts.PlanJson.TIMES_PER_WEEK),
                    getExercisesListFromJsonArray(planJson.getJSONArray(Constatnts.PlanJson.EXERCISES)),
                    null, false, planJson.getLong(Constatnts.PlanJson.CREATION_TIME), planJson.getLong(Constatnts.PlanJson.EXPIRATION_TIME));
        }
        return retPlan;
    }

    private static ArrayList<IExercise> getExercisesListFromJsonArray(JSONArray exercisesJsonArray) throws JSONException {
        ArrayList<IExercise> exercises = new ArrayList<IExercise>();
        for (int i = 0; i < exercisesJsonArray.length(); i++) {
            exercises.add(getExerciseFromJson((JSONObject) exercisesJsonArray.get(i)));
        }
        return exercises;
    }

    private static IExercise getExerciseFromJson(JSONObject exerciseJson) throws JSONException {
        String exerciseType = exerciseJson.getString(Constatnts.ExerciseJson.TYPE);
        if (exerciseType.equals("single"))
            return SingleExercise.fromJson(exerciseJson);
        else if (exerciseType.equals("double"))
            return DoubleExercise.fromJson(exerciseJson);
        return null;
    }


    private static String readFromFile() {
        String ret = "";
        try {

            InputStream inputStream = sContext.openFileInput(Constatnts.PLANS_FILE_NAME);
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            } else {
                Log.d("HHH", "File not Found!");
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    public static void getAcFromFile() {
        try {

            InputStream inputStream = sContext.openFileInput(Constatnts.AC_FILE_NAME);
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                String currentType = "";
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    while (true) {
                        if (receiveString == null || !receiveString.contains(Constatnts.AC_SEPARATOR))
                            break;
                        else {
                            currentType = receiveString.split(";")[1];
                            receiveString = bufferedReader.readLine();
                        }
                    }
                    putAcInArray(currentType, receiveString);
                }
                inputStream.close();
            } else {
                Log.d("HHH", "File not Found!");
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
    }

    public static void getCardioFromFile() {
        try {

            InputStream inputStream = sContext.openFileInput(Constatnts.CARDIO_FILE_NAME);
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                while((receiveString = bufferedReader.readLine()) != null) {
                    String[] cardio = receiveString.split(";");
                    sCardios.put(cardio[0], cardio[1]);
                }
                inputStream.close();

            } else {
                Log.d("HHH", "File not Found!");
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
    }

    private static void putAcInArray(String currentType, String receiveString) {
        if (receiveString == null)
            return;
        if (currentType.equals(Constatnts.AC_CLUB))
            acClub.add(receiveString);
        if (currentType.equals(Constatnts.AC_TRAINER))
            acTrainer.add(receiveString);
        if (currentType.equals(Constatnts.AC_MUSCLE))
            acMuscle.add(receiveString);
        if (currentType.equals(Constatnts.AC_NAME))
            acExName.add(receiveString);
        if (currentType.equals(Constatnts.AC_DESCRIPTION))
            acExDescription.add(receiveString);
    }

    public static void addToAcTrainer(String value) {
        if (!acTrainer.contains(value))
            acTrainer.add(value);
    }

    public static void addToAcClub(String value) {
        if (!acClub.contains(value))
            acClub.add(value);
    }

    public static void addToAcMuscle(String value) {
        if (!acMuscle.contains(value))
            acMuscle.add(value);
    }

    public static void addToAcExName(String value) {
        if (!acExName.contains(value))
            acExName.add(value);
    }

    public static void addToAcDescription(String value) {
        if (!acExDescription.contains(value))
            acExDescription.add(value);
    }

    public static IPlan getPlanById(String ID) {
        Iterator it = sPlans.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if(((IPlan)pair.getValue()).planId().equals(ID))
                return ((IPlan)pair.getValue());
        }
        return null;
    }

    public static ArrayList<String> getAcExDescription() {
        return acExDescription;
    }

    public static ArrayList<String> getAcExName() {
        return acExName;
    }

    public static ArrayList<String> getAcMuscle() {
        return acMuscle;
    }

    public static ArrayList<String> getAcClub() {
        return acClub;
    }

    public static ArrayList<String> getAcTrainer() {
        return acTrainer;
    }

    public static HashMap<String, String> getCardios() {
        return sCardios;
    }

}
