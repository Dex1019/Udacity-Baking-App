package com.example.dex.letsbake.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.example.dex.letsbake.R;
import com.example.dex.letsbake.ui.adapter.RecipeAdapter;
import com.example.dex.letsbake.ui.adapter.RecipeListAdapter;
import com.example.dex.letsbake.ui.fragment.RecipeDetailFragment;
import com.example.dex.letsbake.ui.fragment.RecipeFragment;
import com.example.dex.letsbake.utils.NetworkUtils;


public class RecipeActivity extends AppCompatActivity
        implements RecipeFragment.OnStepSelectedListener {

    private int recipeId;
    private Bundle args;
    private Snackbar curShownSnackbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        args = getIntent().getExtras();
        if (args != null) {
            recipeId = args.getInt(RecipeListAdapter.RECIPE_ID);

            getSupportActionBar().setTitle(args.getString(RecipeListAdapter.RECIPE_NAME));
        }

        if (getResources().getBoolean(R.bool.is_tablet)) {
            if (args != null) {
                args.putInt(RecipeAdapter.RECIPE_STEP, 0);
                createStepFragment(args, 0);
            }

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }

        setContentView(R.layout.activity_recipe);
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

    public int getRecipeId() {
        return recipeId;
    }

    @Override
    public void onStepSelected(int stepId) {
        View detailView = findViewById(R.id.recipe_detail_container);

        if (args != null) {
            args.putInt(RecipeAdapter.RECIPE_STEP, stepId);

            if (detailView == null) {
                Intent intent = new Intent(this, RecipeDetailActivity.class);
                intent.putExtras(args);
                startActivity(intent);
            } else {
                createStepFragment(args, stepId);
            }
        }
    }

    private void createStepFragment(Bundle args, int stepId) {
        RecipeDetailFragment detailFragment = (RecipeDetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.recipe_detail_container);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        if (detailFragment == null) {
            detailFragment = new RecipeDetailFragment();
            transaction.replace(R.id.recipe_detail_container, detailFragment);
        } else {
            detailFragment.setRecipeStep(stepId);
        }

        detailFragment.setArguments(args);

        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}