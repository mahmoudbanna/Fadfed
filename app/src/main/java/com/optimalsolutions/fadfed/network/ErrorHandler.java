package com.optimalsolutions.fadfed.network;

import com.optimalsolutions.fadfed.utils.Alerts;

/**
 * Created by mahmoud on 3/12/18.
 *
 */


public class ErrorHandler extends AbstractErrorHandler {

    @Override
    public void handleError() {
        Alerts.hideProgressDialog();
        Alerts.hideLoadingsDialog();

    }
}