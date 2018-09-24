package com.example.dex.letsbake.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dex.letsbake.R;
import com.example.dex.letsbake.database.RecipeColumns;
import com.example.dex.letsbake.utils.GlideApp;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.RecipeListAdapterViewHolder> {

    public static final String RECIPE_ID = "recipeId";
    public static final String RECIPE_NAME = "recipeName";

    private final RecipeListAdapterOnClickHandler mClickHandler;
    private Cursor mCursor;


    public RecipeListAdapter(RecipeListAdapterOnClickHandler clickHandler) {
        this.mClickHandler = clickHandler;
    }

    @Override
    public RecipeListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.list_item_recipe;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new RecipeListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeListAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.recipeName.setText(mCursor.getString(mCursor.getColumnIndex(RecipeColumns.NAME)));
        holder.recipeServings.setText(mCursor.getString(mCursor.getColumnIndex(RecipeColumns.SERVINGS)));

        String url = mCursor.getString(mCursor.getColumnIndex(RecipeColumns.IMAGE));
        if (url.equals("") || url.length() <= 0)
            url = null;

        GlideApp.with(holder.itemView)
                .load(url)
                .fitCenter()
                .centerCrop()
                .placeholder(R.drawable.logo)
                .fallback(R.drawable.logo)
                .into(holder.recipeImage);

    }

    public String getNameForId(int position) {
        mCursor.moveToPosition(position - 1);
        return mCursor.getString(1);
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


    public interface RecipeListAdapterOnClickHandler {
        void onClick(int recipeId);
    }

    public class RecipeListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tv_recipe_name)
        TextView recipeName;
        @BindView(R.id.tv_recipe_servings)
        TextView recipeServings;

        @BindView(R.id.iv_recipe_image)
        ImageView recipeImage;


        public RecipeListAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int recipeId = mCursor.getInt(mCursor.getColumnIndex(RecipeColumns.ID));
            mClickHandler.onClick(recipeId);
        }
    }
}