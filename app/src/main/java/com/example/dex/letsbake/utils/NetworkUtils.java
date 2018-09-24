package com.example.dex.letsbake.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;


public class NetworkUtils {

    public static final int NOT_CONNECTED = -1;
    public static final int TYPE_MOBILE = 0;
    public static final int TYPE_WIFI = 1;
    private static final String BAKING_BASE_URL
            = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";
    private static NetworkInfo networkInfo;


    public static URL getBakingUrl() {
        Uri buildUri = Uri.parse(BAKING_BASE_URL);

        URL url = null;
        try {
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    private static boolean isOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE
        );

        if (connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        }

        return (networkInfo != null && networkInfo.isConnected());
    }

    public static int getNetworkInfo(Context context) {
        if (!isOnline(context))
            return NOT_CONNECTED;

        return networkInfo.getType();
    }

    public static String getResponseFromHttpsUrl(URL url) throws IOException {
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            String response = null;
            if (scanner.hasNext()) {
                response = scanner.next();
            }
            scanner.close();

            return response;
        } finally {
            urlConnection.disconnect();
        }
    }
}