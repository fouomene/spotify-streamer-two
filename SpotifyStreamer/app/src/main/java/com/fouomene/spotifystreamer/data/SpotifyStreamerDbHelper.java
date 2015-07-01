/**
 *
 * Created by FOUOMENE
 * EmailAuthor: fouomenedaniel@gmail.com .
 *
 **/
package com.fouomene.spotifystreamer.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fouomene.spotifystreamer.data.SpotifyStreamerContract.TopTrackEntry;


/**
 * Manages a local database for toptrack data.
 */
public class SpotifyStreamerDbHelper extends SQLiteOpenHelper {


    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 4;

    public static final String DATABASE_NAME = "spotifystreamer.db";

    public SpotifyStreamerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Create a table to hold toptrack.

        final String SQL_CREATE_TOPTRACK_TABLE = "CREATE TABLE " + TopTrackEntry.TABLE_NAME + " (" +

                TopTrackEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                TopTrackEntry.COLUMN_ARTIST_ID + " TEXT NOT NULL, " +
                TopTrackEntry.COLUMN_ARTIST_NAME + " TEXT NOT NULL, " +
                TopTrackEntry.COLUMN_ALBUM_NAME + " TEXT NOT NULL, " +
                TopTrackEntry.COLUMN_ALBUM_ARTWORK_URL + " TEXT NOT NULL, " +
                TopTrackEntry.COLUMN_TRACK_NAME + " TEXT NOT NULL, " +
                TopTrackEntry.COLUMN_TRACK_URL + " TEXT NOT NULL, " +
                TopTrackEntry.COLUMN_TRACK_DURATION + " TEXT NOT NULL " + // in ms
                " );";


        sqLiteDatabase.execSQL(SQL_CREATE_TOPTRACK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TopTrackEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
