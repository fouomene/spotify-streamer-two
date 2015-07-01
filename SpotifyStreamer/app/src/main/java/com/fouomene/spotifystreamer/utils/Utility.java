/**
 * Created by FOUOMENE EmailAuthor: fouomenedaniel@gmail.com .
 */
package com.fouomene.spotifystreamer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.fouomene.spotifystreamer.R;

public class Utility {
    private static final String LOG_TAG = Utility.class.getSimpleName();

    public static final String EXTRA_ARTIST_ID = "kaaes.spotify.webapi.android.models.artist.Id";
    public static final String EXTRA_ARTIST_NAME = "kaaes.spotify.webapi.android.models.artist.Name";

    public static String getPreferredArtist(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_artist_key),
                context.getString(R.string.pref_artist_default));
    }

    public static void setPreferredArtist(Context context, String artist) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getString(R.string.pref_artist_key), artist);
        editor.commit();
    }


    public static String getPreferredPosition(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_position_key),
                context.getString(R.string.pref_position_default));
    }

    public static void setPreferredPosition(Context context, String artist) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getString(R.string.pref_position_key), artist);
        editor.commit();
    }

    public static  String formatDuration (String duration){

        double d ;
         if (duration != null) {
             d = Double.parseDouble(duration);
             Log.e(LOG_TAG," duree double = "+d);
             d = d/60000;
         }
         else {
             d = 3.000;
             Log.e(LOG_TAG," duree double = "+d);
         }

        return String.format("%.2f",d);
    }

}