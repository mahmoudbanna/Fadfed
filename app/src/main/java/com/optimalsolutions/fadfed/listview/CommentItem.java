package com.optimalsolutions.fadfed.listview;

import java.io.Serializable;

public class CommentItem implements Serializable{

	private String commentContent, commentCreatedAt, nickName, commentId,
			userId,commentLikeCount,commentUnlikeCount;

	public String getCommentLikeCount() {
		return commentLikeCount;
	}

	public void setCommentLikeCount(String commentLikeCount) {
		this.commentLikeCount = commentLikeCount;
	}

	public String getCommentUnlikeCount() {
		return commentUnlikeCount;
	}

	public void setCommentUnlikeCount(String commentUnlikeCount) {
		this.commentUnlikeCount = commentUnlikeCount;
	}

	public String getCommentContent() {
		return commentContent;
	}

	public void setCommentContent(String commentContent) {
		this.commentContent = commentContent;
	}

	public String getCommentCreatedAt() {
		return commentCreatedAt;
	}

	public void setCommentCreatedAt(String commentCreatedAt) {
		this.commentCreatedAt = commentCreatedAt;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getCommentId() {
		return commentId;
	}

	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
