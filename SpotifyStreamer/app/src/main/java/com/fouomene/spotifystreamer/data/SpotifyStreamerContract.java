/**
 *
 * Created by FOUOMENE
 * EmailAuthor: fouomenedaniel@gmail.com .
 *
 **/
package com.fouomene.spotifystreamer.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by FOUOMENE on 15/03/2015.
 */
public class SpotifyStreamerContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.fouomene.spotifystreamer";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.fouomene.spotifystreamer.app/toptrack/ is a valid path for
    // looking at weather data. content://com.fouomene.spotifystreamer.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    public static final String PATH_TOPTRACK = "toptrack";


    /* Inner class that defines the table contents of the toptrack table */
    public static final class TopTrackEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TOPTRACK).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOPTRACK;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOPTRACK;

        // Table name
        public static final String TABLE_NAME = "toptrack";

        // Artist id Str
        public static final String COLUMN_ARTIST_ID = "artist_id";

        // Artist id name
        public static final String COLUMN_ARTIST_NAME = "artist_name";

        //Album name
        public static final String COLUMN_ALBUM_NAME = "album_name";

        //Album artwork url
        public static final String COLUMN_ALBUM_ARTWORK_URL = "album_artwork_url";

        //Track name
        public static final String COLUMN_TRACK_NAME = "track_name";

        //Track url
        public static final String COLUMN_TRACK_URL = "track_url";

        //Track duration
        public static final String COLUMN_TRACK_DURATION = "track_duration";

        public static Uri buildTopTrackUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildTopTrackName(String name) {
            return CONTENT_URI.buildUpon().appendPath(name).build();
        }

        public static String getTopTrackNameFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }


    }
}
