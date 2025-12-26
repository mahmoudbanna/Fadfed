package com.optimalsolutions.fadfed.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.optimalsolutions.fadfed.AppController;
import com.optimalsolutions.fadfed.Home;
import com.optimalsolutions.fadfed.R;
import com.optimalsolutions.fadfed.animation.PagerSlidingTabStrip;
import com.optimalsolutions.fadfed.messages.MessageInput;
import com.optimalsolutions.fadfed.network.ErrorHandler;
import com.optimalsolutions.fadfed.network.NetworkHandler;
import com.optimalsolutions.fadfed.utils.Alerts;
import com.optimalsolutions.fadfed.utils.SoftKeypad;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class HomeFragment extends Fragment {

    private static final String name = "Home Screen";
    private Tracker mTracker;
    private PostsFragment newPostsFragment, mostActivePostsFragment, followersPostsFragment;
    private MessageInput newpostinput;
    private View view;
    private ViewPager viewPager;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        newPostsFragment = PostsFragment.of(PostsFragment.NEW_POSTS);
        mostActivePostsFragment = PostsFragment.of(PostsFragment.MOST_ACTIVE_POSTS);
        followersPostsFragment = PostsFragment.of(PostsFragment.FOLLOWERS_POSTS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("test", "on home fragment onCreateView");

        if (view == null) {

            view = inflater.inflate(R.layout.fragment_home, null);
            newpostinput = view.findViewById(R.id.newpostinput);
            newpostinput.setInputListener(this::onSubmit);

            // Get the ViewPager and set it's PagerAdapter so that it can display items
            viewPager = (ViewPager) view.findViewById(R.id.viewpager);
            viewPager.setAdapter(new MyPagerAdapter(Home.getInstacne().getSupportFragmentManager()));

            // Give the PagerSlidingTabStrip the ViewPager
            PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
            // Attach the view pager to the tab strip
            tabsStrip.setViewPager(viewPager);

            viewPager.setCurrentItem(3);
        }

        return view;
    }


    @Override
    public void onResume() {

        super.onResume();

        Log.d("test", "on home fragment onResume");

        mTracker = AppController.getInstance().getDefaultTracker();
        mTracker.setScreenName(name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public boolean onSubmit(CharSequence input) {

        SoftKeypad.hideSoftKeypad(view);

        if (AppController.isBrowseApp()) {
            Alerts.showLogin();
            return false;

        } else {

            try {
                NetworkHandler.execute(AppController.server
                                + "f_add_post.php?userId=" + AppController.getInstance().getUserId()
                                + "&sessionNumber=" + AppController.getInstance().getSessionNumber()
                                + "&content=" + URLEncoder.encode(StringEscapeUtils.escapeJava(input.toString()), "utf-8")
                                + "&categoryId=1", null, this::handleNewPostResponse,
                        new ErrorHandler(),false, true);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
        return true;
    }

    private void handleNewPostResponse(JSONObject response) {
        Alerts.hideProgressDialog();

        try {
            if (response.getInt("resultId") == 9000) {
                viewPager.setCurrentItem(3);
                newPostsFragment.refreshPosts();
            } else {
                Alerts.hideProgressDialog();
                Alerts.showError(response.getString("resultMessage"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {


        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return AppController.isBrowseApp() ? 2 : 3;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {

            if (AppController.isBrowseApp()) {

                switch (position) {
                    case 1:
                        return newPostsFragment;
                    default:
                        return mostActivePostsFragment;
                }
            } else {
                switch (position) {
                    case 1:
                        return mostActivePostsFragment;
                    case 2:
                        return newPostsFragment;
                    default:
                        return followersPostsFragment;
                }
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {

            if (AppController.isBrowseApp()) {
                switch (position) {
                    case 1:
                        return getString(R.string.nav_new_post);
                    default:
                        return getString(R.string.nav_most_active);
                }
            } else {
                switch (position) {
                    case 1:
                        return getString(R.string.nav_most_active);
                    case 2:
                        return getString(R.string.nav_new_post);
                    default:
                        return getString(R.string.nav_follower_post);
                }
            }

        }
    }
}