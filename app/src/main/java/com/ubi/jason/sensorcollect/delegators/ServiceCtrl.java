package com.ubi.jason.sensorcollect.delegators;

import com.ubi.jason.sensorcollect.interfaces.ServiceOptions;

/**
 * Created by jason on 25-Feb-16.
 */
public class ServiceCtrl implements ServiceOptions {

    private ServiceOptions service;
    /*
    Class delegators
   */
    private static ViewCtrl viewCtrl = ViewCtrl.getInstance();
    private StatusCtrl statusCtrl = StatusCtrl.getInstance();
    private ActivityCtrl activityCtrl = ActivityCtrl.getInstance();

    private static final ServiceCtrl INSTANCE = new ServiceCtrl();

    private ServiceCtrl() {

    }

    public static ServiceCtrl getInstance() {
        return INSTANCE;
    }

    public void setService(ServiceOptions service) {
        this.service = service;
    }

    @Override
    public void startOrPause() {
        if (statusCtrl.isINFO_COMPLETE()) {
            if (statusCtrl.isCALIBRATED()) {
                if (service != null) {
                    service.startOrPause();
                }
            } else {
                activityCtrl.showToast("É necessário calibrar");
                activityCtrl.calibrate();
                viewCtrl.updateStartToggleStatus(false);
            }
        } else {
            activityCtrl.showToast("Por favor complete os seus dados");
            activityCtrl.openDrawer();
            viewCtrl.updateStartToggleStatus(false);
        }
    }

    @Override
    public void stop() {
        if (service != null) {
            service.stop();
        }
    }

    @Override
    public int getStatus() {
        if (service == null) {
            return 2;
        } else {
            return service.getStatus();
        }
    }

    @Override
    public void error(String error) {
        //fragViewUpdate.updateStartToggleStatus(false);
    }
}
