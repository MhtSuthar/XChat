package com.chatmodule.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static String DB_NAME = "ChatModule.db";
    private static int DB_VERSION = 1;

    private static String TAG = "TAG";

    private SQLiteDatabase mDataBase;
    private final Context mContext;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            ChatConversationHandler countryHandler = new ChatConversationHandler();
            countryHandler.createCountry(sqLiteDatabase);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}