package com.optimalsolutions.fadfed.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.optimalsolutions.fadfed.AppController;
import com.optimalsolutions.fadfed.Home;
import com.optimalsolutions.fadfed.R;
import com.optimalsolutions.fadfed.listview.NotificationItem;
import com.optimalsolutions.fadfed.listview.NotificationsListAdapter;
import com.optimalsolutions.fadfed.network.AbstractErrorHandler;
import com.optimalsolutions.fadfed.network.NetworkHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private static final String TAG = NotificationsFragment.class.getSimpleName();
    private ListView listView;
    private NotificationsListAdapter listAdapter;
    private List<NotificationItem> notificationItems;
    private int page;
    private boolean canScroll = true;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View v;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (v == null) {

            page = 1;

            v = inflater.inflate(R.layout.fragment_notifcation, null);
            listView = (ListView) v.findViewById(R.id.notificationlist);
            notificationItems = new ArrayList<NotificationItem>();
            listAdapter = new NotificationsListAdapter(notificationItems);
            listView.setAdapter(listAdapter);

            listView.setOnScrollListener(new OnScrollListener() {

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                    if (listView != null && listView.getChildCount() > 0 && canScroll && listView.getLastVisiblePosition() == totalItemCount - 1) {

                        Log.v("test", "page ::::" + page);
                        page++;
                        refreshNotifications();
                        canScroll = false;
                    }
                }
            });

            mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.listsrl);

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

            refreshNotifications();

        }

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        refreshNotifications();
    }

    private void refreshNotifications() {

        mSwipeRefreshLayout.setRefreshing(true);

        String url = AppController.server + "f_user_notification.php?userId="
                + AppController.getInstance().getUserId() + "&sessionNumber="
                + AppController.getInstance().getSessionNumber() + "&page="
                + this.page;


        NetworkHandler.execute(url, null,this::handleRefreshNotificationsResponse,
                new AbstractErrorHandler(){

                    public void handleError() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        canScroll = true;
                        page--;
                    }
                },
                false,false
        );

    }

    private void handleRefreshNotificationsResponse(JSONObject response) {
        canScroll = true;
        mSwipeRefreshLayout.setRefreshing(false);
        Log.v("test", response.toString());

        if (response != null) {

            parseJsonNotifications(response);
            Home.getInstacne().getHomeActionBar().refreshNotificationsCount();

        }
    }

    private void parseJsonNotifications(JSONObject response) {


        if (page == 1) {
            notificationItems.clear();
            listAdapter.notifyDataSetChanged();
        }
        try {
            JSONArray feedArray = response.getJSONArray("notificationList");

            if (feedArray != null && feedArray.length() > 0) {
                // feedItems = new ArrayList<FeedItem>();

                for (int i = 0; i < feedArray.length(); i++) {

                    JSONObject notificationObj = (JSONObject) feedArray.get(i);

                    NotificationItem item = new NotificationItem();

                    try {
                        item.setNotificationId(notificationObj.getString("notificationId"));
                    } catch (JSONException e) {
                    }
                    try {
                        item.setType(notificationObj.getString("type"));
                    } catch (JSONException e) {
                    }
                    try {
                        item.setStatus(notificationObj.getString("status"));
                    } catch (JSONException e) {
                    }
                    try {
                        item.setCreatedAt(notificationObj.getString("createdAt"));
                    } catch (JSONException e) {
                    }
                    try {
                        item.setUserId(notificationObj.getString("userId"));
                    } catch (JSONException e) {
                    }
                    try {
                        item.setContent(notificationObj
                                .getString("notificationContent"));
                    } catch (JSONException e) {
                    }

                    try {
                        item.setUserId(notificationObj.getString("userId"));
                    } catch (JSONException e) {
                    }

                    try {
                        item.setUserName(notificationObj.getString("nickName"));
                    } catch (JSONException e) {
                    }

                    try {
                        item.setPostId(notificationObj.getString("postId"));
                    } catch (JSONException e) {
                    }
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


}
