package com.ubi.jason.sensorcollect.interfaces;

/**
 * Created by jasoncosta on 12/11/2015.
 */
public interface ServiceListener {

    void updateEE(Double kjoule);

    void updateSensorValues(float[] sensor);

    void updateTime(int timestamp);

}
