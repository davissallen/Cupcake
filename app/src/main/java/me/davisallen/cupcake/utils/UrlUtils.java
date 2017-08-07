package me.davisallen.cupcake.utils;

import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Package Name:   me.davisallen.cake.utils
 * Project:        Cake
 * Created by davis, on 8/2/17
 */

public class UrlUtils {

    private static final String LOG_TAG = UrlUtils.class.getSimpleName();

    public static URL convertStringToURL(String string) {
        if (string == null || string.length() == 0) {
            Log.d(LOG_TAG, "No video or thumbnail for this step");
            return null;
        }

        try {
            URL url = new URL(string);
            return url;
        } catch (MalformedURLException e) {
            Log.w(LOG_TAG, "Not a valid URL for video or thumbnail: " + string);
            e.printStackTrace();
            return null;
        }
    }
}
