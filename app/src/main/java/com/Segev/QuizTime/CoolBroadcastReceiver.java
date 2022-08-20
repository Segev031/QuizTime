package com.Segev.QuizTime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.widget.Toast;

public class CoolBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // check if it is the right broadcast receiver
        if(ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            // check if there is a connectivity
            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            if(noConnectivity){
                Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT).show();
            } else {
                /// everything is good, no problem
            }
        }
    }
}
