package com.optimalsolutions.fadfed;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.google.android.gms.analytics.HitBuilders;
import com.optimalsolutions.fadfed.GCM.NotificationsListenerService;
import com.optimalsolutions.fadfed.fragments.ChatsFragment;
import com.optimalsolutions.fadfed.fragments.HomeFragment;
import com.optimalsolutions.fadfed.fragments.NotificationsFragment;
import com.optimalsolutions.fadfed.fragments.PostReviewFragment;
import com.optimalsolutions.fadfed.fragments.PostsFragment;
import com.optimalsolutions.fadfed.fragments.UserFragment;
import com.optimalsolutions.fadfed.listview.ChannelItem;
import com.optimalsolutions.fadfed.listview.ChatMessage;
import com.optimalsolutions.fadfed.listview.FeedItem;
import com.optimalsolutions.fadfed.network.ErrorHandler;
import com.optimalsolutions.fadfed.network.JSONParser;
import com.optimalsolutions.fadfed.network.LoginHandler;
import com.optimalsolutions.fadfed.network.NetworkHandler;
import com.optimalsolutions.fadfed.view.ActionBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Home extends FragmentActivity  {

    private static final String TAG = Home.class.getSimpleName();

    private static Home instance;

    private ActionBar actionBar;


    private NotificationBroadcastReceiver notificationBroadcastReceiver;
    private ChatBroadcastReceiver chatBroadcastReceiver;

    public static void open() {

        Intent i = new Intent(AppController.getCurrentContext(), Home.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        AppController.getInstance().startActivity(i);
    }

    public static Home getInstacne() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Log.d("test", "home create method ");
        instance = this;
        AppController.setCurrentContext(this);

        FacebookSdk.sdkInitialize(getApplicationContext());



        actionBar = new ActionBar(this);

        if (getIntent() == null || getIntent().getAction() == null
//                || ! getIntent().getAction().equalsIgnoreCase(NotificationsListenerService.SHOW_MESSAGES)
//                || ! getIntent().getAction().equalsIgnoreCase(NotificationsListenerService.SHOW_POST)
//                || ! getIntent().getAction().equalsIgnoreCase(NotificationsListenerService.SHOW_USER)
//                || ! getIntent().getAction().equalsIgnoreCase(NotificationsListenerService.SHOW_NOTIFCIARION)
                ) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container, new HomeFragment())
                    .addToBackStack(HomeFragment.class.getName()).commit();

        } else {
            Log.d("test", "intent  ::" + getIntent().getAction().toString());
            if (MainActivity.isAppRunning()) {
                Log.d("test", "home create method app running  ");
                openIntent();

            } else {
                new LoginHandler().login();
            }
        }
    }

    @Override
    protected void onResume() {

        super.onResume();

        AppController.getInstance().getDefaultTracker().setScreenName("Home Screen");
        AppController.getInstance().getDefaultTracker().send(new HitBuilders.ScreenViewBuilder().build());

        chatBroadcastReceiver = new ChatBroadcastReceiver();
        IntentFilter chatBroadcastReceiverIntentFilter = new IntentFilter(NotificationsListenerService.DISPLAY_CHAT_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(chatBroadcastReceiver, chatBroadcastReceiverIntentFilter);

        notificationBroadcastReceiver = new NotificationBroadcastReceiver();
        IntentFilter notificationBroadcastReceiverIntentFilter = new IntentFilter(NotificationsListenerService.DISPLAY_MESSAGE_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(notificationBroadcastReceiver, notificationBroadcastReceiverIntentFilter);

        actionBar.refreshNotificationsCount();
        actionBar.refreshMessagesCount();

    }

    @Override
    protected void onDestroy() {

        if (chatBroadcastReceiver != null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(chatBroadcastReceiver);
        if (notificationBroadcastReceiver != null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(notificationBroadcastReceiver);


        super.onDestroy();
    }

    private void openIntent() {

        Log.d("test", "Intent Data :: "+ getIntent().getStringExtra("data"));

        if (getIntent() != null && getIntent().getAction() != null) {


            switch (getIntent().getAction()) {

                case NotificationsListenerService.SHOW_POST:


                    try {

                        JSONObject postNotificationObj = new JSONObject(getIntent().getStringExtra("data"));
                        int notificationType = postNotificationObj.getInt("type");
                        FeedItem feedItem = new FeedItem();
                        feedItem.setPostId(postNotificationObj.getString("postId"));
                        openPostProfile(feedItem);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;

                case NotificationsListenerService.SHOW_MESSAGES:

                    try {
                        openChat(new JSONObject(getIntent().getStringExtra("data")).getString("fromUserId"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;

                case NotificationsListenerService.SHOW_USER:

                    try {
                        UserFragment.openUserProfile(new JSONObject(getIntent().getStringExtra("data")).getString("fromUserId"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;

                default:

                    NotificationsFragment notificationsFragment = new NotificationsFragment();
                    Home.getInstacne().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame_container, notificationsFragment)
                            .addToBackStack(NotificationsFragment.class.getName())
                            .commit();
                    break;
            }
        }
    }

    private void openChat(String fromUserId) {

        ChatsFragment chatsFragment = new ChatsFragment();
        Bundle chatsFragmentBundle = new Bundle();
        ChannelItem channelItem = new ChannelItem();
        channelItem.setToUserId(fromUserId);
        chatsFragmentBundle.putSerializable("channelItem", channelItem);
        chatsFragment.setArguments(chatsFragmentBundle);

        Home.getInstacne().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_container, chatsFragment)
                .addToBackStack(ChatsFragment.class.getName())
                .commit();
    }

    public void back() {

        actionBar.closeNotifications();

        if (getSupportFragmentManager().getBackStackEntryCount() <= 1)
            return;
        getSupportFragmentManager().popBackStack();
    }

    public void openPostProfile(FeedItem feedItem) {

        Log.d(TAG, "openPostProfile: "+ feedItem);
        actionBar.closeNotifications();
        PostReviewFragment.openPostReview(feedItem,null,null );
    }

    public void openPostProfile(String feedItemId){

        Log.d(TAG, "openFeedItem: "+feedItemId);

        NetworkHandler.execute(AppController.server + "f_get_post_profile.php?userId="
                        + AppController.getInstance().getUserId() + "&sessionNumber="
                        + AppController.getInstance().getSessionNumber()
                        + "&postId=" + feedItemId,
                response -> openPostProfile(JSONParser.parseFeed(response)) ,
                new ErrorHandler(), false, false);
    }


    @Override
    public void onBackPressed() {


        List<Fragment> fragments = getSupportFragmentManager().getFragments();

        int count = getSupportFragmentManager().getBackStackEntryCount();
        actionBar.closeNotifications();

        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {

            if (fragments.get(count - 1) instanceof ChatsFragment
                    || fragments.get(count - 1) instanceof PostReviewFragment
                    || fragments.get(count - 1) instanceof UserFragment) {

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_container, new HomeFragment())
                        .addToBackStack(HomeFragment.class.getName()).commit();
                return;

            }

            if (fragments.get(count) instanceof PostsFragment) {
                closeApp();
                return;
            }

        } else if (getCurrentFragment() instanceof PostsFragment) {
            closeApp();
            return;
        }


        super.onBackPressed();
    }

    private void closeApp() {

        getIntent().setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
    }

    private Fragment getCurrentFragment() {

        for (int i = getSupportFragmentManager().getFragments().size() - 1; i >= 0; i--) {

            Fragment fragment = getSupportFragmentManager().getFragments().get(i);

            if (fragment != null && !fragment.toString().equalsIgnoreCase("null"))
                return fragment;

        }
        return null;

    }

    public ActionBar getHomeActionBar(){
        return actionBar;
    }

    private class NotificationBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            actionBar.refreshNotificationsCount();

        }
    }

    private class ChatBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("test", "message  recieve to Home screen :: " + intent.getExtras().get(NotificationsListenerService.EXTRA_MESSAGE));
            actionBar.refreshMessagesCount();

            try {

                JSONObject chatJsonObject = new JSONObject(intent.getExtras().getString(NotificationsListenerService.EXTRA_MESSAGE));

                ChatMessage chatMessage = new ChatMessage(
                        chatJsonObject.getString("fromUserId"),
                        chatJsonObject.getString("message"),
                        chatJsonObject.getString("date"),
                        chatJsonObject.getString("fromUserId")
                                .equalsIgnoreCase(AppController.getInstance().getUserId()));


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}