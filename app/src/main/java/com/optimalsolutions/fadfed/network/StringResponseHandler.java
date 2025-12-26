package com.optimalsolutions.fadfed.network;

import com.android.volley.Response;
import com.optimalsolutions.fadfed.utils.Alerts;

/**
 * Created by mahmoud on 3/12/18.
 *
 */

public abstract class StringResponseHandler<String> implements Response.Listener<String> {

    @Override
    public void onResponse(String response) {

        Alerts.hideProgressDialog();
        Alerts.hideLoadingsDialog();
        handleRespone(response);
    }

    public abstract void handleRespone(String response);
}
