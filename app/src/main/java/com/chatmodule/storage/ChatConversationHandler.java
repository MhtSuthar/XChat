package com.chatmodule.storage;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.chatmodule.model.ChatMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created  by Android Developer on 1/11/2016.
 */
public class ChatConversationHandler {

    private static final String TAG = "ChatConversationHandler";

    private static final String TABLE_CHAT_CONVERSATION = "ChatConversationInfo";
    private static final String KEY_ID = "id";
    private static final String KEY_BODY = "body";
    private static final String KEY_SENDER = "sender";
    private static final String KEY_RECEIVER = "receiver";
    private static final String KEY_SENDER_NAME = "sender_name";
    private static final String KEY_DATE = "date";
    private static final String KEY_TIME = "time";
    private static final String KEY_MSG_ID = "msid";
    private static final String KEY_IS_MINE = "is_mine";
    private static final String KEY_IS_READ = "is_read";
    private static final String KEY_MSG_STATUS = "msg_status";
    private static final String KEY_CHAT_ID = "chat_id";
    private static final String KEY_MSG_TYPE = "msg_type";

    public String CREATE_TABLE = " CREATE TABLE " + TABLE_CHAT_CONVERSATION + " (" +
            KEY_ID + " INTEGER PRIMARY KEY, "
            + KEY_CHAT_ID + " TEXT NOT NULL, "
            + KEY_SENDER + " TEXT NOT NULL, "
            + KEY_SENDER_NAME + " TEXT NOT NULL, "
            + KEY_DATE + " TEXT NOT NULL, "
            + KEY_RECEIVER + " TEXT NOT NULL, "
            + KEY_MSG_ID + " TEXT NOT NULL, "
            + KEY_IS_MINE + " BOOLEAN NOT NULL, "
            + KEY_IS_READ + " BOOLEAN NOT NULL, "
            + KEY_MSG_TYPE + " TEXT NOT NULL, "
            + KEY_MSG_STATUS + " TEXT NOT NULL, "
            + KEY_TIME + " LONG NOT NULL, "
            + KEY_BODY + " TEXT NOT NULL); ";
    private SQLiteDatabase mDb;

