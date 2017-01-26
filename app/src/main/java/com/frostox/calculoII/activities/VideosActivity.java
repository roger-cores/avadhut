package com.frostox.calculoII.activities;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;

import com.frostox.calculoII.R;

import java.io.File;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

public class VideosActivity extends AppCompatActivity {

    SensorManager sensorManager;


    JCVideoPlayerStandard jcVideoPlayerStandard;

    JCVideoPlayer.JCAutoFullscreenListener mSensorEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_video);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        Intent intent = this.getIntent();
        String name = intent.getStringExtra("name");
        String id = intent.getStringExtra("id");

        getSupportActionBar().hide();

        File extraClassFolder = new File(getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()), "extraClass");
        File videoFile = new File(extraClassFolder, id);

        jcVideoPlayerStandard = (JCVideoPlayerStandard) findViewById(R.id.custom_videoplayer_standard);
        jcVideoPlayerStandard.setUp(Uri.fromFile(videoFile).getPath()
                , JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, name);
        jcVideoPlayerStandard.startPlayLogic();
        jcVideoPlayerStandard.looping = true;

        mSensorEventListener = new JCVideoPlayer.JCAutoFullscreenListener();
        //jcVideoPlayerStandard.thumbImageView.setThumbInCustomProject("http://p.qpic.cn/videoyun/0/2449_43b6f696980311e59ed467f22794e792_1/640");






    }

    @Override
    public void onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }
    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
        sensorManager.unregisterListener(mSensorEventListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(mSensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

}
