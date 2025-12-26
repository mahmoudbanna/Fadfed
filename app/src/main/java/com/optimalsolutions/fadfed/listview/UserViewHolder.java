package com.optimalsolutions.fadfed.listview;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.optimalsolutions.fadfed.AppController;
import com.optimalsolutions.fadfed.Home;
import com.optimalsolutions.fadfed.R;
import com.optimalsolutions.fadfed.fragments.UserFragment;
import com.optimalsolutions.fadfed.model.UserInfo;
import com.optimalsolutions.fadfed.network.NetworkHandler;

public class UserViewHolder {

	private TextView name, agetitletv, agetv, gendertitletv, gendertv,
			countrytitletv, countrytv;

	private NetworkImageView profilePic;
	private UserInfo userInfo;

	public UserViewHolder(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	public void setData() {

		name.setText(userInfo.getNickName());
		profilePic.setImageUrl(AppController.server
				+ "f_get_user_img_profile_viewer.php?userId="
				+ AppController.getInstance().getUserId() + "&sessionNumber="
				+ AppController.getInstance().getSessionNumber()
				+ "&userIdImage=" + userInfo.getUserId(),
				NetworkHandler.getImageLoader());

		if (!TextUtils.isEmpty(userInfo.getBirthDate())) {
			agetv.setText(userInfo.getBirthDate());
			agetv.setVisibility(View.VISIBLE);
			agetitletv.setVisibility(View.VISIBLE);
		} else {
			agetv.setVisibility(View.GONE);
			agetitletv.setVisibility(View.GONE);
		}

		if (!TextUtils.isEmpty(userInfo.getCountry())) {
			countrytv.setText(userInfo.getCountry());
			countrytv.setVisibility(View.VISIBLE);
			countrytitletv.setVisibility(View.VISIBLE);
		} else {
			countrytv.setVisibility(View.GONE);
			countrytitletv.setVisibility(View.GONE);
		}

		if (!TextUtils.isEmpty(userInfo.getGender())) {
			gendertv.setText(Home.getInstacne().getResources()
					.getStringArray(R.array.sex_array)[Integer
					.parseInt(userInfo.getGender()) - 1]);
			gendertv.setVisibility(View.VISIBLE);
			gendertitletv.setVisibility(View.VISIBLE);
		} else {
			gendertv.setVisibility(View.GONE);
			gendertitletv.setVisibility(View.GONE);
		}

		if (!TextUtils.isEmpty(userInfo.getCountry())) {
			countrytv.setText(getUserCountryArabicName(userInfo.getCountry()));
			countrytv.setVisibility(View.VISIBLE);
			countrytitletv.setVisibility(View.VISIBLE);
		} else {
			countrytv.setVisibility(View.GONE);
			countrytitletv.setVisibility(View.GONE);
		}

		name.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				UserFragment userFragment = new UserFragment();
				Bundle userFragmentBundle = new Bundle();
				userFragmentBundle.putSerializable("userInfo",userInfo);
				userFragment.setArguments(userFragmentBundle);

				Home.getInstacne()
						.getSupportFragmentManager()
						.beginTransaction()
						.replace(R.id.frame_container,userFragment)
						.addToBackStack(UserFragment.class.getName())
						.commit();

			}
		});

		profilePic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				UserFragment userFragment = new UserFragment();
				Bundle userFragmentBundle = new Bundle();
				userFragmentBundle.putSerializable("userInfo",userInfo);
				userFragment.setArguments(userFragmentBundle);

				Home.getInstacne()
						.getSupportFragmentManager()
						.beginTransaction()
						.replace(R.id.frame_container,userFragment)
						.addToBackStack(UserFragment.class.getName())
						.commit();

			}
		});


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

	public TextView getName() {
		return name;
	}

	public void setName(TextView name) {
		this.name = name;
	}

	public TextView getAgetitletv() {
		return agetitletv;
	}

	public void setAgetitletv(TextView agetitletv) {
		this.agetitletv = agetitletv;
	}

	public TextView getAgetv() {
		return agetv;
	}

	public void setAgetv(TextView agetv) {
		this.agetv = agetv;
	}

	public TextView getGendertitletv() {
		return gendertitletv;
	}

	public void setGendertitletv(TextView gendertitletv) {
		this.gendertitletv = gendertitletv;
	}

	public TextView getGendertv() {
		return gendertv;
	}

	public void setGendertv(TextView gendertv) {
		this.gendertv = gendertv;
	}

	public TextView getCountrytitletv() {
		return countrytitletv;
	}

	public void setCountrytitletv(TextView countrytitletv) {
		this.countrytitletv = countrytitletv;
	}

	public TextView getCountrytv() {
		return countrytv;
	}

	public void setCountrytv(TextView countrytv) {
		this.countrytv = countrytv;
	}

	public NetworkImageView getProfilePic() {
		return profilePic;
	}

	public void setProfilePic(NetworkImageView profilePic) {
		this.profilePic = profilePic;
	}

}
