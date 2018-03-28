package ua.stellar.seatingchart.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiReceiver extends BroadcastReceiver {

    private final String LOG_TAG = "RESERVE";

    @Override
    public void onReceive(Context context, Intent intent) {

        NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        if ((info != null) && (info.isConnected())) {
            Log.d(LOG_TAG, "WI-FI Connected");

            // e.g. To check the Network Name or other info:
            WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String ssid = wifiInfo.getSSID();
        } else {
            Log.d(LOG_TAG, "WI-FI not Connected");
        }

//        final String action = intent.getAction();
//        if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
//            if (intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false)){
//                //do stuff
//            } else {
//                // wifi connection was lost
//            }
//        }
    }
}