package com.optimalsolutions.fadfed.listview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.optimalsolutions.fadfed.AppController;
import com.optimalsolutions.fadfed.CommentsChanged;
import com.optimalsolutions.fadfed.R;
import com.optimalsolutions.fadfed.emojicon.EmojiconTextView;
import com.optimalsolutions.fadfed.fragments.CommentDeletion;
import com.optimalsolutions.fadfed.fragments.SendVioFragment;
import com.optimalsolutions.fadfed.fragments.UserFragment;
import com.optimalsolutions.fadfed.network.AbstractErrorHandler;
import com.optimalsolutions.fadfed.network.ErrorHandler;
import com.optimalsolutions.fadfed.network.JSONParser;
import com.optimalsolutions.fadfed.utils.Alerts;
import com.optimalsolutions.fadfed.network.NetworkHandler;
import com.optimalsolutions.fadfed.view.CommentPopup;
import com.optimalsolutions.fadfed.view.PostReviewToolTip;

import org.json.JSONException;
import org.json.JSONObject;

public class CommentViewHolder {

    private TextView name, feedlikecount, feeddislikecount, timestamp;
    private EmojiconTextView statusMsg;
    private ImageButton morecommentbut, feedlikebut, feeddislikebut;
    private NetworkImageView profilePic;
    private PostReviewToolTip postReviewToolTip;
    private CommentItem commentItem;
    private FeedItem feed;
    private CommentDeletion commentDeletion;
    private int position ;

    public View of( FeedItem feedItem, CommentItem commentItem ,CommentDeletion commentDeletion, int position) {


        LayoutInflater inflater = (LayoutInflater) AppController.getCurrentContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = inflater.inflate(R.layout.comment_item, null);

        this.feed = feedItem;
        this.commentItem = commentItem;
        this.commentDeletion = commentDeletion;
        this.position = position;

        this.name = (TextView) convertView.findViewById(R.id.nametv);
        this.feedlikecount = (TextView) convertView.findViewById(R.id.feedlikecount);
        this.feeddislikecount = (TextView) convertView.findViewById(R.id.feeddislikecount);
        this.timestamp = (TextView) convertView.findViewById(R.id.timestamptv);
        this.statusMsg = (EmojiconTextView) convertView.findViewById(R.id.txtStatusMsgtv);
        this.morecommentbut = (ImageButton) convertView.findViewById(R.id.morecommentbut);
        this.feedlikebut = (ImageButton) convertView.findViewById(R.id.feedlikebut);
        this.feeddislikebut = (ImageButton) convertView.findViewById(R.id.feeddislikebut);
        this.profilePic = (NetworkImageView) convertView.findViewById(R.id.profilePic);

        postReviewToolTip = createTootTipRelativeLayout();
        setData();
        return convertView;
    }

    public CommentItem getCommentItem() {
        return commentItem;
    }

    public void setCommentItem(CommentItem commentItem) {
        this.commentItem = commentItem;
    }

    public void setData() {

        name.setText(commentItem.getNickName());
        feedlikecount.setText(commentItem.getCommentLikeCount());
        feeddislikecount.setText(commentItem.getCommentUnlikeCount());
        timestamp.setText(commentItem.getCommentCreatedAt());
        statusMsg.setText(commentItem.getCommentContent());

        profilePic.setImageUrl(AppController.server
                + "f_get_user_img_profile_viewer.php?userId=" + AppController.getInstance().getUserId()
                + "&sessionNumber=" + AppController.getInstance().getSessionNumber()
                + "&userIdImage=" + commentItem.getUserId(), NetworkHandler.getImageLoader());

        profilePic.setOnClickListener((View v) -> UserFragment.openUserProfile(commentItem.getUserId()));
        morecommentbut.setOnClickListener(postReviewToolTip::show);
        feedlikebut.setOnClickListener((View view) -> likeunlikeComment("1"));
        feeddislikebut.setOnClickListener((View view) -> likeunlikeComment("2"));
    }

