package com.mobilemakers.remindmetv;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class ShowsListActivity extends ActionBarActivity {

    public static final String EXTRA_SEARCH = "extra_search";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shows_list);
        if (savedInstanceState == null) {
            ShowsListFragment showListFragment = new ShowsListFragment();
            String search = getIntent().getStringExtra(EXTRA_SEARCH);
            Bundle bundle = new Bundle();
            bundle.putString(ShowsListFragment.EXTRA_SEARCH, search);
            showListFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, showListFragment)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shows_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}
