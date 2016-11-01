package com.bignerdranch.android.bignerdranch_photogallery.data;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FlickrFetchr {

    private static final String TAG = "FlickrFetchr";
    private static final String API_KEY = "e83bc" + "dd" + "331205310357" + "b83462ed347a6";
    private static int sPage = 1;
    private static int sPageLoaded;

    public static int getLoadedPage() {
        return sPageLoaded;
    }

    public byte[] getUrlBytes(String urlSpec) throws IOException {

        URL url = new URL(urlSpec);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public List<GalleryItem> fetchItems() {

        List<GalleryItem> items = new ArrayList<>();

        try {
            String url = Uri.parse("https://api.flickr.com/services/rest/")
                    .buildUpon()
                    .appendQueryParameter("method", "flickr.photos.getRecent")
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("extras", "url_s")
                    .appendQueryParameter("page", String.valueOf(sPage++))
                    .build().toString();
            String jsonString = getUrlString(url);
            parseItems(items, jsonString);
            Log.i(TAG, "Received JSON: " + jsonString);
        } catch (JsonSyntaxException je) {
            Log.e(TAG, "Failed to parse JSON ", je);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items ", ioe);
        }
        return items;
    }

    private void parseItems(List<GalleryItem> items, String jsonString)
            throws JsonSyntaxException {

        FlickrResponse response = new Gson()
                .fromJson(jsonString, FlickrResponse.class);

        sPageLoaded = response.getPage();

        for (GalleryItem item : response.getGalleryItems()) {
            if (item.getUrl() != null) {
                items.add(item);
            }
        }
    }
}
