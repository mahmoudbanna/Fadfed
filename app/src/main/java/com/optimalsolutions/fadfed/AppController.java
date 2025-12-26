package com.optimalsolutions.fadfed;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONException;
import org.json.JSONObject;


public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();


    private String userId, sessionNumber, email, nickName, showCountry, gender,
            showGender, birthDate, showBirthDate, country, registerationId;

    public static final String server = "http://www.a-m-sh.com/fadfed_v2/";

    private static AppController mInstance;
    private Tracker mTracker;

    public static Context getCurrentContext() {
        return currentContext;
    }

    public static void setCurrentContext(Context currentContext) {
        AppController.currentContext = currentContext;
    }

    private static Context currentContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

    }


    public synchronized Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSessionNumber() {
        return sessionNumber;
    }

    public void setSessionNumber(String sessionNumber) {
        this.sessionNumber = sessionNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getShowCountry() {
        return showCountry;
    }

    public void setShowCountry(String showCountry) {
        this.showCountry = showCountry;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getShowGender() {
        return showGender;
    }

    public void setShowGender(String showGender) {
        this.showGender = showGender;
    }

    public String getShowBirthDate() {
        return showBirthDate;
    }

    public void setShowBirthDate(String showBirthDate) {
        this.showBirthDate = showBirthDate;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }


    public String getRegisterationId() {

        SharedPreferences settings = getApplicationContext().getSharedPreferences("fadfedRegisterationId", 0);
        registerationId = settings.getString("registerationId", null);
        return registerationId;
    }

    public void setRegisterationId(String registerationId) {

        this.registerationId = registerationId;
        SharedPreferences settings = getApplicationContext().getSharedPreferences("fadfedRegisterationId", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("registerationId", registerationId);

        editor.apply();
    }

    public void saveLoginInfo(String username, String password) {

        SharedPreferences settings = getApplicationContext().getSharedPreferences("fadfedlogin", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.apply();
    }

    public void saveLoginUserName(String username) {

        SharedPreferences settings = getApplicationContext().getSharedPreferences("fadfedlogin", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("username", username);
        editor.apply();
    }

    public void saveLoginPassword(String password) {

        SharedPreferences settings = getApplicationContext().getSharedPreferences("fadfedlogin", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("password", password);
        editor.apply();

    }

    public void deleteLoginInfo() {

        SharedPreferences settings = getApplicationContext().getSharedPreferences("fadfedlogin", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.apply();

    }

    public String getUserName() {
        SharedPreferences settings = getApplicationContext().getSharedPreferences("fadfedlogin", 0);
        return settings.getString("username", null);
    }

    public String getPassword() {
        SharedPreferences settings = getApplicationContext().getSharedPreferences("fadfedlogin", 0);
        return settings.getString("password", null);
    }
    public static boolean isBrowseApp() {

        return ! (getInstance().getUserName() != null && getInstance().getUserName().length() > 0 );
    }

    public static boolean isRegisterationIdExist() {

        return AppController.getInstance().getRegisterationId() != null &&
                AppController.getInstance().getRegisterationId().length() > 0;
    }

    public void setUserInfo(JSONObject jsonObject) {
        try {
            String sessionNumber = jsonObject.getString("sessionNumber");
            AppController.getInstance().setSessionNumber(sessionNumber);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        try {
            String userId = jsonObject.getString("userId");
            AppController.getInstance().setUserId(userId);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        try {
            String email = jsonObject.getString("email");
            AppController.getInstance().setEmail(email);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        try {
            String nickName = jsonObject.getString("nickName");
            AppController.getInstance().setNickName(nickName);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        try {
            String showCountry = jsonObject.getString("showCountry");
            AppController.getInstance().setShowCountry(showCountry);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        try {
            String gender = jsonObject.getString("gender");
            AppController.getInstance().setGender(gender);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        try {
            String showGender = jsonObject.getString("showGender");
            AppController.getInstance().setShowGender(showGender);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        try {
            String birthday = jsonObject.getString("birthDate");
            AppController.getInstance().setBirthDate(birthday);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        try {
            String showBirthDate = jsonObject.getString("showBirthDate");
            AppController.getInstance().setShowBirthDate(showBirthDate);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        try {
            String country = jsonObject.getString("country");
            AppController.getInstance().setCountry(country);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }
}