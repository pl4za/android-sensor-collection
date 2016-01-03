package com.ubi.jason.sensorcollect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by jasoncosta on 1/3/2016.
 */
public class NetworkReceiver extends BroadcastReceiver {

    public NetworkReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        new DataUpload(context);
    }
}
