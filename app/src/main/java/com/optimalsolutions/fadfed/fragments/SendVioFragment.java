package com.optimalsolutions.fadfed.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.analytics.HitBuilders;
import com.optimalsolutions.fadfed.AppController;
import com.optimalsolutions.fadfed.Home;
import com.optimalsolutions.fadfed.R;
import com.optimalsolutions.fadfed.listview.CommentItem;
import com.optimalsolutions.fadfed.listview.FeedItem;
import com.optimalsolutions.fadfed.network.ErrorHandler;
import com.optimalsolutions.fadfed.utils.Alerts;
import com.optimalsolutions.fadfed.network.NetworkHandler;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class SendVioFragment extends Fragment {

    private View v;
    private EditText messagecontentet;
    private Button publishmessagebut;
    private String postId, commentId;


    public static void open(FeedItem feedItem , CommentItem commentItem){

        SendVioFragment sendVioFragment = new SendVioFragment();
        Bundle sendVioFragmentBundle = new Bundle(2);
        sendVioFragmentBundle.putString("postId", feedItem.getPostId());
        sendVioFragmentBundle.putString("commentId", commentItem.getCommentId());
        sendVioFragment.setArguments(sendVioFragmentBundle);

        Home.getInstacne().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_container, sendVioFragment)
                .addToBackStack(SendVioFragment.class.getName()).commit();
    }

    public static void open(FeedItem feedItem){

        SendVioFragment sendVioFragment = new SendVioFragment();
        Bundle sendVioFragmentBundle = new Bundle(2);
        sendVioFragmentBundle.putString("postId", feedItem.getPostId());
        sendVioFragment.setArguments(sendVioFragmentBundle);

        Home.getInstacne().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_container, sendVioFragment)
                .addToBackStack(SendVioFragment.class.getName()).commit();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.postId = getArguments().getString("postId");
        this.commentId = getArguments().getString("commentId");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_send_vio, null);

        messagecontentet = (EditText) v.findViewById(R.id.messagecontentet);

        publishmessagebut = (Button) v.findViewById(R.id.publishmessagebut);

        publishmessagebut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                send();

            }
        });

        return v;

    }

    @Override
    public void onResume() {
        super.onResume();
        AppController.getInstance().getDefaultTracker().setScreenName("Send Vio Screen");
        AppController.getInstance().getDefaultTracker().send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void send() {

        if (messagecontentet.getText().toString() == null
                || messagecontentet.getText().toString().length() <= 0
                || messagecontentet.getText().toString().trim()
                .equalsIgnoreCase(getString(R.string.messagecontent)))
            messagecontentet
                    .setError(getString(R.string.pleasentermessagecontent));
        else {

//            Alerts.showProgressDialog();

            String url = "";
            try {
                url = AppController.server
                        + "f_contact_us.php?userId="
                        + AppController.getInstance().getUserId()
                        + "&sessionNumber="
                        + AppController.getInstance().getSessionNumber()
                        + "&message="
                        + URLEncoder.encode(messagecontentet.getText()
                        .toString(), "utf-8");
            } catch (UnsupportedEncodingException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            Log.v("test", url);

            NetworkHandler.execute(url, null,this::handleSendVioResponse,
                    new ErrorHandler(),false,true);

        }
    }

    private void handleSendVioResponse(JSONObject response) {
        try {

            if (response.getInt("resultId") == 9000) {

                messagecontentet.setText("");
                Alerts.showSuccessMessage();

            } else {
                Alerts.showError(  getString(R.string.error_title),
                        response.getString("resultMessage"));
            }
        } catch (Exception e) {

            Alerts.showNetworkError();
            e.printStackTrace();
        }
    }
}
