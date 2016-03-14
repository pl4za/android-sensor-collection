package com.ubi.jason.sensorcollect.delegators;

import com.ubi.jason.sensorcollect.interfaces.FragmentView;

/**
 * Created by jason on 25-Feb-16.
 */
public class ViewCtrl implements FragmentView {

    private FragmentView fragmentView;

    private static final ViewCtrl INSTANCE = new ViewCtrl();

    private ViewCtrl() {

    }

    public static ViewCtrl getInstance() {
        return INSTANCE;
    }

    public void setView(FragmentView fragmentView) {
        this.fragmentView = fragmentView;
    }

    @Override
    public void updateViewTime(int timestamp) {
        fragmentView.updateViewTime(timestamp);
    }

    @Override
    public void updateStartToggleStatus(boolean status) {
        fragmentView.updateStartToggleStatus(status);
    }
}
