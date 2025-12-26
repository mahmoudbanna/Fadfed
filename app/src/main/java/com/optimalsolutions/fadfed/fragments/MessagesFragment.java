package com.optimalsolutions.fadfed.fragments;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.optimalsolutions.fadfed.AppController;
import com.optimalsolutions.fadfed.GCM.NotificationsListenerService;
import com.optimalsolutions.fadfed.Home;
import com.optimalsolutions.fadfed.R;
import com.optimalsolutions.fadfed.emojicon.EmojiconTextView;
import com.optimalsolutions.fadfed.listview.ChannelItem;
import com.optimalsolutions.fadfed.listview.ChannelsListAdapter;
import com.optimalsolutions.fadfed.network.ErrorHandler;
import com.optimalsolutions.fadfed.utils.Alerts;
import com.optimalsolutions.fadfed.network.NetworkHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MessagesFragment extends Fragment implements OnScrollListener, AdapterView.OnItemClickListener {

    private static final String TAG = MessagesFragment.class.getSimpleName();
    private ListView listView;
    private ChannelsListAdapter listAdapter;
    private List<ChannelItem> channelItems;
    private int page;
    private boolean canScroll = true;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View v;
    private int scrollState;
    private ChatBroadcastReceiver mHandleMessageReceiver;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mHandleMessageReceiver = new ChatBroadcastReceiver();
        IntentFilter intent = new IntentFilter(NotificationsListenerService.DISPLAY_CHAT_ACTION);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mHandleMessageReceiver, intent);

    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        page = 1;

        if (v == null) {

            v = inflater.inflate(R.layout.fragment_messages, null);
            listView = (ListView) v.findViewById(R.id.channelsList);
            channelItems = new ArrayList<ChannelItem>();
            listAdapter = new ChannelsListAdapter(channelItems);
            listView.setAdapter(listAdapter);

            listView.setOnScrollListener(this);
            listView.setOnItemClickListener(this);

            mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.listsrl);
            mSwipeRefreshLayout.setColorScheme(
                    android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);

            mSwipeRefreshLayout.setOnRefreshListener(()-> {
                    page = 1;
                    Log.d("test", "Refresh channels layout");
                    refreshChannels();
            });

            refreshChannels();

        }

        return v;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        if (mHandleMessageReceiver != null)
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mHandleMessageReceiver);

    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        this.scrollState = scrollState;
        Log.d("test", "Refresh scroll view onScrollStateChanged :: scrollState::" + scrollState);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {


        Log.d("test", "Refresh scroll view channels :: totalItemCount::" + totalItemCount);

        if (listView != null && listView.getChildCount() > 0 && canScroll
                && listView.getLastVisiblePosition() == totalItemCount - 1
                && scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {

            Log.d("test", "Refresh scroll view channels");
            page++;
            refreshChannels();
            canScroll = false;
        }
    }

    private void refreshChannels() {

        Log.d("test", "Refresh channels");
        canScroll = false;
        mSwipeRefreshLayout.setRefreshing(true);
//        Alerts.showLoadingDialog();

        String url = AppController.server + "f_user_message_channels.php?userId="
                + AppController.getInstance().getUserId() + "&sessionNumber="
                + AppController.getInstance().getSessionNumber();


        Log.d("test", "Refresh channels ::" + url);

        NetworkHandler.execute(url, null, (JSONObject response) ->{

                        canScroll = true;
                        mSwipeRefreshLayout.setRefreshing(false);
//                        Alerts.hideLoadingsDialog();

                        if (response != null) {

                            Log.d("test", "Refresh channels::" + response.toString());

                            parseJsonChannels(response);
                        }
                    }
                ,new ErrorHandler(),true,false);
    }

    private void parseJsonChannels(JSONObject response) {

        if (page == 1) {
            channelItems.clear();
            listAdapter.notifyDataSetChanged();
        }

        try {
            JSONArray channelsArray = response.getJSONArray("messageList");

            if (channelsArray != null && channelsArray.length() > 0) {

                for (int i = 0; i < channelsArray.length(); i++) {

                    JSONObject channelObj = (JSONObject) channelsArray.get(i);
                    parseChannel(channelObj);

                }

            } else {
                page--;
            }


        } catch (JSONException e) {

            e.printStackTrace();
            canScroll = false;
            page--;
        }
    }

    private void parseChannel(JSONObject channelObj) {

        ChannelItem channelItem = new ChannelItem();

        try {
            channelItem.setMessageId(channelObj.getString("messageId"));
        } catch (JSONException e) {
        }
        try {
            channelItem.setLastMessage(channelObj.getString("lastMessage"));
        } catch (JSONException e) {
        }
        try {
            channelItem.setStatus(channelObj.getString("status"));
        } catch (JSONException e) {
        }
        try {
            channelItem.setLastReplayFromUserId(channelObj.getString("lastReplayFromUserId"));
        } catch (JSONException e) {
        }
        try {
            channelItem.setLastReplayFromUserNickName(channelObj.getString("lastReplayFromUserNickName"));
        } catch (JSONException e) {
        }
        try {
            channelItem.setToUserId(channelObj.getString("toUserId"));
        } catch (JSONException e) {
        }
        try {
            channelItem.setToUserNickName(channelObj.getString("toUserNickName"));
        } catch (JSONException e) {
        }
        try {
            channelItem.setDate(channelObj.getString("date"));
        } catch (JSONException e) {
        }

        if (channelItem.getLastMessage() != null && !channelItem.getLastMessage().equalsIgnoreCase("null")) {
            channelItems.add(channelItem);
            if (listAdapter != null)
                listAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


        if (AppController.isBrowseApp()) {
            Alerts.showLogin();
            return;
        }

        ChatsFragment chatsFragment = new ChatsFragment();
        Bundle chatsFragmentBundle = new Bundle();
        chatsFragmentBundle.putSerializable("channelItem", channelItems.get(position));
        chatsFragment.setArguments(chatsFragmentBundle);

        Home.getInstacne().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_container, chatsFragment)
                .addToBackStack(ChatsFragment.class.getName())
                .commit();

    }


    private class ChatBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("test", "message  recieve to Message Fragment :: " + intent.getExtras().get(NotificationsListenerService.EXTRA_MESSAGE));
            try {

                JSONObject chatMessage = new JSONObject(intent.getExtras().getString(NotificationsListenerService.EXTRA_MESSAGE));
                String messageId = chatMessage.getString("messageId");

                for (int index = 0; index < channelItems.size(); index++) {

                    ChannelItem channelItem = channelItems.get(index);

                    if (channelItem.getMessageId().equalsIgnoreCase(messageId)) {

                        Log.d("test", "message  recieve to Message Fragment :: channel found at index :: " + index);
                        updateView(index, parseNotification(chatMessage));
                        return;

                    }
                }

                addNewChannel(parseNotification(chatMessage));


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        private void updateView(int index, ChannelItem channelItem) {

            View v = listView.getChildAt(index - listView.getFirstVisiblePosition());

            if (v == null)
                return;

            EmojiconTextView txtStatusMsgtv = (EmojiconTextView) v.findViewById(R.id.txtStatusMsgtv);
            txtStatusMsgtv.setText(channelItem.getLastMessage());

            TextView metv = (TextView) v.findViewById(R.id.metv);

            if (channelItem.getLastReplayFromUserId().equalsIgnoreCase(AppController.getInstance().getUserId()))
                metv.setText(AppController.getCurrentContext().getText(R.string.you));
            else
                metv.setText(channelItem.getLastReplayFromUserNickName());
        }

        private ChannelItem parseNotification(JSONObject channelObj) {
            ChannelItem channelItem = new ChannelItem();

            try {
                channelItem.setMessageId(channelObj.getString("messageId"));
            } catch (JSONException e) {
            }

            try {
                channelItem.setLastMessage(channelObj.getString("message"));
            } catch (JSONException e) {
            }
            channelItem.setStatus("1");

            try {
                channelItem.setLastReplayFromUserId(channelObj.getString("fromUserId"));
            } catch (JSONException e) {
            }


            try {
                channelItem.setLastReplayFromUserNickName(channelObj.getString("fromUserNickName"));
            } catch (JSONException e) {
            }

            try {
                channelItem.setToUserId(channelObj.getString("fromUserId"));
            } catch (JSONException e) {
            }


            try {
                channelItem.setToUserNickName(channelObj.getString("fromUserNickName"));
            } catch (JSONException e) {
            }


            try {
                channelItem.setDate(channelObj.getString("date"));
            } catch (JSONException e) {
            }


            return channelItem;
        }

        private void addNewChannel(ChannelItem channelItem) {

            channelItems.add(0, channelItem);
            listAdapter.notifyDataSetChanged();


        }
    }

}
