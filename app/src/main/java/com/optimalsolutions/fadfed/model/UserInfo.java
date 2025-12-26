package com.optimalsolutions.fadfed.model;

import java.io.Serializable;

public class UserInfo implements Serializable{

	private String userId, email, nickName, showCountry, gender, showGender,
			birthDate, showBirthDate, country,postCount,followersCount,isUserForbidden,isUserFollowed;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getShowCountry() {
		return showCountry;
	}

	public void setShowCountry(String showCountry) {
		this.showCountry = showCountry;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getShowGender() {
		return showGender;
	}

	public void setShowGender(String showGender) {
		this.showGender = showGender;
	}

	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}

	public String getShowBirthDate() {
		return showBirthDate;
	}

	public void setShowBirthDate(String showBirthDate) {
		this.showBirthDate = showBirthDate;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPostCount() {
		return postCount;
	}

	public void setPostCount(String postCount) {
		this.postCount = postCount;
	}

	public String getFollowersCount() {
		return followersCount;
	}

	public void setFollowersCount(String followersCount) {
		this.followersCount = followersCount;
	}

	public String getIsUserForbidden() {
		return isUserForbidden;
	}

	public void setIsUserForbidden(String isUserForbidden) {
		this.isUserForbidden = isUserForbidden;
	}

	public String getIsUserFollowed() {
		return isUserFollowed;
	}

	public void setIsUserFollowed(String isUserFollowed) {
		this.isUserFollowed = isUserFollowed;
	}

}
