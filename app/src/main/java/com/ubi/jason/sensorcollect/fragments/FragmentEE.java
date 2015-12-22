package com.ubi.jason.sensorcollect.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ubi.jason.sensorcollect.MainActivity;
import com.ubi.jason.sensorcollect.R;
import com.ubi.jason.sensorcollect.helper.Config;
import com.ubi.jason.sensorcollect.interfaces.FragmentEEViewUpdate;
import com.ubi.jason.sensorcollect.interfaces.ServiceControl;

import java.text.DecimalFormat;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentEE extends Fragment implements View.OnClickListener, FragmentEEViewUpdate {

    private static final String TAG = "MAIN_FRAG";
    private ToggleButton tglTrack;
    private Button btnStop, btnCalibrate;
    private TextView tvKcal, tvKjoule, tvTime, tvSensor;
    private View view;
    private static Context context;
    private static boolean START_COMMAND = false;

    public FragmentEE() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        view = inflater.inflate(R.layout.fragment_main, container, false);
        context = getContext();
        setButtonListeners();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).registerFragmentListener(this);
        int status = ((MainActivity)getActivity()).getStatus();
        tglTrack.setChecked(status == Config.SERVICE_STATUS_RUNNING);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_settings);
        item.setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    private void setButtonListeners() {
        tglTrack = (ToggleButton) view.findViewById(R.id.tglTrack);
        tvKcal = (TextView) view.findViewById(R.id.tvKcal);
        tvKjoule = (TextView) view.findViewById(R.id.tvJoule);
        tvTime = (TextView) view.findViewById(R.id.tvTime);
        tvSensor = (TextView) view.findViewById(R.id.tvSensor);
        btnStop = (Button) view.findViewById(R.id.btnStop);
        btnCalibrate = (Button) view.findViewById(R.id.btnCalibrate);
        tglTrack.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnCalibrate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tglTrack) {
            if (!tglTrack.isChecked()) {
                ((ServiceControl)getActivity()).pause();
            } else {
                if (MainActivity.isInfoComplete()) {
                    START_COMMAND = true;
                    ((ServiceControl)getActivity()).start();
                } else {
                    tglTrack.setChecked(false);
                    Toast.makeText(context, "Por favor complete os seus dados",
                            Toast.LENGTH_LONG).show();
                }
            }
        } else if (id == R.id.btnStop) {
            ((ServiceControl)getActivity()).stop();
            tglTrack.setChecked(false);
        } else if (id == R.id.btnCalibrate) {
            ((ServiceControl)getActivity()).openFragmentCalibrate();
        }
    }

    @Override
    public void updateViewEE(double kjoule) {
        DecimalFormat numberFormat = new DecimalFormat("#.##");
        tvKcal.setText(numberFormat.format(kjoule / 4.184) + " kcal");
        tvKjoule.setText(numberFormat.format(kjoule) + " kJ");
    }

    @Override
    public void updateViewSensor(float[] sensor) {
        DecimalFormat numberFormat = new DecimalFormat("#.##");
        tvSensor.setText("X: "+numberFormat.format(sensor[0])+" Y: "+numberFormat.format(sensor[1])+" Z: "+numberFormat.format(sensor[2]));
    }

    @Override
    public void updateViewTime(int timestamp) {
        tvTime.setText(String.format("%02d", (timestamp / 3600)) + ":" + String.format("%02d", ((timestamp % 3600) / 60)) + ":" + String.format("%02d", (timestamp % 60)));
    }

}
