/**
 *
 Author: Daniel Fouomene
 EmailAuthor: fouomenedaniel@gmail.com

 */
package com.fouomene.spotifystreamer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fouomene.spotifystreamer.R;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import kaaes.spotify.webapi.android.models.Track;


public class TopTracksAdapter extends BaseAdapter  {

    private final String LOG_TAG = TopTracksAdapter.class.getSimpleName();

    private List<Track> mTracks = Collections.emptyList();
    private final Context context;



    private static class ViewHolder {
        public final ImageView mThumbnailAlbumImage;
        public final TextView mAlbumNameView;
        public final TextView mTrackNameView;

        public ViewHolder(ImageView thumbnailAlbumImage,TextView albumNameView,TextView trackNameView ) {
            this.mThumbnailAlbumImage = thumbnailAlbumImage;
            this.mAlbumNameView = albumNameView;
            this.mTrackNameView = trackNameView;
        }
    }

    // the context is needed to inflate views in getView()
    public TopTracksAdapter(Context context) {
        this.context = context;
    }

    public void updateTracks(List<Track> tracks) {
        this.mTracks = tracks;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mTracks.size();
    }

    // getItem(int) in Adapter returns Object but we can override
    // it to Track thanks to Java return type covariance
    @Override
    public Track getItem(int position) {
        return mTracks.get(position);
    }

    // getItemId() is often useless, I think this should be the default
    // implementation in BaseAdapter
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView thumbnailAlbumImage ;
        TextView albumNameView ;
        TextView trackNameView ;
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.list_item_track, parent, false);
            thumbnailAlbumImage = (ImageView)convertView.findViewById(R.id.list_item_thumbnail_album_imageview);
            albumNameView = (TextView) convertView.findViewById(R.id.list_item_album_textview);
            trackNameView = (TextView) convertView.findViewById(R.id.list_item_track_textview);
            convertView.setTag(new ViewHolder(thumbnailAlbumImage, albumNameView, trackNameView));
        } else {
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            thumbnailAlbumImage = viewHolder.mThumbnailAlbumImage;
            albumNameView = viewHolder.mAlbumNameView;
            trackNameView = viewHolder.mTrackNameView;
        }

        Track track = getItem(position);

        albumNameView.setText(track.album.name);
        trackNameView.setText(track.name);

        if (track.album.images.size() != 0){
            Picasso.with(context).load(track.album.images.get(0).url).into(thumbnailAlbumImage);
        } else {
            Picasso.with(context).load(R.mipmap.ic_launcher).into(thumbnailAlbumImage);
        }

        return convertView;

    }

}

