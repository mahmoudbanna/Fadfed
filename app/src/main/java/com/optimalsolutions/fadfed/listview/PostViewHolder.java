package com.optimalsolutions.fadfed.listview;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.optimalsolutions.fadfed.AppController;
import com.optimalsolutions.fadfed.Home;
import com.optimalsolutions.fadfed.R;
import com.optimalsolutions.fadfed.emojicon.EmojiconTextView;
import com.optimalsolutions.fadfed.fragments.PostDeletion;
import com.optimalsolutions.fadfed.fragments.PostRefresher;
import com.optimalsolutions.fadfed.fragments.SendVioFragment;
import com.optimalsolutions.fadfed.fragments.UserFragment;
import com.optimalsolutions.fadfed.network.ErrorHandler;
import com.optimalsolutions.fadfed.network.JSONParser;
import com.optimalsolutions.fadfed.network.NetworkHandler;
import com.optimalsolutions.fadfed.utils.Alerts;
import com.optimalsolutions.fadfed.view.PostPopup;
import com.optimalsolutions.fadfed.view.PostReviewToolTip;

import org.json.JSONException;
import org.json.JSONObject;

public class PostViewHolder {

    private static final String TAG = "PostViewHolder";

    private TextView name, timestamp, feedcommentcount, feedlikecount, feeddislikecount;
    private EmojiconTextView statusMsg;
    private NetworkImageView profilePic;
    private ImageButton morecommentbut, feedlikebut, feeddislikebut;
    private FeedItem feedItem;
    private PostRefresher postRefresher;
    private PostDeletion postDeletion;
    private int position;
    private PostReviewToolTip postReviewToolTip;

    public View of(PostRefresher postRefresher, PostDeletion postDeletion, FeedItem feedItem, int position) {

        Log.d(TAG, "of: feed " + feedItem);
        this.postRefresher = postRefresher;
        this.postDeletion = postDeletion;
        this.position = position;


        LayoutInflater inflater = (LayoutInflater) AppController.getCurrentContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = inflater.inflate(R.layout.post_item, null);

        name = (TextView) convertView.findViewById(R.id.nametv);
        feedlikecount = (TextView) convertView.findViewById(R.id.feedlikecount);
        feeddislikecount = (TextView) convertView.findViewById(R.id.feeddislikecount);
        feedcommentcount = (TextView) convertView.findViewById(R.id.feedcommentcount);
        timestamp = (TextView) convertView.findViewById(R.id.timestamptv);
        statusMsg = (EmojiconTextView) convertView.findViewById(R.id.txtStatusMsgtv);
        morecommentbut = (ImageButton) convertView.findViewById(R.id.morecommentbut);
        feedlikebut = (ImageButton) convertView.findViewById(R.id.feedlikebut);
        feeddislikebut = (ImageButton) convertView.findViewById(R.id.feeddislikebut);
        profilePic = (NetworkImageView) convertView.findViewById(R.id.profilePic);
        this.feedItem = feedItem;

        postReviewToolTip = createTootTipRelativeLayout();

        init();
        setData();

        return convertView;
    }

    private PostReviewToolTip createTootTipRelativeLayout() {

        PostReviewToolTip postReviewToolTip = new PostReviewToolTip().of(morecommentbut);

        if (feedItem.getUserId().equalsIgnoreCase(AppController.getInstance().getUserId()))
            postReviewToolTip.setEditButVisible(View.VISIBLE)
                    .setDeleteButVisible(View.VISIBLE)
                    .setSendVioButVisible(View.GONE);
        else
            postReviewToolTip.setEditButVisible(View.GONE)
                    .setDeleteButVisible(View.GONE)
                    .setSendVioButVisible(View.VISIBLE);

        postReviewToolTip.setEditAction((View v) -> editPost(postReviewToolTip));
        postReviewToolTip.setDeleteAction((View v) -> deletePost(postReviewToolTip));
        postReviewToolTip.setSendVioAction((View v) -> sendVio(postReviewToolTip));

        return postReviewToolTip;
    }

    public void setData() {

        NetworkHandler.execute(AppController.server + "f_get_post_profile.php?userId=" + AppController.getInstance().getUserId()
                        + "&sessionNumber=" + AppController.getInstance().getSessionNumber() + "&postId=" + feedItem.getPostId(),
                this::handleGetPost, new ErrorHandler(), false, false);
    }

