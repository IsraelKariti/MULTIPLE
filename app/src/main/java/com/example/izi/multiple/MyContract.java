package com.example.izi.multiple;

import android.provider.BaseColumns;

/**
 * Created by izi on 8/6/2018.
 */

public class MyContract {


    public static final class INITIALS implements BaseColumns{
        public static final String TABLE_NAME_INITIALS = "Initials";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PARENT = "Parent";
        public static final String COLUMN_PHRASE = "Phrase";

        public static final String CREATE_TABLE_INITIALS = "CREATE TABLE " + TABLE_NAME_INITIALS + " ( " + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+ COLUMN_PARENT + " INTEGER , " +COLUMN_PHRASE + " text" +" ) ";
    }

    public static final class LOG implements BaseColumns{
        public static final String TABLE_NAME_LOG = "Log";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_TOTAL_DAY = "ToatalDay";
        public static final String COLUMN_PHRASE_ID = "PhraseID";
        public static final String COLUMN_DAY = "Day";
        public static final String COLUMN_MONTH = "Month";
        public static final String COLUMN_YEAR = "Year";

        public static final String CREATE_TABLE_LOG = "CREATE TABLE " + TABLE_NAME_LOG + " ( " +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "+
                COLUMN_TOTAL_DAY + " INTEGER , " +
                COLUMN_PHRASE_ID + " INTEGER , "+
                COLUMN_DAY + " INTEGER ," +
                COLUMN_MONTH + " INTEGER ," +
                COLUMN_YEAR + " INTEGER )";
    }
}
