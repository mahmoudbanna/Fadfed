package com.optimalsolutions.fadfed.listview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.optimalsolutions.fadfed.CommentsChanged;
import com.optimalsolutions.fadfed.fragments.CommentDeletion;
import com.optimalsolutions.fadfed.fragments.PostDeletion;
import com.optimalsolutions.fadfed.fragments.PostRefresher;

import java.util.List;

public class CommentsListAdapter extends BaseAdapter {

	private List items;
	private PostRefresher postRefresher;
	private PostDeletion postDeletion;
	private CommentDeletion commentDeletion;

	public CommentsListAdapter(List items , PostRefresher postRefresher, PostDeletion postDeletion , CommentDeletion commentDeletion)  {

		this.items = items;
		this.postRefresher = postRefresher;
		this.postDeletion = postDeletion;
		this.commentDeletion = commentDeletion;
	}

	@Override
	public int getCount() {

		return items.size();
	}

	@Override
	public Object getItem(int location) {

		return items.get(location);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (items.get(position) instanceof CommentItem)
			return new CommentViewHolder().of((FeedItem) items.get(0) , (CommentItem) items.get(position), commentDeletion ,position);
		else
			return new PostViewHolder().of(postRefresher,postDeletion , (FeedItem)items.get(0) , position);

	}

}
