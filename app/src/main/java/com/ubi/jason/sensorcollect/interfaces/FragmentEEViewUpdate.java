package com.ubi.jason.sensorcollect.interfaces;

/**
 * Created by jason on 14-Dec-15.
 */
public interface FragmentEEViewUpdate {

    void updateViewEE(double kjoule);

    void updateViewTime(int timestamp);

    void updateViewSensor(float[] sensor);

}
