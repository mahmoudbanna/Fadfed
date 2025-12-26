package com.optimalsolutions.fadfed.view;

import android.annotation.SuppressLint;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.optimalsolutions.fadfed.AppController;
import com.optimalsolutions.fadfed.Home;
import com.optimalsolutions.fadfed.R;
import com.optimalsolutions.fadfed.fragments.ChatFragment;
import com.optimalsolutions.fadfed.fragments.SearchFragment;
import com.optimalsolutions.fadfed.fragments.UserFragment;
import com.optimalsolutions.fadfed.listview.NotificationItem;
import com.optimalsolutions.fadfed.listview.NotificationsListAdapter;
import com.optimalsolutions.fadfed.network.AbstractErrorHandler;
import com.optimalsolutions.fadfed.network.ErrorHandler;
import com.optimalsolutions.fadfed.network.JSONParser;
import com.optimalsolutions.fadfed.network.NetworkHandler;
import com.optimalsolutions.fadfed.tooltip.ToolTip;
import com.optimalsolutions.fadfed.tooltip.ToolTipRelativeLayout;
import com.optimalsolutions.fadfed.tooltip.ToolTipView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahmoud on 3/9/18.
 *
 */

public class ActionBar implements ToolTipView.OnToolTipViewClickedListener, AbsListView.OnScrollListener {

    private Home home;
    private SlideMenu slideMenu;

    private ImageButton menuButton, notificationbut, messagesbut, searchbut, backbut;
    private TextView notificationcounttv, messagescounttv;
    private ToolTipView notificaToolTipView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private boolean tooltipvisible = false;
    private boolean canScroll = true;
    private int scrollState;
    private int page;
    private ListView listView;
    private NotificationsListAdapter listAdapter;
    private List<NotificationItem> notificationItems;
    private static final String TAG = "ActionBar";


    public ActionBar(Home home) {

        page = 1;

        this.home = home;

        home.getActionBar().setDisplayShowHomeEnabled(false);
        home.getActionBar().setDisplayShowTitleEnabled(false);
        home.getActionBar().setDisplayShowCustomEnabled(true);
        home.getActionBar().setCustomView(home.getLayoutInflater().inflate(R.layout.action_bar, null));

        menuButton = home.findViewById(R.id.menubut);
        notificationbut = home.findViewById(R.id.notificationbut);
        notificationcounttv = home.findViewById(R.id.notificationcounttv);
        messagesbut = home.findViewById(R.id.messagesbut);
        messagescounttv = home.findViewById(R.id.messagescounttv);
        searchbut =  home.findViewById(R.id.searchbut);
        backbut =  home.findViewById(R.id.backbut);

        menuButton.setOnClickListener((View v) -> openCloseMenu());
        notificationbut.setOnClickListener((View v) -> setNotificationTooltip());
        notificationcounttv.setOnClickListener((View v) -> setNotificationTooltip());
        messagesbut.setOnClickListener((View v) -> openMessagesScreen());
        messagescounttv.setOnClickListener((View v) -> openMessagesScreen());
        searchbut.setOnClickListener((View v) -> openSearchScreen());
        backbut.setOnClickListener((View v) -> home.back());

        if (AppController.isBrowseApp()) {

            notificationbut.setVisibility(View.GONE);
            notificationcounttv.setVisibility(View.GONE);
            messagesbut.setVisibility(View.GONE);
            messagescounttv.setVisibility(View.GONE);
        }

        slideMenu = new SlideMenu();
    }

    private void openCloseMenu() {

        closeNotifications();

        if (slideMenu.isOpen()) {
            slideMenu.closeMenu();
            return;
        }
        slideMenu.openMenu();
    }

