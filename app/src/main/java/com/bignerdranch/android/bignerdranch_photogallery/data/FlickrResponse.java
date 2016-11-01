package com.bignerdranch.android.bignerdranch_photogallery.data;

import com.google.gson.annotations.SerializedName;

public class FlickrResponse {
    @SerializedName("photos")
    private Photos mPhotos;

    public int getPage() {
        return mPhotos.mPage;
    }

    public GalleryItem[] getGalleryItems() {
        return mPhotos.mPhotoArray;
    }

    public static class Photos {

        @SerializedName("page")
        private int mPage;

        @SerializedName("pages")
        private int mPages;

        @SerializedName("perpage")
        private int mPerPage;

        @SerializedName("total")
        private int mTotal;

        @SerializedName("photo")
        private GalleryItem[] mPhotoArray;
    }
}
