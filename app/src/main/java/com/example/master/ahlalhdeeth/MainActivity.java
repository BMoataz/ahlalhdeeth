package com.example.master.ahlalhdeeth;

//import android.app.ActionBar;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
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
import android.widget.Toast;

import com.example.master.ahlalhdeeth.fragment.MainFragment;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private LinearLayout mLinearLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerArrowDrawable drawerArrow;
    private boolean drawerArrowColor;

    public MainActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new MainFragment())
                    .commit();
        }
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeButtonEnabled(true);
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_main);
        ListView mDrawerList = (ListView) findViewById(R.id.navdrawer);
        mLinearLayout = (LinearLayout) findViewById(R.id.c_navd);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open,
                R.string.drawer_close) {

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


        String[] values = new String[]{
                "تسجيل الدخول",
                "الخط الزمني",
                "البحث",
                "نسخة المتصفح",
                "مشاركة التطبيق",
                "تقييم التطبيق",
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Toast.makeText(MainActivity.this, "التطبيق لا يدعم بعد هذه الميزة، نشكركم لتفهمكم", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        break;
                    case 1:
                        Toast.makeText(MainActivity.this, "التطبيق لا يدعم بعد هذه الميزة، نشكركم لتفهمكم", Toast.LENGTH_LONG).show();
                        break;
                    case 2:
                        Toast.makeText(MainActivity.this, "التطبيق لا يدعم بعد هذه الميزة، نشكركم لتفهمكم", Toast.LENGTH_LONG).show();
                        break;
                    case 3:
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.ahlalhdeeth.com/vb/index.php"));
                        startActivity(browserIntent);
                        break;
                    case 4:
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("text/plain");
                        share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                        share.putExtra(Intent.EXTRA_TEXT, getString(R.string.hello) + "\n" +
                                getString(R.string.share) +
                                " https://play.google.com/store/apps/details?id=" + getPackageName());
                        startActivity(Intent.createChooser(share, getString(R.string.app_name)));
                        break;
                    case 5:
                        String appUrl = "https://play.google.com/store/apps/details?id=" + getPackageName();
                        Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(appUrl));
                        startActivity(rateIntent);
                        break;
                }

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (mDrawerLayout.isDrawerOpen(mLinearLayout)) {
                mDrawerLayout.closeDrawer(mLinearLayout);
            } else {
                mDrawerLayout.openDrawer(mLinearLayout);
            }
        }
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            Bundle translateBundle = ActivityOptions.makeCustomAnimation(MainActivity.this, R.anim.slide_in_left, R.anim.slide_out_left).toBundle();
            startActivity(intent, translateBundle);
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
}
