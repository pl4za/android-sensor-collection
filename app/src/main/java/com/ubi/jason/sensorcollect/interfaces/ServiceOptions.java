package com.ubi.jason.sensorcollect.interfaces;

/**
 * Created by jason on 17-Nov-15.
 */
public interface ServiceOptions {

    /**
     * Called to start or pause the service
     */
    void startOrPause();

    /**
     * Called to stop the service
     */
    void stop();

    /**
     * Called to retrieve system status
     * SERVICE_STATUS_RUNNING = 0;
     * SERVICE_STATUS_PAUSED = 1;
     * SERVICE_STATUS_STOP = 2;
     *
     * @return The tracking status (true, false)
     */
    int getStatus();

    void error(String error);

}
