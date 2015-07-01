/**
 *
 * Created by FOUOMENE
 * EmailAuthor:  fouomenedaniel@gmail.com .
 *
 **/
package com.fouomene.spotifystreamer.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class SpotifyStreamerProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private SpotifyStreamerDbHelper mOpenHelper;

    static final int TOPTRACK = 100;
    static final int TOPTRACK_WITH_NAME = 101;

    private final String LOG_TAG = SpotifyStreamerProvider.class.getSimpleName();

    private static final SQLiteQueryBuilder sTopTrackByNameQueryBuilder;

    static{
        sTopTrackByNameQueryBuilder = new SQLiteQueryBuilder();
        sTopTrackByNameQueryBuilder.setTables(
                SpotifyStreamerContract.TopTrackEntry.TABLE_NAME );
    }


    private static final String sTrackNameSelection =
            SpotifyStreamerContract.TopTrackEntry.TABLE_NAME +
                    "." + SpotifyStreamerContract.TopTrackEntry.COLUMN_TRACK_NAME+ " = ?  ";


    private Cursor getTopTrackByName(Uri uri, String[] projection, String sortOrder) {

         String trackName = SpotifyStreamerContract.TopTrackEntry.getTopTrackNameFromUri(uri);

         String[] selectionArgs;
         String selection;
         selection = sTrackNameSelection;
         selectionArgs = new String[]{trackName};
         Cursor toptrackCursor = sTopTrackByNameQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                 projection,
                 selection,
                 selectionArgs,
                 null,
                 null,
                 sortOrder
         );

        Log.e(LOG_TAG, "Number CursorTopTrack  = "+ toptrackCursor.getCount());

        return toptrackCursor;
    }

    /*
       Here is where we need to create the UriMatcher.
     */
    static UriMatcher buildUriMatcher() {

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = SpotifyStreamerContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, SpotifyStreamerContract.PATH_TOPTRACK, TOPTRACK);
        matcher.addURI(authority, SpotifyStreamerContract.PATH_TOPTRACK + "/*", TOPTRACK_WITH_NAME);
        return matcher;
    }

    /*
        We just create a new SpotifyStreamerDbHelper for later use
        here.
     */
    @Override
    public boolean onCreate() {
        mOpenHelper = new SpotifyStreamerDbHelper(getContext());
        return true;
    }


    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case TOPTRACK_WITH_NAME:
                return SpotifyStreamerContract.TopTrackEntry.CONTENT_ITEM_TYPE;
            case TOPTRACK:
                return SpotifyStreamerContract.TopTrackEntry.CONTENT_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "toptrack/*"
            case TOPTRACK_WITH_NAME:
            {
                retCursor = getTopTrackByName(uri, projection, sortOrder);
                break;
            }
            // "TopTrack"
            case TOPTRACK: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        SpotifyStreamerContract.TopTrackEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case TOPTRACK: {
                long _id = db.insert(SpotifyStreamerContract.TopTrackEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = SpotifyStreamerContract.TopTrackEntry.buildTopTrackUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case TOPTRACK:
                rowsDeleted = db.delete(
                        SpotifyStreamerContract.TopTrackEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case TOPTRACK:
                rowsUpdated = db.update(SpotifyStreamerContract.TopTrackEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TOPTRACK:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(SpotifyStreamerContract.TopTrackEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // we do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. we can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
