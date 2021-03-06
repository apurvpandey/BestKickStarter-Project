package com.apurvpandey.payuchallenge.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Apurv Pandey on 13/8/17.
 * apurvpandey@rocektmail.com
 * Rewardz Pte Ltd.
 * Contact No. - +91-8377887369
 */

public class  KickStarterProjectContract {

    static final String CONTENT_AUTHORITY = "com.apurvpandey.payuchallenge";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    static final String BEST_KICK_PATH = "best_kick";

    public static class KickEntry implements BaseColumns {

        public static final String TABLE_NAME = "kick_projects";
        public static final String KICK_SL_NUMBER = "s_no";
        public static final String KICK_AMT_PLEDGED = "amt_pledged";
        public static final String KICK_BLURB = "blurb";
        public static final String KICK_BY = "by";
        public static final String KICK_COUNTRY = "country";
        public static final String KICK_CURRENCY = "currency";
        public static final String KICK_END_TIME = "end_time";
        public static final String KICK_LOCATION = "location";
        public  static final String KICK_PERCENTAGE_FUNDED = "percentage_funded";
        public static final String KICK_BACKERS = "backers";
        public  static final String KICK_STATE = "state";
        public  static final String KICK_TITLE = "title";
        public  static final String KICK_TYPE = "type";
        public  static final String KICK_URL = "url";


        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + BEST_KICK_PATH;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + BEST_KICK_PATH;


        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(BEST_KICK_PATH).build();

        public static String getDetailById(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static Uri buildProjectWithSerial(int sl_number) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(sl_number)).build();
        }
    }

}
