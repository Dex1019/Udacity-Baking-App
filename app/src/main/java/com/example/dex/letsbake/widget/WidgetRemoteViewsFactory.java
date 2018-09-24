package com.example.dex.letsbake.widget;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.dex.letsbake.R;
import com.example.dex.letsbake.database.IngredientsColumns;
import com.example.dex.letsbake.database.RecipesProvider;
import com.example.dex.letsbake.utils.Utils;

import java.text.DecimalFormat;


public class WidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private Cursor mCursor;
    private int recipeId;


    public WidgetRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        if (intent.getData() != null) {
            recipeId = Integer.valueOf(intent.getData().getSchemeSpecificPart());
        }
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        if (mCursor != null) {
            mCursor.close();
        }

        final long identityToken = Binder.clearCallingIdentity();
        Uri ingredientQueryUri = RecipesProvider.Ingredients.ingredientsForRecipe(recipeId);
        mCursor = mContext.getContentResolver().query(
                ingredientQueryUri,
                null,
                null,
                null,
                null
        );

        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
        }
    }

    @Override
    public int getCount() {
        if (mCursor != null) {
            return mCursor.getCount();
        }
        return 0;
    }

    @Override
    public RemoteViews getViewAt(int i) {
        if (i == AdapterView.INVALID_POSITION || mCursor == null || !mCursor.moveToPosition(i)) {
            return null;
        }

        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.list_item_ingredient);
        Resources res = mContext.getResources();
        double quantity = mCursor.getDouble(mCursor.getColumnIndex(IngredientsColumns.QUANTITY));
        String measure = mCursor.getString(mCursor.getColumnIndex(IngredientsColumns.MEASURE));
        String ingredient = mCursor.getString(mCursor.getColumnIndex(IngredientsColumns.INGREDIENT));
        DecimalFormat df = new DecimalFormat("0.##");
        String quantityMeasure =
                res.getQuantityString(R.plurals.ingredient,
                        (int) Math.ceil(Utils.isSingularMeasure(quantity, measure)), df.format(quantity),
                        measure);
        rv.setTextViewText(R.id.tv_ingredient_quantity_measure, quantityMeasure);
        rv.setTextViewText(R.id.tv_ingredient, ingredient);

        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        if (mCursor.moveToPosition(i)) {
            return mCursor.getInt(0);
        }
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}