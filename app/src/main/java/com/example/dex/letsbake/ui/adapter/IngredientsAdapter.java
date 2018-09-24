package com.example.dex.letsbake.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dex.letsbake.R;
import com.example.dex.letsbake.database.IngredientsColumns;
import com.example.dex.letsbake.utils.Utils;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;


public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientsAdapterViewHolder> {

    private Context mContext;
    private Cursor mCursor;


    @Override
    public IngredientsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        int layoutIdForListItem = R.layout.list_item_ingredient;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new IngredientsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IngredientsAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        Resources res = mContext.getResources();
        double quantity = mCursor.getDouble(mCursor.getColumnIndex(IngredientsColumns.QUANTITY));
        String measure = mCursor.getString(mCursor.getColumnIndex(IngredientsColumns.MEASURE));
        String ingredient = mCursor.getString(mCursor.getColumnIndex(IngredientsColumns.INGREDIENT));
        DecimalFormat df = new DecimalFormat("0.##");
        String quantityMeasure =
                res.getQuantityString(R.plurals.ingredient,
                        (int) Math.ceil(Utils.isSingularMeasure(quantity, measure)), df.format(quantity),
                        measure);
        holder.ingredientQuantityAndMeasure.setText(quantityMeasure);
        holder.ingredient.setText(ingredient);
    }

    @Override
    public int getItemCount() {
        if (mCursor != null) {
            return mCursor.getCount();
        }
        return 0;
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }


    public class IngredientsAdapterViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_ingredient_quantity_measure)
        TextView ingredientQuantityAndMeasure;
        @BindView(R.id.tv_ingredient)
        TextView ingredient;

        public IngredientsAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}