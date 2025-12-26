package com.optimalsolutions.fadfed.listview;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import com.optimalsolutions.fadfed.AppController;
import com.optimalsolutions.fadfed.emojicon.EmojiconTextView;
import com.optimalsolutions.fadfed.fragments.ChatsFragment;
import com.optimalsolutions.fadfed.Home;
import com.optimalsolutions.fadfed.R;
import com.optimalsolutions.fadfed.fragments.UserFragment;
import com.optimalsolutions.fadfed.model.UserInfo;
import com.optimalsolutions.fadfed.network.NetworkHandler;

import java.util.List;

public class ChannelsListAdapter extends BaseAdapter implements Runnable {

    private List<ChannelItem> channelItems;
    private LayoutInflater inflater;

    public ChannelsListAdapter(List<ChannelItem> channelItems) {
        this.channelItems = channelItems;
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
        return channelItems.size();
    }

    @Override
    public Object getItem(int location) {
        return channelItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ChannelItem item = channelItems.get(position);
        ViewHolder holder;
        if (inflater == null)
            inflater = (LayoutInflater) Home.getInstacne().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.channel_item, null);

        holder = new ViewHolder(item);

        holder.convertView = convertView;

        holder.timestamp = (TextView) convertView.findViewById(R.id.timestamptv);
        holder.statusMsg = (EmojiconTextView) convertView.findViewById(R.id.txtStatusMsgtv);
        holder.name = (TextView) convertView.findViewById(R.id.nametv);
        holder.profilePic = (NetworkImageView) convertView.findViewById(R.id.profilePic);
        holder.bubbleLayout = (LinearLayout) convertView.findViewById(R.id.bubble_layout);
        holder.metv = (TextView) convertView.findViewById(R.id.metv);
        holder.setData();

        return convertView;
    }

    private class ViewHolder {

        private TextView timestamp, name , metv;
        private EmojiconTextView statusMsg;
        private LinearLayout bubbleLayout;
        private NetworkImageView profilePic;
        private ChannelItem item;
        private View convertView;

        public ViewHolder(ChannelItem item) {
            this.item = item;
        }

        public void setData() {

            timestamp.setText(item.getDate());
            name.setText(item.getToUserNickName());

            profilePic.setImageUrl(AppController.server
                    + "f_get_user_img_profile_viewer.php?userId="
                    + AppController.getInstance().getUserId()
                    + "&userIdImage=" + item.getToUserId(),
                    NetworkHandler.getImageLoader());


            if( item.getLastReplayFromUserId().equalsIgnoreCase(AppController.getInstance().getUserId()))
                metv.setText(AppController.getCurrentContext().getText(R.string.you));
            else
                metv.setText(item.getLastReplayFromUserNickName());


            statusMsg.setText(item.getLastMessage());
            profilePic.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    openUserProfile(item);
                }
            });
            name.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    openUserProfile(item);
                }
            });
            statusMsg.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    openChat(item);

                }
            });


        }

        private void openUserProfile(ChannelItem item) {

            Home.getInstacne().getHomeActionBar().closeNotifications();

            UserInfo user = new UserInfo();
            user.setUserId(item.getToUserId());

            UserFragment userFragment = new UserFragment();
            Bundle userFragmentBundle = new Bundle();
            userFragmentBundle.putSerializable("userInfo", user);
            userFragment.setArguments(userFragmentBundle);

            Home.getInstacne().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container, userFragment)
                    .addToBackStack(UserFragment.class.getName())
                    .commit();
        }

    }

    private void openChat(ChannelItem item) {

        ChatsFragment chatsFragment = new ChatsFragment();
        Bundle chatsFragmentBundle = new Bundle();
        chatsFragmentBundle.putSerializable("channelItem", item);
        chatsFragment.setArguments(chatsFragmentBundle);

        Home.getInstacne().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_container, chatsFragment)
                .addToBackStack(ChatsFragment.class.getName())
                .commit();


    }

}
