package com.optimalsolutions.fadfed.fragments;

import com.optimalsolutions.fadfed.listview.CommentItem;

public interface CommentRefresher {
    void refreshComment(CommentItem commentItem);
}
