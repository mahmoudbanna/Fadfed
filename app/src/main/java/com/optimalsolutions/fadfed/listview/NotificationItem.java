package com.optimalsolutions.fadfed.listview;

public class NotificationItem {

	private String notificationId, type, status, createdAt, content, userId,userName, postId;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPostId() {
		return postId;
	}

	public void setPostId(String postId) {
		this.postId = postId;
	}

	public String getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(String notificationId) {
		this.notificationId = notificationId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "NotificationItem{" +
				"notificationId='" + notificationId + '\'' +
				", type='" + type + '\'' +
				", status='" + status + '\'' +
				", createdAt='" + createdAt + '\'' +
				", content='" + content + '\'' +
				", userId='" + userId + '\'' +
				", userName='" + userName + '\'' +
				", postId='" + postId + '\'' +
				'}';
	}
}
