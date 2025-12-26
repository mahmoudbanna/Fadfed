package com.optimalsolutions.fadfed.listview;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.optimalsolutions.fadfed.AppController;
import com.optimalsolutions.fadfed.fragments.ChatsFragment;
import com.optimalsolutions.fadfed.Home;
import com.optimalsolutions.fadfed.R;
import com.optimalsolutions.fadfed.model.UserInfo;
import com.optimalsolutions.fadfed.network.AbstractErrorHandler;
import com.optimalsolutions.fadfed.utils.Alerts;
import com.optimalsolutions.fadfed.network.NetworkHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class UserProfileViewHolder implements Holder {

    private TextView usernametv, useragetv, usersextv, usercountrytv,
            noofposttv, nooffollowertv, showuseragetv, showusergendertv,
            showusercountrytv, sendmessagebut;
    private Button followuserbut, blockuserbut;
    private NetworkImageView userimg;
    private UserInfo userInfo;
    private BaseAdapter adapter;

    public View of(UserInfo userInfo, BaseAdapter adapter ) {

        this.userInfo = userInfo;
        this.adapter = adapter;

        LayoutInflater inflater = (LayoutInflater) AppController.getCurrentContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView= inflater.inflate(R.layout.user_profile_item, null);


        setUsernametv((TextView) convertView.findViewById(R.id.usernametv));
        setUseragetv((TextView) convertView.findViewById(R.id.useragetv));
        setUsersextv((TextView) convertView.findViewById(R.id.usersextv));
        setUsercountrytv((TextView) convertView.findViewById(R.id.usercountrytv));
        setNoofposttv((TextView) convertView.findViewById(R.id.noofposttv));
        setNooffollowertv((TextView) convertView.findViewById(R.id.nooffollowertv));
        setShowuseragetv((TextView) convertView.findViewById(R.id.showuseragetv));
        setShowusergendertv((TextView) convertView.findViewById(R.id.showusergendertv));
        setShowusercountrytv((TextView) convertView.findViewById(R.id.showusercountrytv));
        setFollowuserbut((Button) convertView.findViewById(R.id.followuserbut));
        setBlockuserbut((Button) convertView.findViewById(R.id.blockuserbut));
        setSendmessagebut((TextView) convertView.findViewById(R.id.sendmessagebut));
        setUserimg((NetworkImageView) convertView.findViewById(R.id.userimg));

        setData();

        return convertView;
    }

    public void setData() {


        userimg.setImageUrl(AppController.server
                        + "f_get_user_img_profile_viewer.php?userId="
                        + AppController.getInstance().getUserId() + "&sessionNumber="
                        + AppController.getInstance().getSessionNumber()
                        + "&userIdImage=" + userInfo.getUserId(),
                NetworkHandler.getImageLoader());

        Log.d("test", "setData :: user info id  ::" + userInfo.getUserId());

        followuserbut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (AppController.isBrowseApp()) {
                    Alerts.showLogin();
                    return;
                }
//				Alerts.showProgressDialog();
                if (userInfo.getIsUserFollowed() == null || userInfo.getIsUserFollowed().equalsIgnoreCase("2"))
                    followUser("1");
                else
                    followUser("2");
            }
        });

        blockuserbut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (AppController.isBrowseApp()) {
                    Alerts.showLogin();
                    return;
                }
