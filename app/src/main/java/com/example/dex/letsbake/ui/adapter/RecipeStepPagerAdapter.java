package com.example.dex.letsbake.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.dex.letsbake.R;
import com.example.dex.letsbake.database.StepsColumns;
import com.example.dex.letsbake.ui.fragment.RecipeStepFragment;


public class RecipeStepPagerAdapter extends FragmentStatePagerAdapter {

    public static final String RECIPE_DESCRIPTION = "description";
    public static final String RECIPE_VIDEO_URL = "videoURL";
    public static final String RECIPE_VIDEO_THUMBNAIL = "videoThumbnail";

    private static Resources res;

    private Cursor mCursor;


    public RecipeStepPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        res = context.getResources();
    }

    @Override
    public Fragment getItem(int position) {
        if (position >= getCount())
            position = getCount() - 1;

        mCursor.moveToPosition(position);

        String desc = mCursor.getString(mCursor.getColumnIndex(StepsColumns.DESCRIPTION));
        String videoURL = mCursor.getString(mCursor.getColumnIndex(StepsColumns.VIDEO_URL));
        String videoThumbnail = mCursor.getString(mCursor.getColumnIndex(StepsColumns.THUMBNAIL_URL));

        Bundle args = new Bundle();

        args.putString(RECIPE_DESCRIPTION, desc);
        if (videoURL.length() > 0)
            args.putString(RECIPE_VIDEO_URL, videoURL);
        if (videoThumbnail.length() > 0)
            args.putString(RECIPE_VIDEO_THUMBNAIL, videoThumbnail);

        return RecipeStepFragment.newInstance(args);
    }

    @Override
    public int getCount() {
        if (mCursor != null) {
            return mCursor.getCount();
        }
        return 0;
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        mCursor.moveToPosition(position);
        int step = mCursor.getInt(mCursor.getColumnIndex(StepsColumns.STEP));

        if (step == 0) {
            return mCursor.getString(mCursor.getColumnIndex(StepsColumns.SHORT_DESCRIPTION));
        }

        return res.getString(R.string.recipe_step_number_pager, step);
    }
}