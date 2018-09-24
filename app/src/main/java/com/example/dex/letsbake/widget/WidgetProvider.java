package com.example.dex.letsbake.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.example.dex.letsbake.R;
import com.example.dex.letsbake.ui.MainActivity;
import com.example.dex.letsbake.ui.RecipeActivity;
import com.example.dex.letsbake.ui.adapter.RecipeListAdapter;


public class WidgetProvider extends AppWidgetProvider {


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int[] appWidgetIds, int recipeId, String recipeName) {
        Intent intent;
        PendingIntent pendingIntent;
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

        for (int appWidgetId : appWidgetIds) {
            if (recipeId >= 0 && recipeName != null) {
                Bundle args = new Bundle();
                args.putInt(RecipeListAdapter.RECIPE_ID, recipeId);
                args.putString(RecipeListAdapter.RECIPE_NAME, recipeName);

                intent = new Intent(context, RecipeActivity.class);
                intent.putExtras(args);

                pendingIntent = TaskStackBuilder.create(context)
                        .addParentStack(RecipeActivity.class)
                        .addNextIntent(intent)
                        .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                views.setTextViewText(R.id.widget_tv_recipe_title, recipeName);

                Intent service = new Intent(context, WidgetRemoteViewsService.class);
                service.setData(Uri.fromParts("content", String.valueOf(recipeId), null));
                views.setRemoteAdapter(R.id.widget_lv_recipe_ingredients, service);
            } else {
                intent = new Intent(context, MainActivity.class);
                pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            }

            views.setOnClickPendingIntent(R.id.widget_click, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    public static void setWidgetData(Context context, int recipeId, String recipeName) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context,
                WidgetProvider.class));

        updateAppWidget(context, appWidgetManager, appWidgetIds, recipeId, recipeName);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_lv_recipe_ingredients);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        updateAppWidget(context, appWidgetManager, appWidgetIds, -1, null);
    }
}