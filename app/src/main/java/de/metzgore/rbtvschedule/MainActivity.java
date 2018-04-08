package de.metzgore.rbtvschedule;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.MenuItem;

import de.metzgore.rbtvschedule.baseschedule.BaseScheduleFragment;
import de.metzgore.rbtvschedule.dailyschedule.DailyScheduleFragment;
import de.metzgore.rbtvschedule.databinding.ActivityMainBinding;
import de.metzgore.rbtvschedule.settings.SettingsActivity;
import de.metzgore.rbtvschedule.settings.repository.AppSettings;
import de.metzgore.rbtvschedule.settings.repository.AppSettingsImp;
import de.metzgore.rbtvschedule.weeklyschedule.WeeklyScheduleFragment;

public class MainActivity extends AppCompatActivity implements BaseScheduleFragment.OnScheduleUpdatedListener {

    private static final String SCHEDULE_FRAGMENT_TAG = "schedule_fragment_tag";

    private SparseArray<Runnable> defaultSchedules = new SparseArray<>(2);
    private AppSettings settings = new AppSettingsImp(this);
    private FragmentManager fragmentManager;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setSupportActionBar(binding.activityMainToolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        setupDrawerContent(binding.activityMainNavigationView);

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
        defaultSchedules.put(0, () -> selectDrawerItem(R.id.nav_today_schedule));
        defaultSchedules.put(1, () -> selectDrawerItem(R.id.nav_weekly_schedule));
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(menuItem -> selectDrawerItem(menuItem.getItemId()));
    }

    private boolean selectDrawerItem(@IdRes int menuItemId) {
        binding.activityMainDrawerLayout.closeDrawer(GravityCompat.START);

        switch (menuItemId) {
            case R.id.nav_today_schedule:
                setMenuItemSelected(menuItemId);
                replaceFragmentIfPossible(DailyScheduleFragment.class);
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
        binding.activityMainNavigationView.setCheckedItem(menuItemId);
        settings.saveLastOpenedScheduleId(menuItemId);
    }

    private void replaceFragmentIfPossible(Class<? extends Fragment> fragmentClass) {
        if (!isFragmentAlreadyVisible(fragmentClass)) {
            replaceFragment(fragmentClass);
            binding.activityMainToolbar.setSubtitle(null);
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

    @Override
    public void onScheduleUpdated(String subtitle) {
        binding.activityMainToolbar.setSubtitle(subtitle);
    }
}