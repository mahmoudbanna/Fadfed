package com.optimalsolutions.fadfed.listview;

import java.io.Serializable;

public class ChannelItem  implements Serializable{

	private String  messageId, lastMessage,status,lastReplayFromUserId,lastReplayFromUserNickName,toUserId,toUserNickName,date;


	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getLastMessage() {
		return lastMessage;
	}

	public void setLastMessage(String lastMessage) {
		this.lastMessage = lastMessage;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLastReplayFromUserId() {
		return lastReplayFromUserId;
	}

	public void setLastReplayFromUserId(String lastReplayFromUserId) {
		this.lastReplayFromUserId = lastReplayFromUserId;
	}

	public String getLastReplayFromUserNickName() {
		return lastReplayFromUserNickName;
	}

	public void setLastReplayFromUserNickName(String lastReplayFromUserNickName) {
		this.lastReplayFromUserNickName = lastReplayFromUserNickName;
	}

	public String getToUserId() {
		return toUserId;
	}

	public void setToUserId(String toUserId) {
		this.toUserId = toUserId;
	}

	public String getToUserNickName() {
		return toUserNickName;
	}

	public void setToUserNickName(String toUserNickName) {
		this.toUserNickName = toUserNickName;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}
