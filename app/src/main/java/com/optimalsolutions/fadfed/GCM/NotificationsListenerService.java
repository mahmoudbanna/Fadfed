package com.optimalsolutions.fadfed.GCM;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.optimalsolutions.fadfed.AppController;
import com.optimalsolutions.fadfed.Home;
import com.optimalsolutions.fadfed.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NotificationsListenerService extends GcmListenerService {


    public static final String DISPLAY_MESSAGE_ACTION = "com.optimalsolutions.fadfed.DISPLAY_MESSAGE";
    public static final String DISPLAY_CHAT_ACTION = "com.optimalsolutions.fadfed.DISPLAY_CHAT_ACTION";
    public static final String EXTRA_MESSAGE = "message";

    public static final int NOTIFICATION_LIKE_TYPE = 1;
    public static final int NOTIFICATION_UNLIKE_TYPE = 2;
    public static final int NOTIFICATION_COMMENT_TYPE = 3;
    public static final int NOTIFICATION_FOLLOW_TYPE = 4;
    public static final int NOTIFICATION_CHAT_TYPE = 5;
    public static final int NOTIFICATION_FOLLOWER_POST_TYPE = 6;
    public static final int NOTIFICATION_TYPE_COMMENT_LIKE = 7;
    public static final int NOTIFICATION_TYPE_COMMENT_UNLIKE = 8;

    public static final String SHOW_NOTIFCIARION = "SHOW_NOTIFCIARION";
    public static final String SHOW_MESSAGES = "SHOW_MESSAGES";
    public static final String SHOW_POST = "SHOW_POST";
    public static final String SHOW_USER = "SHOW_USER";


    @Override
    public void onMessageReceived(String from, Bundle data) {

        String message = data.getString("message");
        Log.d("test", "message :: " + message);

        if (isRunningInForeground())
            displayMessage(message);
        else
            showNotification(message);

    }

    private void showNotification(String message) {

        try {


            JSONObject notificationJosJsonObject = new JSONObject(message);
            int notificationType = notificationJosJsonObject.getInt("type");

            Intent intent = new Intent(this, Home.class);
            intent.putExtra("id", AppController.getInstance().getRegisterationId());
            intent.putExtra("data", message);

            String notificationMessage = "";

            switch (notificationType) {


                case NOTIFICATION_LIKE_TYPE:

                    intent.setAction(SHOW_POST);
                    notificationMessage = getString(R.string.likeyourpost) + " " + notificationJosJsonObject.getString("fromUserNickName");
                    break;

                case NOTIFICATION_UNLIKE_TYPE:

                    intent.setAction(SHOW_POST);
                    notificationMessage = getString(R.string.unlikeyourpost) + " " + notificationJosJsonObject.getString("fromUserNickName");
                    break;

                case NOTIFICATION_COMMENT_TYPE:

                    intent.setAction(SHOW_POST);
                    notificationMessage = getString(R.string.commentonyourpost) + " " + notificationJosJsonObject.getString("fromUserNickName");
                    break;

                case NOTIFICATION_FOLLOWER_POST_TYPE:

                    intent.setAction(SHOW_POST);
                    notificationMessage = getString(R.string.addnewpost) + " " + notificationJosJsonObject.getString("fromUserNickName");
                    break;

                case NOTIFICATION_TYPE_COMMENT_LIKE:

                    intent.setAction(SHOW_POST);
                    notificationMessage = getString(R.string.likeyourcomment) + " " + notificationJosJsonObject.getString("fromUserNickName");
                    break;

                case NOTIFICATION_TYPE_COMMENT_UNLIKE:

                    intent.setAction(SHOW_POST);
                    notificationMessage = getString(R.string.unlikeyourcomment) + " " + notificationJosJsonObject.getString("fromUserNickName");
                    break;

                case NOTIFICATION_FOLLOW_TYPE:

                    intent.setAction(SHOW_USER);
                    notificationMessage = getString(R.string.followyounow) + " " + notificationJosJsonObject.getString("fromUserNickName");
                    break;

                case NOTIFICATION_CHAT_TYPE:

                    intent.setAction(SHOW_MESSAGES);
                    notificationMessage = getString(R.string.sendmessagetoyou) + " " + notificationJosJsonObject.getString("fromUserNickName");
                    break;

                default:

                    intent.setAction(SHOW_NOTIFCIARION);
                    break;
            }


            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(notificationMessage)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            if (Build.VERSION.SDK_INT >= 21) notificationBuilder.setVibrate(new long[0]);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notificationBuilder.build());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void displayMessage(String message) {

        try {

            JSONObject notificationJosJsonObject = new JSONObject(message);
            int notificationType = notificationJosJsonObject.getInt("type");

            switch (notificationType) {

                case NOTIFICATION_CHAT_TYPE:

                    Intent chatIntent = new Intent(DISPLAY_CHAT_ACTION);
                    chatIntent.putExtra(EXTRA_MESSAGE, message);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(chatIntent);
                    break;


                case NOTIFICATION_COMMENT_TYPE:

                    Intent postIntent = new Intent(DISPLAY_MESSAGE_ACTION);
                    postIntent.putExtra(EXTRA_MESSAGE, message);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(postIntent);
                    break;

                default:

                    Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
                    intent.putExtra(EXTRA_MESSAGE, message);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

                    break;
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private boolean isRunningInForeground() {

        String[] activePackages;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            activePackages = getActivePackages();
        } else {
            activePackages = getActivePackagesCompat();
        }
        if (activePackages != null) {
            for (String activePackage : activePackages) {
                if (activePackage.equals("com.optimalsolutions.fadfed")) {
                    return true;
                }
            }
        }
        return false;
    }

    private String[] getActivePackagesCompat() {

        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningTaskInfo> taskInfo = activityManager.getRunningTasks(1);
        final ComponentName componentName = taskInfo.get(0).topActivity;
        final String[] activePackages = new String[1];
        activePackages[0] = componentName.getPackageName();
        return activePackages;
    }

    private String[] getActivePackages() {

        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final Set<String> activePackages = new HashSet<>();
        final List<ActivityManager.RunningAppProcessInfo> processInfos = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                activePackages.addAll(Arrays.asList(processInfo.pkgList));
            }
        }
        return activePackages.toArray(new String[activePackages.size()]);
    }


}
