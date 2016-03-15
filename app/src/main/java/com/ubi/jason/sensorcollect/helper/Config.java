package com.ubi.jason.sensorcollect.helper;

/**
 * Created by jasoncosta on 12/1/2015.
 */
public class Config {
    // File upload url (replace the ip with your server address)
    // LOCAL
    //public static final String FILE_UPLOAD_URL = "http://192.168.209.36/sensorsDataTese/fileUpload.php";
    // SOCIA
    public static final String FILE_UPLOAD_URL = "http://193.136.67.246/~jason/sensorsDataTese/fileUpload.php";

    //Service
    public static final int SERVICE_STATUS_RUNNING = 0;
    public static final int SERVICE_STATUS_PAUSED = 1;
    public static final int SERVICE_STATUS_STOP = 2;

    public static final int MIN_FREE_SPACE = 75;

    public static final int TIME_TO_CALIBRATE = 5;

}
