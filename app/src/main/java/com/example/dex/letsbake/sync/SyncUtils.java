package com.example.dex.letsbake.sync;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.example.dex.letsbake.R;
import com.example.dex.letsbake.database.RecipeColumns;
import com.example.dex.letsbake.database.RecipesProvider;


public class SyncUtils {

    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;


    synchronized public static void initialize(final Context context, boolean immediate) {
        if (immediate) {
            startImmediateSync(context);
            return;
        }

        Thread checkForEmpty = new Thread(new Runnable() {
            @Override
            public void run() {
                Uri recipeQueryUri = RecipesProvider.Recipes.CONTENT_URI;
                String[] projection = {RecipeColumns.ID};

                Cursor cursor = context.getContentResolver().query(
                        recipeQueryUri,
                        projection,
                        null,
                        null,
                        null
                );

                if (cursor == null || cursor.getCount() == 0) {
                    startImmediateSync(context);
                } else {
                    cursor.close();
                    startSync(context);
                }
            }
        });

        checkForEmpty.start();
    }

    private static void startSync(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String lastSyncKey = context.getString(R.string.pref_last_sync);
        long lastSync = prefs.getLong(lastSyncKey, 0);

        if (System.currentTimeMillis() - lastSync >= DAY_IN_MILLIS) {
            startImmediateSync(context);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong(lastSyncKey, System.currentTimeMillis());
            editor.apply();
        }
    }

    private static void startImmediateSync(Context context) {
        Intent syncIntent = new Intent(context, SyncIntentService.class);
        context.startService(syncIntent);
    }
}