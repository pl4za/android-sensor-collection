package com.ubi.jason.sensorcollect.delegators;

/**
 * Created by jason on 25-Feb-16.
 */
public class StatusCtrl {

    private boolean INFO_COMPLETE = false;
    /*
    Class delegators
    */
    private static SettingsCtrl settingsCtrl = SettingsCtrl.getInstance();

    private static final StatusCtrl INSTANCE = new StatusCtrl();

    private StatusCtrl() {
    }

    public static StatusCtrl getInstance() {
        return INSTANCE;
    }

    public boolean isCALIBRATED() {
        return settingsCtrl.isCalibrated();
    }

    public boolean isINFO_COMPLETE() {
        return INFO_COMPLETE;
    }

    public void setINFO_COMPLETE(boolean INFO_COMPLETE) {
        this.INFO_COMPLETE = INFO_COMPLETE;
    }
}
