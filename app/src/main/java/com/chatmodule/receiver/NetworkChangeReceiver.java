package com.chatmodule.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.chatmodule.chatUtils.ChatUtils;
import com.chatmodule.xmpp.MyXmppService;


/**
 * Created by Android-132 on 11-Feb-16.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        boolean status = ChatUtils.isOnline(context);
        Log.e("Network Chnage Service", ""+isMyServiceRunning(MyXmppService.class, context));
        if(status){
            MyXmppService.xmpp.disconnect();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Intent service = new Intent(context, MyXmppService.class);
                    //context.startService(service);
                    MyXmppService.xmpp.connect("onCreate");
                }
            }, 1500);
        }else{
            //MyXmppService.xmpp.disconnect();
            /*Intent inten = new Intent(context, MyXmppService.class);
            context.stopService(inten);*/
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
