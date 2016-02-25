package com.ubi.jason.sensorcollect.delegators;

import android.content.Context;
import android.content.SharedPreferences;

import com.ubi.jason.sensorcollect.R;

/**
 * Created by jason on 25-Feb-16.
 */
public class SettingsCtrl {

    private Context context;

    private static final SettingsCtrl INSTANCE = new SettingsCtrl();

    private SettingsCtrl() {

    }

    public static SettingsCtrl getInstance() {
        return INSTANCE;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public boolean isCalibrated() {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getResources().getString(R.string.offset), 0);
        return (sharedPref.contains("x") && sharedPref.contains("y") && sharedPref.contains("z"));
    }

    public void setCalibrationValues(float[] sensorValues) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getResources().getString(R.string.offset), 0);
        float[] offset = new float[3];
        offset[0] = sensorValues[0] + (float) 0.10;
        offset[1] = sensorValues[1] + (float) 0.10;
        offset[2] = sensorValues[2] - (float) 9.81;
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat("x", offset[0]);
        editor.putFloat("y", offset[1]);
        editor.putFloat("z", offset[2]);
        editor.commit();
    }
}