    public void createCountry(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    public long addMsg(ChatMessage chatMessage) {
        Log.e(TAG, "msg id: "+hasMessageAlready(chatMessage.msgid));
        if(!hasMessageAlready(chatMessage.msgid)) {
            ContentValues initialValues = new ContentValues();
            initialValues.put(KEY_BODY, chatMessage.body);
            initialValues.put(KEY_CHAT_ID, chatMessage.chat_id);
            initialValues.put(KEY_SENDER, chatMessage.sender);
            initialValues.put(KEY_RECEIVER, chatMessage.receiver);
            initialValues.put(KEY_SENDER_NAME, chatMessage.senderName);
            initialValues.put(KEY_DATE, chatMessage.Date);
            initialValues.put(KEY_MSG_ID, chatMessage.msgid);
            initialValues.put(KEY_IS_MINE, chatMessage.isMine);
            initialValues.put(KEY_IS_READ, chatMessage.isRead);
            initialValues.put(KEY_TIME, chatMessage.TimeMill);
            initialValues.put(KEY_MSG_STATUS, chatMessage.msg_status);
            initialValues.put(KEY_MSG_TYPE, chatMessage.MsgType);
            mDb = DatabaseManager.getInstance().openDatabase();
            return mDb.insert(TABLE_CHAT_CONVERSATION, null, initialValues);
        }
        return -1;
    }

    public boolean hasMessageAlready(String msgId){
        mDb = DatabaseManager.getInstance().openDatabase();
        String Query = "select * from " + TABLE_CHAT_CONVERSATION +" where "+KEY_MSG_ID+" = '"+msgId+"'";
        Cursor cursor = mDb.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public boolean updateChat(ChatMessage chatMessage) {
        Log.e("", "_id " + chatMessage.body);

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_BODY, chatMessage.body);

        mDb = DatabaseManager.getInstance().openDatabase();
        return mDb.update(TABLE_CHAT_CONVERSATION, initialValues, "id=" + chatMessage.body, null) > 0;
    }

    public boolean deleteCountry(String _id) {
        mDb = DatabaseManager.getInstance().openDatabase();
        return mDb.delete(TABLE_CHAT_CONVERSATION, "id=" + _id, null) > 0;
    }

    public ArrayList<ChatMessage> getAllMessage() {

        ArrayList<ChatMessage> arrayList = new ArrayList<>();

        try {
            mDb = DatabaseManager.getInstance().openDatabase();
            Cursor mCursor = mDb.query(TABLE_CHAT_CONVERSATION, null, null, null, null, null, KEY_ID + " ASC");

            mCursor.moveToNext();
            for (int i = 0; i < mCursor.getCount(); i++) {
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.id = mCursor.getInt(Integer.valueOf(mCursor.getColumnIndex(KEY_ID)));
                chatMessage.chat_id = mCursor.getString(mCursor.getColumnIndex(KEY_CHAT_ID));
                chatMessage.body =  mCursor.getString(mCursor.getColumnIndex(KEY_BODY));
                chatMessage.Date = mCursor.getString(mCursor.getColumnIndex(KEY_DATE));
                chatMessage.sender = mCursor.getString(mCursor.getColumnIndex(KEY_SENDER));
                chatMessage.receiver = mCursor.getString(mCursor.getColumnIndex(KEY_RECEIVER));
                chatMessage.senderName = mCursor.getString(mCursor.getColumnIndex(KEY_SENDER_NAME));
                chatMessage.msgid = mCursor.getString(mCursor.getColumnIndex(KEY_MSG_ID));
                chatMessage.msg_status = mCursor.getString(mCursor.getColumnIndex(KEY_MSG_STATUS));
                chatMessage.TimeMill = mCursor.getLong(mCursor.getColumnIndex(KEY_TIME));
                chatMessage.isMine = mCursor.getInt(mCursor.getColumnIndex(KEY_IS_MINE)) > 0;
                chatMessage.isRead = mCursor.getInt(mCursor.getColumnIndex(KEY_IS_READ)) > 0;
                chatMessage.MsgType = mCursor.getString(mCursor.getColumnIndex(KEY_MSG_TYPE));
                arrayList.add(chatMessage);
                mCursor.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return arrayList;
    }

    public ArrayList<ChatMessage> getChatMessages(String chatId) {

        ArrayList<ChatMessage> arrayList = new ArrayList<>();

        try {
            mDb = DatabaseManager.getInstance().openDatabase();
            Cursor mCursor = mDb.query(TABLE_CHAT_CONVERSATION, null, KEY_CHAT_ID +"= ?", new String[]{ chatId }, null, null, null);

            mCursor.moveToNext();
            for (int i = 0; i < mCursor.getCount(); i++) {
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.id = mCursor.getInt(Integer.valueOf(mCursor.getColumnIndex(KEY_ID)));
                chatMessage.chat_id = mCursor.getString(mCursor.getColumnIndex(KEY_CHAT_ID));
                chatMessage.body =  mCursor.getString(mCursor.getColumnIndex(KEY_BODY));
                chatMessage.Date = mCursor.getString(mCursor.getColumnIndex(KEY_DATE));
                chatMessage.sender = mCursor.getString(mCursor.getColumnIndex(KEY_SENDER));
                chatMessage.receiver = mCursor.getString(mCursor.getColumnIndex(KEY_RECEIVER));
                chatMessage.senderName = mCursor.getString(mCursor.getColumnIndex(KEY_SENDER_NAME));
                chatMessage.msgid = mCursor.getString(mCursor.getColumnIndex(KEY_MSG_ID));
                chatMessage.msg_status = mCursor.getString(mCursor.getColumnIndex(KEY_MSG_STATUS));
                chatMessage.TimeMill = mCursor.getLong(mCursor.getColumnIndex(KEY_TIME));
                chatMessage.isMine = mCursor.getInt(mCursor.getColumnIndex(KEY_IS_MINE)) > 0;
                chatMessage.isRead = mCursor.getInt(mCursor.getColumnIndex(KEY_IS_READ)) > 0;
                chatMessage.MsgType = mCursor.getString(mCursor.getColumnIndex(KEY_MSG_TYPE));
                arrayList.add(chatMessage);
                mCursor.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return arrayList;
    }


    public ArrayList<ChatMessage> getOfflineMessages() {

        ArrayList<ChatMessage> arrayList = new ArrayList<>();

        try {
            mDb = DatabaseManager.getInstance().openDatabase();
            Cursor mCursor = mDb.query(TABLE_CHAT_CONVERSATION, null, KEY_MSG_STATUS +"= ?", new String[]{ "-1" }, null, null, null);

            mCursor.moveToNext();
            for (int i = 0; i < mCursor.getCount(); i++) {
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.id = mCursor.getInt(Integer.valueOf(mCursor.getColumnIndex(KEY_ID)));
                chatMessage.chat_id = mCursor.getString(mCursor.getColumnIndex(KEY_CHAT_ID));
                chatMessage.body =  mCursor.getString(mCursor.getColumnIndex(KEY_BODY));
                chatMessage.Date = mCursor.getString(mCursor.getColumnIndex(KEY_DATE));
                chatMessage.sender = mCursor.getString(mCursor.getColumnIndex(KEY_SENDER));
                chatMessage.receiver = mCursor.getString(mCursor.getColumnIndex(KEY_RECEIVER));
                chatMessage.senderName = mCursor.getString(mCursor.getColumnIndex(KEY_SENDER_NAME));
                chatMessage.msgid = mCursor.getString(mCursor.getColumnIndex(KEY_MSG_ID));
                chatMessage.msg_status = mCursor.getString(mCursor.getColumnIndex(KEY_MSG_STATUS));
                chatMessage.TimeMill = mCursor.getLong(mCursor.getColumnIndex(KEY_TIME));
                chatMessage.isMine = mCursor.getInt(mCursor.getColumnIndex(KEY_IS_MINE)) > 0;
                chatMessage.isRead = mCursor.getInt(mCursor.getColumnIndex(KEY_IS_READ)) > 0;
                chatMessage.MsgType = mCursor.getString(mCursor.getColumnIndex(KEY_MSG_TYPE));
                arrayList.add(chatMessage);
                mCursor.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return arrayList;
    }


    public ArrayList<ChatMessage> getUnreadMessages() {

        ArrayList<ChatMessage> arrayList = new ArrayList<>();

        try {
            mDb = DatabaseManager.getInstance().openDatabase();
            Cursor mCursor = mDb.query(TABLE_CHAT_CONVERSATION, null, KEY_IS_READ +"= ?", new String[]{ "0" }, null, null, KEY_ID+" DESC");

            mCursor.moveToNext();
            for (int i = 0; i < mCursor.getCount(); i++) {
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.id = mCursor.getInt(Integer.valueOf(mCursor.getColumnIndex(KEY_ID)));
                chatMessage.chat_id = mCursor.getString(mCursor.getColumnIndex(KEY_CHAT_ID));
                chatMessage.body =  mCursor.getString(mCursor.getColumnIndex(KEY_BODY));
                chatMessage.Date = mCursor.getString(mCursor.getColumnIndex(KEY_DATE));
                chatMessage.sender = mCursor.getString(mCursor.getColumnIndex(KEY_SENDER));
                chatMessage.receiver = mCursor.getString(mCursor.getColumnIndex(KEY_RECEIVER));
                chatMessage.senderName = mCursor.getString(mCursor.getColumnIndex(KEY_SENDER_NAME));
                chatMessage.msgid = mCursor.getString(mCursor.getColumnIndex(KEY_MSG_ID));
                chatMessage.msg_status = mCursor.getString(mCursor.getColumnIndex(KEY_MSG_STATUS));
                chatMessage.TimeMill = mCursor.getLong(mCursor.getColumnIndex(KEY_TIME));
                chatMessage.isMine = mCursor.getInt(mCursor.getColumnIndex(KEY_IS_MINE)) > 0;
                chatMessage.isRead = mCursor.getInt(mCursor.getColumnIndex(KEY_IS_READ)) > 0;
                chatMessage.MsgType = mCursor.getString(mCursor.getColumnIndex(KEY_MSG_TYPE));
                arrayList.add(chatMessage);
                mCursor.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return arrayList;
    }

    private void sortAlphabeticAscendingList(List<ChatMessage> groupContactModelList) {
        Collections.sort(groupContactModelList, new Comparator<ChatMessage>() {
            @Override
            public int compare(ChatMessage s1, ChatMessage s2)
            {
                return s1.senderName.compareToIgnoreCase(s2.senderName);
            }
        });
    }

    public void updateChatMsgAsRead(String chatId) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_IS_READ, true);

        mDb = DatabaseManager.getInstance().openDatabase();
        mDb.update(TABLE_CHAT_CONVERSATION, initialValues, KEY_CHAT_ID +" = '" + chatId+"'", null);
    }

    public int getUnreadMsgCount(String chatId) {
        int count = 0;
        mDb = DatabaseManager.getInstance().openDatabase();
        Cursor cursor = mDb.query(TABLE_CHAT_CONVERSATION,
                null,
                KEY_CHAT_ID + "= ?" + " AND " + KEY_IS_READ + "=?",
                new String[]{chatId, "0"},
                null, null, null);
        count = cursor.getCount();
        cursor.close();
        return count;
    }

    public void updateMsgStatusDelivered(String msgId) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_MSG_STATUS, "1");

        mDb = DatabaseManager.getInstance().openDatabase();
        mDb.update(TABLE_CHAT_CONVERSATION, initialValues, KEY_MSG_ID +" = '" + msgId+"'", null);
    }

    public void updateMsgStatus(String msgId, String status) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_MSG_STATUS, status);

        mDb = DatabaseManager.getInstance().openDatabase();
        mDb.update(TABLE_CHAT_CONVERSATION, initialValues, KEY_MSG_ID +" = '" + msgId+"'", null);
    }

}
