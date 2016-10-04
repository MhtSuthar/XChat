package com.chatmodule.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.chatmodule.xmpp.MyXmppService;

import java.util.Calendar;

/**
 * Created by ubuntu on 6/7/16.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        startXmppService(context);
    }

    private void startXmppService(Context context) {
        Intent service = new Intent(context, MyXmppService.class);
        context.startService(service);
    }
}
