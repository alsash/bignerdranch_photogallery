package com.bignerdranch.android.bignerdranch_photogallery.data;

import com.google.gson.annotations.SerializedName;

public class FlickrResponse {
    @SerializedName("photos")
    private Photos mPhotos;

    public GalleryItem[] getGalleryItems() {
        return mPhotos.mPhotoArray;
    }

    public static class Photos {
        @SerializedName("photo")
        private GalleryItem[] mPhotoArray;
    }
}
