package de.metzgore.rbtvschedule;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.metzgore.rbtvschedule.dailyschedule.ScheduleFragment;
import de.metzgore.rbtvschedule.settings.SettingsActivity;
import de.metzgore.rbtvschedule.settings.repository.AppSettings;
import de.metzgore.rbtvschedule.settings.repository.AppSettingsImp;
import de.metzgore.rbtvschedule.weeklyschedule.WeeklyScheduleFragment;

public class MainActivity extends AppCompatActivity {

    private static final String SCHEDULE_FRAGMENT_TAG = "schedule_fragment_tag";

    @BindView(R.id.activity_main_drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.activity_main_toolbar)
    Toolbar toolbar;
    @BindView(R.id.activity_main_navigation_view)
    NavigationView navigationView;

    private SparseArray<Runnable> defaultSchedules = new SparseArray<>(2);
    private AppSettings settings = new AppSettingsImp(this);
    private FragmentManager fragmentManager;
    @IdRes
    private int lastSelectedItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        setupDrawerContent(navigationView);

        fragmentManager = getSupportFragmentManager();

        Fragment scheduleFragment = fragmentManager.findFragmentByTag(SCHEDULE_FRAGMENT_TAG);

        if (scheduleFragment != null) {
            fragmentManager.beginTransaction().replace(R.id.fragment_container, scheduleFragment, SCHEDULE_FRAGMENT_TAG).commit();
        } else {
            if (settings.shouldRememberLastOpenedSchedule()) {
                @IdRes int navDrawerItem = settings.getLastOpenedScheduleId();
                selectDrawerItem(navDrawerItem);
            } else {
                createDefaultFragment();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        settings.saveLastOpenedScheduleId(lastSelectedItemId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    private void createDefaultSchedules() {
        defaultSchedules.put(0, () -> selectDrawerItem(R.id.nav_today_schedule));
        defaultSchedules.put(1, () -> selectDrawerItem(R.id.nav_weekly_schedule));
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(menuItem -> selectDrawerItem(menuItem.getItemId()));
    }

    private boolean selectDrawerItem(@IdRes int menuItemId) {
        drawer.closeDrawer(GravityCompat.START);

        switch (menuItemId) {
            case R.id.nav_today_schedule:
                setMenuItemSelected(menuItemId);
                replaceFragmentIfPossible(ScheduleFragment.class);
                return true;
            case R.id.nav_weekly_schedule:
                setMenuItemSelected(menuItemId);
                replaceFragmentIfPossible(WeeklyScheduleFragment.class);
                return true;
            case R.id.nav_settings:
                openActivity(SettingsActivity.class);
                return false;
            default:
                return false;
        }
    }

    private void setMenuItemSelected(@IdRes int menuItemId) {
        lastSelectedItemId = menuItemId;
        navigationView.setCheckedItem(menuItemId);
    }

    private void replaceFragmentIfPossible(Class<? extends Fragment> fragmentClass) {
        if (!isFragmentAlreadyVisible(fragmentClass))
            replaceFragment(fragmentClass);
    }

    private void openActivity(Class<? extends AppCompatActivity> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }

    private void replaceFragment(Class<? extends Fragment> fragmentClass) {
        Fragment fragment = null;

        try {
            //TODO wrong newInstance?
            fragment = fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment, SCHEDULE_FRAGMENT_TAG).commit();
    }


    private void createDefaultFragment() {
        if (defaultSchedules.size() == 0) {
            createDefaultSchedules();
        }

        int defaultScheduleValue = settings.getDefaultScheduleValue();

        Runnable createScheduleFragment = defaultSchedules.get(defaultScheduleValue, () -> selectDrawerItem(R.id.nav_today_schedule));
        createScheduleFragment.run();
    }

    private boolean isFragmentAlreadyVisible(Class<? extends Fragment> fragmentClass) {
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);
        return currentFragment != null && currentFragment.getClass() == fragmentClass && currentFragment.isVisible();

    }
}