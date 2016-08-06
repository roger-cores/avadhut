package com.frostox.calculo.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;
import com.frostox.calculo.apis.PHP;
import com.frostox.calculo.entities.wrappers.Response;


import calculo.frostox.com.calculo.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class Activate extends AppCompatActivity{

    EditText keyEditText;
    Button act;
    TextView tv;
    String pushId;

    String a;
    int keyDel;

    SharedPreferences sharedPreferences;

    boolean canGoBack = false;

    Firebase base = new Firebase("https://extraclass.firebaseio.com/");
    Firebase ref = new Firebase("https://extraclass.firebaseio.com/users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        canGoBack = this.getIntent().getBooleanExtra("canGoBack", false);

        keyEditText = (EditText) findViewById(R.id.productKey);

        keyEditText.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        keyEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                boolean flag = true;
                String eachBlock[] = keyEditText.getText().toString().split("-");
                for (int i = 0; i < eachBlock.length; i++) {
                    if (eachBlock[i].length() > 5) {
                        flag = false;
                    }
                }
                if (flag) {

                    keyEditText.setOnKeyListener(new View.OnKeyListener() {

                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {

                            if (keyCode == KeyEvent.KEYCODE_DEL)
                                keyDel = 1;
                            return false;
                        }
                    });

                    if (keyDel == 0) {

                        if (((keyEditText.getText().length() + 1) % 6) == 0) {

                            if (keyEditText.getText().toString().split("-").length <= 3) {
                                keyEditText.setText(keyEditText.getText() + "-");
                                keyEditText.setSelection(keyEditText.getText().length());
                            }
                        }
                        a = keyEditText.getText().toString();
                    } else {
                        a = keyEditText.getText().toString();
                        keyDel = 0;
                    }

                } else {
                    keyEditText.setText(a);
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        act = (Button) findViewById(R.id.activate);
        tv = (TextView) findViewById(R.id.tvAcc);
        tv.setVisibility(View.INVISIBLE);

        Query query = ref.orderByChild("uid").equalTo(ref.getAuth().getUid()).limitToFirst(1);

        /*
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    pushId = postSnapshot.getKey();
                    Log.d("Checkuid",pushId);
                    Log.d("Check", postSnapshot.child("activated").getValue() + "");
                    if(postSnapshot.child("activated").getValue().equals("true")){
                        tv.setVisibility(View.VISIBLE);
                        keyEditText.setVisibility(View.INVISIBLE);
                        act.setVisibility(View.INVISIBLE);
                        canGoBack = true;
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(getApplicationContext(), firebaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        */

        sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean("activated", false)){
            tv.setVisibility(View.VISIBLE);
            keyEditText.setVisibility(View.INVISIBLE);
            act.setVisibility(View.INVISIBLE);
            canGoBack = true;
        }
    }

    @Override
    public void onBackPressed() {
        if(canGoBack) super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onActivate(View v) {


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.base_url_extraclass))
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        PHP phpService = retrofit.create(PHP.class);

        com.frostox.calculo.entities.User user1 = new com.frostox.calculo.entities.User();
        user1.setUid(base.getAuth().getUid());
        user1.setPkey(keyEditText.getText().toString());

        Call<Response> callForVerifyKey = phpService.verifyKey(user1);

        callForVerifyKey.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if(response.body().isRes()){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("activated", true);
                    editor.apply();
                    editor.commit();
                    keyEditText.setVisibility(View.INVISIBLE);
                    act.setVisibility(View.INVISIBLE);
                    tv.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "Account Activated", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Toast.makeText(Activate.this, "Something Went Wrong", Toast.LENGTH_LONG).show();
                Log.d("error", t.getMessage());
            }
        });

        Log.d("CheckuidonClicked",ref.getAuth().getUid());
        if (keyEditText.getText().toString().equals(ref.getAuth().getUid())) {
            Log.d("Checkuidhere","Reached");
            keyEditText.setVisibility(View.INVISIBLE);
            act.setVisibility(View.INVISIBLE);
            tv.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(), "Account Activated", Toast.LENGTH_LONG).show();

            final Firebase activeStated = ref.child(pushId);
            activeStated.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    activeStated.child("activated").setValue("true");
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Toast.makeText(getApplicationContext(), firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.buy:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "l2newasa@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT,"About purchasing ExtraCLASS");
                startActivity(Intent.createChooser(emailIntent, "Send Email"));

//                Intent i = new Intent(Intent.ACTION_SEND);
//                i.setType("message/rfc822");
//                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"foo@bar.com"});
//                i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
//                i.putExtra(Intent.EXTRA_TEXT   , "body of email");
//                try {
//                    startActivity(Intent.createChooser(i, "Choose email..."));
//                } catch (android.content.ActivityNotFoundException ex) {
//                    // handle edge case where no email client is installed
//                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
