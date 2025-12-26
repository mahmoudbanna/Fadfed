package com.optimalsolutions.fadfed.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.optimalsolutions.fadfed.AppController;
import com.optimalsolutions.fadfed.Home;
import com.optimalsolutions.fadfed.Login;
import com.optimalsolutions.fadfed.R;
import com.optimalsolutions.fadfed.fragments.AboutUsFragment;
import com.optimalsolutions.fadfed.fragments.ContactUsFragment;
import com.optimalsolutions.fadfed.fragments.EditUserProfileFragment;
import com.optimalsolutions.fadfed.fragments.UserProfileFragment;
import com.optimalsolutions.fadfed.network.LoginHandler;
import com.optimalsolutions.fadfed.utils.Alerts;


import java.util.ArrayList;

/**
 * Created by mahmoud on 3/9/18.
 *
 */

public class SlideMenu {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;
    private ShareDialog shareDialog;

    public SlideMenu(){

        // nav drawer icons from resources

        mDrawerLayout = (DrawerLayout) Home.getInstacne().findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) Home.getInstacne().findViewById(R.id.list_slidermenu);
        navDrawerItems = new ArrayList<>();
        mDrawerList.setOnItemClickListener((AdapterView<?> adapterView, View view, int position, long l) -> displayView(position));

        adapter = new NavDrawerListAdapter(AppController.getCurrentContext(),navDrawerItems);
        mDrawerList.setAdapter(adapter);
        initMenu();
    }

    public void initMenu() {

        navDrawerItems.clear();

        if (AppController.isBrowseApp()) {
            Log.d("test", "initMenu: browse app ");
            navMenuTitles = AppController.getCurrentContext().getResources().getStringArray(R.array.nav_drawer_items_browse_app);
            navMenuIcons = AppController.getCurrentContext().getResources().obtainTypedArray(R.array.nav_drawer_icons_browse_app);
        } else {
            Log.d("test", "initMenu: not browse app ");
            navMenuTitles = AppController.getCurrentContext().getResources().getStringArray( R.array.nav_drawer_items);
            navMenuIcons = AppController.getCurrentContext().getResources().obtainTypedArray(R.array.nav_drawer_icons);
        }

        for (int i = 0; i < navMenuTitles.length; i++)
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[i], navMenuIcons.getResourceId(i, -1)));

        adapter.notifyDataSetChanged();
    }

    public void openMenu(){

        initMenu();
        mDrawerLayout.openDrawer(mDrawerList);
    }

    public void closeMenu(){
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    public boolean isOpen(){

        return mDrawerLayout.isDrawerOpen(mDrawerList);
    }

    public void setSelection(int position  ){

        mDrawerList.setItemChecked(position, true);
        mDrawerList.setSelection(position);

    }

    private void displayView(int position) {

        // update the main content by replacing fragments

        Fragment fragment = null;

        if (AppController.isBrowseApp()) {
            switch (position) {
                case 0:
                    showHomeScreen(position);
                    return;
                case 1:
                    fragment = new ContactUsFragment();
                    break;
                case 2:
                    fragment = new AboutUsFragment();
                    break;

                case 3:
                    showFacebookShareDialog();
                    break;

                case 4:
                    AppController.getInstance().deleteLoginInfo();
                    Login.show();
                default:
                    break;
            }
        } else {
            switch (position) {
                case 0:
                    showHomeScreen(position);
                    return;

                case 1:
                    if (AppController.isBrowseApp()) {
                        Alerts.showLogin();
                        return;
                    }

                    fragment = new UserProfileFragment();
                    break;

                case 2:
                    if (AppController.isBrowseApp()) {
                        Alerts.showLogin();
                        return;
                    }
                    fragment = new EditUserProfileFragment();
                    break;

                case 3:
                    fragment = new ContactUsFragment();
                    break;
                case 4:
                    fragment = new AboutUsFragment();
                    break;

                case 5:
                    showFacebookShareDialog();
                    break;

                case 6:
                    AppController.getInstance().deleteLoginInfo();
                    Home.open();

                default:
                    break;
            }
        }

        setSelection(position);
        closeMenu();

        if (fragment != null) {

            Home.getInstacne().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_container, fragment)
                    .addToBackStack(fragment.getClass().getName()).commit();

            // update selected item and title, then close the drawer

        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }


    private void showHomeScreen(int position) {

        Home.getInstacne().getHomeActionBar().closeNotifications();
        setSelection(position);
        closeMenu();

        if (Home.getInstacne().getSupportFragmentManager().getBackStackEntryCount() <= 1)
            return;
        else
            Home.getInstacne().getSupportFragmentManager().popBackStackImmediate(0, 0);

    }

    private void showFacebookShareDialog() {

        if(shareDialog==null)
            shareDialog = new ShareDialog(Home.getInstacne());


        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle(Home.getInstacne().getString(R.string.app_name))
                    .setContentDescription(Home.getInstacne().getString(R.string.login_hint))
                    .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.optimalsolutions.fadfed"))
                    .build();

            shareDialog.show(linkContent);
        }
    }


    private class NavDrawerListAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<NavDrawerItem> navDrawerItems;

        public NavDrawerListAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems) {
            this.context = context;
            this.navDrawerItems = navDrawerItems;
        }

        @Override
        public int getCount() {
            return navDrawerItems.size();
        }

        @Override
        public Object getItem(int position) {
            return navDrawerItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.drawer_list_item, null);
            }

            ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
            TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
            ImageButton arrowbut = (ImageButton) convertView
                    .findViewById(R.id.arrowbut);

            imgIcon.setImageResource(navDrawerItems.get(position).getIcon());
            txtTitle.setText(navDrawerItems.get(position).getTitle());
            arrowbut.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    displayView(position);

                }
            });

            return convertView;
        }
    }

    private class NavDrawerItem {

        private String title;
        private int icon;

        public NavDrawerItem(String title, int icon) {
            this.title = title;
            this.icon = icon;
        }

        public String getTitle() {
            return this.title;
        }

        public int getIcon() {
            return this.icon;
        }
    }
}
