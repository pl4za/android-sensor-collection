package com.ubi.jason.sensorcollect;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.ubi.jason.sensorcollect.interfaces.SensorListener;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by jason on 16-Nov-15.
 */
public class Sensors implements SensorEventListener {

    private final Set<SensorListener> mListeners;
    private static final String TAG = "SENSOR_MAN";
    private static SensorManager mSensorManager;

    public Sensors(SensorManager sensorManager) {
        mSensorManager = sensorManager;
        mListeners = new LinkedHashSet<>();
    }

    public void addOnChangedListener(SensorListener listener) {
        mListeners.add(listener);
    }

    public Map<String, Sensor> getAvailableSensors() {
        Map<String, Sensor> sensorMap = new HashMap<String, Sensor>();
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            List<Sensor> sensorList = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
            for (Sensor s : sensorList) {
                Log.i(TAG, "Sensor: " + s.getVendor()+" Maximum range: "+s.getMaximumRange());
                sensorMap.put("sensorAcce", s);
            }
        }
        return sensorMap;
    }

    public void start(Map<String, Sensor> sensorMap) {
        Log.i(TAG, "Registering listeners");
        if (sensorMap.isEmpty()) {
            notifySensorError();
        } else {
            for (Map.Entry<String, Sensor> entry : sensorMap.entrySet()) {
                mSensorManager.registerListener(this, entry.getValue(), SensorManager.SENSOR_DELAY_FASTEST);
            }
        }
    }

    public void stop() {
        Log.i(TAG, "Unregistering listeners");
        if (mSensorManager!=null) {
            mSensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        notifySensorChanged(event);
    }

    private void notifySensorChanged(SensorEvent event) {
        for (SensorListener listener : mListeners) {
            listener.onSensorChanged(event);
        }
    }

    private void notifySensorError() {
        for (SensorListener listener : mListeners) {
            listener.onError("No sensors are available");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
