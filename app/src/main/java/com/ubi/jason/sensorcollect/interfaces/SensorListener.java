package com.ubi.jason.sensorcollect.interfaces;

import android.hardware.SensorEvent;

/**
 * Created by jason on 17-Nov-15.
 */
public interface SensorListener {
    /**
     * Called when the sensors detect change
     * @param event the array of events collected
     */
    void onSensorChanged(SensorEvent event);

    /**
     * Called when the sensor data collection fails.
     * @param errorMessage the cause of the error
     */
    void onError(String errorMessage);
}
