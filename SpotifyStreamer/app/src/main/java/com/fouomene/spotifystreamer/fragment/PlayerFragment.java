package com.fouomene.spotifystreamer.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fouomene.spotifystreamer.R;
import com.fouomene.spotifystreamer.data.SpotifyStreamerContract.*;
import com.fouomene.spotifystreamer.utils.Utility;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = PlayerFragment.class.getSimpleName();
    public static final String PLAYER_URI = "URI";

    private static final String TOPTRACK_SHARE_HASHTAG = " #SpotifyStreamerApp";
    private ShareActionProvider mShareActionProvider;
    private String mSpotifyStreamer;

    private Uri mUri;



    private static final int TRACK_LOADER = 0;

    private static final String[] TRACK_COLUMNS = {
            TopTrackEntry.TABLE_NAME + "." + TopTrackEntry._ID,
            TopTrackEntry.COLUMN_ARTIST_ID,
            TopTrackEntry.COLUMN_ARTIST_NAME,
            TopTrackEntry.COLUMN_ALBUM_NAME,
            TopTrackEntry.COLUMN_ALBUM_ARTWORK_URL,
            TopTrackEntry.COLUMN_TRACK_NAME,
            TopTrackEntry.COLUMN_TRACK_URL,
            TopTrackEntry.COLUMN_TRACK_DURATION
    };

    // These indices are tied to TRACK_COLUMNS.  If TRACKS_COLUMNS changes, these
    // must change.
    public static final int COLUMN_TRACK_ID = 0;
    public static final int COLUMN_ARTIST_ID = 1;
    public static final int COLUMN_ARTIST_NAME = 2;
    public static final int COLUMN_ALBUM_NAME = 3;
    public static final int COLUMN_ALBUM_ARTWORK_URL = 4;
    public static final int COLUMN_TRACK_NAME = 5;
    public static final int COLUMN_TRACK_URL = 6;
    public static final int COLUMN_TRACK_DURATION = 7;

    private TextView artist_name_textview;
    private TextView album_name_textview;
    private ImageView album_artwork_imageview;
    private TextView track_name_textview;
    private SeekBar seekBar;
    private TextView current_duration_textview;
    private TextView max_duration_textview;
    private ImageView previous_imageview;
    private ImageView play_pause_imageview;
    private ImageView next_imageview;
    private long idTrack;
    private String trackUrl;
    private boolean isPlay = false;
    private boolean isPrepare = false;


    private MediaPlayer mediaPlayer;
    private double timeElapsed , finalTime;
    private Handler durationHandler;


    public PlayerFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(PlayerFragment.PLAYER_URI);
        }

        // If there's instance state, mine it for useful information.
        // The end-goal here is that the user never knows that turning their device sideways
        // does crazy lifecycle related things.  It should feel like some stuff stretched out,
        // or magically appeared to take advantage of room, but data or place in the app was never
        // actually *lost*.
        if (savedInstanceState != null && savedInstanceState.containsKey(PlayerFragment.PLAYER_URI)) {
            // probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.

            mUri = savedInstanceState.getParcelable(PlayerFragment.PLAYER_URI);

            Log.d(LOG_TAG, "Uri Save =" + mUri.toString());

        }

        View rootView = inflater.inflate(R.layout.fragment_player, container, false);
        artist_name_textview = (TextView) rootView.findViewById(R.id.artist_name_textview);
        album_name_textview = (TextView) rootView.findViewById(R.id.album_name_textview);
        track_name_textview = (TextView) rootView.findViewById(R.id.track_name_textview);
        current_duration_textview = (TextView) rootView.findViewById(R.id.current_duration_textview);
        max_duration_textview = (TextView) rootView.findViewById(R.id.max_duration_textview);
        album_artwork_imageview = (ImageView) rootView.findViewById(R.id.album_artwork_imageview);
        seekBar = (SeekBar) rootView.findViewById(R.id.seekBar);
        seekBar.setClickable(false);
        previous_imageview = (ImageView) rootView.findViewById(R.id.previous_imageview);
        previous_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previous();
            }
        });
        play_pause_imageview = (ImageView) rootView.findViewById(R.id.play_pause_imageview);
        play_pause_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlay){
                    pause();
                }else {
                    play();
                }
            }
        });
        next_imageview = (ImageView) rootView.findViewById(R.id.next_imageview);
        next_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });

        return rootView;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_fragment_player, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (mSpotifyStreamer != null) {
            mShareActionProvider.setShareIntent(createShareSpotifyStreamerIntent());
        }
    }

    private Intent createShareSpotifyStreamerIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mSpotifyStreamer + TOPTRACK_SHARE_HASHTAG);
        return shareIntent;
    }

    // previous toptrack
    public void previous() {

        if (isPlay) {
            play_pause_imageview.setImageResource(R.mipmap.play);
            mediaPlayer.stop();
        }
        // check if the previous toptrack exists in the db
        Cursor toptrackCursor = getActivity().getContentResolver().query(
                TopTrackEntry.CONTENT_URI,
                new String[]{TopTrackEntry.COLUMN_TRACK_NAME},
                TopTrackEntry._ID + " = ? ",
                new String[]{(idTrack-1)+""},
                null);
        String trackName;
        if (toptrackCursor.moveToFirst()) {

            isPrepare = false;

            int indexTrackName = toptrackCursor.getColumnIndex(TopTrackEntry.COLUMN_TRACK_NAME);
            trackName = toptrackCursor.getString(indexTrackName);
            Log.d(LOG_TAG, "Track previous exit with name=" + trackName);

            mUri = TopTrackEntry.buildTopTrackName(trackName);
            getLoaderManager().restartLoader(TRACK_LOADER, null, this);
        }
    }

    // next toptrack
    public void next() {
        if (isPlay) {
            play_pause_imageview.setImageResource(R.mipmap.play);
            mediaPlayer.stop();
        }
        // check if the next toptrack exists in the db
        Cursor toptrackCursor = getActivity().getContentResolver().query(
                TopTrackEntry.CONTENT_URI,
                new String[]{TopTrackEntry.COLUMN_TRACK_NAME},
                TopTrackEntry._ID + " = ? ",
                new String[]{(idTrack+1)+""},
                null);
        String trackName;
        if (toptrackCursor.moveToFirst()) {

            isPrepare = false;

            int indexTrackName = toptrackCursor.getColumnIndex(TopTrackEntry.COLUMN_TRACK_NAME);
            trackName = toptrackCursor.getString(indexTrackName);
            Log.d(LOG_TAG, "Track next exit with name=" + trackName);

            mUri = TopTrackEntry.buildTopTrackName(trackName);
            getLoaderManager().restartLoader(TRACK_LOADER, null, this);
        }
    }

    // play toptrack
    public void play() {
        Toast.makeText(getActivity(), "preview !", Toast.LENGTH_SHORT).show();
        isPlay=true;
        play_pause_imageview.setImageResource(R.mipmap.pause);
        //prepare
        if (!isPrepare) prepare();
        //start
        mediaPlayer.start();
        timeElapsed = mediaPlayer.getCurrentPosition();
        seekBar.setProgress((int) timeElapsed);
        durationHandler.postDelayed(updateSeekBarTime, 100);
    }

    // pause toptrack
    public void pause() {
           isPlay = false;
           play_pause_imageview.setImageResource(R.mipmap.play);
           mediaPlayer.pause();
    }

    // prepare toptrack
    public void prepare() {
        isPrepare = true;
        try {
            //prepare
            mediaPlayer.setDataSource(trackUrl);
            mediaPlayer.prepare(); // might take long! (for buffering, etc)
            finalTime = mediaPlayer.getDuration();
            seekBar.setMax((int) finalTime);

        } catch (IOException e) {
            Log.v(LOG_TAG, e.getMessage());
        }
    }

    public void initialize (){
        timeElapsed = 0; finalTime = 0;
        durationHandler = new Handler();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setWakeMode(getActivity(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isPlay = false;
                play_pause_imageview.setImageResource(R.mipmap.play);
            }
        });

        updateSeekBarTime = new Runnable() {
            public void run() {
                //get current position
                timeElapsed = mediaPlayer.getCurrentPosition();
                //set seekbar progress
                seekBar.setProgress((int) timeElapsed);
                //set time remaing
                double timeRemaining = finalTime - timeElapsed;
                if (finalTime == 0) {
                    current_duration_textview.setText("0.00");
                    seekBar.setProgress((int) 0);
                } else {
                    current_duration_textview.setText(String.format("%d.%d", TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining), TimeUnit.MILLISECONDS.toSeconds((long) timeRemaining) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining))));
                }
                //repeat yourself that again in 100 miliseconds
                durationHandler.postDelayed(this, 100);
            }
        };
    }
    //handler to change seekBarTime
    private Runnable updateSeekBarTime = new Runnable() {
        public void run() {
            //get current position
            timeElapsed = mediaPlayer.getCurrentPosition();
            //set seekbar progress
            seekBar.setProgress((int) timeElapsed);
            //set time remaing
            double timeRemaining = finalTime - timeElapsed;
            if (finalTime == 0) {
                current_duration_textview.setText("0.00");
            } else {

                current_duration_textview.setText(String.format("%d.%d", TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining), TimeUnit.MILLISECONDS.toSeconds((long) timeRemaining) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining))));
            }
            //repeat yourself that again in 100 miliseconds
            durationHandler.postDelayed(this, 100);
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        getLoaderManager().initLoader(TRACK_LOADER, null, this);

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        outState.putParcelable(PLAYER_URI, mUri);
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mUri ) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    TRACK_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {

            initialize ();

            idTrack = data.getLong(COLUMN_TRACK_ID);
            Log.e(LOG_TAG, " IdTrack Duration = " + data.getLong(COLUMN_TRACK_ID));
            artist_name_textview.setText(data.getString(COLUMN_ARTIST_NAME));
            album_name_textview.setText(data.getString(COLUMN_ALBUM_NAME));
            track_name_textview.setText(data.getString(COLUMN_TRACK_NAME));
            current_duration_textview.setText("0.00");

            Log.e(LOG_TAG, " Track Duration = " + data.getString(COLUMN_TRACK_DURATION));

            max_duration_textview.setText(Utility.formatDuration(data.getString(COLUMN_TRACK_DURATION)));
            current_duration_textview.setText("0.00");

            if (data.getString(COLUMN_ALBUM_ARTWORK_URL) != "ic_launcher"){
                Picasso.with(getActivity()).load(data.getString(COLUMN_ALBUM_ARTWORK_URL)).into(album_artwork_imageview);
            } else {
                Picasso.with(getActivity()).load(R.mipmap.ic_launcher).into(album_artwork_imageview);
            }

            trackUrl = data.getString(COLUMN_TRACK_URL);

            Log.e(LOG_TAG, " Track URL = " + trackUrl);

            // We still need this for the share intent
            mSpotifyStreamer = data.getString(COLUMN_ARTIST_NAME)+":"+data.getString(COLUMN_TRACK_NAME)+"|"+trackUrl;

            // If onCreateOptionsMenu has already happened, we need to update the share intent now.
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareSpotifyStreamerIntent());
            }

        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }

}
