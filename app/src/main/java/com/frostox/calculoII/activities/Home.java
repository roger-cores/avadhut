package com.frostox.calculoII.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.frostox.calculoII.MyApplication;
import com.frostox.calculoII.R;
import com.frostox.calculoII.fragments.EntityFragment1;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.text.ParseException;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, EntityFragment1.OnFragmentInteractionListener {


    CoordinatorLayout coordinatorLayout;

    DatabaseReference ref;

    private DrawerLayout drawer;

    private EntityFragment1 standardFragment;

    private EntityFragment1 subjectFragment;

    private EntityFragment1 topicFragment;

    private EntityFragment1 mcqFragment;

    private EntityFragment1 noteFragment;

    private EntityFragment1 videoFragment;

    private boolean mcqMode = true, noteMode = false, videoMode = false;

    private String current = "Standard", userid, userkey, date, dateToday;

    private TextView courses, subjects, topics, mcqnotes;

    private HorizontalScrollView scrollView;

    int  installedDate, currDate,valipPeriod;

    private FirebaseAuth.AuthStateListener mAuthListener;


    long differenceDates;

    private boolean check;

    NavigationView navigationView;


    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener(){

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                    Intent intent = new Intent(Home.this, Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        };

        try {
            boolean verified = ((MyApplication) this.getApplication()).verifyProduct();
            if(!verified){
                Toast.makeText(getBaseContext(),"Your trial period is up " +differenceDates,Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Home.this, Activate.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
                Home.this.startActivity(intent);
                return;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ref = FirebaseDatabase.getInstance().getReference();
        userid = mAuth.getCurrentUser().getUid();
        getUserKey();




        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageView bb = (ImageView) findViewById(R.id.content_home_back_button);
        bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!current.equals("Standard")) {
                    navPrev();
                } else if (current.equals("Standard")) {
                    onBackPressed();
                }
            }
        });
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.colayout);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        scrollView = (HorizontalScrollView) findViewById(R.id.scrollView);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        standardFragment = new EntityFragment1();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, standardFragment)
                .commit();

        courses = (TextView) findViewById(R.id.courses);
        subjects = (TextView) findViewById(R.id.subjects);
        topics = (TextView) findViewById(R.id.topics);
        mcqnotes = (TextView) findViewById(R.id.mcqnotes);


        String mode = getIntent().getStringExtra("mode");

        switch(mode){
            case "mcqs":
                mcqMode = true;
                noteMode = false;
                videoMode = false;
                break;

            case "notes":
                mcqMode = false;
                noteMode = true;
                videoMode = false;
                break;

            case "videos":
                mcqMode = false;
                noteMode = false;
                videoMode = true;
                break;
        }




    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if(drawer.isDrawerOpen(GravityCompat.START)){
                drawer.closeDrawer(GravityCompat.START);
            } else {
                drawer.openDrawer(GravityCompat.START);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (!current.equals("Standard")) {
            navPrev();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_mcq) {
            mcqMode = true;
            noteMode = false;
            videoMode = false;
            if (current.equals("Note") || current.equals("Video"))
                navPrev();

            //TODO Fragment operations
        } else if (id == R.id.nav_notes) {
            noteMode = true;
            mcqMode = false;
            videoMode = false;
            if (current.equals("MCQ") || current.equals("Video"))
                navPrev();
            //TODO Fragement operations
        } else if (id == R.id.nav_videos){
            videoMode = true;
            mcqMode = false;
            noteMode = false;
            if(current.equals("MCQ") || current.equals("Note"))
                navPrev();
        }else if (id == R.id.activate) {
           Intent intent = new Intent(Home.this,Activate.class);
            intent.putExtra("canGoBack", true);
            startActivity(intent);
            //TODO Fragement operations
        } else if (id == R.id.nav_logout) {
            mAuth.signOut();
            SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            check = false;
            editor.putBoolean("check", check);
            editor.commit();

        } else if (id == R.id.results) {
            Intent intent = new Intent(this, Result.class);
            intent.putExtra("userkey", userkey);
            startActivity(intent);
        } else if (id == R.id.nav_share) {
//            String shareBody = "Hey guys, check out this awesome app that'll make studies way easier \n https://play.google.com/store/apps/details?id="+getApplication().getPackageName();
//            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
//            sharingIntent.setType("text/plain");
//            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "ExtraCLASS is an awesome app");
//            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
//            startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));

            try {

                PackageManager pm = getPackageManager();
                ApplicationInfo ai = pm.getApplicationInfo(getPackageName(), 0);
                File srcFile = new File(ai.publicSourceDir);
                Intent share = new Intent();
                share.setAction(Intent.ACTION_SEND);
                share.setType("application/vnd.android.package-archive");
                share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(srcFile));
                startActivity(Intent.createChooser(share, "ExtraCLASS"));
            } catch (Exception e) {
                Log.e("ShareApp", e.getMessage());
            }
        } else if(id == R.id.nav_share_data){
            Intent intent = new Intent(this, ShareDataActivity.class);
            this.startActivity(intent);

        } else if (id == R.id.nav_send) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto","l2newasa@gmail.com", null));

            emailIntent.putExtra(Intent.EXTRA_SUBJECT,"About ExtraCLASS");

            startActivity(Intent.createChooser(emailIntent, "Send Email"));
        } else if (id == R.id.nav_about_us) {
            Intent intent = new Intent(this, About.class);
            this.startActivity(intent);
        }

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    boolean doubleBackToExitPressedOnce = false;


    @Override
    public void onFragmentInteraction(Uri uri) {
        Snackbar.make(coordinatorLayout, "fragment interaction", Snackbar.LENGTH_LONG).show();
    }


    public void navNext(String key, String name) {
        name = name.toUpperCase();
        switch (current) {
            case "Standard":
                if (name.equals("9TH GRADE")) {
                    name = "9th GRADE";
                } else if (name.equals("10TH GRADE")) {
                    name = "10th GRADE";
                }
                current = "Subject";
                subjectFragment = new EntityFragment1();

                Bundle bundle = new Bundle();
                bundle.putString("current", current);
                bundle.putString("id", key);

                bundle.putString("userkey", userkey);
                // set Fragmentclass Arguments
                subjectFragment.setArguments(bundle);

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in, R.anim.slide_out, R.anim.slide_out2, R.anim.slide_in2)
                        .replace(R.id.content_frame, subjectFragment).addToBackStack("Standard")
                        .commit();

                courses.setText(name + " >");
                subjects.setVisibility(View.VISIBLE);
                subjects.setText("SUBJECTS >");
                topics.setVisibility(View.GONE);
                mcqnotes.setVisibility(View.GONE);

                break;
            case "Subject":
                current = "Topic";
                topicFragment = new EntityFragment1();
                bundle = new Bundle();
                bundle.putString("current", current);
                bundle.putString("id", key);
                bundle.putString("userkey", userkey);
                // set Fragmentclass Arguments
                topicFragment.setArguments(bundle);
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in, R.anim.slide_out, R.anim.slide_out2, R.anim.slide_in2)
                        .replace(R.id.content_frame, topicFragment).addToBackStack("Subject")
                        .commit();

                subjects.setText(name + " >");
                topics.setVisibility(View.VISIBLE);
                mcqnotes.setVisibility(View.GONE);

                break;
            case "Topic":
                if (mcqMode) {
                    current = "MCQ";
                    mcqFragment = new EntityFragment1();
                    bundle = new Bundle();
                    bundle.putString("current", current);
                    bundle.putString("id", key);
                    bundle.putString("userkey", userkey);
                    mcqFragment.setArguments(bundle);
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.slide_in, R.anim.slide_out, R.anim.slide_out2, R.anim.slide_in2)
                            .replace(R.id.content_frame, mcqFragment).addToBackStack("Topic")
                            .commit();

                    topics.setText(name + " >");
                    mcqnotes.setVisibility(View.VISIBLE);
                    mcqnotes.setText("MCQs >");
                } else if(noteMode) {
                    current = "Note";
                    noteFragment = new EntityFragment1();
                    bundle = new Bundle();
                    bundle.putString("current", current);
                    bundle.putString("id", key);
                    bundle.putString("userkey", userkey);
                    noteFragment.setArguments(bundle);
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.slide_in, R.anim.slide_out, R.anim.slide_out2, R.anim.slide_in2)
                            .replace(R.id.content_frame, noteFragment).addToBackStack("Topic")
                            .commit();

                    topics.setText(name + " >");
                    mcqnotes.setVisibility(View.VISIBLE);
                    mcqnotes.setText("NOTES >");
                } else {
                    current = "Video";
                    videoFragment = new EntityFragment1();
                    bundle = new Bundle();
                    bundle.putString("current", current);
                    bundle.putString("id", key);
                    bundle.putString("userkey", userkey);
                    videoFragment.setArguments(bundle);
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.slide_in, R.anim.slide_out, R.anim.slide_out2, R.anim.slide_in2)
                            .replace(R.id.content_frame, videoFragment).addToBackStack("Topic")
                            .commit();

                    topics.setText(name + " >");
                    mcqnotes.setVisibility(View.VISIBLE);
                    mcqnotes.setText("VIDEOS >");
                }


                break;
            case "MCQ":

                //Goto mcq activity
               /*Intent intent = new Intent(this, McqActivity.class);
                intent.putExtra("name", name);
                // intent.putExtra("id", id);

                startActivity(intent);*/
                break;
            case "Note":
                //Goto note activity
                Intent intent = new Intent(this, NotesActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("id", key);
                startActivity(intent);
                break;

            case "Video":

//                File extraClassFolder = new File(getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()), "extraClass");
//                File videoFile = new File(extraClassFolder, key);

                intent = new Intent(this, VideosActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("id", key);
                startActivity(intent);

                break;

            case "Timeout":
                Toast.makeText(getBaseContext(),"Sorry your trial period is up",Toast.LENGTH_LONG).show();
                break;
        }


        scrollView.post(new Runnable() {
            public void run() {
                scrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        });
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public void navPrev() {


        switch (current) {

            case "Subject":
                current = "Standard";
                subjects.setVisibility(View.GONE);
                courses.setText("COURSES >");
                break;

            case "Topic":
                current = "Subject";
                topics.setVisibility(View.GONE);
                subjects.setText("SUBJECTS >");
                break;

            case "MCQ":
                current = "Topic";
                mcqnotes.setVisibility(View.GONE);
                topics.setText("TOPICS >");
                break;

            case "Note":
                current = "Topic";
                mcqnotes.setVisibility(View.GONE);
                topics.setText("TOPICS >");

            case "Video":
                current = "Topic";
                mcqnotes.setVisibility(View.GONE);
                topics.setText("TOPICS >");

        }

        getSupportFragmentManager().popBackStack();

        scrollView.post(new Runnable() {
            public void run() {
                scrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        });
    }


    public void getUserKey() {
        ;
        DatabaseReference userRef = ref.child("users");
        Query query = userRef.orderByChild("uid").equalTo(userid);

        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    userkey = postSnapshot.getKey();

                    /*
                    User user = postSnapshot.getValue(User.class);
                    Calendar calendar = Calendar.getInstance();
                    java.util.Date now = calendar.getTime();
                    long time = user.getTime();
                    long difference = now.getTime()-time;

                    Log.d("Checkdate",now.getTime()+"");

                    differenceDates = difference / (24 * 60 * 60 * 1000);
                    if(differenceDates >= 7 && user.getActivated()==false)
                    {
                        Toast.makeText(getBaseContext(),"Your trial period is up " +differenceDates,Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Home.this, Activate.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
                        Home.this.startActivity(intent);
                    }
                    if(differenceDates != 7)
                    {
                     Toast.makeText(getBaseContext(),"Still time .."+differenceDates,Toast.LENGTH_LONG).show();
                    }
                    */
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                System.out.println("The read failed: " + databaseError.getMessage());
            }


        });

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}