package com.optimalsolutions.fadfed.network;

import com.android.volley.Response;
import com.optimalsolutions.fadfed.utils.Alerts;

/**
 * Created by mahmoud on 3/13/18.
 *
 */

public interface NetworkResponseListener<T> extends Response.Listener<T> {

    @Override
    default void onResponse(T response) {

        Alerts.hideLoadingsDialog();
        Alerts.hideProgressDialog();
        response(response);

    }
    void response(T response);
}
