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
    private TextView tvTime;
    private View view;
    private static Context context;

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
        tvTime = (TextView) view.findViewById(R.id.tvTime);
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
            ((ServiceControl)getActivity()).startOrPause();
        } else if (id == R.id.btnStop) {
            ((ServiceControl)getActivity()).stop();
            tglTrack.setChecked(false);
        } else if (id == R.id.btnCalibrate) {
            ((ServiceControl)getActivity()).openFragmentCalibrate();
        }
    }

    @Override
    public void updateViewTime(int timestamp) {
        tvTime.setText(String.format("%02d", (timestamp / 3600)) + ":" + String.format("%02d", ((timestamp % 3600) / 60)) + ":" + String.format("%02d", (timestamp % 60)));
    }

    @Override
    public void updateStartToggleStatus(boolean status) {
        tglTrack.setChecked(status);
    }


}
