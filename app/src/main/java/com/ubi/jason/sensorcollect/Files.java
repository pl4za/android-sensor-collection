package com.ubi.jason.sensorcollect;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by jason on 17-Nov-15.
 */
public class Files {

    private static final String TAG = "FILES";
    Context context;
    private static BufferedWriter fileBuff;
    private static File mainFolder;
    private static File subFolder;
    private static File valuesFile;

    public Files(Context context) {
        this.context = context;
        boolean externalStorage = isExternalStorageWritable();
        CreateFolder(externalStorage);
    }

    //TODO: check free space
    private void CreateFolder(boolean externalStorage) {
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
        // One directory per sensor (only available sensors)
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
        // One file per day
        valuesFile = new File(subFolder, today.toString() + ".csv");
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

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public void writeSensorData(String EEactKcal) {
        try {
            //Log.i(TAG, event.sensor.getName().replace(" ", "_").toString());
            fileBuff = new BufferedWriter(new FileWriter(valuesFile, true));
            fileBuff.append(EEactKcal + "," + System.currentTimeMillis());
            fileBuff.newLine();
            fileBuff.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
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
}
