package com.optimalsolutions.fadfed.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.optimalsolutions.fadfed.AppController;

/**
 * Created by mahmoud on 2/24/18.
 *
 */

public class SoftKeypad {

    public static void hideSoftKeypad(View view) {

        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) AppController.getCurrentContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void showSoftKeypad(View view) {

        if (view != null) {

            InputMethodManager inputManager = (InputMethodManager) AppController.getCurrentContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(view, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}
