package de.metzgore.rbtvschedule;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;

import de.metzgore.rbtvschedule.about.AboutActivity;
import de.metzgore.rbtvschedule.baseschedule.RefreshableScheduleFragment;
import de.metzgore.rbtvschedule.dailyschedule.RefreshableDailyScheduleFragment;
import de.metzgore.rbtvschedule.databinding.ActivityMainBinding;
import de.metzgore.rbtvschedule.settings.SettingsActivity;
import de.metzgore.rbtvschedule.settings.repository.AppSettings;
import de.metzgore.rbtvschedule.settings.repository.AppSettingsImp;
import de.metzgore.rbtvschedule.weeklyschedule.WeeklyScheduleFragment;

public class MainActivity extends AppCompatActivity implements RefreshableScheduleFragment.OnScheduleRefreshedListener {

    private static final String CURRENT_FRAGMENT_TAG = "current_fragment_tag";

    private String dailyScheduleId;
    private String weeklyScheduleId;

    private SparseArray<Runnable> defaultSchedules = new SparseArray<>(2);
    private AppSettings settings = new AppSettingsImp(this);
    private FragmentManager fragmentManager;
    @IdRes
    private int selectedItemId;
    private boolean itemSelected = false;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        dailyScheduleId = getString(R.string.fragment_daily_schedule_id);
        weeklyScheduleId = getString(R.string.fragment_weekly_schedule_id);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setSupportActionBar(binding.activityMainToolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        setupDrawerContent(binding.activityMainNavigationView);

        binding.activityMainDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                if (itemSelected) {
                    itemSelected = false;
                    selectDrawerItem(selectedItemId);
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        fragmentManager = getSupportFragmentManager();

        Fragment scheduleFragment = fragmentManager.findFragmentByTag(CURRENT_FRAGMENT_TAG);

        if (scheduleFragment != null) {
            fragmentManager.beginTransaction().replace(R.id.fragment_container, scheduleFragment, CURRENT_FRAGMENT_TAG).commit();
        } else {
            if (settings.shouldRememberLastOpenedSchedule()) {
                restoreLastFragment();
            } else {
                createDefaultFragment();
            }
        }
    }

    private void restoreLastFragment() {
        String fragmentId = settings.getLastOpenedScheduleId();
        @IdRes int navDrawerItem;

        if (fragmentId.equals(weeklyScheduleId)) {
            navDrawerItem = R.id.nav_weekly_schedule;
        } else {
            navDrawerItem = R.id.nav_daily_schedule;
        }

        selectDrawerItem(navDrawerItem);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                binding.activityMainDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (binding.activityMainDrawerLayout.isDrawerOpen(GravityCompat.START))
            binding.activityMainDrawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    private void createDefaultSchedules() {
        defaultSchedules.put(0, () -> selectDrawerItem(R.id.nav_daily_schedule));
        defaultSchedules.put(1, () -> selectDrawerItem(R.id.nav_weekly_schedule));
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            selectedItemId = menuItem.getItemId();
            itemSelected = true;
            binding.activityMainDrawerLayout.closeDrawer(GravityCompat.START);
            return false;
        });
    }

    private void selectDrawerItem(@IdRes int menuItemId) {
        binding.activityMainDrawerLayout.closeDrawer(GravityCompat.START);

        switch (menuItemId) {
            case R.id.nav_daily_schedule:
                setMenuItemSelected(menuItemId, dailyScheduleId);
                replaceFragmentIfPossible(RefreshableDailyScheduleFragment.class);
                break;
            case R.id.nav_weekly_schedule:
                setMenuItemSelected(menuItemId, weeklyScheduleId);
                replaceFragmentIfPossible(WeeklyScheduleFragment.class);
                break;
            case R.id.nav_settings:
                openActivity(SettingsActivity.class);
                break;
            case R.id.nav_about:
                openActivity(AboutActivity.class);
                break;
        }
    }

    private void setMenuItemSelected(@IdRes int menuItemId, final String fragmentId) {
        binding.activityMainNavigationView.setCheckedItem(menuItemId);
        settings.setLastOpenedFragmentId(fragmentId);
    }

    private void replaceFragmentIfPossible(Class<? extends Fragment> fragmentClass) {
        if (!isFragmentAlreadyVisible(fragmentClass)) {
            replaceFragment(fragmentClass);
        }
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

        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment, CURRENT_FRAGMENT_TAG).commit();
    }


    private void createDefaultFragment() {
        if (defaultSchedules.size() == 0) {
            createDefaultSchedules();
        }

        int defaultScheduleValue = settings.getDefaultScheduleValue();

        Runnable createScheduleFragment = defaultSchedules.get(defaultScheduleValue, () -> selectDrawerItem(R.id.nav_daily_schedule));
        createScheduleFragment.run();
    }

    private boolean isFragmentAlreadyVisible(Class<? extends Fragment> fragmentClass) {
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);
        return currentFragment != null && currentFragment.getClass() == fragmentClass && currentFragment.isVisible();

    }

    @Override
    public void onScheduleRefreshed(String subtitle) {
        binding.activityMainToolbar.setSubtitle(subtitle);
    }
}