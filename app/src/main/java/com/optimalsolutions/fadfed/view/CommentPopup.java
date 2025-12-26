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
import com.optimalsolutions.fadfed.fragments.CommentRefresher;
import com.optimalsolutions.fadfed.listview.CommentItem;
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

public class CommentPopup extends DialogFragment {

    private static final String TAG = "CommentPopup";
    private View view;
    private CommentItem commentItem;
    private MessageInput editcommentinput;
    private CommentRefresher commentRefresher;

    public void setCommentRefresher(CommentRefresher commentRefresher) {
        this.commentRefresher = commentRefresher;
    }

    public static void showDialog(CommentItem commentItem , CommentRefresher commentRefresher) {

        Log.d(TAG, "showDialog: commmentId :"+commentItem.getCommentId());
        CommentPopup commentPopup = new CommentPopup();
        Bundle  commentPopupBundle= new Bundle(1);
        commentPopupBundle.putSerializable("commentItem", commentItem);
        commentPopup.setArguments(commentPopupBundle);


        Home.getInstacne().getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.frame_container, commentPopup)
                .addToBackStack(null).commit();

        commentPopup.setCommentRefresher(commentRefresher);

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


        commentItem = (CommentItem) getArguments().getSerializable("commentItem");
        Log.d(TAG, "onCreateView: commentId :" +commentItem.getCommentId());

        if (view == null) {
            view = inflater.inflate(R.layout.comment_popup, container, false);
            editcommentinput = (MessageInput) view.findViewById(R.id.editcommentinput);
            editcommentinput.setInputListener(this::onSubmit);
        }
        SoftKeypad.showSoftKeypad(editcommentinput);
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

            NetworkHandler.execute( AppController.server
                            + "f_update_post_comment.php?userId=" + AppController.getInstance().getUserId()
                            + "&sessionNumber=" + AppController.getInstance().getSessionNumber()
                            + "&commentId=" + commentItem.getCommentId()
                            + "&comment=" +  URLEncoder.encode(StringEscapeUtils.escapeJava(input.toString()), "utf-8")
                    , this::handleUpdateCommentResponse, new ErrorHandler(), false, true
            );
            commentItem.setCommentContent(input.toString());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void handleUpdateCommentResponse(JSONObject jsonObject) {

        dismiss();

        try {
            if (jsonObject.getInt("resultId") == 9000) {
                commentRefresher.refreshComment(commentItem);
            }
        } catch (JSONException e) {
            e.getMessage();
            Alerts.showNetworkError();
        }

    }

}
