package com.frostox.calculoII.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.frostox.calculoII.R;
import com.frostox.calculoII.activities.About;
import com.frostox.calculoII.activities.Home;
import com.frostox.calculoII.activities.Result;
import com.frostox.calculoII.activities.ShareDataActivity;
import com.frostox.calculoII.entities.wrappers.MenuItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by roger on 11/14/2016.
 */
public class GridAdapter extends BaseAdapter {

    private final DatabaseReference ref;
    private final String userid;
    private FirebaseAuth mAuth;
    List<MenuItem> menuItems;
    private String userkey;


    public GridAdapter() {
        ref = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        userid = mAuth.getCurrentUser().getUid();
        getUserKey();
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }



    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    @Override
    public int getCount() {
        if(menuItems != null)
            return menuItems.size();
        else return 0;
    }

    @Override
    public Object getItem(int position) {
        if(menuItems != null && menuItems.size()>position)
            return menuItems.get(position);
        else return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item, null);
        }

        final MenuItem item = menuItems.get(position);

        ImageView image = (ImageView) convertView.findViewById(R.id.menu_item_image);
        TextView text = (TextView) convertView.findViewById(R.id.menu_item_text_view);

        //Picasso.with(parent.getContext()).load(item.getImageId()).into(image);
        image.setImageResource(item.getImageId());

        text.setText(item.getMenuString());

//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                switch(item.getMenuString()){
//                    case "MCQ test":
//                        //MCQ
//                        Intent intent = new Intent(parent.getContext(), Home.class);
//                        intent.putExtra("mode", "mcqs");
//                        parent.getContext().startActivity(intent);
//                        break;
//
//                    case "Notes":
//                        //Notes
//                        intent = new Intent(parent.getContext(), Home.class);
//                        intent.putExtra("mode", "notes");
//                        parent.getContext().startActivity(intent);
//                        break;
//
//                    case "Videos":
//                        //Videos
//                        intent = new Intent(parent.getContext(), Home.class);
//                        intent.putExtra("mode", "videos");
//                        parent.getContext().startActivity(intent);
//                        break;
//
//                    case "Results":
//                        intent = new Intent(parent.getContext(), Result.class);
//                        intent.putExtra("userkey", userkey);
//                        parent.getContext().startActivity(intent);
//                        break;
//
//                    case "About Us":
//                        intent = new Intent(parent.getContext(), About.class);
//                        parent.getContext().startActivity(intent);
//                        break;
//
//                    case "Share App":
//                        try {
//
//                            PackageManager pm = parent.getContext().getPackageManager();
//                            ApplicationInfo ai = pm.getApplicationInfo(parent.getContext().getPackageName(), 0);
//                            File srcFile = new File(ai.publicSourceDir);
//                            Intent share = new Intent();
//                            share.setAction(Intent.ACTION_SEND);
//                            share.setType("application/vnd.android.package-archive");
//                            share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(srcFile));
//                            parent.getContext().startActivity(Intent.createChooser(share, "ExtraCLASS"));
//                        } catch (Exception e) {
//                            Log.e("ShareApp", e.getMessage());
//                        }
//                        break;
//
//                    case "Share Data":
//                        intent = new Intent(parent.getContext(), ShareDataActivity.class);
//                        parent.getContext().startActivity(intent);
//                        break;
//
//                    case "Tell Us":
//                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
//                                "mailto","l2newasa@gmail.com", null));
//
//                        emailIntent.putExtra(Intent.EXTRA_SUBJECT,"About Avadhut");
//
//                        parent.getContext().startActivity(Intent.createChooser(emailIntent, "Send Email"));
//                        break;
//
//                    case "Log out":
//                        mAuth.signOut();
//                        SharedPreferences sharedPreferences = parent.getContext().getSharedPreferences("MyData", Context.MODE_PRIVATE);
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                        boolean check = false;
//                        editor.putBoolean("check", check);
//                        editor.commit();
//                        break;
//                }
//            }
//        });

        return convertView;
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
