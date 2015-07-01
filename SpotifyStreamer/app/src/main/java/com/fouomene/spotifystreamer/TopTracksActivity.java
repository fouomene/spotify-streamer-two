/**
 *
 Author: Daniel Fouomene
 EmailAuthor: fouomenedaniel@gmail.com

 */

package com.fouomene.spotifystreamer;

import android.content.Intent;
import android.net.Uri;
import  android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import com.fouomene.spotifystreamer.fragment.PlayerFragment;
import com.fouomene.spotifystreamer.fragment.TopTracksFragment;

public class TopTracksActivity extends ActionBarActivity implements TopTracksFragment.Callback{

    private static final String PLAYERFRAGMENT_TAG = "PFTAG";
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.action_bar_title_top_tracks);

        setContentView(R.layout.activity_top_tracks);
        if (findViewById(R.id.fragment_player) != null) {
            // The player container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the player view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_player, new PlayerFragment(), PLAYERFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            //getSupportActionBar().setElevation(0f);
        }


    }

    @Override
    public void onItemSelected(Uri contentUri) {

        if (mTwoPane) {
            // In two-pane mode, show the player view in this activity by
            // adding or replacing the player fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(PlayerFragment.PLAYER_URI, contentUri);

            PlayerFragment fragment = new PlayerFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_player, fragment, PLAYERFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, PlayerActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }

    }
}
