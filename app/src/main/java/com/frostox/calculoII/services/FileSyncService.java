package com.frostox.calculoII.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.frostox.calculoII.R;
import com.frostox.calculoII.activities.MainActivity;
import com.frostox.calculoII.apis.FilesSync;
import com.frostox.calculoII.entities.wrappers.FileListWrapper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by roger on 26/4/16.
 */
public class FileSyncService extends IntentService{



    public final Integer NOTIF_ID = 0;
    public final String wakeTag = "fileSyncWakeTag";

    NotificationManager mNotifyManager;
    android.support.v4.app.NotificationCompat.Builder mBuilder;

    List<com.frostox.calculoII.entities.File> files = new ArrayList<>();

    SimpleDateFormat sdf = new SimpleDateFormat("dd:MM:yyyy:HH:mm:ss");
    File extraClassFolder;

    PowerManager.WakeLock wakeLock;


    boolean downloaded = false;
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public FileSyncService(String name) {
        super(name);
    }

    public FileSyncService() {
        super("FileSyncService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, wakeTag);
        wakeLock.acquire();

        extraClassFolder = new File(getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()), "extraClass");
        files.clear();
        this.walk(extraClassFolder);
        FileListWrapper wrapper = new FileListWrapper();
        for(com.frostox.calculoII.entities.File file: files){
            Log.d("FileList", file.getPath());
            Log.d("FileList", Long.toString(file.getSize()));
            Log.d("FileList", file.getModified());
        }
        wrapper.setFiles(files);

        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("Updating study material")
                .setContentText("Download in progress")
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher);


        mBuilder.setProgress(100, 0, false);

        mNotifyManager.notify(this.NOTIF_ID, mBuilder.build());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.base_url_extraclass))
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        FilesSync syncService = retrofit.create(FilesSync.class);

        Call<FileListWrapper> callSync = syncService.getDownloadableFiles(wrapper);


        try {
            Response<FileListWrapper> res = callSync.execute();
            downloadFiles(res.body());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(downloaded){
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setContentTitle("New MCQs/Notes/Videos are availabe now")
                            .setContentText("Start exploring now!");
            int NOTIFICATION_ID = 12345;

            Intent targetIntent = new Intent(this, MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(contentIntent);
            NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nManager.notify(NOTIFICATION_ID, builder.build());
        }




    }



    public void downloadFiles(FileListWrapper wrapper){
        float i = -1;
        List<com.frostox.calculoII.entities.File> files = wrapper.getFiles();
        Long totalSize = wrapper.getTotalSize();
        Long downloadedSize = new Long(0);

        Log.d("FileList downloading", "downloading here");


        for(com.frostox.calculoII.entities.File file : files){
            Log.d("FileList", file.getPath());
            Log.d("FileList", Long.toString(file.getFilesize()));
            //Log.d("FileList", file.getModified());
        }

        for(com.frostox.calculoII.entities.File file : files){

            File rootDir = new File(getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()), "extraClass");



            File destination = new File(rootDir, file.getPath());

            if(destination.exists()) destination.delete();



            if(file.getAction() != null && file.getAction().equals("d")){
                destination.delete();

                continue;
            }

            i++;
            System.out.print("Data:: i:" + i);
            int count;
            try {
                URL url = new URL(getString(R.string.base_url_extraclass) + "uploads/" + file.getPath().replaceAll(" ", "%20"));
                URLConnection conection = url.openConnection();
                conection.connect();



                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(
                        url.openStream(), 8192);


                String destinationPath = destination.getAbsolutePath();
                new File(destinationPath.substring(0, destinationPath.lastIndexOf('/'))).mkdirs();
                Log.d("testing this", destinationPath.substring(0, destinationPath.lastIndexOf('/')));

                // Output stream to write file
                OutputStream output = new FileOutputStream(destination);
                System.out.println("Data::" + destination.getAbsolutePath());

                byte data[] = new byte[2048];

                long total = 0;
                double progress = ((downloadedSize / ((float) totalSize)) * 95.0d);

                while ((count = input.read(data)) != -1) {
                    downloaded = true;
                    System.out.println("Data::" + count);
                    total += count;
                    downloadedSize += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called

                    if (!isOnline(FileSyncService.this)) {
                        mBuilder.setContentText("Download Failed");
                        mBuilder.setProgress(100, 0, false);
                        mNotifyManager.notify(NOTIF_ID, mBuilder.build());
                    } else {

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            int temp = (int) ((downloadedSize / ((float) totalSize)) * 95.0d);
                            if(Math.abs(temp - progress) > 5){
                                mBuilder.setProgress(100, temp, false);
                                mNotifyManager.notify(this.NOTIF_ID, mBuilder.build());
                            }
                        }



                    }

                    // writing data to file
                    output.write(data, 0, count);

                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnknownHostException u) {
                u.printStackTrace();

                if (!isOnline(FileSyncService.this)) {


                    mNotifyManager.cancel(NOTIF_ID);
                }
            } catch (IOException e) {
                e.printStackTrace();

            }




        }

        mNotifyManager.cancel(NOTIF_ID);
    }


    public boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());

    }

    public void walk(File root) {


        File[] list = root.listFiles();
        if(list == null) return;
        for (File f : list) {
            if (f.isDirectory()) {
                walk(f);
            }
            else {
                Log.d("", "File: " + f.getAbsoluteFile());
                com.frostox.calculoII.entities.File file = new com.frostox.calculoII.entities.File();
                file.setSize(f.length());
                file.setModified(sdf.format(f.lastModified()));
                file.setPath(extraClassFolder.toURI().relativize(f.toURI()).getPath());
                files.add(file);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mNotifyManager.cancel(NOTIF_ID);
        wakeLock.release();
    }
}
