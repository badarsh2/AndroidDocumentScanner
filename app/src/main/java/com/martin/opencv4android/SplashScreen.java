package com.martin.opencv4android;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by k002 on 20/10/16.
 */
public class SplashScreen extends AppCompatActivity {

    private static boolean splashLoaded = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (!splashLoaded) {
            setContentView(R.layout.activity_splash);
            int secondsDelayed = 1;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    startActivity(new Intent(SplashScreen.this, HomeActivity.class));
                    finish();
                }
            }, secondsDelayed * 1500);

            splashLoaded = true;
        }
        else {
            Intent goToMainActivity = new Intent(SplashScreen.this, HomeActivity.class);
            goToMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(goToMainActivity);
            finish();
        }

    }


}