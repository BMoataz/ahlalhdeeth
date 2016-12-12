package com.example.master.ahlalhdeeth;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.master.ahlalhdeeth.fragment.DetailFragment;
//import com.ikimuhendis.ldrawer.ActionBarDrawerToggle;
//import com.ikimuhendis.ldrawer.DrawerArrowDrawable;

public class DetailActivity extends AppCompatActivity {


    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private LinearLayout mLinearLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerArrowDrawable drawerArrow;

    public DetailActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeButtonEnabled(true);
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_detail);
        mDrawerList = (ListView) findViewById(R.id.navdrawer);
        mLinearLayout = (LinearLayout) findViewById(R.id.drawercontainer);

//        drawerArrow = new DrawerArrowDrawable(this) {
//            @Override
//            public boolean isLayoutRtl() {
//                return false;
//            }
//        };
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();


        String[] Titles = getIntent().getStringArrayExtra("alltitles");
//        final String[] Links = getIntent().getStringArrayExtra("alllinks");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, Titles);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent OpenActivityDrawerIntent = new Intent(getApplicationContext(), MainActivity.class);
                OpenActivityDrawerIntent.putExtra("position", position);
                startActivity(OpenActivityDrawerIntent);
                mDrawerLayout.closeDrawer(mLinearLayout);
//                finish();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
//            if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
//                mDrawerLayout.closeDrawer(mDrawerList);
//            } else {
//                mDrawerLayout.openDrawer(mDrawerList);
//            }
            if (mDrawerLayout.isDrawerOpen(mLinearLayout)) {
                mDrawerLayout.closeDrawer(mLinearLayout);
            } else {
                mDrawerLayout.openDrawer(mLinearLayout);
            }
        }
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}
