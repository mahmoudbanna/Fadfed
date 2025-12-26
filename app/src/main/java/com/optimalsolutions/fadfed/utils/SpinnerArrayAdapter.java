package com.optimalsolutions.fadfed.utils;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.optimalsolutions.fadfed.R;

import java.util.Vector;

public class SpinnerArrayAdapter extends ArrayAdapter<String> {

	private Activity context;

	public SpinnerArrayAdapter(Activity context, Vector<String> data) {
		super(context, android.R.layout.simple_spinner_item, data);
		this.context = context;
	}

	public SpinnerArrayAdapter(Activity context, String[] data) {
		super(context, android.R.layout.simple_spinner_item, data);
		this.context = context;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v = super.getView(position, convertView, parent);

		((TextView) v).setTextSize(16);
		((TextView) v).setTextColor(context.getResources().getColorStateList(
				R.color.black));
		((TextView) v).setGravity(Gravity.CENTER);
		return v;
	}

	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		View v = super.getDropDownView(position, convertView, parent);
		// v.setBackgroundResource(R.drawable.spinner_bg);

		((TextView) v).setTextColor(context.getResources().getColorStateList(
				R.color.black));
		// ((TextView) v).setTypeface(fontStyle);
		((TextView) v).setGravity(Gravity.CENTER);

		return v;
	}
}
