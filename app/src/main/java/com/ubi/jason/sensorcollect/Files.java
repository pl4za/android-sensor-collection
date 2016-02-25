package com.ubi.jason.sensorcollect;

import android.content.Context;
import android.hardware.Sensor;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import com.ubi.jason.sensorcollect.helper.Config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jason on 17-Nov-15.
 */
public class Files {

    private static final String TAG = "FILES";
    Context context;
    private static BufferedWriter fileBuff;
    private static File mainFolder;
    private static File subFolder;
    private static Map <Integer, File> filesMap;
    private static boolean externalStorage;
    private Map<String, Sensor> sensorMap;

    public Files(Context context) {
        this.context = context;
        externalStorage = isExternalStorageWritable();
        createFolder();
    }

    public Files(Context context, Map<String, Sensor> sensorMap) {
        this.context = context;
        externalStorage = isExternalStorageWritable();
        this.sensorMap = sensorMap;
        createFolder();
    }

    //TODO: check free space
    private void createFolder() {
        Log.i(TAG, "Using external card: " + externalStorage);
        if (externalStorage) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mainFolder = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOCUMENTS), "EEmonitor");
            } else {
                mainFolder = new File(Environment.getExternalStorageDirectory() + "/Documents", "EEmonitor");
            }
        } else {
            mainFolder = new File(context.getFilesDir(), "EEmonitor");
        }
        if (!mainFolder.isDirectory()) {
            Log.i(TAG, "Directory doesn't exist");
            if (!mainFolder.mkdirs()) {
                Log.i(TAG, "Directory not created");
            } else {
                Log.i(TAG, "Directory created: " + mainFolder.getAbsolutePath());
                createSubFolders();
            }
        } else {
            Log.i(TAG, "Directory already exists");
            createSubFolders();
        }
    }

    private void createSubFolders() {
        subFolder = new File(mainFolder, "values");
        if (!subFolder.isDirectory()) {
            if (!subFolder.mkdirs()) {
                Log.i(TAG, subFolder + " Directory not created");
            } else {
                Log.i(TAG, subFolder + " Directory created");
            }
        } else {
            Log.i(TAG, "Using subfolder: " + subFolder.getAbsolutePath());
        }
        CreateFile();
    }

    private void CreateFile() {
        Date date = Calendar.getInstance().getTime();
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String today = formatter.format(date);
        // One file per sensor per day
        filesMap = new HashMap<>();
        for (Map.Entry<String, Sensor> s : sensorMap.entrySet()) {
            File valuesFile = new File(subFolder, s.getKey()+"_"+today.toString() + ".csv");
            filesMap.put(s.getValue().getType(), valuesFile);
            if (!valuesFile.isFile()) {
                Log.i(TAG, "Creating file: " + valuesFile + " in " + subFolder);
                try {
                    if (!valuesFile.createNewFile()) {
                        Log.i(TAG, valuesFile + " file not created");
                    } else {
                        Log.i(TAG, valuesFile + " file created");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Log.i(TAG, "Using file: " + valuesFile.getAbsolutePath());
                // We dont create the header because it already exists
            }
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public void writeSensorData(int sensorType, float[] event) {
        try {
            //Log.i(TAG, event.sensor.getName().replace(" ", "_").toString());
            fileBuff = new BufferedWriter(new FileWriter(filesMap.get(sensorType), true));
            for (Float f : event) {
                fileBuff.append(Float.toString(f));
            }
            fileBuff.append(String.valueOf(System.currentTimeMillis()));
            fileBuff.newLine();
            fileBuff.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeFile() {
        Log.i(TAG, "Closing files");
        if (fileBuff != null) {
            try {
                fileBuff.close();
            } catch (IOException e) {

            }
        }
    }

    public boolean hasFreeSpace() {
        long blocksAvailable = 0;
        long mbAvailable = 0;
        StatFs stat;
        if (externalStorage) {
            stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        } else {
            stat = new StatFs(Environment.getDataDirectory().getPath());
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            blocksAvailable = stat.getAvailableBlocksLong();
            mbAvailable = (blocksAvailable * stat.getBlockSizeLong()) / 1048576;
        } else {
            blocksAvailable = stat.getAvailableBlocks();
            mbAvailable = (blocksAvailable * stat.getBlockSize()) / 1048576;
        }
        System.out.println("MB: " + mbAvailable);
        return mbAvailable >= Config.MIN_FREE_SPACE;
    }
}
