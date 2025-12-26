package com.optimalsolutions.fadfed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.optimalsolutions.fadfed.network.LoginHandler;

public class Login extends Activity {

    private Button userloginbut;
    private EditText usernameet, userpwet;
    private TextView newuserbut;
    private static final String name = "Login Screen";

    private Tracker mTracker;


    public static void show() {

        Intent i = new Intent(Home.getInstacne(), Login.class);
        Home.getInstacne().startActivity(i);
        Home.getInstacne().finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AppController.setCurrentContext(this);

        mTracker = AppController.getInstance().getDefaultTracker();

        userloginbut = (Button) findViewById(R.id.userloginbut);
        newuserbut = (TextView) findViewById(R.id.newuserbut);

        usernameet = (EditText) findViewById(R.id.usernameet);
        userpwet = (EditText) findViewById(R.id.userpwet);

        userloginbut.setOnClickListener((View v)-> login());

        userpwet.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) ->{
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    login();
                    return true;
                }
                return false;
        });

        newuserbut.setOnClickListener((View v)-> startActivity(new Intent(Login.this, Registration.class)));
    }

    @Override
    protected void onResume() {

        super.onResume();
        AppController.setCurrentContext(this);
        usernameet.setText("");
        userpwet.setText("");
        mTracker.setScreenName(name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void login() {

        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Login")
                .build());

        View view = getCurrentFocus();

        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }

        String username = usernameet.getText().toString();
        String password = userpwet.getText().toString();

        if (username == null || username.length() <= 0 || username.trim().equalsIgnoreCase(getString(R.string.nickname)))
            usernameet.setError(getString(R.string.pleaseenterusername));

        else if (password == null || password.length() <= 0 || password.trim().equalsIgnoreCase( getString(R.string.password)))
            userpwet.setError(getString(R.string.pleaseenterpassword));

        else {
            AppController.getInstance().saveLoginUserName(username);
            AppController.getInstance().saveLoginPassword(password);
            new LoginHandler().login();
        }

    }

}
