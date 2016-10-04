package com.chatmodule.chatUtils;

import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.format.DateUtils;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by mht on 20/9/16.
 */
public class ChatUtils {

    private static final String TAG = "ChatUtils";
    public final static String XMPP_USERNAME = "xmpp_username";
    public final static String XMPP_RECEIVER = "xmpp_receiver";

    public final static int REQUEST_CHAT_LIST = 111;

    public final static String EXTRA_MSG_DELIVERED = "msg_delivered";
    public final static String EXTRA_MSG_ID = "msg_id";


    private static DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
    private static DateFormat timeFormat = new SimpleDateFormat("HH:mm a");

    public static String getCurrentTime() {

        Date today = Calendar.getInstance().getTime();
        return timeFormat.format(today);
    }

    public static String getCurrentDate() {
        Date today = Calendar.getInstance().getTime();
        return dateFormat.format(today);
    }

    public static long getTimeStamp() {
        return Calendar.getInstance().getTimeInMillis();
    }

    public static String getTime(Context context, long time){
        Date df = new java.util.Date(time);
        String date = timeFormat.format(df).toString();
        return date;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivityManager.getNetworkInfo(mNetwork);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    return true;
                }
            }
        }else {
            if (connectivityManager != null) {
                //noinspection deprecation
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

}
