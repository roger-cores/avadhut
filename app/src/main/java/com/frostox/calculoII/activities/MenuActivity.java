package com.frostox.calculoII.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.frostox.calculoII.MyApplication;
import com.frostox.calculoII.R;
import com.frostox.calculoII.adapters.GridAdapter;
import com.frostox.calculoII.entities.wrappers.MenuItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    GridView menuGrid;


    private String userkey;

    private DatabaseReference ref;
    private String userid;
    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        ((MyApplication) this.getApplication()).registerForUpdate();
        mAuth = FirebaseAuth.getInstance();

        ref = FirebaseDatabase.getInstance().getReference();
        userid = mAuth.getCurrentUser().getUid();
        getUserKey();

        mAuthListener = new FirebaseAuth.AuthStateListener(){

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                    Intent intent = new Intent(MenuActivity.this, Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        };

        menuGrid = (GridView) findViewById(R.id.activity_menu_grid_view);
        List<MenuItem> menuItemList = new ArrayList<>();
        menuItemList.add(new MenuItem(R.drawable.ic_check_circle_black_48dp, "MCQ test"));
        menuItemList.add(new MenuItem(R.drawable.ic_notes, "Notes"));
        menuItemList.add(new MenuItem(R.drawable.ic_local_movies_black_24dp, "Videos"));
        menuItemList.add(new MenuItem(R.drawable.ic_notes, "Results"));
        menuItemList.add(new MenuItem(R.drawable.ic_people_black_48dp, "About Us"));
        menuItemList.add(new MenuItem(R.drawable.ic_menu_share, "Share App"));
        menuItemList.add(new MenuItem(R.drawable.ic_menu_share, "Share Data"));
        menuItemList.add(new MenuItem(R.drawable.ic_message_black_48dp, "Tell Us"));
        menuItemList.add(new MenuItem(R.drawable.logout, "Log out"));

        final GridAdapter adapter = new GridAdapter();
        adapter.setMenuItems(menuItemList);

        menuGrid.setAdapter(adapter);

        menuGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
                final MenuItem item = adapter.getMenuItems().get(position);

                switch(item.getMenuString()){
                    case "MCQ test":
                        //MCQ
                        Intent intent = new Intent(parent.getContext(), Home.class);
                        intent.putExtra("mode", "mcqs");
                        parent.getContext().startActivity(intent);
                        break;

                    case "Notes":
                        //Notes
                        intent = new Intent(parent.getContext(), Home.class);
                        intent.putExtra("mode", "notes");
                        parent.getContext().startActivity(intent);
                        break;

                    case "Videos":
                        //Videos
                        intent = new Intent(parent.getContext(), Home.class);
                        intent.putExtra("mode", "videos");
                        parent.getContext().startActivity(intent);
                        break;

                    case "Results":
                        intent = new Intent(parent.getContext(), Result.class);
                        intent.putExtra("userkey", userkey);
                        parent.getContext().startActivity(intent);
                        break;

                    case "About Us":
                        intent = new Intent(parent.getContext(), About.class);
                        parent.getContext().startActivity(intent);
                        break;

                    case "Share App":


                        new AlertDialog.Builder(parent.getContext())
                                .setTitle("How would you like to share?")
                                .setPositiveButton("Share link", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        try {
                                            Intent i = new Intent(Intent.ACTION_SEND);
                                            i.setType("text/plain");
                                            i.putExtra(Intent.EXTRA_SUBJECT, "Avadhut");
                                            String sAux = "\nLet me recommend you this application\n\n";
                                            sAux = sAux + "https://play.google.com/store/apps/details?id=Orion.Soft \n\n";
                                            i.putExtra(Intent.EXTRA_TEXT, sAux);
                                            startActivity(Intent.createChooser(i, "choose one"));
                                        } catch(Exception e) {
                                            //e.toString();
                                        }
                                    }
                                })
                                .setNegativeButton("Share apk", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        try {

                                            PackageManager pm = parent.getContext().getPackageManager();
                                            ApplicationInfo ai = pm.getApplicationInfo(parent.getContext().getPackageName(), 0);
                                            File srcFile = new File(ai.publicSourceDir);
                                            Intent share = new Intent();
                                            share.setAction(Intent.ACTION_SEND);
                                            share.setType("application/vnd.android.package-archive");
                                            share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(srcFile));
                                            parent.getContext().startActivity(Intent.createChooser(share, "ExtraCLASS"));
                                        } catch (Exception e) {
                                            Log.e("ShareApp", e.getMessage());
                                        }
                                    }
                                })
                                .show();



                        break;

                    case "Share Data":
                        intent = new Intent(parent.getContext(), ShareDataActivity.class);
                        parent.getContext().startActivity(intent);
                        break;

                    case "Tell Us":
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto","l2newasa@gmail.com", null));

                        emailIntent.putExtra(Intent.EXTRA_SUBJECT,"About Avadhut");

                        parent.getContext().startActivity(Intent.createChooser(emailIntent, "Send Email"));
                        break;

                    case "Log out":
                        mAuth.signOut();
                        SharedPreferences sharedPreferences = parent.getContext().getSharedPreferences("MyData", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        boolean check = false;
                        editor.putBoolean("check", check);
                        editor.commit();
                        break;
                }
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

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public void getUserKey() {
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

}
