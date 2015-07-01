/**
 *
 Author: Daniel Fouomene
 EmailAuthor: fouomenedaniel@gmail.com
 *
 */

package com.fouomene.spotifystreamer.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fouomene.spotifystreamer.R;
import com.fouomene.spotifystreamer.adapter.TopTracksAdapter;
import com.fouomene.spotifystreamer.data.SpotifyStreamerContract;
import com.fouomene.spotifystreamer.task.FetchTopTracksTask;
import com.fouomene.spotifystreamer.utils.Utility;

import kaaes.spotify.webapi.android.models.Track;

/**
 * A placeholder fragment containing a simple view.
 */
public class TopTracksFragment extends Fragment {

    private final String LOG_TAG = TopTracksFragment.class.getSimpleName();

    private TopTracksAdapter mTopTracksAdapter;
    private String mArtistIdStr;
    private String mArtistNameStr;
    private ListView mListView;

    private int mPosition = ListView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * PlayerFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri contentUri);
    }

    public TopTracksFragment() {
        setHasOptionsMenu(true);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void updateTopTracks(){
        FetchTopTracksTask weatherTask = new FetchTopTracksTask(getActivity(), mTopTracksAdapter,mListView, mPosition,mArtistIdStr,mArtistNameStr);
        weatherTask.execute(mArtistIdStr);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //The TopTracks Activity called via intent. Inspect the intent for Tracks data.
        Intent intent = getActivity().getIntent();
        View rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);
        if ( intent != null && intent.getStringExtra(Utility.EXTRA_ARTIST_ID) != null && intent.getStringExtra(Utility.EXTRA_ARTIST_NAME) != null){
            mArtistIdStr = intent.getStringExtra(Utility.EXTRA_ARTIST_ID);
            mArtistNameStr = intent.getStringExtra(Utility.EXTRA_ARTIST_NAME);

            ((TextView) getActivity().findViewById(R.id.action_bar_artist)).setText(mArtistNameStr);

        }

        // The TopTracksAdapter will take data from a source and
        // use it to populate the ListView it's attached to.
        mTopTracksAdapter = new TopTracksAdapter(getActivity());

        // Get a reference to the ListView, and attach this adapter to it.
        mListView = (ListView) rootView.findViewById(R.id.listview_track);
        mListView.setAdapter(mTopTracksAdapter);
        // We'll call our MainActivity
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // TopTracksAdapter returns a track at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Track track = (Track) adapterView.getItemAtPosition(position);

                if (track != null) {

                    ///Toast.makeText(getActivity(), "You want to play track : " + track.name, Toast.LENGTH_SHORT).show();

                    String trackName = track.name;
                    Log.d(LOG_TAG, "URI = " + SpotifyStreamerContract.TopTrackEntry.buildTopTrackName(trackName).toString());
                    ((Callback) getActivity())
                            .onItemSelected(SpotifyStreamerContract.TopTrackEntry.buildTopTrackName(trackName));
                }


                mPosition = position;
            }
        });
        // If there's instance state, mine it for useful information.
        // The end-goal here is that the user never knows that turning their device sideways
        // does crazy lifecycle related things.  It should feel like some stuff stretched out,
        // or magically appeared to take advantage of room, but data or place in the app was never
        // actually *lost*.
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);

        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateTopTracks();
    }

}