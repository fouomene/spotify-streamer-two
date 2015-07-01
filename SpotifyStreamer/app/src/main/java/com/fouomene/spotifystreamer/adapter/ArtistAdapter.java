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

import kaaes.spotify.webapi.android.models.Artist;


public class ArtistAdapter extends BaseAdapter {

    private final String LOG_TAG = ArtistAdapter.class.getSimpleName();

    private List<Artist> mArtists = Collections.emptyList();
    private final Context context;

    private static class ViewHolder {
        public final ImageView mThumbnailImage;
        public final TextView mArtistNameView;

        public ViewHolder(ImageView thumbnailImage,TextView artistNameView ) {
            this.mThumbnailImage = thumbnailImage;
            this.mArtistNameView = artistNameView;
        }
    }

    // the context is needed to inflate views in getView()
    public ArtistAdapter(Context context) {
        this.context = context;
    }

    public void updateArtists(List<Artist> artists) {
        this.mArtists = artists;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mArtists.size();
    }

    // getItem(int) in Adapter returns Object but we can override
    // it to Artist thanks to Java return type covariance
    @Override
    public Artist getItem(int position) {
        return mArtists.get(position);
    }

    // getItemId() is often useless, I think this should be the default
    // implementation in BaseAdapter
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView thumbnailImage ;
        TextView artistNameView ;
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.list_item_artist, parent, false);
            thumbnailImage = (ImageView)convertView.findViewById(R.id.list_item_thumbnail_imageview);
            artistNameView = (TextView) convertView.findViewById(R.id.list_item_name_artist_textview);
            convertView.setTag(new ViewHolder(thumbnailImage, artistNameView));
        } else {
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            thumbnailImage = viewHolder.mThumbnailImage;
            artistNameView = viewHolder.mArtistNameView;
        }

        Artist artist = getItem(position);

        artistNameView.setText(artist.name);

        if (artist.images.size() != 0){
            Picasso.with(context).load(artist.images.get(0).url).into(thumbnailImage);
        } else {
            Picasso.with(context).load(R.mipmap.ic_launcher).into(thumbnailImage);
        }

        return convertView;

    }

}

