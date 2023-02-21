package com.scarcamo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.scarcamo.utilities.SharedPreferenceHelper;

public class SplashActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
    private static int SPLASH_TIME_OUT = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                if(SharedPreferenceHelper.getSharedPreferenceBoolean(SplashActivity.this,SharedPreferenceHelper.IS_TUTORIAL_SHOWN,false)){
                    Intent introIntent = new Intent(SplashActivity.this, TabbedActivity.class);
                    startActivity(introIntent);
                }else {
                    SharedPreferenceHelper.setSharedPreferenceBoolean(SplashActivity.this,SharedPreferenceHelper.IS_TUTORIAL_SHOWN,true);
                    Intent introIntent = new Intent(SplashActivity.this, IntroActivity.class);
                    startActivity(introIntent);
                }
                 //My test
            /*    Intent introIntent = new Intent(SplashActivity.this, IntroActivity.class);
                startActivity(introIntent);
                finish();*/
            }
        }, SPLASH_TIME_OUT);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