    private void handleGetPost(JSONObject response) {

        try {
            Log.d(TAG, "handleGetPost: " + response.toString());
            if (response.getInt("resultId") == 9000) {
                feedItem = JSONParser.parseFeed(response);
                init();
            } else {
                Alerts.showError(response.getString("resultMessage"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init() {

        if (feedItem == null)
            return;

        profilePic.setImageUrl(AppController.server
                        + "f_get_user_img_profile_viewer.php?userId=" + AppController.getInstance().getUserId()
                        + "&sessionNumber=" + AppController.getInstance().getSessionNumber()
                        + "&userIdImage=" + feedItem.getUserId(),
                NetworkHandler.getImageLoader());

        name.setText(feedItem.getNickName());
        feedcommentcount.setText(feedItem.getPostCommentCount());
        feedlikecount.setText(feedItem.getPostLikeCount());
        feeddislikecount.setText(feedItem.getPostUnlikeCount());
        timestamp.setText(feedItem.getPostCreatedAt());

        // Chcek for empty status message
        if (!TextUtils.isEmpty(feedItem.getPostContant())) {
            statusMsg.setText(feedItem.getPostContant());
            statusMsg.setVisibility(View.VISIBLE);
        } else {
            // status is empty, remove from view
            statusMsg.setVisibility(View.GONE);
        }

        name.setOnClickListener((View v) -> UserFragment.openUserProfile(feedItem.getUserId()));
        feedlikebut.setOnClickListener((View v) -> likeunlike("1"));
        feeddislikebut.setOnClickListener((View v) -> likeunlike("2"));
        profilePic.setOnClickListener((View v) -> UserFragment.openUserProfile(feedItem.getUserId()));
        morecommentbut.setOnClickListener(postReviewToolTip::show);
    }

    private void editPost(PostReviewToolTip postReviewToolTip) {
        postReviewToolTip.dismiss();
        Log.d(TAG, "editPost: postId :" + feedItem.getPostId());
        PostPopup.showDialog(feedItem, this::refreshPost);
    }

    private void refreshPost(FeedItem feedItem) {
        this.feedItem = feedItem;
        if (postRefresher != null)
            postRefresher.refreshPost(this.feedItem);
        init();
    }


    private void deletePost(PostReviewToolTip postReviewToolTip) {

        postReviewToolTip.dismiss();

        String url = AppController.server + "f_delete_post.php?userId="
                + AppController.getInstance().getUserId()
                + "&sessionNumber="
                + AppController.getInstance().getSessionNumber()
                + "&postId=" + feedItem.getPostId();

        Log.v("test", url);

        NetworkHandler.execute(url, null, this::handleDeletePostResponse, new ErrorHandler(),
                false, true);
    }

    private void handleDeletePostResponse(JSONObject response) {

        try {

            if (response.getInt("resultId") == 9000) {
                Log.v("test", response.toString());
                postDeletion.deletePost(position);
                Home.getInstacne().getSupportFragmentManager().popBackStack();

            } else {
                Alerts.hideProgressDialog();
                Alerts.showError(response.getString("resultMessage"));
            }
        } catch (Exception e) {
            Alerts.showNetworkError();
            e.printStackTrace();
        }
    }

    private void sendVio(PostReviewToolTip postReviewToolTip) {

        postReviewToolTip.dismiss();
        if (AppController.isBrowseApp())
            Alerts.showLogin();
        else
            SendVioFragment.open(feedItem);
    }

    private void likeunlike(String likeType) {

        Log.d(TAG, "likeunlike: test");

        if (AppController.isBrowseApp())
            Alerts.showLogin();

        else {
            String url = AppController.server + "f_like_unlike_post.php?userId="
                    + AppController.getInstance().getUserId() + "&sessionNumber="
                    + AppController.getInstance().getSessionNumber() + "&postId="
                    + feedItem.getPostId() + "&likeType=" + likeType;

            NetworkHandler.execute(url, null, this::handlelikeDislikeResponse,
                    new ErrorHandler(),
                    false,
                    true
            );
        }
    }

    private void handlelikeDislikeResponse(JSONObject response) {

        Log.d(TAG, "likeunlike: response done");

        try {
            if (response != null) {
                if (response.getInt("resultId") == 9000) {

                    feeddislikecount.setText(response.getString("postUnlikeCount"));
                    feedlikecount.setText(response.getString("postLikeCount"));
                    feedcommentcount.setText(response.getString("postCommentCount"));
                } else {
                    Alerts.showError(response.getString("resultMessage"));
                }

            }
        } catch (JSONException e) {
            Alerts.showParsingError();
        }
    }

}