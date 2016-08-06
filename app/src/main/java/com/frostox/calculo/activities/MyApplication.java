package com.frostox.calculo.activities;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.frostox.calculo.services.FileSyncService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by shannonpereira601 on 21/04/16.
 */
public class MyApplication extends Application{

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private int fileSyncServiceQueue = 0;


    Firebase refUpdate;

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
        Firebase.getDefaultConfig().setPersistenceCacheSizeBytes(104857600);
        Firebase refCourses = new Firebase("https://extraclass.firebaseio.com/courses");
        Firebase refSubjects = new Firebase("https://extraclass.firebaseio.com/subjects");
        Firebase refNotes = new Firebase("https://extraclass.firebaseio.com/notes");
        Firebase refMcqs = new Firebase("https://extraclass.firebaseio.com/mcqs");
        Firebase refTopics = new Firebase("https://extraclass.firebaseio.com/topics");
        Firebase refUsers = new Firebase("https://extraclass.firebaseio.com/users");
        refUpdate =  new Firebase("https://extraclass.firebaseio.com/update");



        refCourses.keepSynced(true);
        refSubjects.keepSynced(true);
        refNotes.keepSynced(true);
        refMcqs.keepSynced(true);
        refUsers.keepSynced(true);
        refTopics.keepSynced(true);




    }

    public void registerForUpdate(){
        refUpdate.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //call update here
                Intent syncServiceIntent = new Intent(MyApplication.this, FileSyncService.class);
                MyApplication.this.startService(syncServiceIntent);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public boolean verifyProduct() throws ParseException {
        SharedPreferences sharedpreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        if(!sharedpreferences.getBoolean("activated", false)){
            Date date = sdf.parse(sharedpreferences.getString("startDate", sdf.format(Calendar.getInstance().getTime())));
            Long difference = Calendar.getInstance().getTime().getTime() - date.getTime();
            long days = difference / (24 * 60 * 60 * 1000);
            Log.d("days", "" + days);
            if(days >= 7) return false;
            else return true;
        } else return true;
    }


}
