package com.optimalsolutions.fadfed.GCM;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.optimalsolutions.fadfed.MainActivity;
import com.optimalsolutions.fadfed.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class GCMIntentService extends GCMBaseIntentService {

    static final String DISPLAY_MESSAGE_ACTION = "com.optimalsolutions.fadfed.DISPLAY_MESSAGE";
    static final String EXTRA_MESSAGE = "message";
    static final String MESSAGE_TYPE = "type";
    static final int REGISTERD = 1;
    static final int UNREGISTERD = 2;
    static final int MESSAGE = 3;
    static final int MESSAGE_DELETED = 4;
    static final int ERROR = 5;
    static final int RCOVERED_ERROR = 6;

    public GCMIntentService() {
        super("451113990366");
    }

    /**
     * Method called on device registered
     **/
    @Override
    protected void onRegistered(Context context, String registrationId) {

        Log.v("test", "Device registered: regId = " + registrationId);
        displayMessage(context, REGISTERD, registrationId);

    }

    /**
     * Method called on device un registred
     */
    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.v("test", "Device unregistered");
        displayMessage(context, UNREGISTERD, getString(R.string.gcm_unregistered));
    }

    /**
     * Method called on Receiving a new message
     */
    @Override
    protected void onMessage(Context context, Intent intent) {

        String message = intent.getExtras().getString("message");
        Log.v("test", "Received message :::" + message);


        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> services = activityManager.getRunningTasks(Integer.MAX_VALUE);

        boolean isActivityFound = false;

        if (services.get(0).topActivity.getPackageName().toString()
                .equalsIgnoreCase(context.getPackageName().toString())) {
            isActivityFound = true;
        }

        if ( isActivityFound) {
            displayMessage(context, MESSAGE, message);
        }
        else{
            generateNotification(context, message);
        }
    }

    /**
     * Method called on receiving a deleted message
     */
    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
//        String message = getString(R.string.gcm_deleted, total);
//        displayMessage(context, MESSAGE_DELETED, message);
        // notifies user
//        generateNotification(context, message);
    }

    /**
     * Method called on Error
     */
    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
//        displayMessage(context, ERROR, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
//        displayMessage(context, RCOVERED_ERROR,
//                getString(R.string.gcm_recoverable_error, errorId));
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String message) {

        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();


        try {
            JSONObject jsonObj = new JSONObject(message);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = new Notification(icon,message, when);

            String title = context.getString(R.string.app_name);

            Intent notificationIntent = new Intent(context, MainActivity.class);
            notificationIntent.putExtra("message", message);

            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingNotificationIntent = PendingIntent.getActivity(context,0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);

//            notification.setLatestEventInfo(context , title, jsonObj.getString("message") , pendingNotificationIntent);

            notification.defaults |= Notification.DEFAULT_SOUND;
            // Vibrate if vibrate is enabled
            notification.defaults |= Notification.DEFAULT_VIBRATE;
                notificationManager.notify(0, notification);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

    }

    public static void displayMessage(Context context, int messageType, String message) {

        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(MESSAGE_TYPE, messageType);
        intent.putExtra(EXTRA_MESSAGE, message);

        context.sendBroadcast(intent);
    }
}
