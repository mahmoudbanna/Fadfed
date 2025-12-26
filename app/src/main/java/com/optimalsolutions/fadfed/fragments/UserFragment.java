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

import com.google.android.gms.analytics.HitBuilders;
import com.optimalsolutions.fadfed.AppController;
import com.optimalsolutions.fadfed.Home;
import com.optimalsolutions.fadfed.R;
import com.optimalsolutions.fadfed.model.UserInfo;
import com.optimalsolutions.fadfed.listview.FeedItem;
import com.optimalsolutions.fadfed.listview.FeedListAdapter;
import com.optimalsolutions.fadfed.network.AbstractErrorHandler;
import com.optimalsolutions.fadfed.network.ErrorHandler;
import com.optimalsolutions.fadfed.utils.Alerts;
import com.optimalsolutions.fadfed.network.NetworkHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserFragment extends Fragment {

    private static final String TAG = UserFragment.class.getSimpleName();
    private ListView listView;
    private FeedListAdapter listAdapter;
    private ArrayList items;
    private UserInfo userInfo;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int page;
    private boolean canScroll = true;
    private View v;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.userInfo = (UserInfo) getArguments().get("userInfo");
    }

    @Override
    public void onStart() {
        super.onStart();
        refreshContent();
    }


    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (v == null) {

            page = 1;
            items = new ArrayList();
            listAdapter = new FeedListAdapter(getActivity(), items);

            v = inflater.inflate(R.layout.fragment_user, container, false);

            listView = (ListView) v.findViewById(R.id.list);
            listView = (ListView) v.findViewById(R.id.list);
            mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.listsrl);

            listView.setAdapter(listAdapter);

            listView.setOnItemClickListener(new PostClickListener());
            items.add(userInfo);

            mSwipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);

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

                    Log.v("test", "listView.getLastVisiblePosition()  :::"
                            + listView.getLastVisiblePosition()
                            + " :: totalItemCount ::" + totalItemCount);

                    if (listView != null
                            && listView.getChildCount() > 0
                            && canScroll
                            && listView.getLastVisiblePosition() == totalItemCount - 1) {

                        page++;

                        refreshContent();
                        canScroll = false;

                    }
                }
            });
        }
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppController.getInstance().getDefaultTracker().setScreenName("User Info screen	");
        AppController.getInstance().getDefaultTracker().send(new HitBuilders.ScreenViewBuilder().build());
    }

    private class PostClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if (position > 0) {
                PostReviewFragment postReviewFragment = new PostReviewFragment();
                Bundle postReviewFragmentBundle = new Bundle(1);
                postReviewFragmentBundle.putSerializable("feedItem", (FeedItem) items.get(position));
                postReviewFragment.setArguments(postReviewFragmentBundle);

                Home.getInstacne().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_container, postReviewFragment)
                        .addToBackStack(PostReviewFragment.class.getName())
                        .commit();
            }

        }
    }

    private void refreshContent() {

        if (mSwipeRefreshLayout != null)
            mSwipeRefreshLayout.setRefreshing(true);

        String url = AppController.server + "f_get_post.php?userId="
                + AppController.getInstance().getUserId() + "&sessionNumber="
                + AppController.getInstance().getSessionNumber()
                + "&postGroup=5" + "&page=" + this.page + "&specificUserId="
                + userInfo.getUserId();

        Log.v("test", url);

        NetworkHandler.execute(url, null,this::handleGetUserPostResonse,
                new AbstractErrorHandler(){
                    public void handleError() {
                        if (mSwipeRefreshLayout != null)
                            mSwipeRefreshLayout.setRefreshing(false);
                        canScroll = true;
                        page--;
                    }
                }, false,false
        );

    }

    private void handleGetUserPostResonse(JSONObject response) {
        canScroll = true;
        Log.d("test", response.toString());

        if (response != null) {
            parseJsonFeed(response);

            if (mSwipeRefreshLayout != null)
                mSwipeRefreshLayout.setRefreshing(false);
            listAdapter.notifyDataSetChanged();
        }
    }

    private void parseJsonFeed(JSONObject response) {


        try {
            JSONArray feedArray = response.getJSONArray("postList");

            if (feedArray != null && feedArray.length() > 0) {
                // feedItems = new ArrayList<FeedItem>();

                for (int i = 0; i < feedArray.length(); i++) {
                    JSONObject feedObj = (JSONObject) feedArray.get(i);

                    FeedItem item = new FeedItem();

                    try {
                        item.setPostCreatedAt(feedObj
                                .getString("postCreatedAt"));
                    } catch (JSONException e) {
                    }
                    try {
                        item.setPostUnlikeCount(feedObj
                                .getString("postUnlikeCount"));
                    } catch (JSONException e) {
                    }
                    try {
                        item.setPostLikeCount(feedObj
                                .getString("postLikeCount"));
                    } catch (JSONException e) {
                    }
                    try {
                        item.setNickName(feedObj.getString("nickName"));
                    } catch (JSONException e) {
                    }
                    try {
                        item.setUserId(feedObj.getString("userId"));
                    } catch (JSONException e) {
                    }
                    try {
                        item.setPostCommentCount(feedObj
                                .getString("postCommentCount"));
                    } catch (JSONException e) {
                    }
                    try {
                        item.setPostContant(feedObj.getString("postContant"));
                    } catch (JSONException e) {
                    }
                    try {
                        item.setPostId(feedObj.getString("postId"));
                    } catch (JSONException e) {
                    }

                    items.add(item);
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

    public static void openUserProfile(String userId) {


        Home.getInstacne().getHomeActionBar().closeNotifications();

        Log.d("test", "user id ::" + userId);

        if (userId.equalsIgnoreCase(AppController.getInstance().getUserId())) {

            Home.getInstacne().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_container, new UserProfileFragment())
                    .addToBackStack(UserProfileFragment.class.getName())
                    .commit();
            return;

        }

        String url = AppController.server + "f_get_user_profile.php?userId="
                + AppController.getInstance().getUserId() + "&sessionNumber="
                + AppController.getInstance().getSessionNumber()
                + "&userIdProfile=" + userId;

        Log.v("test", url);

        NetworkHandler.execute(url, null, response -> handleGetUserInfoResponse(response ,userId),
                new ErrorHandler(),
                true,
                false
        );
    }

    private static void handleGetUserInfoResponse(JSONObject response,String userId) {
        Log.v("test", response.toString());
        try {
            Log.v("test", response.toString());

            if (response != null  && response.getInt("resultId") == 9000) {

                UserInfo userInfo = new UserInfo();
                userInfo.setUserId(userId);

                try {
                    userInfo.setNickName(response.getString("nickName"));
                } catch (JSONException e) {
                }
                try {
                    userInfo.setBirthDate(response.getString("birthDate"));
                } catch (JSONException e) {
                }
                try {
                    userInfo.setEmail(response.getString("email"));
                } catch (JSONException e) {
                }
                try {
                    userInfo.setCountry(response.getString("country"));
                } catch (JSONException e) {
                }
                try {
                    userInfo.setGender(response.getString("gender"));
                } catch (JSONException e) {
                }
                try {
                    userInfo.setPostCount(response.getString("userPostCount"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    userInfo.setFollowersCount(response.getString("userFollowersCount"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    userInfo.setIsUserForbidden(response.getString("isUserForbidden"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    userInfo.setIsUserFollowed(response.getString("isUserFollowed"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                UserFragment userFragment = new UserFragment();
                Bundle userFragmentBundle = new Bundle();
                userFragmentBundle.putSerializable("userInfo", userInfo);
                userFragment.setArguments(userFragmentBundle);
                Home.getInstacne().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_container, userFragment)
                        .addToBackStack(UserFragment.class.getName())
                        .commit();

            } else {
                Alerts.showError(response.getString("resultMessage"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
