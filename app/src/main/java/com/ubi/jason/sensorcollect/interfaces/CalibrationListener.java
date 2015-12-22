package com.ubi.jason.sensorcollect.interfaces;

/**
 * Created by jason on 17-Dec-15.
 */
public interface CalibrationListener {

    void calibrationUpdate();

    void calibrationReset();

    void calibrationStop();

    void calibrationDone();

}
