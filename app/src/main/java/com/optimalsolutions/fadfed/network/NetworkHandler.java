package com.optimalsolutions.fadfed.network;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.optimalsolutions.fadfed.AppController;
import com.optimalsolutions.fadfed.listview.LruBitmapCache;
import com.optimalsolutions.fadfed.utils.Alerts;

import org.json.JSONObject;

/**
 * Created by Mahmoud Banna on 09/09/2016.
 *
 */

public class NetworkHandler {

    private static RequestQueue requestQueue;
    private static ImageLoader mImageLoader;
    private static LruBitmapCache mLruBitmapCache;

    public static void execute(String url, JSONObject jsonRequest, NetworkResponseListener<JSONObject> responseListener,
                               Response.ErrorListener errorListener, boolean showLoading, boolean showProgress) {

        if (showLoading) {
            Alerts.showLoadingDialog();
        }
        if (showProgress) {
            Alerts.showProgressDialog();
        }

        JsonObjectRequest request = new JsonRequest(url, jsonRequest, responseListener, errorListener);
        addToRequestQueue(request);
    }

    public static void execute(String url, NetworkResponseListener<JSONObject> responseListener,
                               Response.ErrorListener errorListener, boolean showLoading, boolean showProgress) {

        if (showLoading) {
            Alerts.showLoadingDialog();
        }
        if (showProgress) {
            Alerts.showProgressDialog();
        }

        JsonObjectRequest request = new JsonRequest(url, null, responseListener, errorListener);
        addToRequestQueue(request);
    }

    public static void execute(StringRequest request, boolean showLoading, boolean showProgress) {

        if (showLoading)
            Alerts.showLoadingDialog();
        if (showProgress)
            Alerts.showProgressDialog();

        addToRequestQueue(request);
    }


    private static <T> void addToRequestQueue(Request<T> req) {

        req.setRetryPolicy(new DefaultRetryPolicy(25000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue().add(req);
    }

    private static RequestQueue getRequestQueue() {

        if (requestQueue == null) {
            // Instantiate the cache
            Cache cache = new DiskBasedCache(AppController.getInstance().getCacheDir(), 1024 * 1024 * 10); // MB cap
            // Set up the network to use HttpURLConnection as the HTTP client.
            Network network = new BasicNetwork(new HurlStack());
            // Instantiate the RequestQueue with the cache and network.
            requestQueue = new RequestQueue(cache, network);
            // Start the queue
            requestQueue.start();
        }

        return requestQueue;
    }

    public static ImageLoader getImageLoader() {

        getRequestQueue();
        if (mImageLoader == null) {
            getLruBitmapCache();
            mImageLoader = new ImageLoader(requestQueue, mLruBitmapCache);
        }

        return mImageLoader;
    }

    private static LruBitmapCache getLruBitmapCache() {

        if (mLruBitmapCache == null)
            mLruBitmapCache = new LruBitmapCache();
        return mLruBitmapCache;
    }

    public void cancelPendingRequests(Object tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }
}
