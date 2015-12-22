package com.ubi.jason.sensorcollect.helper;

/**
 * Created by jasoncosta on 12/1/2015.
 */
public class Config {
    // File upload url (replace the ip with your server address)
    public static final String FILE_UPLOAD_URL = "http://192.168.42.11/sensorsDataTese/fileUpload.php";
    //public static final String FILE_UPLOAD_URL = "http://192.168.209.199/sensorsDataTese/fileUpload.php";
    //public static final String FILE_UPLOAD_URL = "http://193.126.80.249/sensorsDataTese/fileUpload.php";

    //Service
    public static final int SERVICE_STATUS_RUNNING = 0;
    public static final int SERVICE_STATUS_PAUSED = 1;
    public static final int SERVICE_STATUS_STOP = 2;

    public static final int TIME_TO_CALIBRATE = 5;

}
