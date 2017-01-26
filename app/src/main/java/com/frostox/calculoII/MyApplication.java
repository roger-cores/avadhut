package com.frostox.calculoII;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.multidex.MultiDexApplication;
import android.util.Log;


import com.frostox.calculoII.services.FileSyncService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by shannonpereira601 on 21/04/16.
 */
public class MyApplication extends MultiDexApplication{

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private int fileSyncServiceQueue = 0;


    DatabaseReference refUpdate;

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        //Firebase.getDefaultConfig().setPersistenceCacheSizeBytes(104857600);

        DatabaseReference base = FirebaseDatabase.getInstance().getReference();
        DatabaseReference refCourses = base.child("courses");
        DatabaseReference refSubjects = base.child("subjects");
        DatabaseReference refNotes = base.child("notes");
        DatabaseReference refMcqs = base.child("mcqs");
        DatabaseReference refTopics = base.child("topics");
        refUpdate = base.child("update");



        refCourses.keepSynced(true);
        refSubjects.keepSynced(true);
        refNotes.keepSynced(true);
        refMcqs.keepSynced(true);
        refTopics.keepSynced(true);
        refUpdate.keepSynced(true);


        refUpdate.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //call update here
                refUpdate.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });



    }

    public void registerForUpdate(){
        refUpdate.addValueEventListener(new ValueEventListener() {

            boolean first = true;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //call update here
                Intent syncServiceIntent = new Intent(MyApplication.this, FileSyncService.class);
                MyApplication.this.startService(syncServiceIntent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
