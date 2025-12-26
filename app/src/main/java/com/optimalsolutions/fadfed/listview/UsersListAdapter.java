package com.optimalsolutions.fadfed.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.optimalsolutions.fadfed.Home;
import com.optimalsolutions.fadfed.R;
import com.optimalsolutions.fadfed.model.UserInfo;

import java.util.List;

public class UsersListAdapter extends BaseAdapter implements Runnable{
	
	private LayoutInflater inflater;
	private List<UserInfo> userInfos;

	public UsersListAdapter( List<UserInfo> userInfos) {
		this.userInfos = userInfos;
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
		return userInfos.size();
	}

	@Override
	public Object getItem(int location) {
		return userInfos.get(location);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		UserInfo item = userInfos.get(position);
		UserViewHolder holder = new UserViewHolder(item ) ;

		if (inflater == null)
			inflater = (LayoutInflater) Home.getInstacne()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null)
			convertView = inflater.inflate(R.layout.user_item, null);

		holder.setName((TextView) convertView.findViewById(R.id.nametv));
		holder.setAgetitletv((TextView) convertView.findViewById(R.id.agetitletv));
		holder.setAgetv((TextView) convertView.findViewById(R.id.agetv));
		holder.setGendertitletv((TextView) convertView.findViewById(R.id.gendertitletv));
		holder.setGendertv((TextView) convertView.findViewById(R.id.gendertv));
		holder.setCountrytitletv((TextView) convertView.findViewById(R.id.countrytitletv));
		holder.setCountrytv((TextView) convertView.findViewById(R.id.countrytv));
		holder.setProfilePic((NetworkImageView) convertView.findViewById(R.id.profilePic));

		holder.setData();

		return convertView;
	}

}
