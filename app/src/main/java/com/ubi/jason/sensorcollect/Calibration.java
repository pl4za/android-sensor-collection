package com.ubi.jason.sensorcollect;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.ubi.jason.sensorcollect.helper.Config;
import com.ubi.jason.sensorcollect.interfaces.CalibrationControl;
import com.ubi.jason.sensorcollect.interfaces.CalibrationListener;
import com.ubi.jason.sensorcollect.interfaces.SensorListener;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by jason on 17-Dec-15.
 */
public class Calibration implements SensorListener, CalibrationControl {

    private static final String TAG = "CALIBRATION";
    private static int timeToCalibrate = Config.TIME_TO_CALIBRATE;
    private static float[] currentValues;
    private static float[] offset;
    private static Context context;
    private static Timer calibrateTime;
    private static CalibrationListener calibrationListener;
    private static Sensors sensors;
    private static Map<String, Sensor> sensorMap;

    public Calibration(Context context, CalibrationListener listener) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensors = new Sensors(sensorManager);
        this.context = context;
        sensorMap = sensors.getAvailableSensors();
        calibrationListener = listener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.i(TAG, "x: " + String.valueOf(event.values[0]) + " y: " + String.valueOf(event.values[1]) + " z: " + String.valueOf(event.values[2]));
        currentValues = new float[]{event.values[0], event.values[1], event.values[2]};
        if ((event.values[0] < 0.5 && event.values[0] > -0.5) && //X
                (event.values[1] < 0.5 && event.values[1] > -0.5) && //Y
                (event.values[2] < 10.5 && event.values[2] > 4)) { //Z TODO: Tablet deco reports very bad values. DEFAULT: 8.5
            if (calibrateTime == null) {
                calibrateTime = new Timer();
                calibrateTime.schedule(new calibrateTime(), 0, 1000); //Countdowns from 5 and resets if device moves
                calibrationListener.calibrationUpdate();
            }
        } else {
            if (calibrateTime != null) {
                calibrationListener.calibrationReset();
                calibrateTime.cancel();
                calibrateTime.purge();
                calibrateTime = null;
                timeToCalibrate = Config.TIME_TO_CALIBRATE;
            }
        }
    }

    @Override
    public void onError(String errorMessage) {

    }

    @Override
    public void startCalibrate() {
        // Ask user to place on surface
        offset = null;
        sensors.addOnChangedListener(this);
        sensors.start(sensorMap);
    }

    @Override
    public void stopCalibrate() {
        // Ask user to place on surface
        offset = null;
        sensors.stop();
        if (calibrateTime != null) {
            calibrateTime.cancel();
            calibrateTime.purge();
            calibrateTime = null;
        }
        timeToCalibrate = Config.TIME_TO_CALIBRATE;
    }

    private void setCalibrationValues() {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getResources().getString(R.string.offset), 0);
        offset = new float[3];
        offset[0] = currentValues[0] + (float) 0.10;
        offset[1] = currentValues[1] + (float) 0.10;
        offset[2] = currentValues[2] - (float) 9.81;
        Log.i(TAG, "Offset from sensor: " + offset[0] + ", " + offset[1] + ", " + offset[2]);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat("x", offset[0]);
        editor.putFloat("y", offset[1]);
        editor.putFloat("z", offset[2]);
        editor.commit();
    }

    class calibrateTime extends TimerTask {
        public void run() {
            if (--timeToCalibrate == 0) {
                sensors.stop();
                if (calibrateTime != null) {
                    setCalibrationValues();
                    calibrateTime.cancel();
                    calibrateTime.purge();
                    calibrateTime = null;
                    timeToCalibrate = Config.TIME_TO_CALIBRATE;
                }
                Handler refresh = new Handler(Looper.getMainLooper());
                refresh.post(new Runnable() {
                    public void run() {
                        calibrationListener.calibrationDone();
                    }
                });
            }
        }
    }
}
