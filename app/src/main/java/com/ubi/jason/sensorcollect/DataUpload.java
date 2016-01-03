package com.ubi.jason.sensorcollect;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.ubi.jason.sensorcollect.helper.Config;
import com.ubi.jason.sensorcollect.helper.MultiPartUploader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by jasoncosta on 12/1/2015.
 */
public class DataUpload {

    private static Context context;
    private static final String TAG = "UPLOAD_RECEIVER";
    // Notification
    private static NotificationManager mNotifyManager;
    private static Notification notification;
    private static NotificationCompat.Builder mBuilder;
    //
    private static long totalSize = 0;
    private static final ArrayList<File> filesToSend = new ArrayList<>();
    private static final HashMap<String, String> userInfoToSend = new HashMap<>();
    private static int i = 0;
    private static String android_id;
    private static boolean ERROR = false;
    private static int progress = 0;
    private static int newProgress = 0;

    public DataUpload(Context context) {
        this.context = context;
        instantiate();
    }

    private void instantiate() {
        if (isNetworkAvailable()) { // Wifi only
            setFilesToSend();
            setUserInfoToSend();
            if (!filesToSend.isEmpty()) {
                Log.i(TAG, "Sending collected data");
                android_id = Settings.Secure.getString(context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                createNotificationProgress();
                new UploadFileToServer().execute();
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean wifi = false;
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            wifi = activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI;
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected() && wifi;
    }

    public void setFilesToSend() {
        i = 0;
        filesToSend.clear();
        File sensorsFolder = getMainFolder();
        File[] subFolders = sensorsFolder.listFiles();
        for (File folder : subFolders) {
            if (folder.isDirectory()) {
                File[] files = folder.listFiles();
                for (File file : files) {
                    if (file.isFile()) {
                        Log.i(TAG, file.getAbsolutePath() + ", size: " + file.length());
                        if (file.length() > 0) {
                            filesToSend.add(file);
                        } else {
                            file.delete();
                        }
                    }
                }
            }
        }
    }

    public void setUserInfoToSend() {
        userInfoToSend.clear();
        String userInfoKeys[] = context.getResources().getStringArray(R.array.user_info_keys);
        SharedPreferences sharedPref = context.getSharedPreferences(context.getResources().getString(R.string.preference), 0);
        for (String a : userInfoKeys) {
            Log.i(TAG, "Got: " + sharedPref.getString(a, ""));
            userInfoToSend.put(a, sharedPref.getString(a, ""));
        }
    }

    public File getMainFolder() {
        if (isExternalStorageWritable()) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                return new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOCUMENTS), "EEmonitor");
            } else {
                return new File(Environment.getExternalStorageDirectory() + "/Documents", "EEmonitor");
            }
        } else {
            return new File(context.getFilesDir(), "EEmonitor");
        }
    }

    /**
     * Uploading the file to server
     */
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {

        Timer updateTransfered;
        long progress = 0;

        @Override
        protected void onPreExecute() {
            updateNotificationProgress(0);
            //Log.i(TAG, "Tamanho em Bytes: " + String.valueOf(filesToSend.get(i).length()));
            //Log.i(TAG, "Tamanho em KB: " + (filesToSend.get(i).length() / 1024) + "KB");
            updateNotification("Ficheiro " + String.valueOf(i + 1) + " de " + filesToSend.size() + ". Tamanho: " + (filesToSend.get(i).length() / 1024) + "KB");
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // updating progress bar value
            updateNotificationProgress(progress[0]);
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Config.FILE_UPLOAD_URL);
            try {
                MultiPartUploader entity = new MultiPartUploader(new multiPartUploaderProgress(this));
                File sourceFile = new File(filesToSend.get(i).getAbsolutePath());
                // Adding file data to http body
                entity.addPart("file", new FileBody(sourceFile));
                String jsonArray = buildJson();
                // Extra parameters if you want to pass to server
                Log.i(TAG, jsonArray);
                entity.addPart("json", new StringBody(jsonArray));
                totalSize = entity.getContentLength();
                //Log.i(TAG, "Upload size: " + totalSize);
                httppost.setEntity(entity);
                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                    Log.i(TAG, "message from server: " + responseString);
                } else {
                    Log.e(TAG, "Error. Code: " + statusCode);
                    responseString = "error";
                    notificationError();
                }
            } catch (ClientProtocolException e) {
                Log.e(TAG, "Error. Exception: " + e.toString());
                responseString = "error";
                notificationError();
            } catch (IOException e) {
                Log.e(TAG, "Error. Exception: " + e.toString());
                responseString = "error";
                notificationError();
            }
            return responseString;

        }

        private String buildJson() {
            JSONObject mainObj = new JSONObject();
            try {
                // Device info
                JSONObject jDevice = new JSONObject();
                jDevice.put("device_id", android_id);
                jDevice.put("sensor_type", "accel");
                // Build main json
                mainObj.put("deviceInfo", jDevice);
                // User info
                if (!userInfoToSend.isEmpty()) {
                    JSONObject jUser = new JSONObject();
                    for (Map.Entry<String, String> entry : userInfoToSend.entrySet()) {
                        jUser.put(entry.getKey(), entry.getValue());
                    }
                    mainObj.put("userInfo", jUser);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return mainObj.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "Response from server: " + result);
            super.onPostExecute(result);
            JSONObject jsonObject = null;
            String parsedError = "true";
            try {
                jsonObject = new JSONObject(result);
                parsedError = jsonObject.getString("error");
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing json");
            }
            Log.i(TAG, "Parsed error message: " + parsedError);
            if (parsedError.equals("false")) {
                filesToSend.get(i).delete();
                ERROR = false;
            } else {
                mBuilder.setContentText("Ficheiro " + filesToSend.get(i).getName().substring(0, 5) + " não enviado");
                mBuilder.setColor(ContextCompat.getColor(context, R.color.red));
                notification = mBuilder.build();
                mNotifyManager.notify(2, notification);
                Toast.makeText(context, "Ficheiro " + filesToSend.get(i).getName().substring(0, 5) + " não enviado",
                        Toast.LENGTH_LONG).show();
                ERROR = true;
            }
            if (updateTransfered != null) {
                updateTransfered.cancel();
                updateTransfered.purge();
            }
            if (i < filesToSend.size() - 1) {
                i++;
                new UploadFileToServer().execute();
            } else {

                if (ERROR) {
                    notificationError();
                } else {
                    notificationDone();
                }
            }
        }

        private class multiPartUploaderProgress implements MultiPartUploader.ProgressListener {

            UploadFileToServer uploadFileToServer;

            multiPartUploaderProgress(UploadFileToServer uploadFileToServer) {
                this.uploadFileToServer = uploadFileToServer;
            }

            @Override
            public void transferred(long num) {
                progress = num;
                if (updateTransfered == null) {
                    updateTransfered = new Timer();
                    updateTransfered.schedule(new updateTransfered(uploadFileToServer), 2000, 2000);
                }
            }
        }

        class updateTransfered extends TimerTask {

            UploadFileToServer uploadFileToServer;

            updateTransfered(UploadFileToServer uploadFileToServer) {
                this.uploadFileToServer = uploadFileToServer;
            }

            public void run() {
                Log.i(TAG, "Progress: " + progress + " - " + (int) ((progress / (float) totalSize) * 100));
                uploadFileToServer.publishProgress(((int) ((progress / (float) totalSize) * 100)));
            }
        }

    }

    // Notification
    private void updateNotificationProgress(int s) {
        mBuilder.setProgress(100, s, false);
        mBuilder.setColor(ContextCompat.getColor(context, R.color.green));
        notification = mBuilder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        mNotifyManager.notify(2, notification);
    }

    private void updateNotification(String s) {
        mBuilder.setContentText(s);
        mBuilder.setColor(ContextCompat.getColor(context, R.color.green));
        notification = mBuilder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        mNotifyManager.notify(2, notification);
    }

    private void notificationError() {
        mBuilder.setContentText("Erro. Ficheiros não enviados");
        mBuilder.setProgress(0, 0, false);
        mBuilder.setColor(ContextCompat.getColor(context, R.color.red));
        notification = mBuilder.build();
        mNotifyManager.notify(2, notification);
    }

    private void notificationDone() {
        mBuilder.setContentText("Enviado.");
        mBuilder.setProgress(100, 100, false);
        mBuilder.setColor(ContextCompat.getColor(context, R.color.green));
        notification = mBuilder.build();
        mNotifyManager.notify(2, notification);
    }

    private void createNotificationProgress() {
        if (mNotifyManager == null) {
            Intent resultIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setContentIntent(pendingIntent);
            mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mBuilder = new NotificationCompat.Builder(context);
            mBuilder.setContentTitle("Sensores UBI")
                    .setContentText("A enviar dados recolhidos...")
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.ic_launcher);
            mBuilder.setColor(ContextCompat.getColor(context, R.color.green));
            notification = mBuilder.build();
            notification.flags = Notification.FLAG_ONGOING_EVENT;
            mNotifyManager.notify(2, notification);
        } else {
            mBuilder.setContentTitle("Sensores UBI")
                    .setContentText("A enviar dados recolhidos...");
            mBuilder.setColor(ContextCompat.getColor(context, R.color.green));
            notification = mBuilder.build();
            mNotifyManager.notify(2, notification);
        }
    }
}
