package com.pkmpei.mobile;

import com.pkmpei.mobile.Fragments.AllListsFragment;
import com.pkmpei.mobile.Fragments.AuthorizationFragment;
import com.pkmpei.mobile.Fragments.ConcursGroupFragment;
import com.pkmpei.mobile.Fragments.NewsFragment;
import com.pkmpei.mobile.Fragments.QueueFragment;
import com.pkmpei.mobile.Fragments.SignedInFragment;
import com.pkmpei.mobile.Utils.ServiceUtils;
import com.pkmpei.mobile.Utils.Utils;
import com.pkmpei.mobile.adapter.NavDrawerListAdapter;
import com.pkmpei.mobile.model.MenuItem;

import java.util.ArrayList;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

;import com.dyploma.garik.dyploma.R;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ArrayList<MenuItem> menuItems;
    private NavDrawerListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // сохраняем в настройки imei
        TelephonyManager telephonyManager = (android.telephony.TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        UserPreferences.getInstance().setImei(telephonyManager.getDeviceId());

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        // пытаемся залогиниться
        setContentView(R.layout.activity_main);

        mTitle = mDrawerTitle = getTitle();

        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // nav drawer icons from resources
        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        menuItems = new ArrayList<>();

        // Добавляем пункты меню в боковое меню
        // Новости
        menuItems.add(new MenuItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        // Электронная очередь
        menuItems.add(new MenuItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        // Личный кабинет
        String FIO = UserPreferences.getInstance().getFIO();
        if (!Utils.isEmpty(FIO)) {
            menuItems.add(new MenuItem(FIO, navMenuIcons.getResourceId(2, -1)));
        } else {
            menuItems.add(new MenuItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        }
        // Конкурсные группы
        menuItems.add(new MenuItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
        // Тестовая группа
        menuItems.add(new MenuItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));

        // Recycle the typed array
        navMenuIcons.recycle();

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                menuItems);
        mDrawerList.setAdapter(adapter);

        // enabling action bar app icon and behaving it as toggle button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mTitle);
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_hamburger);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                super.onDrawerSlide(drawerView, 0); // this disables the arrow @ completed state
                getSupportActionBar().setTitle(mDrawerTitle);
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, 0); // this disables the animation
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        logon();

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(0);
        }
    }

    /**
     * Slide menu item click listener
     */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /***
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     */
    private void displayView(int position) {
        // update the main_menu content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new NewsFragment();
                break;
            case 1:
                fragment = new QueueFragment();
                break;
            case 2:
                if (UserPreferences.getInstance().getFIO().isEmpty()) {
                    fragment = new AuthorizationFragment();
                } else {
                    fragment = new SignedInFragment();
                }
                break;
            case 3:
                fragment = new AllListsFragment();
                break;
            case 4:
                fragment = new ConcursGroupFragment();
                break;

            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e(TAG, "Error in creating fragment");
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {

        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getFragmentManager().popBackStack();
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
        MenuItem personalOfficeMenuItem = ((MenuItem) mDrawerList.getAdapter().getItem(2));
        if (personalOfficeMenuItem != null) {
            personalOfficeMenuItem.setTitle(!Utils.isEmpty(FIO) ? FIO : navMenuTitles[2]);
        } else {
            Log.e(TAG, "Не удалось задать текст пункту меню \"Личный кабинет\"");
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int position = mDrawerList.getSelectedItemPosition();
                mDrawerList.setItemChecked(position, true);
                mDrawerList.setSelection(position);
            }
        });

    }
}
