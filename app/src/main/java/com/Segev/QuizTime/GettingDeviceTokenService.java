package com.Segev.QuizTime;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class GettingDeviceTokenService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // get a token from the device
        String DeviceToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("DEVICE TOKEN:", DeviceToken);
    }
}
