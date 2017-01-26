package com.frostox.calculoII.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.frostox.calculoII.R;
import com.squareup.picasso.Picasso;


public class About extends AppCompatActivity {

    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        image = (ImageView) findViewById(R.id.image);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Picasso.with(this).load(R.drawable.chavan2).into(image);
    }

    public void openLearningLink(View v){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.learninglinkscet.com"));
        startActivity(browserIntent);
    }

    public void openMobi(View v){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.mobitutors.in"));
        startActivity(browserIntent);
    }

}
