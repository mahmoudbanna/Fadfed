package com.optimalsolutions.fadfed.listview;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.optimalsolutions.fadfed.AppController;
import com.optimalsolutions.fadfed.Home;
import com.optimalsolutions.fadfed.R;
import com.optimalsolutions.fadfed.emojicon.EmojiconTextView;
import com.optimalsolutions.fadfed.fragments.UserFragment;
import com.optimalsolutions.fadfed.network.NetworkHandler;

/**
 * Created by mahmoud on 3/24/18.
 *
 */

public class NotificationViewHolder {

    private TextView timestamp , name;
    private EmojiconTextView statusMsg;
    private NetworkImageView profilePic;
    private NotificationItem notificationItem;


    public View of (NotificationItem notificationItem) {
        this.notificationItem = notificationItem;
        LayoutInflater inflater = (LayoutInflater) AppController.getCurrentContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = inflater.inflate(R.layout.comment_item, null);

        timestamp = (TextView) convertView.findViewById(R.id.timestamptv);
        statusMsg = (EmojiconTextView) convertView.findViewById(R.id.txtStatusMsgtv);
        name =(TextView) convertView.findViewById(R.id.nametv);
        profilePic = (NetworkImageView) convertView.findViewById(R.id.profilePic);
        setData(convertView);
        return convertView;

    }

    public void setData(View convertView) {

        timestamp.setText(notificationItem.getCreatedAt());
        name.setText(notificationItem.getUserName());

        profilePic.setImageUrl(AppController.server + "f_get_user_img_profile_viewer.php?userId="
                        + AppController.getInstance().getUserId()
                        + "&sessionNumber=" + AppController.getInstance().getSessionNumber()
                        + "&userIdImage=" + notificationItem.getUserId(), NetworkHandler.getImageLoader());

        if (!TextUtils.isEmpty(notificationItem.getStatus()) && notificationItem.getStatus().equalsIgnoreCase("2")) {
            convertView.setBackgroundResource(R.color.cat_item_bg_select);
        }

        if (!TextUtils.isEmpty(notificationItem.getContent())) {
            statusMsg.setText(notificationItem.getContent());
            statusMsg.setVisibility(View.VISIBLE);
        } else {
            statusMsg.setVisibility(View.GONE);
        }
        profilePic.setOnClickListener(v -> UserFragment.openUserProfile(notificationItem.getUserId()));

        name.setOnClickListener(v -> UserFragment.openUserProfile(notificationItem.getUserId()));

        statusMsg.setOnClickListener(v -> {
            if ((notificationItem.getType().equalsIgnoreCase("3") || notificationItem.getType().equalsIgnoreCase("1"))
                    && notificationItem.getPostId() != null && notificationItem.getPostId().length() > 0) {

                Home.getInstacne().openPostProfile(notificationItem.getPostId());
            }
        });
    }
}