package com.example.dex.letsbake.ui.fragment;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dex.letsbake.R;
import com.example.dex.letsbake.database.RecipesProvider;
import com.example.dex.letsbake.ui.adapter.RecipeAdapter;
import com.example.dex.letsbake.ui.adapter.RecipeListAdapter;
import com.example.dex.letsbake.ui.adapter.RecipeStepPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RecipeDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String STATE_STEP_ID = "stepId";
    private static final int ID_STEP_LOADER = 90;

    @BindView(R.id.vp_steps)
    ViewPager mPager;

    private int recipeId;
    private int stepId;
    private RecipeStepPagerAdapter mPagerAdapter;


    public RecipeDetailFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_detail, container, false);
        ButterKnife.bind(this, rootView);

        mPagerAdapter = new RecipeStepPagerAdapter(getChildFragmentManager(), getContext());
        mPager.setAdapter(mPagerAdapter);

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                stepId = position;

                int first = 0;
                int last = mPagerAdapter.getCount() - 1;
                int prev = position - 1;
                int next = position + 1;

                RecipeStepFragment fCur = (RecipeStepFragment) mPagerAdapter.instantiateItem(mPager, position);
                fCur.fragmentIsVisible(true);

                RecipeStepFragment fPrev;
                if (prev >= first) {
                    fPrev = (RecipeStepFragment) mPagerAdapter.instantiateItem(mPager, prev);
                    if (fPrev != null) fPrev.fragmentIsVisible(false);
                }

                RecipeStepFragment fNext;
                if (next <= last) {
                    fNext = (RecipeStepFragment) mPagerAdapter.instantiateItem(mPager, next);
                    if (fNext != null) fNext.fragmentIsVisible(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle args = getArguments();

        if (args != null) {
            stepId = args.getInt(RecipeAdapter.RECIPE_STEP);
            recipeId = args.getInt(RecipeListAdapter.RECIPE_ID);

            if (savedInstanceState != null) {
                stepId = savedInstanceState.getInt(STATE_STEP_ID);
            }

            getActivity().getSupportLoaderManager().initLoader(ID_STEP_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case ID_STEP_LOADER:
                Uri stepQueryUri = RecipesProvider.Steps.stepsForRecipe(recipeId);
                return new CursorLoader(getActivity(), stepQueryUri, null, null, null, null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            mPagerAdapter.swapCursor(data);
        }
        setRecipeStep(stepId);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void setRecipeStep(int step) {
        if (mPager != null) {
            mPager.setCurrentItem(step);
            ((RecipeStepFragment) mPagerAdapter.instantiateItem(mPager, step)).fragmentIsVisible(true);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_STEP_ID, stepId);

        super.onSaveInstanceState(outState);
    }
}