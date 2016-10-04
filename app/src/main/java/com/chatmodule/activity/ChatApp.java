package com.chatmodule.activity;

import android.app.Application;

import com.chatmodule.storage.DatabaseHelper;
import com.chatmodule.storage.DatabaseManager;
import com.chatmodule.storage.SharedPreferenceUtil;

/**
 * Created by mht on 24/9/16.
 */

public class ChatApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferenceUtil.init(getApplicationContext());

        DatabaseManager.initializeInstance(new DatabaseHelper(this));
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        DatabaseManager.getInstance().closeDatabase();
    }
}
