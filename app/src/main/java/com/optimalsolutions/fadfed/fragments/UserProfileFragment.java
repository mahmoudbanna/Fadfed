package com.optimalsolutions.fadfed.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.analytics.HitBuilders;
import com.optimalsolutions.fadfed.AppController;
import com.optimalsolutions.fadfed.R;
import com.optimalsolutions.fadfed.network.ErrorHandler;
import com.optimalsolutions.fadfed.network.NetworkHandler;

import org.json.JSONObject;

public class UserProfileFragment extends Fragment {

    private View v;
    private FragmentTabHost mTabHost;
    private TextView usernametv, useragetv, usersextv, usercountrytv,
            postcounttv, folowercounttv;
    private NetworkImageView userimg;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (v == null) {
            v = inflater.inflate(R.layout.fragment_user_profile, null);

            usernametv = (TextView) v.findViewById(R.id.usernametv);
            useragetv = (TextView) v.findViewById(R.id.useragetv);
            usersextv = (TextView) v.findViewById(R.id.usersextv);
            usercountrytv = (TextView) v.findViewById(R.id.usercountrytv);
            userimg = (NetworkImageView) v.findViewById(R.id.userimg);

            mTabHost = (FragmentTabHost) v.findViewById(android.R.id.tabhost);
            mTabHost.setup(getActivity(), getChildFragmentManager(),
                    android.R.id.tabcontent);

            View userview = LayoutInflater.from(mTabHost.getContext()).inflate(
                    R.layout.user_tab_layout, null);
            TextView tv = (TextView) userview.findViewById(R.id.textView);
            tv.setText(R.string.noofposts);
            postcounttv = (TextView) userview.findViewById(R.id.notv);

            View followerview = LayoutInflater.from(mTabHost.getContext()).inflate(
                    R.layout.user_tab_layout, null);
            TextView tv1 = (TextView) followerview.findViewById(R.id.textView);
            tv1.setText(R.string.noofollowers);
            folowercounttv = (TextView) followerview.findViewById(R.id.notv);

            mTabHost.addTab(mTabHost.newTabSpec("tab1").setIndicator(followerview), UserFollowersFragment.class, null);
            mTabHost.addTab(mTabHost.newTabSpec("tab2").setIndicator(userview), UserPostsFragment.class, null);


            mTabHost.setCurrentTab(1);
        }

        getUserProfileInfo();
        return v;

    }

    @Override
    public void onResume() {
        super.onResume();
        AppController.getInstance().getDefaultTracker().setScreenName("User Profile screen");
        AppController.getInstance().getDefaultTracker().send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void getUserProfileInfo() {

        String url = AppController.server + "f_get_user_profile.php?userId="
                + AppController.getInstance().getUserId() + "&sessionNumber="
                + AppController.getInstance().getSessionNumber()
                + "&userIdProfile=" + AppController.getInstance().getUserId();

        userimg.setImageUrl(AppController.server
                        + "f_get_user_img_profile_viewer.php?userId="
                        + AppController.getInstance().getUserId() + "&sessionNumber="
                        + AppController.getInstance().getSessionNumber()
                        + "&userIdImage=" + AppController.getInstance().getUserId(),
                NetworkHandler.getImageLoader());


//        Alerts.showLoadingDialog();

        NetworkHandler.execute(url, null,this::handelGetUserProfileInfoResponse,
                new ErrorHandler(),
                true,
                false

        );

    }

    private void handelGetUserProfileInfoResponse(JSONObject response) {
        Log.v("test", response.toString());
        try {
            Log.v("test", response.toString());

            if (response != null
                    && response.getInt("resultId") == 9000) {

                usernametv.setText(response
                        .getString("nickName"));

                try {
                    if (response.getString("gender") != null) {

                        usersextv
                                .setText(getResources()
                                        .getStringArray(
                                                R.array.sex_array)[Integer.parseInt(response
                                        .getString("gender")) - 1]);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (response.getString("birthDate") != null) {

                        useragetv.setText(response.getString("birthDate"));
                    }
                } catch (Exception e) {
                    useragetv.setText(getString(R.string.notspecfied));
                    e.printStackTrace();
                }
                try {
                    if (response.getString("country") != null) {
                        usercountrytv.setText(getUserCountryArabicName(response.getString("country")));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    if (response.getString("userPostCount") != null) {
                        postcounttv.setText(response
                                .getString("userPostCount"));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    if (response
                            .getString("userFollowersCount") != null) {
                        folowercounttv.setText(response
                                .getString("userFollowersCount"));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getUserCountryArabicName(String engSymbol) {

        String arName = "";
        int index = 0;

        for (String str : getResources().getStringArray(
                R.array.arab_countries_en_symbols)) {
            if (str.equalsIgnoreCase(engSymbol)) {
                arName = getResources().getStringArray(R.array.arab_countries)[index];
            }
            index++;

        }
        return arName;
    }
}
