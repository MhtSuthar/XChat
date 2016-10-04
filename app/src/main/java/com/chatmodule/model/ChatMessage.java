package com.chatmodule.model;

import android.util.Log;

import com.chatmodule.R;

import java.io.Serializable;
import java.util.Random;

public class ChatMessage implements Serializable{
 
    public String body, sender, receiver, senderName;
    public String Date, MsgType;
    public String msgid, chat_id, msg_status;
    public boolean isMine, isRead;
    public int id;
    public long TimeMill;
    // Did I send the message.
 
    public ChatMessage(String Sender, String Receiver, String messageString,
            String msgId, boolean isMINE) {
        body = messageString;
        isMine = isMINE;
        sender = Sender;
        msgid = msgId;
        receiver = Receiver;
    }

    public ChatMessage(){}
 
    public void setMsgID() {
 
        msgid += "-" + String.format("%02d", new Random().nextInt(100));
    }

}