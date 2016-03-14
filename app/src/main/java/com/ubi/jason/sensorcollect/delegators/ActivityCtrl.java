package com.ubi.jason.sensorcollect.delegators;

import com.ubi.jason.sensorcollect.interfaces.ActivityOptions;

/**
 * Created by jason on 25-Feb-16.
 */
public class ActivityCtrl implements ActivityOptions {

    private ActivityOptions activityOptions;

    private static final ActivityCtrl INSTANCE = new ActivityCtrl();

    private ActivityCtrl() {

    }

    public static ActivityCtrl getInstance() {
        return INSTANCE;
    }

    public void setActivity(ActivityOptions activityOptions) {
        this.activityOptions = activityOptions;
    }

    @Override
    public void showToast(String text) {
        activityOptions.showToast(text);
    }

    @Override
    public void calibrate() {
        activityOptions.calibrate();
    }

    @Override
    public void openDrawer() {
        activityOptions.openDrawer();
    }
}
