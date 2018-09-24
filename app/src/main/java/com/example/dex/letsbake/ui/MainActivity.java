package com.example.dex.letsbake.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.dex.letsbake.R;
import com.example.dex.letsbake.database.RecipesProvider;
import com.example.dex.letsbake.sync.SyncUtils;
import com.example.dex.letsbake.ui.adapter.RecipeListAdapter;
import com.example.dex.letsbake.utils.NetworkUtils;
import com.example.dex.letsbake.widget.WidgetProvider;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        RecipeListAdapter.RecipeListAdapterOnClickHandler {

    private static final int ID_RECIPE_LIST_LOADER = 12;

    @BindView(R.id.rv_recipes)
    RecyclerView mRecyclerView;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;

    private RecipeListAdapter mRecipeListAdapter;
    private Snackbar curShownSnackbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mRecipeListAdapter = new RecipeListAdapter(this);
        mRecyclerView.setAdapter(mRecipeListAdapter);

        if (getResources().getBoolean(R.bool.is_tablet)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

            mRecyclerView.setLayoutManager(
                    new GridLayoutManager(this, 3)
            );
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

            mRecyclerView.setLayoutManager(
                    new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            );
        }
        mRecyclerView.setHasFixedSize(true);

        loadData(false);

        getSupportLoaderManager().initLoader(ID_RECIPE_LIST_LOADER, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String msg = "";
        switch (NetworkUtils.getNetworkInfo(this)) {
            case NetworkUtils.TYPE_WIFI:
                break;

            case NetworkUtils.TYPE_MOBILE:
                msg = getString(R.string.msg_on_mobile);
                break;

            case NetworkUtils.NOT_CONNECTED:
                msg = getString(R.string.msg_offline);
                break;

            default:
                break;
        }

        if (!msg.equals("") && msg.length() > 0) {
            curShownSnackbar = Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_INDEFINITE);
            curShownSnackbar.setAction(getString(R.string.msg_dismiss), new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            curShownSnackbar.show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        curShownSnackbar = null;
    }

    private void loadData(boolean immediate) {
        if (NetworkUtils.getNetworkInfo(this) == NetworkUtils.TYPE_WIFI) {
            showLoading();
            SyncUtils.initialize(this, immediate);
        }
    }

    private void showLoading() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    private void showRecipeList() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {
            case ID_RECIPE_LIST_LOADER:
                Uri recipeListQueryUri = RecipesProvider.Recipes.CONTENT_URI;
                return new CursorLoader(
                        this,
                        recipeListQueryUri,
                        null,
                        null,
                        null,
                        null
                );

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mRecipeListAdapter.swapCursor(data);
        showRecipeList();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onClick(int recipeId) {
        String recipeName = mRecipeListAdapter.getNameForId(recipeId);

        WidgetProvider.setWidgetData(this, recipeId, recipeName);

        Intent intent = new Intent(this, RecipeActivity.class);
        Bundle args = new Bundle();
        args.putInt(RecipeListAdapter.RECIPE_ID, recipeId);
        args.putString(RecipeListAdapter.RECIPE_NAME, recipeName);
        intent.putExtras(args);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                loadData(true);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}