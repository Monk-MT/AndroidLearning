package com.cmt.photogallery;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * @author 陈明涛 Email:cmt96@foxmail.com
 * @version V1.0
 * @Description: 管理连接到Flickr的网络连接
 * @Date 2021/5/21 16:21
 */
public class FlickrFetchr {
    public static final String TAG = "FlickrFetchr";
    public static final String API_KEY = "8a8d5ea71eb27dc8fee9ba8940a46c3e";

    /**
     * 从指定URL获取原始数据并返回一个字节流数组
     * @param urlSpec
     * @return
     * @throws IOException
     */
    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = null;
        HttpURLConnection connection = null;
        Log.d(TAG, "start connection");

        try {
            url = new URL(urlSpec);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET"); // 设置为请求数据
            connection.setConnectTimeout(10000); // 链接超时
            connection.setReadTimeout(10000); // 读取超时
            InputStream in = connection.getInputStream(); // 获取数据流
            Log.d(TAG, "start in");
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Log.d(TAG, "start out");

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "connection failed");
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                Log.d(TAG, "get one buffer");
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            Log.d(TAG, "data is: " + Arrays.toString(out.toByteArray()));
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    /**
     * 将getUrlBytes(String)方法返回的结果转换为String
     * @param urlSpec
     * @return
     * @throws IOException
     */
    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    /**
     * 获取Flickr中图片的josn数据
     */
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
                    .build().toString();
            Log.d(TAG, "URL: " + url);
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(items, jsonBody);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        }

        return items;
    }

    private void parseItems(List<GalleryItem> items, JSONObject jsonBody) throws IOException, JSONException {
        JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
        JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");

        for (int i = 0; i < photoJsonArray.length(); i++) {
            JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);

            GalleryItem item = new GalleryItem();
            item.setId(photoJsonObject.getString("id"));
            item.setCaption(photoJsonObject.getString("title"));

            if (!photoJsonObject.has("url_s")) {
                continue;
            }

            item.setUrl(photoJsonObject.getString("url_s"));
            items.add(item);
        }
    }
}
