package com.optimalsolutions.fadfed.GCM;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.optimalsolutions.fadfed.R;

import java.io.IOException;


public class RegistrationService extends IntentService {

    public static final String RGISTERATION_MESSAGE_ACTION = "com.optimalsolutions.fadfed.REGISTERATION_MESSAGE";


    public RegistrationService() {

        super("RegistrationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        InstanceID instanceId = InstanceID.getInstance(this);

        String registrationToken = null;

        try {

            registrationToken = instanceId.getToken(
                    getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE);

            Intent i = new Intent(RGISTERATION_MESSAGE_ACTION);
            i.putExtra("registrationToken", registrationToken);
            getApplicationContext().sendBroadcast(i);


        } catch (IOException e) {
            e.printStackTrace();
            Intent i = new Intent(RGISTERATION_MESSAGE_ACTION);
            i.putExtra("error", e.getMessage());
            getApplicationContext().sendBroadcast(i);

        }
    }
}

