package com.optimalsolutions.fadfed.listview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.optimalsolutions.fadfed.Home;
import com.optimalsolutions.fadfed.model.UserInfo;

import java.util.ArrayList;

public class FeedListAdapter extends ArrayAdapter implements Runnable {

    private ArrayList items;

    public FeedListAdapter(Context context, ArrayList items) {

        super(context, 0, items);
        this.items = items;
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

        if (items.get(position) instanceof UserInfo)
            return new UserProfileViewHolder().of((UserInfo) items.get(position), this);
        else
            return new FeedViewHolder().of((FeedItem) items.get(position), this, items ,position);
    }

}
