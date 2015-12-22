package com.ubi.jason.sensorcollect;

import android.util.Log;

import com.ubi.jason.sensorcollect.interfaces.EECalcListener;

import java.util.HashMap;

/**
 * Created by jasoncosta on 12/11/2015.
 */
public class EECalc {

    private static final String TAG = "EE_CALC";
    double aL;
    double bL;
    double sensorSumXY;
    double sensorSumZ;
    double EEact;
    int nValues = 0;
    EECalcListener sensorsService;
    float[] priorValues;

    public EECalc(SensorsService sensorsService, HashMap<String, String> userInfo) {
        this.sensorsService = sensorsService;
        calculateParameters(userInfo);
    }

    /*
    * time smoothing constant for low-pass filter
    * 0 ≤ alpha ≤ 1 ; a smaller value basically means more smoothing
    * See: http://en.wikipedia.org/wiki/Low-pass_filter#Discrete-time_realization
    */
    static final float ALPHA = 0.15f;
    /**
     * @see http://en.wikipedia.org/wiki/Low-pass_filter#Algorithmic_implementation
     * @see http://developer.android.com/reference/android/hardware/SensorEvent.html#values
     */
    private float[] lowPass( float[] prior, float[] after ) {
        if ( after == null ) return prior;
        for ( int i=0; i<prior.length; i++ ) {
            after[i] = after[i] + ALPHA * (prior[i] - after[i]);
        }
        return after;
    }


    public void calculateParameters(HashMap<String, String> userInfo) {
        //TODO: update parameters on change
        // aL = (5.78*pesoKG+11.95*alturaCM+6.98*25-2001)/1000
        // bL = (5.96*pesoKG+349.5)/1000
        float weight = Float.parseFloat(userInfo.get("weight"));
        float height = Float.parseFloat(userInfo.get("height"));
        aL = (5.78*weight+11.95*height+6.98*25-2001)/1000;
        bL = (5.96*weight+349.5)/1000;
    }

    public void addValue(float[] sensorValues) {
        float[] filterValues = sensorValues;
        if (priorValues!=null) {
            filterValues = lowPass(priorValues, sensorValues);
        }
        // Raiz do somatório do quadrado de X e Y.
        nValues++;
        sensorSumXY = sensorSumXY+Math.sqrt(Math.pow(filterValues[0], 2)+Math.pow(filterValues[1], 2));
        sensorSumZ = sensorSumZ+filterValues[2];
        //Log.i(TAG, nValues+"-> x: "+sensorValues[0]+" y: "+sensorValues[1]+" z: "+sensorValues[2]);
        priorValues = sensorValues;
    }

    public void reset() {
        EEact = 0;
        sensorSumXY = 0;
        sensorSumZ = 0;
        nValues = 0;
    }

    public void calcEE() {
        // EEact = (aL*medSensorSumXY)+(bL*medSensorSumZ)
        double medSensorSumXY = sensorSumXY/nValues;
        double medSensorSumZ = sensorSumZ/nValues;
        EEact = EEact+(aL*medSensorSumXY)+(bL*medSensorSumZ);
        Log.i(TAG, "\nsensorSumXY: "+sensorSumXY+" sensorSumZ: "+sensorSumZ+" aL: "+aL+" bL: "+bL);
        Log.i(TAG, "\nEEact: "+(EEact/4.184)+" medSensorSumXY: "+medSensorSumXY+" medSensorSumZ: "+medSensorSumZ);
        sensorsService.EECalcComplete(EEact);
        sensorSumXY = 0;
        sensorSumZ = 0;
        nValues = 0;
    }
}
