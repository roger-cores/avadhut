package com.frostox.calculo.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import com.frostox.calculo.pulled_sourses.wifidirect.WiFiDirectActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipOutputStream;

import calculo.frostox.com.calculo.R;

import static com.frostox.calculo.pulled_sourses.Util.zip;

public class ShareDataActivity extends AppCompatActivity {

    File extraClassFolder;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_data2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Share Data!");
        extraClassFolder = new File(getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()), "extraClass");

    }

    public void send(View view){
        new ZipFileTask().execute("");
        dialog = ProgressDialog.show(this, "Preparing files", "Please wait... It may take a few minutes!", true);
        dialog.show();
//        Intent intent = new Intent(this, WiFiDirectActivity.class);
//        intent.putExtra("send", true);
//        this.startActivity(intent);
    }

    public void receive(View view){
        Intent intent = new Intent(this, WiFiDirectActivity.class);
        intent.putExtra("send", false);
        this.startActivity(intent);

    }


    class ZipFileTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(ShareDataActivity.this, "Zipping started", Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(new File(getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()), "extraClass.zip").getPath()));

                //zipSubFolder(zos, extraClassFolder, extraClassFolder.getPath().length());
                zip(extraClassFolder, new File(getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()), "extraClass.zip"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.v("ShareData", e.getStackTrace().toString());
            } catch (IOException e) {
                e.printStackTrace();
                Log.v("ShareData", e.getStackTrace().toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            //super.onPostExecute(s);

            Toast.makeText(ShareDataActivity.this, "Zipping done", Toast.LENGTH_LONG).show();
            File zipFile = new File(getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()), "extraClass.zip");
            dialog.dismiss();

            Intent intent = new Intent(ShareDataActivity.this, WiFiDirectActivity.class);
            intent.putExtra("send", true);
            ShareDataActivity.this.startActivity(intent);


        }
    }

}
