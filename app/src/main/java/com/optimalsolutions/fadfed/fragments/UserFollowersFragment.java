package com.optimalsolutions.fadfed.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ListView;

import com.optimalsolutions.fadfed.AppController;
import com.optimalsolutions.fadfed.Home;
import com.optimalsolutions.fadfed.R;
import com.optimalsolutions.fadfed.model.UserInfo;
import com.optimalsolutions.fadfed.listview.UsersListAdapter;
import com.optimalsolutions.fadfed.network.AbstractErrorHandler;
import com.optimalsolutions.fadfed.network.NetworkHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserFollowersFragment extends Fragment {

    private ListView listView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int page;
    private boolean canScroll = true;
    private List<UserInfo> users;
    private UsersListAdapter listAdapter;
    private View v;


    @Override
    public void onStart() {
        super.onStart();
        refreshContent();
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (v == null) {
            page = 1;
            users = new ArrayList<UserInfo>();
            listAdapter = new UsersListAdapter(users);

            v = inflater.inflate(R.layout.fragment_user_posts, container,
                    false);

            listView = (ListView) v.findViewById(R.id.list);
            mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.listsrl);

            mSwipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);

            listView.setOnItemClickListener(new UserClickListener());
            listView.setAdapter(listAdapter);

            mSwipeRefreshLayout
                    .setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            page = 1;
                            refreshContent();
                        }
                    });

            listView.setOnScrollListener(new OnScrollListener() {

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem,
                                     int visibleItemCount, int totalItemCount) {

                    if (listView != null && listView.getChildCount() > 0
                            && canScroll) {

                        if (listView.getFirstVisiblePosition() >= users.size() - 3) {

                            Log.v("test", "page ::::" + page);
                            page++;
                            refreshContent();
                            canScroll = false;

                        }
                    }
                }
            });

        }

        return v;
    }

    private void refreshContent() {
        if (mSwipeRefreshLayout != null)
            mSwipeRefreshLayout.setRefreshing(true);

        String url = AppController.server + "f_get_user_followers.php?userId="
                + AppController.getInstance().getUserId() + "&sessionNumber="
                + AppController.getInstance().getSessionNumber() + "&page="
                + this.page;

        Log.v("test", url);

        NetworkHandler.execute(url, null,this::handleGetUserFollowersResponse,
                new AbstractErrorHandler(){

                    public void handleError() {
                        canScroll = true;
                        page--;

                    }
                },false, false
        );
    }

    private void handleGetUserFollowersResponse(JSONObject response) {
        canScroll = true;
        Log.v("test", response.toString());

        if (response != null) {
            parseJsonUser(response);
            if (mSwipeRefreshLayout != null)
                mSwipeRefreshLayout.setRefreshing(false);
            listAdapter.notifyDataSetChanged();
        }
    }

    private void parseJsonUser(JSONObject response) {
        if (page == 1) {
            users.clear();
            listAdapter.notifyDataSetChanged();
        }
        canScroll = true;

        try {
            JSONArray feedArray = response.getJSONArray("searchList");

            if (feedArray != null && feedArray.length() > 0) {

                for (int i = 0; i < feedArray.length(); i++) {
                    JSONObject feedObj = (JSONObject) feedArray.get(i);

                    UserInfo item = new UserInfo();
                    try {
                        item.setUserId(feedObj.getString("userId"));
                    } catch (JSONException e) {
                    }
                    try {
                        item.setNickName(feedObj.getString("nickName"));
                    } catch (JSONException e) {
                    }
                    try {
                        item.setBirthDate(feedObj.getString("birthDate"));
                    } catch (JSONException e) {
                    }
                    try {
                        item.setEmail(feedObj.getString("email"));
                    } catch (JSONException e) {
                    }
                    try {
                        item.setCountry(feedObj.getString("country"));
                    } catch (JSONException e) {
                    }
                    try {
                        item.setGender(feedObj.getString("gender"));
                    } catch (JSONException e) {
                    }
                    users.add(item);
                    if (listAdapter != null)
                        listAdapter.notifyDataSetChanged();
                }

            } else {
                page--;
            }

        } catch (JSONException e) {
            canScroll = false;
            e.printStackTrace();
            page--;
        }
    }

    private class UserClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            UserFragment userFragment = new UserFragment();
            Bundle userFragmentBundle = new Bundle();
            userFragmentBundle.putSerializable("userInfo", users.get(position));
            userFragment.setArguments(userFragmentBundle);

            Home.getInstacne()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container, userFragment)
                    .addToBackStack(UserFragment.class.getName())
                    .commit();

        }
    }
}
