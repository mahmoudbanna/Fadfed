package com.optimalsolutions.fadfed;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.optimalsolutions.fadfed.network.LoginHandler;

public class MainActivity extends Activity {

    private static boolean appRunning = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppController.setCurrentContext(this);
    }

    @Override
    protected void onResume() {

        super.onResume();
        appRunning = true;
        new LoginHandler().login();

    }

    public static boolean isAppRunning() {
        return appRunning;
    }
}
