package com.ubi.jason.sensorcollect;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.ubi.jason.sensorcollect.delegators.SettingsCtrl;
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
    private static Timer calibrateTime;
    private static Sensors sensors;
    private static Map<String, Sensor> sensorMap;
    private static CalibrationListener calibrationListener;
    private static float hOffset = 2f;
    private static float maxVOffset = 12f;
    private static float minVOffset = 6f;
    /*
    Class delegators
    */
    private static SettingsCtrl settingsCtrl = SettingsCtrl.getInstance();

    public Calibration(Context context, CalibrationListener calibrationListener) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensors = new Sensors(sensorManager);
        sensorMap = sensors.getAvailableSensors();
        this.calibrationListener = calibrationListener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //Log.i(TAG, "x: " + String.valueOf(event.values[0]) + " y: " + String.valueOf(event.values[1]) + " z: " + String.valueOf(event.values[2]));
        currentValues = new float[]{event.values[0], event.values[1], event.values[2]};
        if ((event.values[0] < hOffset && event.values[0] > -hOffset) && //X
                (event.values[1] < hOffset && event.values[1] > -hOffset) && //Y
                (event.values[2] < maxVOffset && event.values[2] > minVOffset)) { //Z TODO: Tablet deco reports very bad values. DEFAULT: 8.5
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
        sensors.addOnChangedListener(this);
        sensors.start(sensorMap);
    }

    @Override
    public void stopCalibrate() {
        sensors.stop();
        if (calibrateTime != null) {
            calibrateTime.cancel();
            calibrateTime.purge();
            calibrateTime = null;
        }
        timeToCalibrate = Config.TIME_TO_CALIBRATE;
    }

    class calibrateTime extends TimerTask {
        public void run() {
            if (--timeToCalibrate == 0) {
                sensors.stop();
                if (calibrateTime != null) {
                    settingsCtrl.setCalibrationValues(currentValues);
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
