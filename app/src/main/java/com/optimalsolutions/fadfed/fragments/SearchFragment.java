package com.optimalsolutions.fadfed.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.google.android.gms.analytics.HitBuilders;
import com.optimalsolutions.fadfed.AppController;
import com.optimalsolutions.fadfed.Home;
import com.optimalsolutions.fadfed.R;
import com.optimalsolutions.fadfed.model.UserInfo;
import com.optimalsolutions.fadfed.listview.FeedItem;
import com.optimalsolutions.fadfed.listview.FeedListAdapter;
import com.optimalsolutions.fadfed.listview.UsersListAdapter;
import com.optimalsolutions.fadfed.network.AbstractErrorHandler;
import com.optimalsolutions.fadfed.utils.Alerts;
import com.optimalsolutions.fadfed.network.NetworkHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private static final String TAG = SearchFragment.class.getSimpleName();
    private ListView listView;
    private BaseAdapter listAdapter, useradapter, feedadapter;
    private ArrayList<FeedItem> feedItems;
    private List<UserInfo> users;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String searchText = "";
    private String searchType = "2";
    private EditText searchet;
    private RadioGroup searchtyperg;
    private Button searchbut;

    private int page;
    private boolean canScroll = true;
    private View v;

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_search, container, false);
            page = 1;

            feedItems = new ArrayList<FeedItem>();
            users = new ArrayList<UserInfo>();

            useradapter = new UsersListAdapter(users);
            feedadapter = new FeedListAdapter(getActivity(), feedItems);

            listView = (ListView) v.findViewById(R.id.list);
            searchet = (EditText) v.findViewById(R.id.searchet);
            searchtyperg = (RadioGroup) v.findViewById(R.id.searchtyperg);
            searchbut = (Button) v.findViewById(R.id.searchbut);

            mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.listsrl);

            mSwipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);

            mSwipeRefreshLayout.setOnRefreshListener(this::handleRefreshResponse);
            listView.setOnItemClickListener(new PostClickListener());

            listAdapter = feedadapter;
            listView.setAdapter(listAdapter);

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

                        Log.v("test", "page ::::" + page);
                        page++;

                        refreshContent();
                        canScroll = false;

                    }
                }
            });

            searchet.setOnEditorActionListener(new OnEditorActionListener() {

                public boolean onEditorAction(TextView v, int actionId,
                                              KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        search();
                        return true;
                    }
                    return false;
                }
            });

            searchbut.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    search();

                }
            });
        }
        return v;
    }

    private void handleRefreshResponse() {
        page = 1;
        Log.v("test", "on refresh page ::::" + page);
        searchType = searchtyperg.getCheckedRadioButtonId() == R.id.postsearchrb ? "2"
                : "1";

        if (searchType.equalsIgnoreCase("2")) {
            feedItems.clear();
            listAdapter = feedadapter;

        } else {
            users.clear();
            listAdapter = useradapter;

        }
        listView.setAdapter(listAdapter);
        refreshContent();
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        AppController.getInstance().getDefaultTracker().setScreenName("Search screen");
        AppController.getInstance().getDefaultTracker().send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void search() {

        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }

        try {
            searchText = URLEncoder.encode(searchet.getText().toString(),
                    "utf-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        searchType = searchtyperg.getCheckedRadioButtonId() == R.id.postsearchrb ? "2"
                : "1";

        page = 1;

        if (searchType.equalsIgnoreCase("2")) {
            feedItems.clear();
            listAdapter = feedadapter;
            listView.setOnItemClickListener(new PostClickListener());
        } else {
            users.clear();
            listAdapter = useradapter;
            listView.setOnItemClickListener(new UserClickListener());
        }
        listView.setAdapter(listAdapter);
        refreshContent();
    }

    private void refreshContent() {

        mSwipeRefreshLayout.setRefreshing(true);

        searchType = searchtyperg.getCheckedRadioButtonId() == R.id.postsearchrb ? "2" : "1";

        String url = AppController.server + "f_search.php?userId="
                + AppController.getInstance().getUserId() + "&sessionNumber="
                + AppController.getInstance().getSessionNumber() + "&keyword="
                + searchText + "&searchType=" + searchType + "&page="
                + this.page;

        Log.v("test", url);

        NetworkHandler.execute(url, null,this::handleSearchResponse,
                new AbstractErrorHandler() {

                    public void handleError() {
                        canScroll = true;
                        page--;
                    }
                },false,false);

    }

    private void handleSearchResponse(JSONObject response) {
        Log.v("test", response.toString());

        if (response != null) {
            if (searchType.equalsIgnoreCase("2")) {
                parseJsonFeed(response);

            } else {
                parseJsonUser(response);
            }

            mSwipeRefreshLayout.setRefreshing(false);
            listAdapter.notifyDataSetChanged();

        }
    }

    private void parseJsonFeed(JSONObject response) {

        canScroll = true;

        try {
            JSONArray feedArray = response.getJSONArray("searchList");

            if (feedArray != null && feedArray.length() > 0) {

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

                    feedItems.add(item);
                }

            } else {
                page--;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            canScroll = false;
            page--;

        }
        if (feedItems.size() <= 0) {
            Alerts.showError(
                    getString(R.string.nodatafound));
        }
    }

    private void parseJsonUser(JSONObject response) {

        canScroll = true;

        try {
            JSONArray feedArray = response.getJSONArray("searchList");

            if (feedArray != null && feedArray.length() > 0) {
                // feedItems = new ArrayList<FeedItem>();

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
                }

            } else {
                page--;
            }

        } catch (JSONException e) {
            canScroll = false;
            e.printStackTrace();
            page--;
        }
        if (users.size() <= 0) {
            Alerts.showError(getString(R.string.error_title),
                    getString(R.string.nodatafound));
        }
    }

    private class PostClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {

            PostReviewFragment postReviewFragment = new PostReviewFragment();
            Bundle postReviewFragmentBundle = new Bundle(1);
            postReviewFragmentBundle.putSerializable("feedItem", feedItems.get(position));
            postReviewFragment.setArguments(postReviewFragmentBundle);

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.frame_container, postReviewFragment)
                    .addToBackStack(PostReviewFragment.class.getName())
                    .commit();

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
