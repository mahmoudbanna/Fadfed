package com.optimalsolutions.fadfed.view;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.google.android.gms.analytics.HitBuilders;
import com.optimalsolutions.fadfed.AppController;
import com.optimalsolutions.fadfed.Home;
import com.optimalsolutions.fadfed.R;
import com.optimalsolutions.fadfed.fragments.PostRefresher;
import com.optimalsolutions.fadfed.listview.FeedItem;
import com.optimalsolutions.fadfed.messages.MessageInput;
import com.optimalsolutions.fadfed.network.ErrorHandler;
import com.optimalsolutions.fadfed.network.NetworkHandler;
import com.optimalsolutions.fadfed.utils.Alerts;
import com.optimalsolutions.fadfed.utils.SoftKeypad;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by mahmoud on 3/16/18.
 *
 */

public class PostPopup extends DialogFragment {

    private static final String TAG = "PostPopup";
    private View view;
    private FeedItem feedItem;
    private MessageInput editpostinput;

    public void setPostRefresher(PostRefresher postRefresher) {
        this.postRefresher = postRefresher;
    }

    private PostRefresher postRefresher;

    public static void showDialog(FeedItem feedItem , PostRefresher postRefresher) {

        Log.d("test", "showDialog: feedId :"+feedItem.getPostId());
        PostPopup postPopup = new PostPopup();
        Bundle postPopupBundle = new Bundle(1);
        postPopupBundle.putSerializable("feedItem", feedItem);
        postPopup.setArguments(postPopupBundle);


        Home.getInstacne().getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.frame_container, postPopup)
                .addToBackStack(null).commit();

        postPopup.setPostRefresher(postRefresher);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        feedItem = (FeedItem) getArguments().getSerializable("feedItem");
        Log.d(TAG, "onCreateView: feedId :" +feedItem.getPostId());

        if (view == null) {
            view = inflater.inflate(R.layout.post_popup, container, false);
            editpostinput = (MessageInput) view.findViewById(R.id.editpostinput);
            editpostinput.setInputListener(this::onSubmit);
        }
        SoftKeypad.showSoftKeypad(editpostinput);
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        AppController.getInstance().getDefaultTracker().setScreenName("Update Post Screen");
        AppController.getInstance().getDefaultTracker().send(new HitBuilders.ScreenViewBuilder().build());
    }

    private boolean onSubmit(CharSequence input) {

        SoftKeypad.hideSoftKeypad(view);

        if (AppController.isBrowseApp()) {
            Alerts.showLogin();
            return false;
        }

        try {
            NetworkHandler.execute(AppController.server
                            + "f_update_post.php?userId=" + AppController.getInstance().getUserId()
                            + "&sessionNumber=" + AppController.getInstance().getSessionNumber()
                            + "&postId=" + feedItem.getPostId()
                            + "&content=" + URLEncoder.encode(StringEscapeUtils.escapeJava(input.toString()), "utf-8")
                            + "&categoryId=1"
                    , this::handleUpdatePostResponse, new ErrorHandler(), false, true
            );
            feedItem.setPostContant(input.toString());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void handleUpdatePostResponse(JSONObject jsonObject) {

        dismiss();

        try {
            if (jsonObject.getInt("resultId") == 9000) {
                postRefresher.refreshPost(feedItem);
            }
        } catch (JSONException e) {
            e.getMessage();
            Alerts.showNetworkError();
        }

    }

}
