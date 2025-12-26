package com.optimalsolutions.fadfed.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;


import com.optimalsolutions.fadfed.AppController;
import com.optimalsolutions.fadfed.GCM.NotificationsListenerService;
import com.optimalsolutions.fadfed.Home;
import com.optimalsolutions.fadfed.R;
import com.optimalsolutions.fadfed.emojicon.EmojiconEditText;
import com.optimalsolutions.fadfed.emojicon.EmojiconsPopup;
import com.optimalsolutions.fadfed.listview.ChannelItem;
import com.optimalsolutions.fadfed.listview.ChatAdapter;
import com.optimalsolutions.fadfed.listview.ChatMessage;
import com.optimalsolutions.fadfed.network.AbstractErrorHandler;
import com.optimalsolutions.fadfed.network.ErrorHandler;
import com.optimalsolutions.fadfed.utils.Alerts;
import com.optimalsolutions.fadfed.network.NetworkHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class ChatsFragment extends Fragment {

    public static ArrayList<ChatMessage> chatlist;
    public static ChatAdapter chatAdapter;
    private ChatBroadcastReceiver chatBroadcastReceiver;
    private EmojiconEditText msg_edittext;
    private ChannelItem channelItem;
    private ListView msgListView;
    private ImageButton sendButton;
    private String date;
    private boolean canScroll = true;
    private int scrollPosition ;
    private int downloadItemsCount;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        channelItem = (ChannelItem) getArguments().get("channelItem");

//        chatBroadcastReceiver = new ChatBroadcastReceiver();
//        IntentFilter intent = new IntentFilter(NotificationsListenerService.DISPLAY_CHAT_ACTION);
//        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(chatBroadcastReceiver, intent);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.chat_layout, container, false);
        msg_edittext = (EmojiconEditText) view.findViewById(R.id.messageEditText);
        msgListView = (ListView) view.findViewById(R.id.msgListView);
        sendButton = (ImageButton) view.findViewById(R.id.sendMessageButton);

        final View rootView = view.findViewById(R.id.root_view);
        final ImageView emojiButton = (ImageView) view.findViewById(R.id.emojiBut);
        new EmojiconsPopup(rootView, getActivity(), emojiButton, msg_edittext);

        sendButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTextMessage(v);
            }
        });

        // ----Set autoscroll of listview when a new message arrives----//
        msgListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        msgListView.setStackFromBottom(true);

        chatlist = new ArrayList<ChatMessage>();
        chatAdapter = new ChatAdapter(getActivity(), chatlist);
        msgListView.setAdapter(chatAdapter);
        msgListView.setOnScrollListener(new EndlessScrollListener());
        Home.getInstacne().getHomeActionBar().refreshMessagesCount();
        return view;

    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        if (chatBroadcastReceiver != null)
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(chatBroadcastReceiver);

    }

    private void getMessagesBefore(String date) {


        if (channelItem == null || channelItem.getMessageId() == null) {
            setMessageId();
            return;
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


        NetworkHandler.execute(url, null,(JSONObject response)-> {
                        try {
                            if (response.getInt("resultId") == 9000) {
                                setChatMessages(response.toString());


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

    private void getMessagesAfter(String date) {

        String url = AppController.server
                + "f_get_message_conversation_after_date.php?userId=" + AppController.getInstance().getUserId()
                + "&sessionNumber=" + AppController.getInstance().getSessionNumber()
                + "&messageId=" + channelItem.getMessageId();

        if (date != null && date.length() > 0)
            try {
                url += "&date=" + URLEncoder.encode(date, "utf8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        Log.d("test", "get Messages after ::" + date + "::" + url);

        NetworkHandler.execute(url, null,this::handleGetMessageAfterDateResponse,
                new ErrorHandler(),
                false,
                false);
    }

    private void handleGetMessageAfterDateResponse(JSONObject response) {
        Log.d("test", "get Messages after ::" + response.toString());
        try {
            if (response.getInt("resultId") == 9000) {
                setChatMessages(response.toString());

            } else {
                Alerts.showError(response.getString("resultMessage"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setMessageId() {

        String url = AppController.server
                + "f_get_message_channel_for_user.php?userId=" + AppController.getInstance().getUserId()
                + "&sessionNumber=" + AppController.getInstance().getSessionNumber()
                + "&toUserId=" + channelItem.getToUserId();

        Log.d("test", "setMessageId :: url  ::" + url);

        NetworkHandler.execute(url, null,this::handleSetMessageIdResponse,
                new ErrorHandler(),
                false,
                false
        );

    }

    private void handleSetMessageIdResponse(JSONObject response) {
        try {
            if (response.getInt("resultId") == 9000 && response.getInt("messageId") > 0) {

                channelItem.setMessageId(response.getString("messageId"));
                Log.d("test", "setMessageId :: message Id ::" + channelItem.getMessageId());
                getMessagesBefore(date);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setChatMessages(String messages) {


        try {

            JSONObject jsonObject = new JSONObject(messages);
            JSONArray jsonArray = jsonObject.getJSONArray("messageList");

            if(jsonArray != null  || jsonArray.length() >= 10 )
                canScroll = true;

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonMsg = (JSONObject) jsonArray.get(i);

                ChatMessage chatMessage = new ChatMessage(
                        jsonMsg.getString("userId"),
                        jsonMsg.getString("message"),
                        jsonMsg.getString("date"),
                        jsonMsg.getString("userId")
                                .equalsIgnoreCase(AppController
                                        .getInstance().getUserId()));

                if (!checkMessageExist(chatMessage)) {

                    if (isMessageFirst(chatMessage)) {
                        chatlist.add(0, chatMessage);

                    } else {
                        chatlist.add(chatMessage);
                    }
                    chatAdapter.notifyDataSetChanged();

                }
            }

            msgListView.smoothScrollToPositionFromTop(scrollPosition + jsonArray.length()   ,0);

        } catch (JSONException e) {
            canScroll = false;
            e.printStackTrace();
        }
    }

    private boolean checkMessageExist(ChatMessage chatMessage) {

        for (int i = 0; i < chatlist.size(); i++) {
            if (chatlist.get(i).equals(chatMessage))
                return true;
        }

        return false;

    }

    private boolean isMessageFirst(ChatMessage chatMessage) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            if (chatlist != null && !chatlist.isEmpty() &&
                    sdf.parse(chatMessage.Date)
                            .before(sdf.parse(chatlist.get(0).Date)))
                return true;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void sendTextMessage(View v) {

        try {

            final String message = msg_edittext.getURLString();

            if (!message.equalsIgnoreCase("")) {

                sendButton.setEnabled(false);

                String url = AppController.server
                        + "f_add_message.php?userId=" + AppController.getInstance().getUserId()
                        + "&sessionNumber=" + AppController.getInstance().getSessionNumber()
                        + "&toUserId=" + channelItem.getToUserId()
                        + "&message=" + msg_edittext.getURLString();

                Log.d("test", "send message ::" + url);


                NetworkHandler.execute(url, null,this::handleSendTextMessageResponse,
                        new AbstractErrorHandler() {

                            public void handleError() {
                                sendButton.setEnabled(true);
                            }
                        }
                        ,
                        false,
                        true
                );
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        }
    }

    private void handleSendTextMessageResponse(JSONObject response) {
        sendButton.setEnabled(true);

        try {

            if (response.getInt("resultId") == 9000) {
                channelItem.setMessageId(response.getString("messageId"));
                msg_edittext.setText(null);
                getMessagesAfter(chatlist.get(chatlist.size() - 1).Date);
            } else {
                Alerts.showError(response.getString("resultMessage"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ChatBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("test", "message  recieve to chat :: " + intent.getExtras().get(NotificationsListenerService.EXTRA_MESSAGE));
//            Log.d("test", "message  recieve to chat :: channelItem.getMessageId() ::  "+channelItem.getMessageId());
//            Log.d("test", "message  recieve to chat :: channelItem.getToUserId() ::  "+channelItem.getToUserId());

            try {

                JSONObject chatJsonObject = new JSONObject(intent.getExtras().getString(NotificationsListenerService.EXTRA_MESSAGE));

                ChatMessage chatMessage = new ChatMessage(
                        chatJsonObject.getString("fromUserId"),
                        chatJsonObject.getString("message"),
                        chatJsonObject.getString("date"),
                        chatJsonObject.getString("fromUserId")
                                .equalsIgnoreCase(AppController.getInstance().getUserId()));

                if (channelItem != null
                        && channelItem.getMessageId().equalsIgnoreCase(chatJsonObject.getString("messageId"))
                        && !checkMessageExist(chatMessage)) {

                    chatlist.add(chatMessage);
                    chatAdapter.notifyDataSetChanged();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class EndlessScrollListener implements AbsListView.OnScrollListener {

        // The minimum number of items to have below your current scroll position
        // before loading more.
        private int visibleThreshold = 5;

        // The current offset index of data you have loaded
        private int currentPage = 0;
        // The total number of items in the dataset after the last load
        private int previousTotalItemCount = 0;
        // True if we are still waiting for the last set of data to load.
        private boolean loading = true;
        // Sets the starting page index
        private int startingPageIndex = 0;

        public EndlessScrollListener() {
        }

        public EndlessScrollListener(int visibleThreshold) {
            this.visibleThreshold = visibleThreshold;
        }

        public EndlessScrollListener(int visibleThreshold, int startPage) {
            this.visibleThreshold = visibleThreshold;
            this.startingPageIndex = startPage;
            this.currentPage = startPage;
        }

        // This happens many times a second during a scroll, so be wary of the code you place here.
        // We are given a few useful parameters to help us work out if we need to load some more data,
        // but first we check if we are waiting for the previous load to finish.

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
        {

            Log.d("test" , "on Scroll called totalItemCount :: " + totalItemCount + "  previousTotalItemCount ::" + previousTotalItemCount +" ::  firstVisibleItem :: "  + firstVisibleItem);

            // If the total item count is zero and the previous isn't, assume the
            // list is invalidated and should be reset back to initial state

            if(firstVisibleItem < 4  && canScroll ){

                if(chatlist != null && chatlist.size() > 0 && chatlist.get(0) != null && chatlist.get(0).Date != null)
                    date = chatlist.get(0).Date;

                canScroll = false;
                scrollPosition = firstVisibleItem ;
                getMessagesBefore(date);

            }


            if (totalItemCount < previousTotalItemCount) {


                getMessagesBefore(date);

                this.currentPage = this.startingPageIndex;
                this.previousTotalItemCount = totalItemCount;

                if (totalItemCount == 0) {
                    this.loading = true;
                }
            }
            // If it's still loading, we check to see if the dataset count has
            // changed, if so we conclude it has finished loading and update the current page
            // number and total item count.

            if (loading && (totalItemCount > previousTotalItemCount)) {
                loading = false;
                previousTotalItemCount = totalItemCount;
                currentPage++;
            }

            // If it isn't currently loading, we check to see if we have breached
            // the visibleThreshold and need to reload more data.
            // If we do need to reload some more data, we execute onLoadMore to fetch the data.
            if (!loading && (firstVisibleItem + visibleItemCount + visibleThreshold) >= totalItemCount ) {
                loading = false;


            }
        }

        // Defines the process for actually loading more data based on page
        // Returns true if more data is being loaded; returns false if there is no more data to load.


        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // Don't take any action on changed
        }
    }


}