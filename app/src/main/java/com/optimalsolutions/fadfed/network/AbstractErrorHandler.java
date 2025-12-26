package com.optimalsolutions.fadfed.network;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.ClientError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.optimalsolutions.fadfed.utils.Alerts;

/**
 * Created by mahmoud on 3/12/18.
 *
 */

public abstract class AbstractErrorHandler implements Response.ErrorListener{

    @Override
    public void onErrorResponse(VolleyError error) {

        Alerts.hideProgressDialog();
        Alerts.hideLoadingsDialog();

        if( error instanceof NetworkError) {
            Log.d("test" , "network  error");
            Alerts.showNetworkError();

        } else if( error instanceof ClientError) {
            Log.d("test" , "client error");

        } else if( error instanceof ServerError) {

            Log.d("test" , "server error");
            Alerts.showNetworkError();

        } else if( error instanceof AuthFailureError) {

            Log.d("test" , "AuthFailureError error");
            Alerts.showNetworkError();

        } else if( error instanceof ParseError) {

            Log.d("test" , "ParseError error");
            Alerts.showNetworkError();

        } else if( error instanceof NoConnectionError) {

            Log.d("test" , "NoConnectionError error");
            Alerts.showNetworkError();


        } else if( error instanceof TimeoutError) {

            Log.d("test" , "TimeoutError error");
            Alerts.showNetworkError();

        }

        handleError();
    }

    public abstract void handleError();
}