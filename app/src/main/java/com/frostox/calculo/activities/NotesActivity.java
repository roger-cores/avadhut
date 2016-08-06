package com.frostox.calculo.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;

import calculo.frostox.com.calculo.R;

public class NotesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        Intent intent = this.getIntent();
        String name = intent.getStringExtra("name");
        String id = intent.getStringExtra("id");
        WebView webView = (WebView)findViewById(R.id.webview);

        File extraClassFolder = new File(getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()), "extraClass");
        File html = new File(extraClassFolder, id + "/index.html");

        if(!html.exists()){
            html = new File("file:///android_asset/html/not_available.html");
        }

        webView.loadUrl("file:///" + html.getAbsolutePath());

        webView.setWebViewClient(new MyWebViewClient());
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
    }
}
