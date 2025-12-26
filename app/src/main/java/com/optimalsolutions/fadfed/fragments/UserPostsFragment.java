package com.optimalsolutions.fadfed.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import com.optimalsolutions.fadfed.MainActivity;
import com.optimalsolutions.fadfed.R;
import com.optimalsolutions.fadfed.listview.FeedItem;
import com.optimalsolutions.fadfed.listview.FeedListAdapter;
import com.optimalsolutions.fadfed.network.AbstractErrorHandler;
import com.optimalsolutions.fadfed.network.JSONParser;
import com.optimalsolutions.fadfed.network.NetworkHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserPostsFragment extends Fragment {

    private static final String TAG ="UserPostsFragment";
    private ListView feedlistView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int page;
    private boolean canScroll = true;
    private FeedListAdapter feedListAdapter;
    private ArrayList<FeedItem> feedItems;
    private View v;


    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (v == null) {

            v = inflater.inflate(R.layout.fragment_user_posts, container, false);
            page = 1;
            feedItems = new ArrayList<>();
            feedListAdapter = new FeedListAdapter(getActivity(), feedItems);

            feedlistView = (ListView) v.findViewById(R.id.list);
            mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.listsrl);

            mSwipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);

            mSwipeRefreshLayout
                    .setOnRefreshListener(() -> {
                        page = 1;
                        feedItems.clear();
                        refreshContent();
                    });


            feedlistView.setAdapter(feedListAdapter);
            feedlistView.setOnScrollListener(new OnScrollListener() {

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem,
                                     int visibleItemCount, int totalItemCount) {

                    if (feedlistView != null && feedlistView.getChildCount() > 0 && canScroll) {

                        if (feedlistView.getFirstVisiblePosition() >= feedItems.size() - 3) {

                            page++;
                            Log.d("test","refresh content");
                            refreshContent();
                            canScroll = false;

                        }
                    }
                }
            });
            refreshContent();
        }
        return v;
    }

    private void refreshContent() {

        if (mSwipeRefreshLayout != null)
            mSwipeRefreshLayout.setRefreshing(true);

        String url = AppController.server + "f_get_post.php?userId="
                + AppController.getInstance().getUserId() + "&sessionNumber="
                + AppController.getInstance().getSessionNumber()
                + "&postGroup=5" + "&page=" + this.page + "&specificUserId="
                + AppController.getInstance().getUserId();

        Log.d("test", url);

        NetworkHandler.execute(url, null,this::handleGetUserPostResponse,
                new AbstractErrorHandler(){

                    public void handleError() {
                        if (mSwipeRefreshLayout != null)
                            mSwipeRefreshLayout.setRefreshing(false);
                        canScroll = false;
                        page--;
                    }
                },false,false);
    }

    private void handleGetUserPostResponse(JSONObject response) {
        canScroll = true;
        Log.d("test", response.toString());

        if (response != null) {
            parseJsonFeed(response);

            if (mSwipeRefreshLayout != null)
                mSwipeRefreshLayout.setRefreshing(false);
            feedListAdapter.notifyDataSetChanged();

        }
    }

    private void parseJsonFeed(JSONObject response) {
        try {
            JSONArray feedArray = response.getJSONArray("postList");

            if (feedArray != null && feedArray.length() > 0) {
                // feedItems = new ArrayList<FeedItem>();

                for (int i = 0; i < feedArray.length(); i++) {
                    JSONObject feedObj = (JSONObject) feedArray.get(i);
                    FeedItem item = JSONParser.parseFeed(feedObj);
                    feedItems.add(item);
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
}
