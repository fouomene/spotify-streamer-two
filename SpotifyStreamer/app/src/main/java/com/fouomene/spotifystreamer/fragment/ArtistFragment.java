/**
 *
 Author: Daniel Fouomene
 EmailAuthor: fouomenedaniel@gmail.com

 */
package com.fouomene.spotifystreamer.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.fouomene.spotifystreamer.task.FetchArtistTask;
import com.fouomene.spotifystreamer.R;
import com.fouomene.spotifystreamer.TopTracksActivity;
import com.fouomene.spotifystreamer.adapter.ArtistAdapter;
import com.fouomene.spotifystreamer.utils.Utility;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistFragment extends Fragment {

    private final String LOG_TAG = ArtistFragment.class.getSimpleName();

    private ArtistAdapter mArtistAdapter;
    private ImageView mImageSearch;
    private EditText mEditTextArtist;
    private ListView mListView;

    private int mPosition = ListView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";

    public ArtistFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private  void updateSeach(){
        FetchArtistTask weatherTask = new FetchArtistTask(getActivity(), mArtistAdapter, mListView, mPosition);
        String artistName= mEditTextArtist.getText().toString();
        weatherTask.execute(artistName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // The ArtistAdapter will take data from a source and
        // use it to populate the ListView it's attached to.
        mArtistAdapter = new ArtistAdapter(getActivity());

        // Get a reference to the ListView, and attach this adapter to it.
        mListView = (ListView) rootView.findViewById(R.id.listview_artist);
        mListView.setAdapter(mArtistAdapter);
        // We'll call our MainActivity
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                view.setSelected(true);
                // ArtistAdapter returns a artist at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Artist artist = (Artist) adapterView.getItemAtPosition(position);
                if (artist != null) {
                    Intent intent = new Intent(getActivity(), TopTracksActivity.class);
                    intent.putExtra(Utility.EXTRA_ARTIST_ID, artist.id.toString());
                    intent.putExtra(Utility.EXTRA_ARTIST_NAME, artist.name.toString());
                    startActivity(intent);
                }
                mPosition = position;
                Utility.setPreferredPosition(getActivity(),Integer.toString(mPosition));
            }
        });

        // Get a reference to the EditText,
        mEditTextArtist = (EditText) rootView.findViewById(R.id.editTextArtist);

        // add a keylistener to keep track user input
        mEditTextArtist.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                // if keydown and "enter" is pressed
                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                     mPosition = 0;
                     updateSeach();
                     Utility.setPreferredArtist(getActivity(),mEditTextArtist.getText().toString());

                    return true;
                }
                return false;
            }
        });
        // Get a reference to the ImageView,
        mImageSearch = (ImageView) rootView.findViewById(R.id.imageSearch);
        mImageSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPosition = 0;
                updateSeach();
                Utility.setPreferredArtist(getActivity(), mEditTextArtist.getText().toString());
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
        mEditTextArtist.setText(Utility.getPreferredArtist(getActivity()));
        mPosition = Integer.parseInt(Utility.getPreferredPosition(getActivity()));
        updateSeach();
    }


}