package com.chatmodule.xmpp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.util.Log;

import com.chatmodule.chatUtils.ChatUtils;
import com.chatmodule.storage.SharedPreferenceUtil;

import org.jivesoftware.smack.chat.Chat;

public class MyXmppService extends Service {

    private static final String DOMAIN = "chat.outperformfitness.com";
    private static final String USERNAME = SharedPreferenceUtil.getString(ChatUtils.XMPP_USERNAME, "");
    private static final String PASSWORD = "123456";
    public static ConnectivityManager cm;
    public static MyXMPP xmpp;
    private static final String TAG = "MyXmppService";
    //52#(+)#9992221111  for sender
    //53#(+)#7778889999  for receiver
    //52#(+)#9992221111, 123456 KITKAT

    @Override
    public IBinder onBind(final Intent intent) {
        return new LocalBinder<MyXmppService>(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate: XMpp Service Call");
        Log.e(TAG, "MyXMPP: Login " + USERNAME + ", " + PASSWORD);
        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        xmpp = MyXMPP.getInstance(MyXmppService.this, DOMAIN, USERNAME, PASSWORD);
        xmpp.connect("onCreate");
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags,
                              final int startId) {
        Log.e(TAG, "onStartCommand: XMpp Service onStartCommand");
        return START_STICKY;
    }

    @Override
    public boolean onUnbind(final Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        xmpp.connection.disconnect();
    }

    public static boolean isNetworkConnected() {
        return cm.getActiveNetworkInfo() != null;
    }
}