//				Alerts.showProgressDialog();

                if (userInfo.getIsUserForbidden() == null || userInfo.getIsUserForbidden().equalsIgnoreCase("2"))
                    blockUser("1");
                else
                    blockUser("2");

            }
        });


        sendmessagebut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (AppController.isBrowseApp()) {
                    Alerts.showLogin();
                    return;
                }

                ChatsFragment chatsFragment = new ChatsFragment();
                Bundle chatsFragmentBundle = new Bundle();

                ChannelItem channelItem = new ChannelItem();
                channelItem.setToUserId(userInfo.getUserId());
                chatsFragmentBundle.putSerializable("channelItem", channelItem);
                chatsFragment.setArguments(chatsFragmentBundle);

                Home.getInstacne().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_container, chatsFragment)
                        .addToBackStack(ChatsFragment.class.getName())
                        .commit();


            }
        });

        setup();
    }

    private void setup() {

        usernametv.setText(userInfo.getNickName());

        try {
            if (userInfo.getGender() != null) {
                showusergendertv.setVisibility(TextView.VISIBLE);
                usersextv.setText(Home.getInstacne().getResources().getStringArray(R.array.sex_array)[Integer.parseInt(userInfo.getGender()) - 1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (userInfo.getBirthDate() != null) {
                showuseragetv.setVisibility(TextView.VISIBLE);
                useragetv.setText(userInfo.getBirthDate());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (userInfo.getCountry() != null) {
                showusercountrytv.setVisibility(TextView.VISIBLE);
                usercountrytv.setText(getUserCountryArabicName(userInfo
                        .getCountry()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (userInfo.getPostCount() != null) {

                noofposttv.setText(userInfo.getPostCount());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (userInfo.getFollowersCount() != null) {

                nooffollowertv.setText(userInfo.getFollowersCount());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (userInfo.getIsUserForbidden() != null
                    && userInfo.getIsUserForbidden().equalsIgnoreCase("1"))

                blockuserbut.setText(Home.getInstacne().getString(
                        R.string.unblock));
            else
                blockuserbut.setText(Home.getInstacne().getString(
                        R.string.block));

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (userInfo.getIsUserFollowed() != null
                    && userInfo.getIsUserFollowed().equalsIgnoreCase("1"))
                followuserbut.setText(Home.getInstacne().getString(
                        R.string.unfollow));
            else
                followuserbut.setText(Home.getInstacne().getString(
                        R.string.follow));

        } catch (Exception e) {
            e.printStackTrace();
        }

        adapter.notifyDataSetChanged();
    }

    private void getUserProfileInfo() {

        followuserbut.setEnabled(false);
        blockuserbut.setEnabled(false);

        String url = AppController.server + "f_get_user_profile.php?userId="
                + AppController.getInstance().getUserId() + "&sessionNumber="
                + AppController.getInstance().getSessionNumber()
                + "&userIdProfile=" + userInfo.getUserId();

        Log.v("test", url);

        NetworkHandler.execute(url, null,this::handleGetUserInfoResponse,
                new AbstractErrorHandler() {

                    public void handleError() {

                        followuserbut.setEnabled(true);
                        blockuserbut.setEnabled(true);
//						Alerts.hideProgressDialog();

                    }
                }, true,
                false
        );

    }

    private void handleGetUserInfoResponse(JSONObject response) {
        try {

            if (response != null && response.getInt("resultId") == 9000) {
                try {
                    userInfo.setUserId(response.getString("userId"));
                } catch (JSONException e) {
                }
                try {
                    userInfo.setNickName(response.getString("nickName"));
                } catch (JSONException e) {
                }
                try {
                    userInfo.setBirthDate(response
                            .getString("birthDate"));
                } catch (JSONException e) {
                }
                try {
                    userInfo.setEmail(response
                            .getString("email"));
                } catch (JSONException e) {
                }
                try {
                    userInfo.setCountry(response
                            .getString("country"));
                } catch (JSONException e) {
                }
                try {
                    userInfo.setGender(response
                            .getString("gender"));
                } catch (JSONException e) {
                }

                try {
                    userInfo.setPostCount(response
                            .getString("userPostCount"));

                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {

                    userInfo.setFollowersCount(response
                            .getString("userFollowersCount"));

                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {

                    userInfo.setIsUserForbidden(response
                            .getString("isUserForbidden"));

                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {

                    userInfo.setIsUserFollowed(response
                            .getString("isUserFollowed"));

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                Alerts.showError(
                        response.getString("resultMessage"));

            }
            followuserbut.setEnabled(true);
            blockuserbut.setEnabled(true);
            setup();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void followUser(String followType) {
        followuserbut.setEnabled(false);
        blockuserbut.setEnabled(false);

        String url = AppController.server
                + "f_follow_unfollow_user.php?userId="
                + AppController.getInstance().getUserId() + "&sessionNumber="
                + AppController.getInstance().getSessionNumber()
                + "&userIdFollow=" + userInfo.getUserId() + "&followType="
                + followType;
        Log.v("test", url);

        NetworkHandler.execute(url, null,this::handleFollowUserResponse,
                new AbstractErrorHandler() {
                    @Override
                    public void handleError() {

                        followuserbut.setEnabled(true);
                        blockuserbut.setEnabled(true);
//						Alerts.hideProgressDialog();
                    }
                },
                false,
                true
        );

    }

    private void handleFollowUserResponse(JSONObject response) {
        Log.v("test", response.toString());

        try {
            if (response != null
                    && response.getInt("resultId") == 9000) {
                getUserProfileInfo();

            } else {
                Alerts.showError(
                        response.getString("resultMessage"));

            }
        } catch (JSONException e) {
            followuserbut.setEnabled(true);
            blockuserbut.setEnabled(true);
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void blockUser(String blockType) {
        followuserbut.setEnabled(false);
        blockuserbut.setEnabled(false);

        String url = AppController.server
                + "f_forbidden_unforbidden_user.php?userId="
                + AppController.getInstance().getUserId() + "&sessionNumber="
                + AppController.getInstance().getSessionNumber()
                + "&userIdForbidden=" + userInfo.getUserId()
                + "&forbiddenType=" + blockType;

        NetworkHandler.execute(url, null,this::handleBlockUserResponse,
                new AbstractErrorHandler() {
                    @Override
                    public void handleError() {
                        followuserbut.setEnabled(true);
                        blockuserbut.setEnabled(true);
                    }
                }, false,true
        );
    }

    private void handleBlockUserResponse(JSONObject response) {
        Log.v("test", response.toString());

        try {
            if (response != null
                    && response.getInt("resultId") == 9000) {
                getUserProfileInfo();

            } else {
//								Alerts.hideProgressDialog();
                Alerts.showError(
                        response.getString("resultMessage"));

            }
        } catch (JSONException e) {
        }
    }

    private String getUserCountryArabicName(String engSymbol) {

        String arName = "";
        int index = 0;

        for (String str : Home.getInstacne().getResources()
                .getStringArray(R.array.arab_countries_en_symbols)) {
            if (str.equalsIgnoreCase(engSymbol)) {
                arName = Home.getInstacne().getResources()
                        .getStringArray(R.array.arab_countries)[index];
            }
            index++;

        }

        return arName;

    }

    public TextView getUsernametv() {
        return usernametv;
    }

    public void setUsernametv(TextView usernametv) {
        this.usernametv = usernametv;
    }

    public TextView getUseragetv() {
        return useragetv;
    }

    public void setUseragetv(TextView useragetv) {
        this.useragetv = useragetv;
    }

    public TextView getUsersextv() {
        return usersextv;
    }

    public void setUsersextv(TextView usersextv) {
        this.usersextv = usersextv;
    }

    public TextView getUsercountrytv() {
        return usercountrytv;
    }

    public void setUsercountrytv(TextView usercountrytv) {
        this.usercountrytv = usercountrytv;
    }

    public TextView getNoofposttv() {
        return noofposttv;
    }

    public void setNoofposttv(TextView noofposttv) {
        this.noofposttv = noofposttv;
    }

    public TextView getNooffollowertv() {
        return nooffollowertv;
    }

    public void setNooffollowertv(TextView nooffollowertv) {
        this.nooffollowertv = nooffollowertv;
    }

    public TextView getShowuseragetv() {
        return showuseragetv;
    }

    public void setShowuseragetv(TextView showuseragetv) {
        this.showuseragetv = showuseragetv;
    }

    public TextView getShowusergendertv() {
        return showusergendertv;
    }

    public void setShowusergendertv(TextView showusergendertv) {
        this.showusergendertv = showusergendertv;
    }

    public TextView getShowusercountrytv() {
        return showusercountrytv;
    }

    public void setShowusercountrytv(TextView showusercountrytv) {
        this.showusercountrytv = showusercountrytv;
    }

    public Button getFollowuserbut() {
        return followuserbut;
    }

    public void setFollowuserbut(Button followuserbut) {
        this.followuserbut = followuserbut;
    }

    public Button getBlockuserbut() {
        return blockuserbut;
    }

    public void setBlockuserbut(Button blockuserbut) {
        this.blockuserbut = blockuserbut;
    }

    public NetworkImageView getUserimg() {
        return userimg;
    }

    public void setUserimg(NetworkImageView userimg) {
        this.userimg = userimg;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public TextView getSendmessagebut() {
        return sendmessagebut;
    }

    public void setSendmessagebut(TextView sendmessagebut) {
        this.sendmessagebut = sendmessagebut;
    }
}
