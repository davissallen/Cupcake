package me.davisallen.cupcake.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Package Name:   me.davisallen.cake
 * Project:        Cake
 * Created by davis, on 8/1/17
 */

public class NetworkUtils {

    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();
    private static final String RECIPES_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

    public static String getResponseFromHttp() {

        /*
         *  Grab URL from string resource
         */
        URL recipeUrl = null;
        try {
            recipeUrl = new URL(RECIPES_URL);
        } catch (MalformedURLException e) {
            Log.w(LOG_TAG, "URL is invalid format: " + RECIPES_URL);
            e.printStackTrace();
            return null;
        }

        /*
         *  Make URL connection
         */
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) recipeUrl.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(10000);
        } catch (IOException e) {
            Log.w(LOG_TAG, "URL cannot make connection: " + recipeUrl);
            e.printStackTrace();
            return null;
        }

        /*
         *  Get InputStream from HttpURLConnection
         */
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } catch (IOException e) {
            Log.w(LOG_TAG, "InputStream not found with URL: " + recipeUrl);
            e.printStackTrace();
            return null;
        } finally {
            urlConnection.disconnect();
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
