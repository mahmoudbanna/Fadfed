package com.optimalsolutions.fadfed;

import com.optimalsolutions.fadfed.listview.CommentViewHolder;

public interface CommentsChanged {

	void refreshComments();
	void editComemnt(int index, CommentViewHolder commentViewHolder);

}
