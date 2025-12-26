package com.optimalsolutions.fadfed.listview;

import android.graphics.Bitmap;

import java.io.Serializable;

public class FeedItem implements Serializable{

	private String postCreatedAt, postUnlikeCount, postLikeCount, nickName,
			userId, postCommentCount, postContant, postId;

	private Bitmap userPic;

	public FeedItem() {}

	public FeedItem(String postCreatedAt, String postUnlikeCount,
			String postLikeCount, String nickName, String userId,
			String postCommentCount, String postContant, String postId) {

		this.postCreatedAt = postCreatedAt;
		this.postUnlikeCount = postUnlikeCount;
		this.postLikeCount = postLikeCount;
		this.nickName = nickName;
		this.userId = userId;
		this.postCommentCount = postCommentCount;
		this.postContant = postContant;
		this.postId = postId;
	}

	public String getPostCreatedAt() {
		return postCreatedAt;
	}

	public void setPostCreatedAt(String postCreatedAt) {
		this.postCreatedAt = postCreatedAt;
	}

	public String getPostUnlikeCount() {
		return postUnlikeCount;
	}

	public void setPostUnlikeCount(String postUnlikeCount) {
		this.postUnlikeCount = postUnlikeCount;
	}

	public String getPostLikeCount() {
		return postLikeCount;
	}

	public void setPostLikeCount(String postLikeCount) {
		this.postLikeCount = postLikeCount;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPostCommentCount() {
		return postCommentCount;
	}

	public void setPostCommentCount(String postCommentCount) {
		this.postCommentCount = postCommentCount;
	}

	public String getPostContant() {
		return postContant;
	}

	public void setPostContant(String postContant) {
		this.postContant = postContant;
	}

	public String getPostId() {
		return postId;
	}

	public void setPostId(String postId) {
		this.postId = postId;
	}

	public Bitmap getUserPic() {
		return userPic;
	}

	public void setUserPic(Bitmap userPic) {
		this.userPic = userPic;
	}

	@Override
	public String toString() {
		return "FeedItem{" +
				"postCreatedAt='" + postCreatedAt + '\'' +
				", postUnlikeCount='" + postUnlikeCount + '\'' +
				", postLikeCount='" + postLikeCount + '\'' +
				", nickName='" + nickName + '\'' +
				", userId='" + userId + '\'' +
				", postCommentCount='" + postCommentCount + '\'' +
				", postContant='" + postContant + '\'' +
				", postId='" + postId + '\'' +
				", userPic=" + userPic +
				'}';
	}
}
