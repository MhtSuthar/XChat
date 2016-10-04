package com.chatmodule.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.chatmodule.R;
import com.chatmodule.chatUtils.ChatUtils;
import com.chatmodule.fragment.ChatListFragment;
import com.chatmodule.storage.SharedPreferenceUtil;
import com.chatmodule.xmpp.LocalBinder;
import com.chatmodule.xmpp.MyXmppService;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Presence;

public class ChatListActivity extends AppCompatActivity {

    private static final String TAG = "ChatListActivity";
    private boolean mBounded;
    private static MyXmppService mService;
    private ChatListFragment chatListFragment;


    private final ServiceConnection mConnection = new ServiceConnection() {

        @SuppressWarnings("unchecked")
        @Override
        public void onServiceConnected(final ComponentName name,
                                       final IBinder service) {
            mService = ((LocalBinder<MyXmppService>) service).getService();
            mBounded = true;
            Log.d(TAG, "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(final ComponentName name) {
            mService = null;
            mBounded = false;
            Log.d(TAG, "onServiceDisconnected");
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_frame);

        SharedPreferenceUtil.putValue(ChatUtils.XMPP_RECEIVER, "53#(+)#7778889999");//lollipop
        SharedPreferenceUtil.putValue(ChatUtils.XMPP_USERNAME, "52#(+)#9992221111");//kitkat
        //SharedPreferenceUtil.save();

        doBindService();

        chatListFragment = new ChatListFragment();
        replace(chatListFragment);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }

    void doBindService() {
        Intent intent = new Intent(this, MyXmppService.class);
        startService(intent);
        bindService(new Intent(this, MyXmppService.class), mConnection,
                Context.BIND_AUTO_CREATE);
    }

    void doUnbindService() {
        if (mConnection != null) {
            unbindService(mConnection);
        }
    }

    public static MyXmppService getmService() {
        return mService;
    }

    void replace(Fragment fragment){
        getSupportFragmentManager().beginTransaction().
                replace(R.id.container, fragment, fragment.getTag()).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ChatUtils.REQUEST_CHAT_LIST) {
            if (resultCode == Activity.RESULT_OK) {
                chatListFragment.refreshAdapter();
            }
        }
    }

    public void setOnlineStatus(){
        Presence presence = new Presence(Presence.Type.available);
        presence.setStatus("Online By Mht!");
        presence.setPriority(24);
        presence.setFrom(SharedPreferenceUtil.getString(ChatUtils.XMPP_USERNAME, ""));
        presence.setMode(Presence.Mode.available);
        try {
            if(getmService().xmpp != null)
                getmService().xmpp.connection.sendPacket(presence);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }
}
