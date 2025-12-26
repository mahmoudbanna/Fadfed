package com.optimalsolutions.fadfed.utils;

import android.widget.ImageView;

import com.optimalsolutions.fadfed.AppController;
import com.squareup.picasso.Picasso;
import com.optimalsolutions.fadfed.chatkit.commons.ImageLoader;

/**
 * Created by mahmoud on 2/26/18.
 *
 */

public class ChatDialogImageLoader implements ImageLoader {

    @Override
    public void loadImage(ImageView imageView, String url) {
        Picasso.with(AppController.getCurrentContext()).load(url).into(imageView);
    }
}
