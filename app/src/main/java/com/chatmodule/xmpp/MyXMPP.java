package com.chatmodule.xmpp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketExtensionFilter;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;

import android.support.v7.util.SortedList;
import android.util.Log;
import android.widget.Toast;

import com.chatmodule.R;
import com.chatmodule.chatUtils.ChatUtils;
import com.chatmodule.chatUtils.NotificationUtils;
import com.chatmodule.model.ChatMessage;
import com.chatmodule.storage.ChatConversationHandler;
import com.chatmodule.storage.SharedPreferenceUtil;
import com.chatmodule.xmpp.readMsg.ReadReceipt;
import com.chatmodule.xmpp.readMsg.ReadReceiptManager;
import com.google.gson.Gson;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.ChatStateListener;
import org.jivesoftware.smackx.chatstates.ChatStateManager;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class MyXMPP {

    private static final String TAG = "MyXMPP";
    public static boolean connected = false;
    public boolean loggedin = false;
    public static boolean isconnecting = false;
    public static boolean isToasted = true;
    private boolean chat_created = false;
    private String serverAddress;
    public static XMPPTCPConnection connection;
    public static String loginUser;
    public static String passwordUser;
    Gson gson;
    MyXmppService context;
    public static MyXMPP instance = null;
    public static boolean instanceCreated = false;
    private String MESSAGE_RECEIVE_BROADCAST = "msg_receive_broadcast";

    public MyXMPP(MyXmppService context, String serverAdress, String logiUser,
                  String passwordser) {
        this.serverAddress = serverAdress;
        this.loginUser = logiUser;
        this.passwordUser = passwordser;
        this.context = context;
        init();

    }

    public static MyXMPP getInstance(MyXmppService context, String server,
                                     String user, String pass) {
        if (instance == null) {
            instance = new MyXMPP(context, server, user, pass);
            instanceCreated = true;
        }
        return instance;

    }

    public Chat Mychat;

    ChatManagerListenerImpl mChatManagerListener;
    MMessageListener mMessageListener;

    static {
        try {
            Class.forName("org.jivesoftware.smack.ReconnectionManager");
        } catch (ClassNotFoundException ex) {
            // problem loading reconnection manager
        }
    }

    public void init() {
        gson = new Gson();
        mMessageListener = new MMessageListener(context);
        mChatManagerListener = new ChatManagerListenerImpl();
        initialiseConnection();
    }

    private void initialiseConnection() {
        XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration
                .builder();
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        config.setServiceName(serverAddress);
        config.setHost(serverAddress);
        config.setPort(5222);
        config.setDebuggerEnabled(true);
        config.setSendPresence(true);//For server client not connected
        XMPPTCPConnection.setUseStreamManagementResumptiodDefault(true);
        XMPPTCPConnection.setUseStreamManagementDefault(true);
        connection = new XMPPTCPConnection(config.build());
        XMPPConnectionListener connectionListener = new XMPPConnectionListener();
        connection.addConnectionListener(connectionListener);
        //connection.addPacketSendingListener(new MyPacketListener(), null);
    }

    public void disconnect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                connection.disconnect();
                isconnecting = false;
                isToasted = true;
                connected = false;
            }
        }).start();
    }


    public void connect(final String caller) {

        AsyncTask<Void, Void, Boolean> connectionThread = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected synchronized Boolean doInBackground(Void... arg0) {
                Log.e(TAG, "doInBackground: conected? "+connection.isConnected());
                if (connection.isConnected())
                    return false;
                isconnecting = true;

                /**
                 * This is for getting typing
                 */
                ProviderManager pm = new ProviderManager();
                pm.addExtensionProvider(ReadReceipt.ELEMENT, ReadReceipt.NAMESPACE, new ReadReceipt.Provider());
                pm.addExtensionProvider("active", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
                pm.addExtensionProvider("composing", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
                pm.addExtensionProvider("paused", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
                pm.addExtensionProvider("inactive", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
                pm.addExtensionProvider("gone", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
                /**
                 * End This is for getting typing
                 */

                if (isToasted)
                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                        @Override
                        public void run() {

                            Toast.makeText(context, caller + "=>connecting....", Toast.LENGTH_LONG).show();
                        }
                    });
                Log.e(TAG, caller + "=>connecting....");

                try {
                    new ProviderManager().addExtensionProvider(ReadReceipt.ELEMENT, ReadReceipt.NAMESPACE, new ReadReceipt.Provider());

                    connection.connect();

                    DeliveryReceiptManager dm = DeliveryReceiptManager
                            .getInstanceFor(connection);
                    dm.setAutoReceiptMode(DeliveryReceiptManager.AutoReceiptMode.always);
                    dm.addReceiptReceivedListener(new ReceiptReceivedListener() {

                        @Override
                        public void onReceiptReceived(final String fromid,
                                                      final String toid, final String msgid,
                                                      final Stanza packet) {
                            new ChatConversationHandler().updateMsgStatusDelivered(msgid);
                            Intent intent = new Intent(MESSAGE_RECEIVE_BROADCAST);
                            intent.putExtra(ChatUtils.EXTRA_MSG_DELIVERED, true);
                            intent.putExtra(ChatUtils.EXTRA_MSG_ID, msgid);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                        }
                    });

                    /**
                     * Get status for users
                     */
                    Roster roster = getRoaster();
                    /*Collection<RosterEntry> entries = roster.getEntries();
                    Presence presence;

                    for(RosterEntry entry : entries) {
                        presence = roster.getPresence(entry.getUser());
                        Log.e(TAG, "Status : "+entry.getUser()+", "+presence.getType().name()+", "+presence.getStatus());
                    }*/

                   roster.addRosterListener(new RosterListener() {
                       @Override
                       public void entriesAdded(Collection<String> addresses) {

                       }

                       @Override
                       public void entriesUpdated(Collection<String> addresses) {

                       }

                       @Override
                       public void entriesDeleted(Collection<String> addresses) {

                       }

                       @Override
                       public void presenceChanged(Presence presence) {
                           Log.e(TAG, "Presence Changed : "+presence.getType()+", "+presence.getStatus()+", "+presence.getFrom());
                       }
                   });
                    /**
                     * End Roser status
                     */


                    /*connection.addAsyncPacketListener(new PacketListener() {
                        @Override
                        public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
                            Log.e(TAG,  "Message Read Successfully  "+packet);
                        }
                    }, new PacketExtensionFilter(ReadReceipt.NAMESPACE));*/
                    /*ReadReceiptManager.getInstanceFor(connection).addReadReceivedListener(new ReceiptReceivedListener() {
                        @Override
                        public void onReceiptReceived(String fromJid, String toJid, String receiptId, Stanza receipt) {
                            Log.e(TAG,  "Message Read Successfully  "+fromJid+",   "+toJid);
                        }
                    });*/

                    /**
                     * This is for Read Mesg
                     */
                    /*ReadReceiptManager.getInstanceFor(connection).addReadReceivedListener(new ReceiptReceivedListener() {
                        @Override
                        public void onReceiptReceived(String fromJid, String toJid, String receiptId, Stanza receipt) {
                            Log.e("Read", "Message Read Successfully  "+fromJid+",   "+toJid);
                        }
                    });*/

                    connected = true;

                } catch (IOException e) {
                    if (isToasted)
                        new Handler(Looper.getMainLooper())
                                .post(new Runnable() {

                                    @Override
                                    public void run() {

                                        Toast.makeText(context, "(" + caller + ")" + "IOException: ", Toast.LENGTH_SHORT).show();
                                    }
                                });

                    Log.e("(" + caller + ")", "IOException: " + e.getMessage());
                } catch (SmackException e) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(context,
                                    "(" + caller + ")" + "SMACKException: ",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.e("(" + caller + ")",
                            "SMACKException: " + e.getMessage());
                } catch (XMPPException e) {
                    if (isToasted)

                        new Handler(Looper.getMainLooper())
                                .post(new Runnable() {

                                    @Override
                                    public void run() {

                                        Toast.makeText(
                                                context,
                                                "(" + caller + ")"
                                                        + "XMPPException: ",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                    Log.e("connect(" + caller + ")",
                            "XMPPException: " + e.getMessage());

                }
                return isconnecting = false;
            }
        };
        connectionThread.execute();
    }

    public void login() {
        try {
            connection.login(loginUser, passwordUser);
            Log.i("LOGIN", "Yey! We're connected to the Xmpp server!");
        } catch (XMPPException | SmackException | IOException e) {
            e.printStackTrace();
            reconnectServer();
        } catch (Exception e) {
            e.printStackTrace();
            reconnectServer();
        }
    }

    /**
     * If login fail then call this method again
     */
    void reconnectServer(){
        disconnect();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                connect("onCreate");
            }
        }, 1000);
    }

    public Roster getRoaster() {
        final Roster roster = Roster.getInstanceFor(connection);
        if (!roster.isLoaded())
            try {
                roster.reloadAndWait();
            } catch (SmackException.NotLoggedInException | SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        return roster;
    }

    private class ChatManagerListenerImpl implements ChatManagerListener {
        @Override
        public void chatCreated(final org.jivesoftware.smack.chat.Chat chat,
                                final boolean createdLocally) {
            if (!createdLocally)
                chat.addMessageListener(mMessageListener);

        }

    }


    public void sendMessage(ChatMessage chatMessage) {
        String body = gson.toJson(chatMessage);

        if (!chat_created) {
            Mychat = ChatManager.getInstanceFor(connection).createChat(
                    chatMessage.receiver + "@"
                            + context.getString(R.string.server),
                    mMessageListener);
            chat_created = true;
        }
        final Message message = new Message();
        message.setBody(body);
        message.setStanzaId(chatMessage.msgid);
        message.setType(Message.Type.chat);
        try {
            if (connection.isAuthenticated()) {
                String deliveryReceiptId = DeliveryReceiptRequest.addTo(message);//For getting Delivery Message
                Mychat.sendMessage(message);
            } else {
                login();
            }
        } catch (SmackException.NotConnectedException e) {
            Log.e("xmpp.SendMessage()", "msg Not sent!-Not Connected!");

        } catch (Exception e) {
            Log.e("Send msg Exception", "msg Not sent!" + e.getMessage());
        }
    }

    public class XMPPConnectionListener implements ConnectionListener {
        @Override
        public void connected(final XMPPConnection connection) {

            Log.d("xmpp", "Connected!");
            connected = true;
            if (!connection.isAuthenticated()) {
                login();
            }
        }

        @Override
        public void connectionClosed() {
            if (isToasted)
                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        Toast.makeText(context, "ConnectionCLosed!",
                                Toast.LENGTH_SHORT).show();

                    }
                });
            Log.d("xmpp", "ConnectionCLosed!");
            connected = false;
            chat_created = false;
            loggedin = false;
        }

        @Override
        public void connectionClosedOnError(Exception arg0) {
            if (isToasted)
                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(context, "ConnectionClosedOn Error!!",
                                Toast.LENGTH_SHORT).show();

                    }
                });
            Log.d("xmpp", "ConnectionClosedOn Error!");
            connected = false;
            chat_created = false;
            loggedin = false;
        }

        @Override
        public void reconnectingIn(int arg0) {
            Log.d("xmpp", "Reconnectingin " + arg0);
            loggedin = false;
        }

        @Override
        public void reconnectionFailed(Exception arg0) {
            if (isToasted)
                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {

                        Toast.makeText(context, "ReconnectionFailed!",
                                Toast.LENGTH_SHORT).show();

                    }
                });
            Log.d("xmpp", "ReconnectionFailed!");
            connected = false;

            chat_created = false;
            loggedin = false;
        }

        @Override
        public void reconnectionSuccessful() {
            if (isToasted)
                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                        Toast.makeText(context, "REConnected!",
                                Toast.LENGTH_SHORT).show();

                    }
                });
            Log.d("xmpp", "ReconnectionSuccessful");
            connected = true;

            chat_created = false;
            loggedin = false;
        }

        @Override
        public void authenticated(XMPPConnection arg0, boolean arg1) {
            Log.d("xmpp", "Authenticated!");
            loggedin = true;

            ChatManager.getInstanceFor(connection).addChatListener(mChatManagerListener);

            chat_created = false;
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }).start();
            if (isToasted)

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                        Toast.makeText(context, "Connected!",
                                Toast.LENGTH_SHORT).show();

                        /**
                         * Check offline message and send them
                         */
                        checkAndSendOfflineMessages();

                    }
                });
        }
    }

    /**
     * Check offline message and send them
     */
    public void checkAndSendOfflineMessages() {
        ChatConversationHandler chatConversationHandler = new ChatConversationHandler();
        ArrayList<ChatMessage> list = chatConversationHandler.getOfflineMessages();
        Log.e("Offline msg size", ""+list.size());
        for (int i = 0; i < list.size(); i++) {
            sendMessage(list.get(i));
            /**
             * Update msg status after send msg
             */
            chatConversationHandler.updateMsgStatus(list.get(i).msgid, "0");
        }
    }

    /**
     * MMessageListener for msg received from sender side
     */
    private class MMessageListener implements ChatMessageListener, ChatStateListener {

        public MMessageListener(Context contxt) {
        }

        @Override
        public void processMessage(final org.jivesoftware.smack.chat.Chat chat,
                                   final Message message) {
            //Log.e("Msg Received", "Xmpp message received: '" + message);

            if (message.getType() == Message.Type.chat
                    && message.getBody() != null) {
                final ChatMessage chatMessage = gson.fromJson(
                        message.getBody(), ChatMessage.class);

                processMessage(chatMessage);
            }
        }

        private void processMessage(final ChatMessage chatMessage) {
            chatMessage.isMine = false;
            chatMessage.isRead = false;

            /**
             * Uniq chat id swap from user to user its my logic
             */
            chatMessage.chat_id = chatMessage.chat_id.split("@")[1] + "@" + chatMessage.chat_id.split("@")[0];

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    ChatConversationHandler chatConversationHandler = new ChatConversationHandler();
                    long  isInsert = chatConversationHandler.addMsg(chatMessage);
                    if(isInsert != -1) {
                        Intent intent = new Intent(MESSAGE_RECEIVE_BROADCAST);
                        intent.putExtra("chat", chatMessage);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                        /**
                         * generate Notification
                         */
                        new NotificationUtils().generateNotification(context);
                    }
                }
            });
        }

        @Override
        public void stateChanged(Chat chat, ChatState state) {
            //Log.e("Chat State", "" + state);
            Intent intent = new Intent(MESSAGE_RECEIVE_BROADCAST);
            if (ChatState.composing.equals(state)) {
                intent.putExtra("chatState", "" + chat.getParticipant() + " is typing..");
            } else if (ChatState.gone.equals(state)) {
                intent.putExtra("chatState", "" + chat.getParticipant() + " has left the conversation.");
            } else if(ChatState.active.equals(state)){
                Log.e(TAG, "Read Msg: "+SharedPreferenceUtil.getString(ChatUtils.XMPP_USERNAME, "")+"    ,  "+chat.getParticipant());
            }else {
                intent.putExtra("chatState", "" + chat.getParticipant() + ": " + state.name());
            }
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
    }


    public void setCurrentState() {
        try {
            mMessageListener = new MMessageListener(context);
            Chat newChat = ChatManager.getInstanceFor(connection).createChat(SharedPreferenceUtil.getString(ChatUtils.XMPP_RECEIVER, "") + "@"
                    + context.getString(R.string.server), mMessageListener);
            ChatStateManager.getInstance(connection).setCurrentState(ChatState.composing, newChat);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }


    /**
     * Its for only android msg reading patch
     */
    public void setMsgReadState() {
        try {
            mMessageListener = new MMessageListener(context);
            Chat newChat = ChatManager.getInstanceFor(connection).createChat(SharedPreferenceUtil.getString(ChatUtils.XMPP_RECEIVER, "") + "@"
                    + context.getString(R.string.server), mMessageListener);
            ChatStateManager.getInstance(connection).setCurrentState(ChatState.active, newChat);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }



}