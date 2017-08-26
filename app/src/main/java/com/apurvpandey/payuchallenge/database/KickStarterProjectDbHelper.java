package com.apurvpandey.payuchallenge.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Apurv Pandey on 13/8/17.
 * apurvpandey@rocektmail.com
 * Rewardz Pte Ltd.
 * Contact No. - +91-8377887369
 */

public class KickStarterProjectDbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "kick.db";
    private static final int DB_VERSION = 3;


    public KickStarterProjectDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_KICK = "CREATE TABLE " + KickStarterProjectContract.KickEntry.TABLE_NAME
                + "(" + KickStarterProjectContract.KickEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + KickStarterProjectContract.KickEntry.KICK_SL_NUMBER + " INTEGER NOT NULL ,"
                + KickStarterProjectContract.KickEntry.KICK_AMT_PLEDGED + " INTEGER NOT NULL,"
                + KickStarterProjectContract.KickEntry.KICK_BLURB + " VARCHAR(255) ,"
                + KickStarterProjectContract.KickEntry.KICK_BY + " VARCHAR(255) ,"
                + KickStarterProjectContract.KickEntry.KICK_COUNTRY + " VARCHAR (255) ,"
                + KickStarterProjectContract.KickEntry.KICK_CURRENCY + " VARCHAR (255) ,"
                + KickStarterProjectContract.KickEntry.KICK_END_TIME + " VARCHAR (255) ,"
                + KickStarterProjectContract.KickEntry.KICK_TITLE + " VARCHAR (255) ,"
                + KickStarterProjectContract.KickEntry.KICK_LOCATION + " VARCHAR (255) ,"
                + KickStarterProjectContract.KickEntry.KICK_PERCENTAGE_FUNDED + " INTEGER ,"
                + KickStarterProjectContract.KickEntry.KICK_BACKERS + " INTEGER ,"
                + KickStarterProjectContract.KickEntry.KICK_STATE + " VARCHAR(255) ,"
                + KickStarterProjectContract.KickEntry.KICK_URL + " VARCHAR(255) ,"
                + KickStarterProjectContract.KickEntry.KICK_TYPE + " VARCHAR (255));";

        sqLiteDatabase.execSQL(SQL_CREATE_KICK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int old_version, int new_version) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + KickStarterProjectContract.KickEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }

}
