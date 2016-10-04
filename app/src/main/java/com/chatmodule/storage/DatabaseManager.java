package com.chatmodule.storage;

import android.database.sqlite.SQLiteDatabase;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created  by Moveo Apps on 8/22/2015.
 */
public class DatabaseManager {

    public static DatabaseManager instance;
    public static DatabaseHelper mDatabaseHelper;
    private AtomicInteger mOpenCounter = new AtomicInteger();
    public static SQLiteDatabase mDatabase;


    public static synchronized void initializeInstance(DatabaseHelper helper) {
        if (instance == null) {
            instance = new DatabaseManager();
            mDatabaseHelper = helper;
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException(DatabaseManager.class.getSimpleName() +
                    " is not initialized, call initializeInstance(..) method first.");
        }
        return instance;
    }

    public synchronized SQLiteDatabase openDatabase() {

        if (mOpenCounter.incrementAndGet() == 1) {
            mDatabase = mDatabaseHelper.getWritableDatabase();
        }
        return mDatabase;
    }

    public synchronized void closeDatabase() {
        if (mOpenCounter.decrementAndGet() == 0) {
            mDatabase.close();

        }
    }

}