    private PostReviewToolTip createTootTipRelativeLayout() {

        PostReviewToolTip postReviewToolTip = new PostReviewToolTip().of(morecommentbut);

        if (commentItem.getUserId().equalsIgnoreCase(AppController.getInstance().getUserId()))
            postReviewToolTip.setEditButVisible(View.VISIBLE)
                    .setDeleteButVisible(View.VISIBLE)
                    .setSendVioButVisible(View.GONE);
        else
            postReviewToolTip.setEditButVisible(View.GONE)
                    .setDeleteButVisible(View.GONE)
                    .setSendVioButVisible(View.VISIBLE);

        postReviewToolTip.setEditAction((View v) -> editComment(postReviewToolTip));
        postReviewToolTip.setDeleteAction((View v) -> deleteComment(postReviewToolTip, feed, commentItem));
        postReviewToolTip.setSendVioAction((View v) -> sendVio(postReviewToolTip, feed, commentItem));

        return postReviewToolTip;
    }

    private void sendVio(PostReviewToolTip postReviewToolTip, FeedItem feedItem, CommentItem commentItem) {

        if (AppController.isBrowseApp())
            Alerts.showLogin();
        else
            openVioScreen(feedItem, commentItem);

        postReviewToolTip.dismiss();
    }

    private void openVioScreen(FeedItem feedItem, CommentItem commentItem) {
        if (AppController.isBrowseApp())
            Alerts.showLogin();
        else
            SendVioFragment.open(feedItem, commentItem);
    }

    private void deleteComment(PostReviewToolTip postReviewToolTip, FeedItem feedItem, CommentItem commentItem) {

        postReviewToolTip.dismiss();

        if (AppController.isBrowseApp()) {
            Alerts.showLogin();
            return;
        }
        NetworkHandler.execute( AppController.server
                        + "f_delete_post_comment.php?userId=" + AppController.getInstance().getUserId()
                        + "&sessionNumber=" + AppController.getInstance().getSessionNumber()
                        + "&postId=" + feedItem.getPostId() + "&commentId=" + commentItem.getCommentId()
                ,this::handleDeleteCOmmentResponse, new ErrorHandler(),
                false,true);


    }

    private void handleDeleteCOmmentResponse(  JSONObject response) {


        try {
            Log.v("test", response.toString());

            if (response.getInt("resultId") == 9000) {
                commentDeletion.deleteComment(position);
            } else
                Alerts.showError(response.getString("resultMessage"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void editComment(PostReviewToolTip postReviewToolTip) {

        if (AppController.isBrowseApp()) {
            Alerts.showLogin();
            return;
        }
        postReviewToolTip.dismiss();
        CommentPopup.showDialog(commentItem, this::refreshCommentInfo);
    }

    private void refreshCommentInfo(CommentItem commentItem) {

        this.commentItem = commentItem;
        setData();
    }

    private void likeunlikeComment(String likeType) {

        if (AppController.isBrowseApp()) {
            Alerts.showLogin();
            return;
        }
        String url = AppController.server + "f_like_unlike_comment.php?userId="
                + AppController.getInstance().getUserId() + "&sessionNumber="
                + AppController.getInstance().getSessionNumber()
                + "&commentId=" + commentItem.getCommentId() + "&likeType=" + likeType;

        Log.v("test", url);

        NetworkHandler.execute(url, null, this::handleLikeUnlikeResponse,
                new ErrorHandler(), false, true);
    }

    private void handleLikeUnlikeResponse(JSONObject response) {

        try {
            if (response != null && response.getInt("resultId") == 9000)
                refreshComment();
            else
                Alerts.showError(response.getString("resultMessage"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void refreshComment() {

        String url = AppController.server
                + "f_get_post_commet_profile.php?userId="
                + AppController.getInstance().getUserId() + "&sessionNumber="
                + AppController.getInstance().getSessionNumber()
                + "&commentId=" + commentItem.getCommentId();

        Log.d("test", url);

        NetworkHandler.execute(url, null, (JSONObject response) -> {

                    try {

                        Log.d("test", "update comment :: " + response.getString("commentContent"));

                        if (response != null && response.getInt("resultId") == 9000) {

                            setCommentItem(JSONParser.parseComment(response));

                            name.setText(getCommentItem().getNickName());
                            feedlikecount.setText(getCommentItem().getCommentLikeCount());
                            feeddislikecount.setText(getCommentItem().getCommentUnlikeCount());
                            timestamp.setText(getCommentItem().getCommentCreatedAt());

                            Log.d("test", "update comment in commentItem :: " + commentItem.getCommentContent());

                            statusMsg.setText(response.getString("commentContent"));

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                new ErrorHandler(),
                false,
                false
        );
    }
}
