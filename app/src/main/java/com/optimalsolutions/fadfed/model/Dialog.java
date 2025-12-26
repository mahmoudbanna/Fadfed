package com.optimalsolutions.fadfed.model;

import com.optimalsolutions.fadfed.AppController;
import com.optimalsolutions.fadfed.chatkit.commons.models.IDialog;
import com.optimalsolutions.fadfed.listview.ChannelItem;

import java.util.ArrayList;


public class Dialog implements IDialog<Message> {

    private String id;
    private String dialogPhoto;
    private String dialogName;
    private ArrayList<User> users;
    private Message lastMessage;
    private int unreadCount;
    private ChannelItem channelItem;

    public Dialog (ChannelItem channelItem){

        ArrayList<User> users = new ArrayList<>();

        users.add(new User(channelItem.getToUserId(), channelItem.getToUserNickName(),
                AppController.server
                        + "f_get_user_img_profile_viewer.php?userId="
                        + AppController.getInstance().getUserId()
                        + "&userIdImage=" + channelItem.getToUserId(), true));



        this.channelItem = channelItem;
        this.id = channelItem.getMessageId();
        this.dialogName = channelItem.getToUserNickName();
        this.users = users;
        this.lastMessage = Message.of(channelItem);
        this.unreadCount = 1;

        this.dialogPhoto = AppController.server + "f_get_user_img_profile_viewer.php?userId="
                + AppController.getInstance().getUserId()
                + "&userIdImage=" + channelItem.getToUserId();


    }


    private Dialog(String id, String name, String photo,
                  ArrayList<User> users, Message lastMessage, int unreadCount) {

        this.id = id;
        this.dialogName = name;
        this.dialogPhoto = photo;
        this.users = users;
        this.lastMessage = lastMessage;
        this.unreadCount = unreadCount;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDialogPhoto() {
        return dialogPhoto;
    }

    @Override
    public String getDialogName() {
        return dialogName;
    }

    @Override
    public ArrayList<User> getUsers() {
        return users;
    }

    @Override
    public Message getLastMessage() {
        return lastMessage;
    }

    @Override
    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    @Override
    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public ChannelItem getChannelItem() {
        return channelItem;
    }
}
