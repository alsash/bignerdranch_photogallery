package com.bignerdranch.android.bignerdranch_photogallery.ui;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bignerdranch.android.bignerdranch_photogallery.R;
import com.bignerdranch.android.bignerdranch_photogallery.data.FlickrFetchr;
import com.bignerdranch.android.bignerdranch_photogallery.data.GalleryItem;
import com.bignerdranch.android.bignerdranch_photogallery.utils.ThumbnailDownloader;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.WeakHashMap;

public class PhotoGalleryFragment extends Fragment {

    private static final String TAG = "PhotoGalleryFragment";

    private List<GalleryItem> mItems = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;

    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItemTask().execute();

        Handler responseHandler = new Handler(); // on main thread
        mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
        mThumbnailDownloader.setThumbnailDownloadListener(
                new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>() {
                    @Override
                    public void onThumbnailDownload(PhotoHolder photoHolder, Bitmap bitmap) {
                        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                        photoHolder.bindDrawable(drawable);
                    }
                });
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        Log.i(TAG, "Background thread started");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        mRecyclerView = (RecyclerView) rootView
                .findViewById(R.id.fragment_photo_gallery_recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        setupAdapter();
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnailDownloader.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailDownloader.quit();
        Log.i(TAG, "Background thread destroyed");
    }

    private void setupAdapter() {
        if (isAdded()) {
            mRecyclerView.setAdapter(new PhotoAdapter(mItems));
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder {

        private ImageView mItemImageView;

        public PhotoHolder(View itemView) {
            super(itemView);
            mItemImageView = (ImageView)
                    itemView.findViewById(R.id.fragment_photo_gallery_image_view);
        }

        public void bindDrawable(Drawable drawable) {
            mItemImageView.setImageDrawable(drawable);
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

        private List<GalleryItem> mGalleryItems;
        private WeakHashMap<String, Boolean> mPreloadMap = new WeakHashMap<>();

        public PhotoAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View rootView = inflater.inflate(R.layout.gallery_item, parent, false);
            return new PhotoHolder(rootView);
        }

        @Override
        public void onBindViewHolder(PhotoHolder photoHolder, int position) {
            GalleryItem galleryItem = mGalleryItems.get(position);
            Drawable placeholder = ResourcesCompat.getDrawable(getResources(),
                    R.drawable.bill_up_close, null);
            photoHolder.bindDrawable(placeholder);
            mThumbnailDownloader.queueThumbnail(photoHolder, galleryItem.getUrl());
            // Preload 10 previous images
            preloadPrevious(position, 10);
            // Preload 10 next images
            preloadNext(position, 10);
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }

        private void preloadNext(int position, int max) {
            ListIterator<GalleryItem> iterator = mGalleryItems.listIterator(position);
            int i = 1;
            while (iterator.hasNext()) {
                String url = iterator.next().getUrl();
                if (mPreloadMap.get(url) == null) {
                    mPreloadMap.put(url, true);
                    mThumbnailDownloader.preloadThumbnail(url);
                }
                if (i++ >= 10) break;
            }
        }

        private void preloadPrevious(int position, int max) {
            ListIterator<GalleryItem> iterator = mGalleryItems.listIterator(position);
            int i = 1;
            while (iterator.hasPrevious()) {
                String url = iterator.previous().getUrl();
                if (mPreloadMap.get(url) == null) {
                    mPreloadMap.put(url, true);
                    mThumbnailDownloader.preloadThumbnail(url);
                }
                if (i++ >= max) break;
            }
        }
    }

    private class FetchItemTask extends AsyncTask<Void, Void, List<GalleryItem>> {
        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
            return new FlickrFetchr().fetchItems();
        }

        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            mItems = items;
            setupAdapter();
        }
    }

}
