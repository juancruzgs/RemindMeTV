package com.mobilemakers.remindmetv;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class CompleteInformationActivity extends ActionBarActivity {

    public static final String EXTRA_SHOW = "SHOW";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_information);
        if (savedInstanceState == null) {
            CompleteInformationFragment completeInformationFragment = new CompleteInformationFragment();
            Show show = getIntent().getParcelableExtra(EXTRA_SHOW);
            Bundle bundle = new Bundle();
            bundle.putParcelable(CompleteInformationFragment.EXTRA_SHOW, show);
            completeInformationFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, completeInformationFragment)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_complete_information, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
