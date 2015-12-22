package com.ubi.jason.sensorcollect.interfaces;

/**
 * Created by jason on 17-Nov-15.
 */
public interface ServiceControl {
    /**
     * Called to start the service
     */
    void start();

    /**
     * Called to stop the service
     */
    void stop();

    /**
     * Called to stop the service
     */
    void pause();

    /**
     * Called to retrieve system status
     * SERVICE_STATUS_RUNNING = 0;
     * SERVICE_STATUS_PAUSED = 1;
     * SERVICE_STATUS_STOP = 2;
     * @return The tracking status (true, false)
     */
    int getStatus();

    void openFragmentCalibrate();
}