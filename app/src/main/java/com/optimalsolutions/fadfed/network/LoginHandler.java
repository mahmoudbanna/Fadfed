package com.optimalsolutions.fadfed.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.optimalsolutions.fadfed.AppController;
import com.optimalsolutions.fadfed.GCM.RegistrationService;
import com.optimalsolutions.fadfed.Home;
import com.optimalsolutions.fadfed.Login;
import com.optimalsolutions.fadfed.R;
import com.optimalsolutions.fadfed.utils.Alerts;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mahmoud on 3/7/18.
 *
 */

public class LoginHandler extends BroadcastReceiver {


    public void login() {

        String username = AppController.getInstance().getUserName();
        String password = AppController.getInstance().getPassword();
        Log.d("test", "login: username " + username + " pw :" + password);

        if (username != null && username.length() > 0 && password != null && password.length() > 0)
            serverLogin(username, password);
        else
            Home.open();

    }

    private void serverLogin(String userName, String password) {

        if (!AppController.isBrowseApp() && !AppController.isRegisterationIdExist()) {
            registerDevice();
            return;
        }

        NetworkHandler.execute(new StringRequest(Request.Method.POST,
                AppController.server + "f_login.php",   this::handleLoginRespone,
                new ErrorHandler()) {
            @Override
            protected Map<String, String> getParams() {
                return getLoginParams(userName, password);
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        }, false, true);
    }

    private void handleLoginRespone(String response) {
        try {

            JSONObject jsonObject = new JSONObject(response);

            if (jsonObject.getInt("resultId") == 9000) {
                AppController.getInstance().setUserInfo(jsonObject);
                Home.open();

            } else {
                Alerts.showError(AppController.getCurrentContext(),
                        AppController.getCurrentContext().getString(R.string.error_title),
                        jsonObject.getString("resultMessage"));
                openLoginScreen();
            }
        } catch (Exception e) {

            Alerts.showNetworkError();
            openLoginScreen();
            e.printStackTrace();
        }
    }

    @NonNull
    private static Map<String, String> getLoginParams(String userName, String password) {

        Map<String, String> params = new HashMap<>();
        params.put("username", userName);
        params.put("password", password);

        if (!AppController.isBrowseApp())
            params.put("phone_register_number", AppController.getInstance().getRegisterationId());

        return params;
    }

    private static void openLoginScreen() {

        if (AppController.getCurrentContext() instanceof Login)
            return;

        Intent i = new Intent(AppController.getCurrentContext(), Login.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        AppController.getInstance().startActivity(i);

    }


    private void registerDevice() {

        AppController.getInstance().registerReceiver(this, new IntentFilter(RegistrationService.RGISTERATION_MESSAGE_ACTION));
        Log.d("test", "start registeration service ");
        Intent i = new Intent(AppController.getCurrentContext(), RegistrationService.class);
        AppController.getInstance().startService(i);

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String registrationToken = intent.getExtras().getString("registrationToken");

        if (registrationToken != null) {
            AppController.getInstance().setRegisterationId(registrationToken);
            serverLogin(AppController.getInstance().getUserName(), AppController.getInstance().getPassword());

        } else {
            Alerts.showNetworkError();
        }
    }

}
