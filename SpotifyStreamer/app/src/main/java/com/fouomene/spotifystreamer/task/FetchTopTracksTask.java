/**
 *
 Author: Daniel Fouomene
 EmailAuthor: fouomenedaniel@gmail.com

 */
package com.fouomene.spotifystreamer.task;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.fouomene.spotifystreamer.R;
import com.fouomene.spotifystreamer.adapter.TopTracksAdapter;
import com.fouomene.spotifystreamer.data.SpotifyStreamerContract;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

public class FetchTopTracksTask extends AsyncTask<String, Void, Tracks> {

    private final String LOG_TAG = FetchTopTracksTask.class.getSimpleName();

    private final Context mContext;
    private TopTracksAdapter mTopTracksAdapter;
    private ListView mListView;
    private int mPosition;
    private String mArtistIdStr;
    private String mArtistNameStr;

    private final SpotifyService mSpotify;
    private  final Map<String, Object> mOptions;

    public FetchTopTracksTask(Context context, TopTracksAdapter topTracksAdapter, ListView listView, int position, String artistIdStr, String artistNameStr) {
        mContext = context;
        mTopTracksAdapter = topTracksAdapter;
        mListView = listView;
        mPosition = position;
        mArtistIdStr = artistIdStr;
        mArtistNameStr = artistNameStr;
        mSpotify = new SpotifyApi().getService();
        mOptions = new HashMap<String, Object>();
        mOptions.put(SpotifyService.OFFSET, 0);
        mOptions.put(SpotifyService.LIMIT, 10);
        //The country: an ISO 3166-1 alpha-2 USA = US.
        mOptions.put(SpotifyService.COUNTRY, "US");
    }

    @Override
    protected Tracks doInBackground(String... params) {

        try {

            /**
             * Get Spotify catalog information about an artist s top tracks by country.
             *
             * @param artistId The Spotify ID for the artist.
             * @param options  Optional parameters. For list of supported parameters see
             *                 <a href="https://developer.spotify.com/web-api/get-artists-top-tracks/">endpoint documentation</a>
             * @return An object whose key is "tracks" and whose value is an array of track objects.
             * @see <a href="https://developer.spotify.com/web-api/get-artists-top-tracks/">Get an Artist s Top Tracks</a>
             */

            return mSpotify.getArtistTopTrack(params[0],mOptions) ;

        } catch (Exception e) {
            Log.e(LOG_TAG,e.getMessage(),e);
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Tracks result) {
        if (result != null){

            mTopTracksAdapter.updateTracks(result.tracks);

            if (mPosition != ListView.INVALID_POSITION) {
                mListView.smoothScrollToPosition(mPosition);
                mListView.setSelection(mPosition);
            }

            // Insert the toptracks information into the database
            Vector<ContentValues> cVVector = new Vector<ContentValues>(result.tracks.size());

            for (Track item : result.tracks) {

                Log.d(LOG_TAG, "Track Name : " + item.name);

                ContentValues toptrackValues = new ContentValues();

                toptrackValues.put(SpotifyStreamerContract.TopTrackEntry.COLUMN_ARTIST_ID, mArtistIdStr);
                toptrackValues.put(SpotifyStreamerContract.TopTrackEntry.COLUMN_ARTIST_NAME, mArtistNameStr);
                toptrackValues.put(SpotifyStreamerContract.TopTrackEntry.COLUMN_ALBUM_NAME, item.album.name);
                if (item.album.images.size() != 0){
                    toptrackValues.put(SpotifyStreamerContract.TopTrackEntry.COLUMN_ALBUM_ARTWORK_URL, item.album.images.get(0).url);
                } else {
                    toptrackValues.put(SpotifyStreamerContract.TopTrackEntry.COLUMN_ALBUM_ARTWORK_URL,"ic_launcher");
                }
                toptrackValues.put(SpotifyStreamerContract.TopTrackEntry.COLUMN_TRACK_NAME, item.name);
                toptrackValues.put(SpotifyStreamerContract.TopTrackEntry.COLUMN_TRACK_URL, item.preview_url);
                toptrackValues.put(SpotifyStreamerContract.TopTrackEntry.COLUMN_TRACK_DURATION, item.duration_ms);

                cVVector.add(toptrackValues);
            }

            // add to database
            if ( cVVector.size() > 0 ) {

                //delete TopTrack all track
                mContext.getContentResolver().delete(SpotifyStreamerContract.TopTrackEntry.CONTENT_URI, null, null);

                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                mContext.getContentResolver().bulkInsert(SpotifyStreamerContract.TopTrackEntry.CONTENT_URI, cvArray);

                Log.d(LOG_TAG, "Top Tracks Complete. " + cVVector.size() + " Inserted");
            }


        } else {
                Toast.makeText(mContext, mContext.getString(R.string.check_network), Toast.LENGTH_SHORT).show();
            }
        //New data is back from the server. yessss!
    }
}