package com.optimalsolutions.fadfed.network;

import android.support.annotation.NonNull;

import com.optimalsolutions.fadfed.listview.CommentItem;
import com.optimalsolutions.fadfed.listview.FeedItem;
import com.optimalsolutions.fadfed.listview.NotificationItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahmoud on 3/16/18.
 */

public class JSONParser {


    public static CommentItem parseComment(JSONObject response) {

        CommentItem item = new CommentItem();

        try {
            item.setCommentContent(response.getString("commentContent"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            item.setCommentCreatedAt(response.getString("commentCreatedAt"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            item.setNickName(response.getString("nickName"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            item.setCommentId(response.getString("commentId"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            item.setUserId(response.getString("userId"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            item.setCommentLikeCount(response.getString("commentLikeCount"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            item.setCommentUnlikeCount(response.getString("commentUnlikeCount"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return item;
    }


    public static NotificationItem parseNotifications(JSONObject notificationObj) {

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
            item.setContent(notificationObj.getString("notificationContent"));
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

        return item;
    }

    public static FeedItem parseFeed(JSONObject feedObj) {


        FeedItem item = new FeedItem();
        try {
            item.setPostCreatedAt(feedObj.getString("postCreatedAt"));
        } catch (JSONException e) {
        }
        try {
            item.setPostUnlikeCount(feedObj.getString("postUnlikeCount"));
        } catch (JSONException e) {
        }
        try {
            item.setPostLikeCount(feedObj.getString("postLikeCount"));
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
            item.setPostCommentCount(feedObj.getString("postCommentCount"));
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
        try {
            item.setPostCreatedAt(feedObj.getString("postCreatedAt"));
        } catch (JSONException e) {
        }
        try {
            item.setPostUnlikeCount(feedObj.getString("postUnlikeCount"));
        } catch (JSONException e) {
        }
        try {
            item.setPostLikeCount(feedObj.getString("postLikeCount"));
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
            item.setPostCommentCount(feedObj.getString("postCommentCount"));
        } catch (JSONException e) {
        }
        try {
            item.setPostContant(feedObj.getString("postContant"));
        } catch (JSONException e) {
        }

        return item;
    }

    public static List<CommentItem> parseCommentsList(JSONObject response) throws JSONException {

        List<CommentItem> items = new ArrayList<>();
        JSONArray feedArray = response.getJSONArray("commentList");

        for (int i = 0; i < feedArray.length(); i++)
            items.add(JSONParser.parseComment((JSONObject) feedArray.get(i)));

        return items;


    }

}
