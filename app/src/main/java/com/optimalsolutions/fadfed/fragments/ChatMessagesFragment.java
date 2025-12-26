package com.optimalsolutions.fadfed.fragments;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.optimalsolutions.fadfed.AppController;
import com.optimalsolutions.fadfed.GCM.NotificationsListenerService;
import com.optimalsolutions.fadfed.R;
import com.optimalsolutions.fadfed.listview.ChannelItem;
import com.optimalsolutions.fadfed.messages.MessageInput;
import com.optimalsolutions.fadfed.messages.MessageInput.AttachmentsListener;
import com.optimalsolutions.fadfed.messages.MessageInput.InputListener;
import com.optimalsolutions.fadfed.messages.MessagesList;
import com.optimalsolutions.fadfed.messages.MessagesListAdapter;
import com.optimalsolutions.fadfed.messages.MessagesListAdapter.SelectionListener;
import com.optimalsolutions.fadfed.model.Message;
import com.optimalsolutions.fadfed.model.User;
import com.optimalsolutions.fadfed.network.ErrorHandler;
import com.optimalsolutions.fadfed.utils.Alerts;
import com.optimalsolutions.fadfed.network.NetworkHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ChatMessagesFragment extends Fragment implements SelectionListener, InputListener, AttachmentsListener {

    private Menu menu;
    private int selectionCount;
    private Date lastLoadedDate;
    private User currentUser;


    private ChatBroadcastReceiver chatBroadcastReceiver;
    //    private SwipeRefreshLayout mSwipeRefreshLayout;
    protected MessagesListAdapter<Message> messagesAdapter;
    private ChannelItem channelItem;
    private MessagesList messagesList;
    private static SimpleDateFormat simpleDateFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        channelItem = (ChannelItem) getArguments().get("channelItem");
        chatBroadcastReceiver = new ChatBroadcastReceiver();
        IntentFilter intent = new IntentFilter(NotificationsListenerService.DISPLAY_CHAT_ACTION);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(chatBroadcastReceiver, intent);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.chat_messages, container, false);

        messagesList = (MessagesList) view.findViewById(R.id.messagesList);

        MessageInput messageInput = (MessageInput) view.findViewById(R.id.messageinput);

//        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.chatsrl);

        messageInput.setInputListener(this);
        messageInput.setAttachmentsListener(this);

//        mSwipeRefreshLayout.setColorScheme(
//                android.R.color.holo_blue_bright,
//                android.R.color.holo_green_light,
//                android.R.color.holo_orange_light,
//                android.R.color.holo_red_light);

//        mSwipeRefreshLayout.setOnRefreshListener(() -> Log.d("test", "Refresh channels layout"));

        messagesAdapter = new MessagesListAdapter<>(
                AppController.getInstance().getUserId(),
                (ImageView imageView, String url) -> Picasso.with(getContext()).load(url).into(imageView)
        );

        messagesAdapter.enableSelectionMode(this);

        messagesAdapter.setLoadMoreListener(

                (page, totalItemsCount) -> {

                    Log.d("test", "test total items count ::" + totalItemsCount);
                    Log.d("test", "test page ::" + page);
                    Log.d("test", "test last loaded date ::" + lastLoadedDate);

                    loadMessagesBefore(simpleDateFormatter.format(lastLoadedDate)
                        , (List<Message> messages) -> messagesAdapter.addToEnd(messages, false));}

        );

        messagesList.setAdapter(messagesAdapter);

