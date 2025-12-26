package com.optimalsolutions.fadfed.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.optimalsolutions.fadfed.AppController;
import com.optimalsolutions.fadfed.Home;
import com.optimalsolutions.fadfed.Login;
import com.optimalsolutions.fadfed.R;

import java.util.Calendar;

public class Alerts {

    private static AlertDialog alertDialog;
    private static Dialog loginDialog;
    private static ProgressDialog progressDialog;
    private static ProgressDialog loadingDailog;


    public static void showDatePickerDialog(DatePickerDialog.OnDateSetListener listener) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        // Create a new instance of DatePickerDialog and return it
        new DatePickerDialog(AppController.getCurrentContext(), listener, year, month, day).show();
    }


    public static void showProgressDialog() {

        if(AppController.getCurrentContext()== null || Home.getInstacne() == null || Home.getInstacne().isFinishing())
            return;

        if (progressDialog == null || !progressDialog.isShowing())
            progressDialog = ProgressDialog.show(AppController.getCurrentContext(),
                    AppController.getInstance().getString(R.string.app_name),
                    AppController.getInstance().getString(R.string.operationunderprogress));

    }

    public static void hideProgressDialog() {

        if(AppController.getCurrentContext()== null || Home.getInstacne() == null || Home.getInstacne().isFinishing())
            return;

        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }


    public static void showLoadingDialog() {

        if(AppController.getCurrentContext()== null || Home.getInstacne() == null || Home.getInstacne().isFinishing())
            return;

        if (loadingDailog == null || !loadingDailog.isShowing())
            loadingDailog = ProgressDialog.show(AppController.getCurrentContext(),
                    AppController.getInstance().getString(R.string.app_name),
                    AppController.getInstance().getString(R.string.loadingdata));

    }

    public static void hideLoadingsDialog() {

        if(AppController.getCurrentContext()== null || Home.getInstacne() == null || Home.getInstacne().isFinishing())
            return;

        if (loadingDailog != null && loadingDailog.isShowing())
                loadingDailog.dismiss();
    }


    public static void showError(String title, String message) {

        if(AppController.getCurrentContext()== null || Home.getInstacne() == null || Home.getInstacne().isFinishing())
            return;

        if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
        if (alertDialog != null && alertDialog.isShowing()) progressDialog.dismiss();
        if (alertDialog == null)
            alertDialog = new android.app.AlertDialog.Builder(AppController.getCurrentContext())
                    .setNegativeButton(R.string.ok, (DialogInterface dialog, int which)->dialog.dismiss())
                    .create();

        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        try {
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void showError(String msg) {

        showError(AppController.getInstance().getString(R.string.error_title), msg);
    }

    public static void showError(Context context, String title, String msg) {

        if(AppController.getCurrentContext()== null || Home.getInstacne() == null || Home.getInstacne().isFinishing())
            return;

        if (context instanceof Activity && !((Activity) context).isFinishing()) {
            new android.app.AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(msg)
                    .setNegativeButton(R.string.ok,(DialogInterface dialog, int which) ->
                            dialog.dismiss()
                    ).show();
        }
    }

    private static void showErrorWithExit(final Context context, String title, String msg) {
        if (context instanceof Activity && !((Activity) context).isFinishing()) {
            new android.app.AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(msg)
                    .setNegativeButton(R.string.ok, Alerts::closeApp)
                    .show();
        }
    }

    private static void closeApp(DialogInterface dialog, int which) {
        dialog.dismiss();
        ((Activity) AppController.getCurrentContext()).finish();
    }
    public static void showNetworkError(){

        showErrorWithExit(AppController.getCurrentContext(),
                AppController.getCurrentContext().getString(R.string.app_name),
                AppController.getCurrentContext().getString(R.string.internet_error));
    }

    public static void showError(int title, int msg) {

        showError(AppController.getInstance().getString(title), AppController.getInstance().getString(msg));
    }


    public static void showSuccessMessage() {

        showError(AppController.getInstance().getString(R.string.error_title), AppController.getInstance().getString(R.string.success_message));

    }

    public static void showParsingError(){

        showError(AppController.getInstance().getString(R.string.error_title), AppController.getInstance().getString(R.string.parsing_error));

    }

    public static void showLogin() {

        if (loginDialog != null && loginDialog.isShowing())
            return;
        if (loginDialog == null) {
            loginDialog = createLoginDialog();
        }
        try {
            loginDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static Dialog createLoginDialog() {

        return new AlertDialog.Builder(AppController.getCurrentContext())
                .setTitle(AppController.getInstance().getString(R.string.app_name))
                .setMessage(AppController.getInstance().getString(R.string.pleaselogin))
                .setNegativeButton(R.string.cancel,(DialogInterface dialog, int which) ->dialog.dismiss())
                .setPositiveButton(R.string.login, (DialogInterface dialog, int which) ->openHomeScreen())
                .create();

    }

    private static void openHomeScreen() {

        Intent i = new Intent(AppController.getInstance().getApplicationContext(), Login.class);
        i.putExtra("id", AppController.getInstance().getRegisterationId());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        AppController.getInstance().getApplicationContext().startActivity(i);
        Home.getInstacne().finish();
    }

}
