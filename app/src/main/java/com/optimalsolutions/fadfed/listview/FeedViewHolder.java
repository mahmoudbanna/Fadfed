package com.optimalsolutions.fadfed.listview;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.optimalsolutions.fadfed.AppController;
import com.optimalsolutions.fadfed.R;
import com.optimalsolutions.fadfed.emojicon.EmojiconTextView;
import com.optimalsolutions.fadfed.fragments.PostReviewFragment;
import com.optimalsolutions.fadfed.fragments.UserFragment;
import com.optimalsolutions.fadfed.network.ErrorHandler;
import com.optimalsolutions.fadfed.network.JSONParser;
import com.optimalsolutions.fadfed.network.NetworkHandler;
import com.optimalsolutions.fadfed.utils.Alerts;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FeedViewHolder{

    private static final String TAG ="FeedViewHolder";
    private TextView feedcommentcount, feedlikecount, feeddislikecount, timestamp, name;
    private EmojiconTextView statusMsg;
    private ImageButton feedlikebut, feeddislikebut;
    private NetworkImageView profilePic;
    private FeedItem feedItem;
    private BaseAdapter adapter;
    private ArrayList items;
    private int position;

    public View of(FeedItem item, BaseAdapter adapter , ArrayList items , int position ) {

        this.feedItem = item;
        this.adapter = adapter;
        this.items = items;
        this.position = position;

        LayoutInflater inflater = (LayoutInflater) AppController.getCurrentContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = inflater.inflate(R.layout.feed_item, null);

        name = (TextView) convertView.findViewById(R.id.nametv);
        feedcommentcount = (TextView) convertView.findViewById(R.id.feedcommentcount);
        feedlikecount = (TextView) convertView.findViewById(R.id.feedlikecount);
        feeddislikecount = (TextView) convertView.findViewById(R.id.feeddislikecount);
        timestamp = (TextView) convertView.findViewById(R.id.timestamptv);
        statusMsg = (EmojiconTextView) convertView.findViewById(R.id.txtStatusMsgtv);
        feedlikebut = (ImageButton) convertView.findViewById(R.id.feedlikebut);
        feeddislikebut = (ImageButton) convertView.findViewById(R.id.feeddislikebut);
        profilePic = (NetworkImageView) convertView.findViewById(R.id.profilePic);

        statusMsg.setOnClickListener(view -> PostReviewFragment.openPostReview(feedItem,this::refreshPost,this::deletePost));
        setData();

        return convertView;
    }

    private void deletePost(int position) {
        
        items.remove(position);
        adapter.notifyDataSetChanged();

    }

    private void refreshPost(FeedItem feedItem){

        items.set(position, feedItem);
        adapter.notifyDataSetChanged();
    }

    public void setData() {

        String imageUrl = AppController.server
                + "f_get_user_img_profile_viewer.php?userId=" + AppController.getInstance().getUserId()
                + "&sessionNumber=" + AppController.getInstance().getSessionNumber()
                + "&userIdImage=" + feedItem.getUserId();

        Log.d("test", "set data for image view in feed adapter for post :: " + feedItem.getPostId() + " :: for user :: " + feedItem.getUserId());

        name.setText(feedItem.getNickName());
        timestamp.setText(feedItem.getPostCreatedAt());
        profilePic.setImageUrl(imageUrl, NetworkHandler.getImageLoader());
        setFeedCounts(feedItem);
        setFeedContent(feedItem);

        name.setOnClickListener((View v) -> UserFragment.openUserProfile(feedItem.getUserId()));
        feedlikebut.setOnClickListener((View v) -> likeunlike("1"));
        feeddislikebut.setOnClickListener((View v) -> likeunlike("2"));
        profilePic.setOnClickListener((View v) -> UserFragment.openUserProfile(feedItem.getUserId()));
        adapter.notifyDataSetChanged();
    }

    private void setFeedContent(FeedItem feedItem) {

        if (feedItem.getPostContant() != null) {
            if (!TextUtils.isEmpty(feedItem.getPostContant().trim())) {
                statusMsg.setText(feedItem.getPostContant());
                statusMsg.setVisibility(View.VISIBLE);
            } else {
                statusMsg.setVisibility(View.GONE);
            }
        }
    }

    private void setFeedCounts(FeedItem feedItem) {

        feedcommentcount.setText(feedItem.getPostCommentCount());
        feedlikecount.setText(feedItem.getPostLikeCount());
        feeddislikecount.setText(feedItem.getPostUnlikeCount());
    }

    private void likeunlike(String likeType) {

        if (AppController.isBrowseApp()) {
            Alerts.showLogin();
            return;
        }

        NetworkHandler.execute(AppController.server + "f_like_unlike_post.php?userId="
                        + AppController.getInstance().getUserId() + "&sessionNumber="
                        + AppController.getInstance().getSessionNumber() + "&postId="
                        + feedItem.getPostId() + "&likeType=" + likeType, null,
                this::handlelikeDislikeResponse, new ErrorHandler(),
                false, true);

    }
    private void handlelikeDislikeResponse(JSONObject response) {

        try {
            if (response != null) {
                if (response.getInt("resultId") == 9000) {
                    setFeedCounts(JSONParser.parseFeed(response));
                } else {
                    Alerts.showError(response.getString("resultMessage"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
