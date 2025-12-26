package com.optimalsolutions.fadfed.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.analytics.HitBuilders;
import com.optimalsolutions.fadfed.AppController;
import com.optimalsolutions.fadfed.R;
import com.optimalsolutions.fadfed.network.ErrorHandler;
import com.optimalsolutions.fadfed.utils.Alerts;
import com.optimalsolutions.fadfed.network.NetworkHandler;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ContactUsFragment extends Fragment {

	private View v;
	private EditText usernameet, emailet, phoneet, messagecontentet;
	private Button publishmessagebut;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(v == null) {
			v = inflater.inflate(R.layout.fragment_contact_us, null);

			usernameet = (EditText) v.findViewById(R.id.usernameet);
			emailet = (EditText) v.findViewById(R.id.emailet);
			phoneet = (EditText) v.findViewById(R.id.phoneet);
			messagecontentet = (EditText) v.findViewById(R.id.messagecontentet);

			publishmessagebut = (Button) v.findViewById(R.id.publishmessagebut);

			publishmessagebut.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					send();

				}
			});
		}
		return v;

	}

	@Override
	public void onResume() {
		super.onResume();
		AppController.getInstance().getDefaultTracker().setScreenName("Contact Us Screen");
		AppController.getInstance().getDefaultTracker().send(new HitBuilders.ScreenViewBuilder().build());
	}

	private void send() {

		if (usernameet.getText().toString() == null
				|| usernameet.getText().toString().length() <= 0
				|| usernameet.getText().toString().trim()
						.equalsIgnoreCase(getString(R.string.username)))
			usernameet.setError(getString(R.string.pleaseenterusername));

		else if (emailet.getText().toString() == null
				|| emailet.getText().toString().length() <= 0
				|| emailet.getText().toString().trim()
						.equalsIgnoreCase(getString(R.string.email)))
			emailet.setError(getString(R.string.pleaseenteremail));

		else if (messagecontentet.getText().toString() == null
				|| messagecontentet.getText().toString().length() <= 0
				|| messagecontentet.getText().toString().trim()
						.equalsIgnoreCase(getString(R.string.messagecontent)))
			messagecontentet
					.setError(getString(R.string.pleasentermessagecontent));
		else {

			String url = "";
			try {
				url = AppController.server
						+ "f_contact_us.php?userId="
						+ AppController.getInstance().getUserId()
						+ "&sessionNumber="
						+ AppController.getInstance().getSessionNumber()
						+ "&name="
						+ usernameet.getText().toString()
						+ "&phone="
						+ phoneet.getText().toString()
						+ "&email="
						+ emailet.getText().toString()
						+ "&message="
						+ URLEncoder.encode(messagecontentet.getText()
						.toString(), "utf-8");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			Log.v("test", url);


			NetworkHandler.execute(url, null,this::handleSendContactMessageResponse,
					new ErrorHandler(),
					false,
					true
			);

		}
	}

	private void handleSendContactMessageResponse(JSONObject response) {
		try {

            if (response.getInt("resultId") == 9000) {

            } else {

                Alerts.showError(
                        getString(R.string.error_title),
                        response.getString("resultMessage"));
            }
        } catch (Exception e) {

            Alerts.showError(
                    getString(R.string.error_title),
                    getString(R.string.error_text));
            e.printStackTrace();
        }
	}

}