    @SuppressLint("ResourceAsColor")
    private void setNotificationTooltip() {

        if (!tooltipvisible) {

            slideMenu.closeMenu();

            ToolTipRelativeLayout mToolTipFrameLayout = (ToolTipRelativeLayout) home.findViewById(R.id.tooltipframelayout);

            ToolTip notificationtooltip = new ToolTip()
                    .withContentView(LayoutInflater.from(home).inflate(R.layout.custom_tooltip, null))
                    .withColor(home.getResources().getColor(R.color.white))
                    .withAnimationType(ToolTip.AnimationType.FROM_TOP);

            notificaToolTipView = mToolTipFrameLayout.showToolTipForView(notificationtooltip, home.findViewById(R.id.notificationbut));

            notificaToolTipView.setOnToolTipViewClickedListener(this);
            notificationItems = new ArrayList<>();

            listView = (ListView) home.findViewById(R.id.notificationlist);
            listAdapter = new NotificationsListAdapter(notificationItems);
            listView.setAdapter(listAdapter);
            listView.setOnScrollListener(this);
            listView.setOnItemClickListener((parent, view, position, id) -> {

                NotificationItem notificationItem = notificationItems.get(position);
                Log.d(TAG, "setNotificationTooltip: " +notificationItem);
                switch (notificationItem.getType()) {

                    case "1":
                        home.openPostProfile(notificationItem.getPostId());
                        break;
                    case "2":
                        home.openPostProfile(notificationItem.getPostId());
                        break;
                    case "3":
                        home.openPostProfile(notificationItem.getPostId());
                        break;
                    case "4":
                        UserFragment.openUserProfile(notificationItem.getUserId());
                        break;
                    case "6":
                        home.openPostProfile(notificationItem.getPostId());
                        break;
                    case "7":
                        home.openPostProfile(notificationItem.getPostId());
                        break;
                    case "8":
                        home.openPostProfile(notificationItem.getPostId());
                        break;

                    default:
                        break;
                }

            });

            mSwipeRefreshLayout = (SwipeRefreshLayout) notificaToolTipView.findViewById(R.id.listsrl);

            mSwipeRefreshLayout.setColorScheme(
                    android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);

            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    page = 1;
                    refreshNotifications();
                }
            });

            tooltipvisible = true;
            refreshNotifications();

        } else {
            notificaToolTipView.remove();
            tooltipvisible = false;
            notificaToolTipView = null;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {


        if (listView != null && listView.getChildCount() > 0 && canScroll
                && listView.getLastVisiblePosition() == totalItemCount - 1
                && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {


            page++;
            refreshNotifications();
            canScroll = false;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.scrollState = scrollState;
    }

    @Override
    public void onToolTipViewClicked(final ToolTipView toolTipView) {
        closeNotifications();
    }

    private void refreshNotifications() {

        mSwipeRefreshLayout.setRefreshing(true);

        String url = AppController.server + "f_user_notification.php?userId="
                + AppController.getInstance().getUserId() + "&sessionNumber="
                + AppController.getInstance().getSessionNumber() + "&page="
                + this.page;


        NetworkHandler.execute(url, null,this::handleGetUserNotificationResponse,
                new AbstractErrorHandler() {

                    public void handleError() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        canScroll = true;
                        page--;
                    }
                },
                false,
                false
        );
    }


    private void handleGetUserNotificationResponse(JSONObject response) {
        canScroll = true;
        mSwipeRefreshLayout.setRefreshing(false);
        Log.v("test", response.toString());

        if (response != null) {
            parseJsonNotifications(response);
            refreshNotificationsCount();

        }
    }

    private void parseJsonNotifications(JSONObject response) {

        Log.d("test", response.toString());

        if (page == 1) {
            notificationItems.clear();
            listAdapter.notifyDataSetChanged();
        }
        try {
            JSONArray feedArray = response.getJSONArray("notificationList");
            if (feedArray != null && feedArray.length() > 0) {
                // feedItems = new ArrayList<FeedItem>();

                for (int i = 0; i < feedArray.length(); i++) {
                    NotificationItem item = JSONParser.parseNotifications((JSONObject) feedArray.get(i));
                    if (item.getContent() != null && !item.getContent().equalsIgnoreCase("null")) {
                        notificationItems.add(item);
                        if (listAdapter != null)
                            listAdapter.notifyDataSetChanged();
                    }
                }

            } else {
                page--;
            }

        } catch (JSONException e) {
            canScroll = false;
            page--;
        }
    }

    public void refreshNotificationsCount() {

        String url = AppController.server
                + "f_user_notification_count.php?userId=" + AppController.getInstance().getUserId()
                + "&sessionNumber=" + AppController.getInstance().getSessionNumber();


        NetworkHandler.execute(url, null,this::handleGetNotificationCountResponse,
                new ErrorHandler(),
                false,
                false
        );
    }

    private void handleGetNotificationCountResponse(JSONObject response) {
        try {
            if (response != null) {
                if (response.getInt("resultId") == 9000) {
                    notificationcounttv.setText(response.getString("notificationCount"));
                }
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }
    public void refreshMessagesCount() {

        String url = AppController.server
                + "f_user_message_count.php?userId=" + AppController.getInstance().getUserId()
                + "&sessionNumber=" + AppController.getInstance().getSessionNumber();


        NetworkHandler.execute(url, null, (JSONObject response) -> {

            try {
                if (response != null) {
                    if (response.getInt("resultId") == 9000) {
                        messagescounttv.setText(response.getString("messageCount"));
                    }
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }

        },
                new ErrorHandler(), false, false
        );

    }

    private void openMessagesScreen() {

        closeNotifications();
        slideMenu.closeMenu();
        FragmentManager fragmentManager = home.getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_container, new ChatFragment())
                .addToBackStack(ChatFragment.class.getName())
                .commit();
    }

    public void closeNotifications() {

        if (tooltipvisible) {
            notificaToolTipView.remove();
            tooltipvisible = false;
            notificaToolTipView = null;
        }
    }

    private void openSearchScreen() {

        closeNotifications();
        FragmentManager fragmentManager = home.getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_container, new SearchFragment())
                .addToBackStack(SearchFragment.class.getName())

                .commit();
    }


}
