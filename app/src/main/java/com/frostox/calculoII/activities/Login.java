package com.frostox.calculoII.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.frostox.calculoII.R;
import com.frostox.calculoII.apis.PHP;
import com.frostox.calculoII.entities.wrappers.ProductStatus;
import com.frostox.calculoII.entities.wrappers.Response;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class Login extends AppCompatActivity {
    String emailtext;
    EditText email, pass;
    boolean check;
    SharedPreferences sharedPreferences;

    SimpleDateFormat sdfLocal, sdfUTC;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
       sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(getString(R.string.base_url_extraclass))
                            .addConverterFactory(JacksonConverterFactory.create())
                            .build();
                    PHP phpService = retrofit.create(PHP.class);

                    com.frostox.calculoII.entities.User user1 = new com.frostox.calculoII.entities.User();
                    user1.setUid(currentUser.getUid());

                    Call<Response> callForRegisterUser = phpService.registerUser(user1);
                    final Call<ProductStatus> callForVerifyActivated = phpService.verifyActivated(user1);

                    callForRegisterUser.enqueue(new Callback<Response>() {
                        @Override
                        public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {


                            callForVerifyActivated.enqueue(new Callback<ProductStatus>() {
                                @Override
                                public void onResponse(Call<ProductStatus> call, retrofit2.Response<ProductStatus> response) {
                                    SharedPreferences sharedpreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putBoolean("activated", response.body().isActivated());
                                    try {
                                        editor.putString("startDate", sdfLocal.format(sdfUTC.parse(response.body().getStartDate())));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    editor.apply();
                                    editor.commit();
                                    Intent intent = new Intent(Login.this, MenuActivity.class);
                                    startActivity(intent);
                                }

                                @Override
                                public void onFailure(Call<ProductStatus> call, Throwable t) {
                                    Toast.makeText(Login.this, "Something went wrong", Toast.LENGTH_LONG).show();
                                    Log.d("phpService", t.getMessage());
                                    mAuth.signOut();
                                }
                            });


                            //changed this file
                        }

                        @Override
                        public void onFailure(Call<Response> call, Throwable t) {
                            Toast.makeText(Login.this, "Something went wrong " + t.getMessage(), Toast.LENGTH_LONG).show();
                            Log.d("phpService", t.getMessage());
                            mAuth.signOut();
                        }
                    });
                } else {

                }
            }
        };


        if(mAuth.getCurrentUser() != null)
        {
            Intent i = new Intent(this, MenuActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }

        sdfLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdfUTC  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdfUTC.setTimeZone(TimeZone.getTimeZone("UTC"));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = (EditText) findViewById(R.id.e1);
        pass = (EditText) findViewById(R.id.e2);

        Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                emailtext = email.getText().toString();
                final String password = pass.getText().toString();
                if (emailtext.length() == 0) {
                    email.setError("Enter an Email");
                }
                if (!isValidEmail(emailtext) && emailtext.length() != 0) {
                    email.setError("Invalid Email");
                }

                if (password.length() == 0) {
                    pass.setError("Enter a Password");

                }
                if (password.length() > 12) {
                    pass.setError("Password should be lesser than 12 characters");

                }

                if (isValidEmail(emailtext) && (password.length() != 0 && password.length() < 13)) {
                    // ref.authWithPassword(, "password", authResultHandler);
                    setcheck();

                    mAuth.signInWithEmailAndPassword(emailtext, password)
                            .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d(Login.class.getName(), "signInWithEmail:onComplete:" + task.isSuccessful());

                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                        if (!task.isSuccessful()) {
                                            Log.w(Login.class.getName(), "signInWithEmail:failed", task.getException());
                                            Toast.makeText(Login.this, "Authentication Failed",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }


                            });


                }


            }
        });


        TextView signup = (TextView) findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });
        final TextInputLayout usernameWrapper = (TextInputLayout) findViewById(R.id.usernameWrapper);
        final TextInputLayout passwordWrapper = (TextInputLayout) findViewById(R.id.passwordWrapper);
        usernameWrapper.setHint("EmailId");
        passwordWrapper.setHint("Password");
    }

    boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public boolean checkLogin() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        check = sharedPreferences.getBoolean("check", false);
        return check;
    }

    public void setcheck() {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        check = true;
        editor.putBoolean("check", check);
        editor.commit();
    }
   /* private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }*/

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
