package com.optimalsolutions.fadfed.fragments;

import android.annotation.SuppressLint;
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

import com.google.android.gms.analytics.HitBuilders;
import com.optimalsolutions.fadfed.AppController;
import com.optimalsolutions.fadfed.R;
import com.optimalsolutions.fadfed.listview.FeedItem;
import com.optimalsolutions.fadfed.listview.FeedListAdapter;
import com.optimalsolutions.fadfed.listview.PostsRefresher;
import com.optimalsolutions.fadfed.network.JSONParser;
import com.optimalsolutions.fadfed.network.NetworkHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PostsFragment extends Fragment implements OnScrollListener {

    public static final String NEW_POSTS = "1";
    public static final String MOST_ACTIVE_POSTS = "2";
    public static final String FOLLOWERS_POSTS = "3";

    private static final String TAG = PostsFragment.class.getSimpleName();
    private ListView listView;
    private FeedListAdapter listAdapter;
    private ArrayList<FeedItem> feedItems;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String strPostGroup;
    private int page;
    private int scrollState;
    private boolean canScroll = true;
    private String categoryId;
    private View v;

    public static PostsFragment of(String group) {

        PostsFragment postsFragment = new PostsFragment();
        Bundle postBundles = new Bundle(2);
        postBundles.putString("group", group);
        postBundles.putString("cat", "");
        postsFragment.setArguments(postBundles);

        return postsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.strPostGroup = getArguments().getString("group");
        this.categoryId = getArguments().getString("cat");
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (v == null) {

            page = 1;
            feedItems = new ArrayList<FeedItem>();
            listAdapter = new FeedListAdapter(getActivity(), feedItems);
            v = inflater.inflate(R.layout.fragment_posts, null);

            listView = (ListView) v.findViewById(R.id.list);
            mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.listsrl);

            mSwipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);

            mSwipeRefreshLayout.setOnRefreshListener(() -> {
                page = 1;
                refreshPosts();

            });

//            listView.setOnItemClickListener((adapterView, view, position, l) -> {
//
//                        PostReviewFragment.openPostReview(feedItems.get(position), this::refreshPost);
//                    }
//            );

            listView.setAdapter(listAdapter);
            listView.setOnScrollListener(this);
        }

        return v;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        refreshPosts();
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("test", "on post fragment onResume");

        AppController.getInstance().getDefaultTracker().setScreenName("Posts group " + strPostGroup + " category " + categoryId + " Screen");
        AppController.getInstance().getDefaultTracker().send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        this.scrollState = scrollState;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

//        if(scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
//            Log.d("test", "onScroll ::: page ::" + page);

        if (listView != null
                && listView.getChildCount() > 0
                && canScroll
                && listView.getLastVisiblePosition() == totalItemCount - 1
                && scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {

            Log.d("test", "page ::::" + page);
            page++;
            refreshPosts();
            canScroll = false;

        }
    }


    public void refreshPosts() {

//        if (mSwipeRefreshLayout != null)
        mSwipeRefreshLayout.setRefreshing(true);
        NetworkHandler.execute(AppController.server + "f_get_post.php?userId="
                        + AppController.getInstance().getUserId() + "&sessionNumber="
                        + AppController.getInstance().getSessionNumber()
                        + "&postGroup=" + this.strPostGroup + "&page=" + this.page
                        + "&categoryId=" + this.categoryId,
                this::handleRefreshPostsResponse,
                error -> {
                    canScroll = true;
                    if (mSwipeRefreshLayout != null)
                        mSwipeRefreshLayout.setRefreshing(false);
                    if (page > 1)
                        page--;

                }, false, false
        );
    }

    private void handleRefreshPostsResponse(JSONObject response) {
        canScroll = true;
        if (mSwipeRefreshLayout != null)
            mSwipeRefreshLayout.setRefreshing(false);

        if (response != null)
            parseJsonFeed(response);
    }

    private void parseJsonFeed(JSONObject response) {

        if (page == 1) {
            feedItems.clear();
            listAdapter.notifyDataSetChanged();
        }

        try {
            JSONArray feedArray = response.getJSONArray("postList");
            if (feedArray != null && feedArray.length() > 0) {

                for (int i = 0; i < feedArray.length(); i++) {
                    feedItems.add(JSONParser.parseFeed((JSONObject) feedArray.get(i)));
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


}