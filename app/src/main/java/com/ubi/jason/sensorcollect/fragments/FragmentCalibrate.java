package com.ubi.jason.sensorcollect.fragments;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ubi.jason.sensorcollect.Calibration;
import com.ubi.jason.sensorcollect.R;
import com.ubi.jason.sensorcollect.interfaces.CalibrationControl;
import com.ubi.jason.sensorcollect.interfaces.CalibrationListener;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentCalibrate extends Fragment implements CalibrationListener {

    private static final String TAG = "FRAG_CALIBRATE";
    View view;
    ValueAnimator anim;
    CalibrationControl calibrationControl;

    public FragmentCalibrate() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_calibrate, container, false);
        createAnimation();
        calibrationControl = new Calibration(getActivity(), this);
        calibrationControl.startCalibrate();
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        calibrationControl.stopCalibrate();
    }

    @Override
    public void calibrationUpdate() {
        if (anim != null) {
            anim.start();
        }
    }

    @Override
    public void calibrationReset() {
        if (anim != null) {
            anim.reverse();
        }
    }

    @Override
    public void calibrationStop() {
        anim.cancel();
    }

    @Override
    public void calibrationDone() {
        Toast.makeText(getActivity(), "Calibrado !",
                Toast.LENGTH_SHORT).show();
        getActivity().onBackPressed();
    }

    private void createAnimation() {
        final float[] from = new float[3],
                to = new float[3];

        Color.colorToHSV(Color.parseColor("#ffffff"), from);
        Color.colorToHSV(Color.parseColor("#76EE00"), to);

        anim = ValueAnimator.ofFloat(0, 1);   // animate from 0 to 1
        anim.setDuration(5000);                              // for 300 ms

        final float[] hsv = new float[3];                  // transition color
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // Transition along each axis of HSV (hue, saturation, value)
                hsv[0] = from[0] + (to[0] - from[0]) * animation.getAnimatedFraction();
                hsv[1] = from[1] + (to[1] - from[1]) * animation.getAnimatedFraction();
                hsv[2] = from[2] + (to[2] - from[2]) * animation.getAnimatedFraction();

                view.setBackgroundColor(Color.HSVToColor(hsv));
            }
        });
    }

}
