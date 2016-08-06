package com.pkmpei.mobile;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;

import com.dyploma.garik.dyploma.R;
import com.pkmpei.mobile.Fragments.AllListsFragment;
import com.pkmpei.mobile.Fragments.AuthorizationFragment;
import com.pkmpei.mobile.Fragments.NewsFragment;
import com.pkmpei.mobile.Fragments.QueueFragment;
import com.pkmpei.mobile.Fragments.SignedInFragment;
import com.pkmpei.mobile.Utils.ServiceUtils;
import com.pkmpei.mobile.Utils.Utils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    private NavigationView navigationView;
    @IdRes private int selectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // сохраняем в настройки imei
        TelephonyManager telephonyManager = (android.telephony.TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        UserPreferences.getInstance().setImei(telephonyManager.getDeviceId());

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer,toolbar,
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_hamburger);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                super.onDrawerSlide(drawerView, 0); // this disables the arrow @ completed state
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, 0); // this disables the animation
            }
        };
        /*ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.some, R.string.some);*/
        drawer.addDrawerListener(toggle);
        toggle.onDrawerClosed(drawer);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Личный кабинет
        if (!Utils.isEmpty(UserPreferences.getInstance().getImei())) {
            String FIO = UserPreferences.getInstance().getFIO();
            if (!Utils.isEmpty(FIO)) {
                navigationView.getMenu().getItem(3).setTitle(FIO);
            } else {
                navigationView.getMenu().getItem(3).setTitle(R.string.menu_profile);
            }
            navigationView.getMenu().getItem(3).setVisible(false);
        } else {
            navigationView.getMenu().getItem(3).setVisible(false);
        }
        logon();
        onNavigationItemSelected(navigationView.getMenu().getItem(0));
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        selectedItem = item.getItemId();
        int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        Fragment fragment = null;
        switch (id) {
            case R.id.menu_news:
                fragment = new NewsFragment();
                break;
            case R.id.menu_queue:
                fragment = new QueueFragment();
                break;
            case R.id.menu_profile:
                if (UserPreferences.getInstance().getFIO().isEmpty()) {
                    fragment = new AuthorizationFragment();
                } else {
                    fragment = new SignedInFragment();
                }
                break;
            case R.id.menu_lists:
                fragment = new AllListsFragment();
                break;

            default:
                break;
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            //setTitle(navMenuTitles[position]);
            item.setChecked(true);
            getSupportActionBar().setTitle(item.getTitle());
            drawer.closeDrawer(GravityCompat.START);
            return true;
        } else {
            // error in creating fragment
            Log.e(TAG, "Error in creating fragment");
            return false;
        }
    }

    private void logon() {
        Log.d(TAG, "Запрос на авторизацию пользователя");
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        ServiceUtils.logon(this.getResources().getString(R.string.logon),
                sharedPref.getString(this.getResources().getString(R.string.supersaved_mobile_password), "defaultPwd"),
                new Callback<String>() {
                    @Override
                    public void call(String input) {
                    }
                });
        updatePersonalCabinetTitle();
    }

    public void updatePersonalCabinetTitle() {
        String FIO = UserPreferences.getInstance().getFIO();
        if (Utils.isEmpty(UserPreferences.getInstance().getImei())) {
            navigationView.getMenu().getItem(3).setVisible(false);
            return;
        }
        MenuItem personalOfficeMenuItem = (navigationView.getMenu().getItem(3));
        navigationView.getMenu().getItem(3).setVisible(true);
        if (personalOfficeMenuItem != null) {
            personalOfficeMenuItem.setTitle(!Utils.isEmpty(FIO) ? FIO : getResources().getString(R.string.menu_profile));
        } else {
            Log.e(TAG, "Не удалось задать текст пункту меню \"Личный кабинет\"");
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                navigationView.getMenu().getItem(selectedItem).setChecked(true);
            }
        });

    }
}
