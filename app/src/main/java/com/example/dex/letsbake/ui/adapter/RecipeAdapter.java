package com.example.dex.letsbake.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dex.letsbake.R;
import com.example.dex.letsbake.database.StepsColumns;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeAdapterViewHolder> {

    public static final String RECIPE_STEP = "recipeStep";

    private final RecipeAdapterOnClickHandler mClickHandler;
    private Cursor mCursor;


    public RecipeAdapter(RecipeAdapterOnClickHandler clickHandler) {
        this.mClickHandler = clickHandler;
    }

    @Override
    public RecipeAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.list_item_recipe_step;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new RecipeAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String stepId = mCursor.getString(mCursor.getColumnIndex(StepsColumns.STEP));
        if (stepId.equals("0")) {
            stepId = "";
        } else {
            stepId += ".";
        }
        holder.stepNumber.setText(stepId);
        holder.stepShortDescription.setText(mCursor.getString(mCursor.getColumnIndex(StepsColumns.SHORT_DESCRIPTION)));
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


    public interface RecipeAdapterOnClickHandler {
        void onClick(int stepId);
    }

    public class RecipeAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tv_recipe_step_short_desc)
        TextView stepShortDescription;

        @BindView(R.id.tv_recipe_step_nr)
        TextView stepNumber;


        public RecipeAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(adapterPosition);
        }
    }
}