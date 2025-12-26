package com.optimalsolutions.fadfed.view;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.optimalsolutions.fadfed.AppController;
import com.optimalsolutions.fadfed.R;
import com.optimalsolutions.fadfed.tooltip.ToolTip;
import com.optimalsolutions.fadfed.tooltip.ToolTipRelativeLayout;

/**
 * Created by mahmoud on 3/16/18.
 *
 */

public class PostReviewToolTip {

    private Button editBut, deleteBut, sendVioBut;
    private PopupWindow popupWindow;


    public PostReviewToolTip of(View view ) {

        ToolTip postActionToolTip = new ToolTip().withContentView(LayoutInflater.from(AppController.getInstance())
                .inflate(R.layout.post_review_popup_menu, null))
                .withColor(AppController.getInstance().getResources().getColor(R.color.feed_item_name))
                .withAnimationType(ToolTip.AnimationType.FROM_TOP)
                .withShadow();

        ToolTipRelativeLayout toolTipRelativeLayout = new ToolTipRelativeLayout(AppController.getCurrentContext());
        toolTipRelativeLayout.showToolTipForView(postActionToolTip, view);

        editBut = (Button) toolTipRelativeLayout.findViewById(R.id.editbut);
        deleteBut = (Button) toolTipRelativeLayout.findViewById(R.id.deletebut);
        sendVioBut = (Button) toolTipRelativeLayout.findViewById(R.id.sendviobut);

        popupWindow = setupPopup(toolTipRelativeLayout);
        return this;
    }

    @NonNull
    private PopupWindow setupPopup(View layout) {

        PopupWindow popupWindow = new PopupWindow(AppController.getCurrentContext());
        popupWindow.setBackgroundDrawable(null);
        popupWindow.setContentView(layout);
        popupWindow.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);

        return popupWindow;
    }

    public void show(View view){
        popupWindow.showAsDropDown(view);
    }

    public PostReviewToolTip setEditButVisible(int visible){
        editBut.setVisibility(visible);
        return this;
    }
    public PostReviewToolTip setDeleteButVisible(int visible){
        deleteBut.setVisibility(visible);
        return this;
    }
    public PostReviewToolTip setSendVioButVisible(int visible){
        sendVioBut.setVisibility(visible);
        return this;
    }

    public void setEditAction(View.OnClickListener clickListener){
        editBut.setOnClickListener(clickListener);
    }

    public void setDeleteAction(View.OnClickListener clickListener){
        deleteBut.setOnClickListener(clickListener);
    }

    public void setSendVioAction(View.OnClickListener clickListener){
        sendVioBut.setOnClickListener(clickListener);
    }

    public void dismiss() {
        popupWindow.dismiss();
    }
}
