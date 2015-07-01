/**
 *
 Author: Daniel Fouomene
 EmailAuthor: fouomenedaniel@gmail.com

 */
package com.fouomene.spotifystreamer.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.fouomene.spotifystreamer.R;
import com.fouomene.spotifystreamer.adapter.ArtistAdapter;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;

public class FetchArtistTask extends AsyncTask<String, Void, ArtistsPager> {

    private final String LOG_TAG = FetchArtistTask.class.getSimpleName();

    private final Context mContext;
    private ArtistAdapter mArtistAdapter;
    private ListView mListView;
    private int mPosition;

    private final SpotifyService mSpotify;


    public FetchArtistTask(Context context, ArtistAdapter artistAdapter, ListView listView, int position) {
        mContext = context;
        mArtistAdapter = artistAdapter;
        mListView = listView;
        mPosition = position;
        mSpotify = new SpotifyApi().getService();
    }

    @Override
    protected ArtistsPager doInBackground(String... params) {

        try {

            /**
             * Get Spotify catalog information about artists that match a keyword string.
             *
             * @param q The search query's keywords (and optional field filters and operators), for example "roadhouse+blues"
             * @return A paginated list of results
             * @see <a href="https://developer.spotify.com/web-api/search-item/">Search for an Item</a>
             */

            return mSpotify.searchArtists(params[0]) ;

        } catch (Exception e) {
            Log.e(LOG_TAG,e.getMessage(),e);
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(ArtistsPager result) {
        if (result != null){
            mArtistAdapter.updateArtists(result.artists.items);
            if ( result.artists.total ==0) {
                Toast.makeText(mContext,mContext.getString(R.string.message_not_found_artist), Toast.LENGTH_SHORT).show();
            }else {

                if (mPosition != ListView.INVALID_POSITION) {
                    mListView.smoothScrollToPosition(mPosition);
                    mListView.setSelection(mPosition);
                }
            }

            //log
            for (Artist item : result.artists.items) {
                Log.d(LOG_TAG,"Artist Name : "+item.name );
            }
        }
        else {

            Toast.makeText(mContext,mContext.getString(R.string.check_network), Toast.LENGTH_SHORT).show();
        }
        //New data is back from the server. yessss!
    }
}