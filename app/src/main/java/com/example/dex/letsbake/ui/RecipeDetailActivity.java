package com.example.dex.letsbake.ui;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.example.dex.letsbake.R;
import com.example.dex.letsbake.ui.adapter.RecipeListAdapter;
import com.example.dex.letsbake.ui.fragment.RecipeDetailFragment;
import com.example.dex.letsbake.utils.NetworkUtils;

import butterknife.ButterKnife;


public class RecipeDetailActivity extends AppCompatActivity {

    private Snackbar curShownSnackbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Bundle args = getIntent().getExtras();
        if (args != null) {
            getSupportActionBar().setTitle(args.getString(RecipeListAdapter.RECIPE_NAME));

            FragmentManager manager = getSupportFragmentManager();
            RecipeDetailFragment detailFragment = (RecipeDetailFragment) manager
                    .findFragmentById(R.id.recipe_detail_fragment);

            FragmentTransaction transaction = manager.beginTransaction();
            detailFragment.setArguments(args);

            transaction.commit();
        }
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