//        messagesAdapter.setDateHeadersFormatter(this);

        messageInput = (MessageInput) view.findViewById(R.id.messageinput);
        messageInput.setInputListener(this);
        messageInput.setAttachmentsListener(this);


        currentUser = new User(AppController.getInstance().getUserId(),
                AppController.getInstance().getNickName(), AppController.server
                + "f_get_user_img_profile_viewer.php?userId="
                + AppController.getInstance().getUserId()
                + "&userIdImage=" + AppController.getInstance().getUserId() , true);


        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        loadMessagesBefore(channelItem.getDate(), (List<Message> messages) -> messagesAdapter.addToEnd(messages, false));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatBroadcastReceiver != null)
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(chatBroadcastReceiver);

    }

    @Override
    public boolean onSubmit(CharSequence input) {
        Log.d("test", "send Message ::" + input);


        if (input.length() > 0) {


            String url = AppController.server
                    + "f_add_message.php?userId=" + AppController.getInstance().getUserId()
                    + "&sessionNumber=" + AppController.getInstance().getSessionNumber()
                    + "&toUserId=" + channelItem.getToUserId()
                    + "&message=" + input;

            Log.d("test", "send message ::" + url);


            NetworkHandler.execute(url, null, (JSONObject response) -> {

                try {

                    if (response.getInt("resultId") == 9000) {

                        String messageId = response.getString("messageId");
                        channelItem.setMessageId(messageId);

                        messagesAdapter.addToStart(new Message(messageId, currentUser,input.toString()),true);

//                                        input.setText(null);
//                                        getMessagesAfter(chatlist.get(chatlist.size() - 1).Date);
                    } else {
                        Alerts.showError(response.getString("resultMessage"));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            },
                    new ErrorHandler(),
                    false,
                    false
            );
        }

        return true;
    }

    @Override
    public void onAddAttachments() {

    }


    @Override
    public void onSelectionChanged(int count) {

    }


    private void setMessageId() {

        String url = AppController.server
                + "f_get_message_channel_for_user.php?userId=" + AppController.getInstance().getUserId()
                + "&sessionNumber=" + AppController.getInstance().getSessionNumber()
                + "&toUserId=" + channelItem.getToUserId();

        Log.d("test", "setMessageId :: url  ::" + url);

        NetworkHandler.execute(url, null,this::handleSetMessageIdResponse,
                new ErrorHandler() ,
                false,
                false
        );
    }

    private void handleSetMessageIdResponse(JSONObject response) {
        try {
            if (response.getInt("resultId") == 9000 && response.getInt("messageId") > 0) {

                channelItem.setMessageId(response.getString("messageId"));
                Log.d("test", "setMessageId :: message Id ::" + channelItem.getMessageId());
//                                getMessagesBefore(date);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadMessagesBefore(String date, MessageHandler handler) {

        final List<Message> messages = new ArrayList<>();

        if (channelItem == null || channelItem.getMessageId() == null) {
            setMessageId();
            return ;
        }

        String url = AppController.server
                + "f_get_message_conversation_before_date.php?userId=" + AppController.getInstance().getUserId()
                + "&sessionNumber=" + AppController.getInstance().getSessionNumber()
                + "&messageId=" + channelItem.getMessageId();

        if (date != null && date.length() > 0)
            try {
                url += "&date=" + URLEncoder.encode(date, "utf8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


        Log.d("test", "get Messages before ::" + date + "::" + url);

        NetworkHandler.execute(url, null, (JSONObject response) -> {

            try {
                if (response.getInt("resultId") == 9000) {
                    messages.addAll(parseMessages(response.toString()));
                    if (messages.size() > 0)
                        handler.apply(messages);


                } else {
                    Alerts.showError(response.getString("resultMessage"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }, new ErrorHandler(),
                false,
                false
        );


    }


    @SuppressLint("SimpleDateFormat")
    private List<Message> parseMessages(String messages) {

        List<Message> chatlist = new ArrayList<>();

        try {

            JSONObject jsonObject = new JSONObject(messages);
            JSONArray jsonArray = jsonObject.getJSONArray("messageList");

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonMsg = (JSONObject) jsonArray.get(i);

                String userId = jsonMsg.getString("userId");
                String userName = "";

                if (userId.equalsIgnoreCase(AppController.getInstance().getUserId())) {
                    userName = AppController.getInstance().getNickName();
                }

                User user = new User(userId, userName, AppController.server
                        + "f_get_user_img_profile_viewer.php?userId="
                        + AppController.getInstance().getUserId()
                        + "&userIdImage=" + userId, true);

                Date messageDate = null;

                try {
                    messageDate = simpleDateFormatter.parse(jsonMsg.getString("date"));
                    lastLoadedDate = messageDate;

                } catch (ParseException e) {
                    e.printStackTrace();
                    messageDate = new Date();
                }

                chatlist.add(new Message("1", user, jsonMsg.getString("message"), messageDate));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("test", "parse messages ::" + chatlist);
        return chatlist;
    }

    private class ChatBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("test", "message  recieve to chat  2:: " + intent.getExtras().get(NotificationsListenerService.EXTRA_MESSAGE));

            try {

                JSONObject json = new JSONObject(intent.getExtras().getString(NotificationsListenerService.EXTRA_MESSAGE));

                messagesAdapter.addToStart(
                        new Message(json.getString("messageId"),
                                new User(json.getString("fromUserId"),
                                        json.getString("fromUserNickName"),
                                        AppController.server + "f_get_user_img_profile_viewer.php?userId="
                                                + AppController.getInstance().getUserId()
                                                + "&userIdImage=" + json.getString("fromUserId"),
                                        true
                                ),
                                json.getString("message"),
                                simpleDateFormatter.parse(json.getString("date"))
                        ),
                        true
                );

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    interface MessageHandler {
        void apply(List<Message> messages);
    }
}
