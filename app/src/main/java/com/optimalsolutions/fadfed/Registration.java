package com.optimalsolutions.fadfed;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.optimalsolutions.fadfed.network.ErrorHandler;
import com.optimalsolutions.fadfed.network.StringResponseHandler;
import com.optimalsolutions.fadfed.utils.Alerts;
import com.optimalsolutions.fadfed.network.NetworkHandler;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Registration extends Activity {

    private Spinner countrysp;
    private EditText usernameet, emailet, passwordet;
    private Button registerbut;
    private ImageButton backbut;
    private RadioGroup sexvaluerg;
    private EditText agesp;
    private static final String name = "Registration Screen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        AppController.setCurrentContext(this);
        setContentView(R.layout.activity_registration);

        agesp = findViewById(R.id.agesp);
        countrysp = findViewById(R.id.countrysp);
        usernameet = findViewById(R.id.usernameet);
        emailet = findViewById(R.id.emailet);
        passwordet = findViewById(R.id.passwordet);
        sexvaluerg = findViewById(R.id.sexvaluerg);
        registerbut = findViewById(R.id.registerbut);
        backbut = findViewById(R.id.backbut);

        countrysp.setAdapter(new ArrayAdapter(this, R.layout.spinner_item, getResources().getStringArray(R.array.arab_countries)));

        registerbut.setOnClickListener(this::registerUser);
        backbut.setOnClickListener(v -> onBackPressed());


    }

    @Override
    protected void onResume() {
        super.onResume();
        AppController.setCurrentContext(this);
        AppController.getInstance().getDefaultTracker().setScreenName(name);
        AppController.getInstance().getDefaultTracker().send(new HitBuilders.ScreenViewBuilder().build());

    }

    private void registerUser(View view) {

        AppController.getInstance().getDefaultTracker().send(new HitBuilders.EventBuilder().setCategory("Action").setAction("Register new user").build());
        final TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        if (usernameet.getText().toString() == null || usernameet.getText().toString().length() <= 0)
            usernameet.setError(getString(R.string.pleaseenternickname));
        else if (passwordet.getText().toString() == null || passwordet.getText().toString().length() <= 0)
            passwordet.setError(getString(R.string.pleaseenterpassword));
        else {

            NetworkHandler.execute(new StringRequest(Method.POST,
                                           AppController.server + "f_register.php",
                                           new StringResponseHandler<String>() {
                                               @Override
                                               public void handleRespone(String response) {
                                                   handleRegisterationResponse(response);
                                               }
                                           },
                                           new ErrorHandler()) {
                                       @Override
                                       protected Map<String, String> getParams() {

                                           return getRegisterationRequestParams();
                                       }

                                       @Override
                                       public Map<String, String> getHeaders() throws AuthFailureError {
                                           Map<String, String> params = new HashMap<String, String>();
                                           params.put("Content-Type", "application/x-www-form-urlencoded");
                                           return params;
                                       }
                                   },
                    false,
                    true);
        }
    }

    private void handleRegisterationResponse(String response) {
        Log.d("test", "registration result :" + response);
        try {
            JSONObject jsonObject = new JSONObject(response);

            if (jsonObject.getInt("resultId") == 9000) {

                AppController.getInstance().saveLoginInfo(usernameet.getText().toString(), passwordet.getText().toString());
                AppController.getInstance().setUserInfo(jsonObject);
                Intent i = new Intent(Registration.this, Home.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                finish();
                startActivity(i);

            } else {
                Alerts.showError(Registration.this, getString(R.string.error_title), jsonObject.getString("resultMessage"));
            }
        } catch (Exception e) {
            Alerts.showError(Registration.this, getString(R.string.error_title), getString(R.string.error_text));
            e.printStackTrace();
        }
    }

    @NonNull
    private Map<String, String> getRegisterationRequestParams() {

        final String country = getResources().getStringArray(R.array.arab_countries_en_symbols)[countrysp.getSelectedItemPosition()];
        final String sex = sexvaluerg.getCheckedRadioButtonId() == R.id.femalerb ? "2" : "1";

        Map<String, String> params = new HashMap<String, String>();
        try {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(Registration.this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                    && telephonyManager.getDeviceId() != null) {

                params.put("imei", telephonyManager.getDeviceId());
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        params.put("username", usernameet.getText().toString());
        params.put("password", passwordet.getText().toString());

        if (emailet.getText().toString().trim().length() > 0)
            params.put("email", emailet.getText().toString());

        params.put("date_of_birth", agesp.getText().toString());
        params.put("gender", sex);
        params.put("country", country);
        params.put("show_birth_date", "1");
        params.put("show_gender", "1");
        params.put("show_country", "1");
        params.put("phone_register_number", AppController.getInstance().getRegisterationId());
        params.put("phone_register_source_type", "1");

        return params;
    }



}
