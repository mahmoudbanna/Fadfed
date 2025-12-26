package com.optimalsolutions.fadfed.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.GridView;
import android.widget.ImageView;

import com.optimalsolutions.fadfed.emojicon.EmojiconEditText;
import com.optimalsolutions.fadfed.emojicon.EmojiconsPopup;
import com.optimalsolutions.fadfed.emojicon.KeyPadListener;
import com.google.android.gms.analytics.HitBuilders;
import com.optimalsolutions.fadfed.AppController;
import com.optimalsolutions.fadfed.Home;
import com.optimalsolutions.fadfed.R;
import com.optimalsolutions.fadfed.listview.PostsRefresher;
import com.optimalsolutions.fadfed.network.ErrorHandler;
import com.optimalsolutions.fadfed.utils.Alerts;
import com.optimalsolutions.fadfed.network.NetworkHandler;
import com.optimalsolutions.fadfed.utils.SoftKeypad;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class NewPostFragment extends Fragment implements KeyPadListener {

    private View view;
    private ImageView emojiBut, sendposttbut;
    private EmojiconEditText postet;
    private String postId, postContent;
    private GridView imagegrid;
    private PostsRefresher postsRefresher;

    public PostsRefresher getPostsRefresher() {
        return postsRefresher;
    }

    public void setPostsRefresher(PostsRefresher postsRefresher) {
        this.postsRefresher = postsRefresher;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            try {
                postId = getArguments().getString("postId");
                postContent = getArguments().getString("postContent");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view == null) {

            view = inflater.inflate(R.layout.fragment_new_post, null);
            postet = (EmojiconEditText) view.findViewById(R.id.postet);
            emojiBut = (ImageView) view.findViewById(R.id.emojiBut);
            sendposttbut = (ImageView) view.findViewById(R.id.sendposttbut);

            EmojiconsPopup popup = new EmojiconsPopup(view, getActivity(), emojiBut, postet);
            popup.setKeyPadListener(this);

            sendposttbut.setOnClickListener((View v) -> publishPost());

            if (postContent != null)
                postet.setText(postContent);

        }
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        AppController.getInstance().getDefaultTracker().setScreenName("New Post Screen");
        AppController.getInstance().getDefaultTracker().send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onClose() {


    }
    @Override
    public void onOpen() {

        emojiBut.setVisibility(View.VISIBLE);
        sendposttbut.setVisibility(View.VISIBLE);
    }

    private void publishPost() {

        SoftKeypad.hideSoftKeypad(view);

        if (postet.getText().toString() == null || postet.getText().toString().length() <= 0) {
            Alerts.showError(getString(R.string.error_title),getString(R.string.pleaseenterpost));
        } else {

            String url = null;
            try {

                if (postId == null) {

                    url = AppController.server
                            + "f_add_post.php?userId="
                            + AppController.getInstance().getUserId()
                            + "&sessionNumber="
                            + AppController.getInstance().getSessionNumber()
                            + "&content=" + postet.getURLString();
                } else {

                    url = AppController.server
                            + "f_update_post.php?userId=" + AppController.getInstance().getUserId()
                            + "&sessionNumber=" + AppController.getInstance().getSessionNumber()
                            + "&postId=" + postId
                            + "&content=" + postet.getURLString();

                }

                Log.d("test", url);
                NetworkHandler.execute(url, null,this::handlePublishPostRespone,
                        new ErrorHandler() ,false,true
                );


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
    }

    private void handlePublishPostRespone(JSONObject response) {
        try {
            Log.v("test", response.toString());


            if (response.getInt("resultId") == 9000) {

                SoftKeypad.hideSoftKeypad(postet);
                Home.getInstacne().getSupportFragmentManager().popBackStack();
                postsRefresher.refreshPosts();

            } else {
                Alerts.showError(response.getString("resultMessage"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}
