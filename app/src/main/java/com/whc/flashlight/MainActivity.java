package com.whc.flashlight;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapText;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_BATTERY_0;
import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_BATTERY_1;
import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_BATTERY_2;
import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_BATTERY_3;
import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_BATTERY_4;



public class MainActivity extends AppCompatActivity {

    private static final int RC_HANDLE_CAMERA_PERM = 2;

    private AwesomeTextView power;
    private ImageView image;
    private boolean openLight = false;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TypefaceProvider.registerDefaultIconSets();
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        power = findViewById(R.id.power);
        image = findViewById(R.id.image);
        image.setOnClickListener(new cameraControl());
        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            CameraManager camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            String cameraId = null;
            try {
                cameraId = camManager.getCameraIdList()[0];
                camManager.setTorchMode(cameraId, openLight);   //Turn ON
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } else {
            requestCameraPermission();
        }
    }

    private class cameraControl implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            openLight = !openLight;
            if(openLight){
                image.setImageResource(R.drawable.close);
            }else{
                image.setImageResource(R.drawable.open);
            }
            CameraManager camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            String cameraId = null;
            try {
                cameraId = camManager.getCameraIdList()[0];
                camManager.setTorchMode(cameraId, openLight);   //Turn ON
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float batteryPct = level * 100 / (float)scale;
            String icon = FA_BATTERY_4;
            if(80>batteryPct && batteryPct>=60){
                icon = FA_BATTERY_3;
            } else if(60>batteryPct && batteryPct>=40){
                icon = FA_BATTERY_2;
            }else if(40>batteryPct && batteryPct>=20){
                icon = FA_BATTERY_1;
            }else if(20>batteryPct && batteryPct>=0) {
                icon = FA_BATTERY_0;
            }
            String powerText = "電量 :" +batteryPct;
            BootstrapText bootstrapText = new BootstrapText.Builder(MainActivity.this)
                    .addFontAwesomeIcon(icon)
                    .addText(powerText)
                    .build();
            power.setBootstrapText(bootstrapText);
        }
    };


    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        final String[] permissions = new String[]{Manifest.permission.CAMERA};
        ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
    }



}