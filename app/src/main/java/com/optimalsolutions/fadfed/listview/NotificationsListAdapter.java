package com.optimalsolutions.fadfed.listview;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.optimalsolutions.fadfed.Home;

import java.util.List;

public class NotificationsListAdapter extends BaseAdapter implements Runnable{

	private List<NotificationItem> notificationItems;

	public NotificationsListAdapter(List<NotificationItem> notificationItems) {
		this.notificationItems = notificationItems;
	}
	@Override
	public void run() {
		super.notifyDataSetChanged();
	}

	@Override
	public void notifyDataSetChanged() {
		Home.getInstacne().runOnUiThread(this);

	}
	@Override
	public int getCount() {
		return notificationItems.size();
	}

	@Override
	public Object getItem(int location) {
		return notificationItems.get(location);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		return new NotificationViewHolder().of(notificationItems.get(position));
	}

}
