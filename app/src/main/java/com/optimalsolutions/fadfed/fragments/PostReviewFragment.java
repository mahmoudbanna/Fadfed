package com.optimalsolutions.fadfed.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
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
import com.optimalsolutions.fadfed.GCM.NotificationsListenerService;
import com.optimalsolutions.fadfed.Home;
import com.optimalsolutions.fadfed.R;
import com.optimalsolutions.fadfed.listview.CommentsListAdapter;
import com.optimalsolutions.fadfed.listview.FeedItem;
import com.optimalsolutions.fadfed.messages.MessageInput;
import com.optimalsolutions.fadfed.network.AbstractErrorHandler;
import com.optimalsolutions.fadfed.network.JSONParser;
import com.optimalsolutions.fadfed.network.NetworkHandler;
import com.optimalsolutions.fadfed.utils.Alerts;
import com.optimalsolutions.fadfed.utils.SoftKeypad;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class PostReviewFragment extends Fragment  {

    private ListView commentslistView;
    private CommentsListAdapter commentsListAdapter;
    private List items;
    private FeedItem feedItem;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int page;
    private boolean canScroll = true;
    private MessageInput newcommentinput;
    private PostRefresher postRefresher;
    private PostDeletion postDeletion;
    private View view;

    private void setPostDelegators(PostRefresher postRefresher,PostDeletion postDeletion) {

        this.postRefresher = postRefresher;
        this.postDeletion =postDeletion;
    }

    public static void openPostReview(FeedItem feedItem , PostRefresher postRefresher ,PostDeletion postDeletion) {

        PostReviewFragment postReviewFragment = new PostReviewFragment();
        Bundle postReviewFragmentBundle = new Bundle(1);
        postReviewFragmentBundle.putSerializable("feedItem", feedItem);
        postReviewFragment.setArguments(postReviewFragmentBundle);
        postReviewFragment.setPostDelegators(postRefresher,postDeletion);

        Home.getInstacne().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_container, postReviewFragment)
                .addToBackStack(PostReviewFragment.class.getName())
                .commit();


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        feedItem = (FeedItem) getArguments().get("feedItem");

        IntentFilter notificationBroadcastReceiverIntentFilter = new IntentFilter(NotificationsListenerService.DISPLAY_MESSAGE_ACTION);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    JSONObject commentJsonObject = new JSONObject(intent.getExtras().getString(NotificationsListenerService.EXTRA_MESSAGE));
                    if (commentJsonObject.getString("postId").equalsIgnoreCase(feedItem.getPostId())) {
                        Log.d("test", "message  recieve to post review screen :: " + intent.getExtras().get(NotificationsListenerService.EXTRA_MESSAGE));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, notificationBroadcastReceiverIntentFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view == null) {

            canScroll = false;
            view = inflater.inflate(R.layout.fragment_post_review, null);
            newcommentinput = (MessageInput) view.findViewById(R.id.newcommentinput);

            items = new ArrayList<>();
            commentsListAdapter = new CommentsListAdapter(items,postRefresher,postDeletion,this::deleteComment);
            commentslistView = (ListView) view.findViewById(R.id.list);
            commentslistView.setAdapter(commentsListAdapter);

            mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.listlay);

            commentslistView.setOnScrollListener(new OnScrollListener() {

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem,
                                     int visibleItemCount, int totalItemCount) {
                    if (commentslistView != null && commentslistView.getChildCount() > 0
                            && canScroll && commentslistView.getLastVisiblePosition() == totalItemCount - 1) {

                        Log.v("test", "refresh scroll called :: LastVisiblePosition :: "
                                + commentslistView.getLastVisiblePosition()
                                + " ::: totalItemCount :::" + totalItemCount);
                        page++;
                        getPostComments();
                    }
                }
            });

            newcommentinput.setInputListener(this::onSubmit);
            mSwipeRefreshLayout.setOnRefreshListener(() -> refreshComments());

            refreshComments();
        }
        return view;
    }

    private void deleteComment(int position) {

        items.remove(position);
        commentsListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {

        super.onResume();
        AppController.getInstance().getDefaultTracker().setScreenName("Post Review Screen");
        AppController.getInstance().getDefaultTracker().send(new HitBuilders.ScreenViewBuilder().build());
    }


    public boolean onSubmit(CharSequence input) {

        SoftKeypad.hideSoftKeypad(commentslistView);

        if (AppController.isBrowseApp()) {
            Alerts.showLogin();
            return false;
        }

        canScroll = false;

            try {

                NetworkHandler.execute( AppController.server
                        + "f_add_post_comment.php?userId=" + AppController.getInstance().getUserId()
                        + "&sessionNumber=" + AppController.getInstance().getSessionNumber()
                        + "&postId=" + feedItem.getPostId()
                        + "&comment=" + URLEncoder.encode(StringEscapeUtils.escapeJava(input.toString()), "utf-8")
                        ,null, this::handleAddNewCommentResponse,
                        new AbstractErrorHandler() {

                            public void handleError() {
                                canScroll = true;
                            }
                        }, false, false
                );

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return false;
            }

        return true;
    }

    private void handleAddNewCommentResponse(JSONObject response) {
        canScroll = true;

        try {
            Log.v("test", response.toString());

            if (response.getInt("resultId") == 9000) {
                page = 1;
                refreshComments();
            } else {
                Alerts.showError(response.getString("resultMessage"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getPostComments() {

        Log.d("test", "page ::::" + page);


        String url = AppController.server
                + "f_get_post_commet.php?userId=" + AppController.getInstance().getUserId()
                + "&sessionNumber=" + AppController.getInstance().getSessionNumber()
                + "&postId=" + feedItem.getPostId() + "&page=" + page;

        Log.d("test", url);

        canScroll = false;
        if (mSwipeRefreshLayout != null)
            mSwipeRefreshLayout.setRefreshing(true);

        NetworkHandler.execute(url, null, this::handleGetPostCommentsResponse,
                new AbstractErrorHandler() {
                    public void handleError() {
                        page--;
                        if (mSwipeRefreshLayout != null)
                            mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, false, false
        );
    }

    private void handleGetPostCommentsResponse(JSONObject response) {
        mSwipeRefreshLayout.setRefreshing(false);

        try {
            Log.d("test", response.toString());

            if (response.getInt("resultId") == 9000 && response.get("commentList") != null) {
                try {
                    items.addAll(JSONParser.parseCommentsList(response));
                    commentsListAdapter.notifyDataSetChanged();
                } catch (JSONException ex) {
                    page--;
                    return;
                }
                canScroll = true;
            } else {
                page--;
                if (response.getString("resultMessage") != null)
                    Alerts.showError(response.getString("resultMessage"));
            }
        } catch (Exception e) {
            canScroll = true;
            page--;
            e.printStackTrace();
        }
    }


    public void refreshComments() {

        page = 1;
        items.clear();
        items.add(feedItem);
        getPostComments();

    }

}
