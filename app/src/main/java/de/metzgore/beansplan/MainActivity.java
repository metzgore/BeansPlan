package de.metzgore.beansplan;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import de.metzgore.beansplan.about.AboutActivity;
import de.metzgore.beansplan.baseschedule.RefreshableScheduleFragment;
import de.metzgore.beansplan.dailyschedule.RefreshableDailyScheduleFragment;
import de.metzgore.beansplan.databinding.ActivityMainBinding;
import de.metzgore.beansplan.settings.SettingsActivity;
import de.metzgore.beansplan.settings.repository.AppSettings;
import de.metzgore.beansplan.weeklyschedule.WeeklyScheduleFragment;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements RefreshableScheduleFragment
        .OnScheduleRefreshedListener, HasSupportFragmentInjector {

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentInjector;

    @Inject
    AppSettings settings;

    private static final String CURRENT_FRAGMENT_TAG = "current_fragment_tag";

    private Map<String, Runnable> defaultSchedules = new HashMap<>(2);
    private FragmentManager fragmentManager;
    @IdRes
    private int selectedItemId;
    private boolean itemSelected = false;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

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
            fragmentManager.beginTransaction().replace(R.id.fragment_container, scheduleFragment,
                    CURRENT_FRAGMENT_TAG).commit();
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

        if (fragmentId.equals(settings.getWeeklyScheduleFragmentId())) {
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
        defaultSchedules.put(settings.getDailyScheduleFragmentId(), selectDailyScheduleRunnable());
        defaultSchedules.put(settings.getWeeklyScheduleFragmentId(), selectWeeklyScheduleRunnable());
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
                selectMenuItemAndSaveFragment(menuItemId, settings.getDailyScheduleFragmentId());
                replaceFragmentIfPossible(RefreshableDailyScheduleFragment.class);
                break;
            case R.id.nav_weekly_schedule:
                selectMenuItemAndSaveFragment(menuItemId, settings.getWeeklyScheduleFragmentId());
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

    private void selectMenuItemAndSaveFragment(@IdRes int menuItemId, final String fragmentId) {
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

        final String defaultScheduleValue = settings.getDefaultScheduleValue();

        Runnable createScheduleFragment = defaultSchedules.get(defaultScheduleValue);

        if (createScheduleFragment == null)
            createScheduleFragment = selectDailyScheduleRunnable();

        createScheduleFragment.run();
    }

    private boolean isFragmentAlreadyVisible(Class<? extends Fragment> fragmentClass) {
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);
        return currentFragment != null && currentFragment.getClass() == fragmentClass && currentFragment.isVisible();

    }

    private Runnable selectDailyScheduleRunnable() {
        return () -> selectDrawerItem(R.id.nav_daily_schedule);
    }

    private Runnable selectWeeklyScheduleRunnable() {
        return () -> selectDrawerItem(R.id.nav_weekly_schedule);
    }

    @Override
    public void onSubTitleUpdated(String subtitle) {
        binding.activityMainToolbar.setSubtitle(subtitle);
    }

    @Override
    public void onAddToolbarElevation() {
        ViewCompat.setElevation(binding.activityMainAppbarlayout, getResources().getDimension(R.dimen
                .abc_action_bar_elevation_material));
    }

    @Override
    public void onRemoveToolbarElevation() {
        ViewCompat.setElevation(binding.activityMainAppbarlayout, 0);
    }

    public void onLastUpdateUpdated(long timestamp) {
        if (timestamp <= 0) {
            binding.activityMainUpdatedTextview.setVisibility(View.GONE);
        } else {
            binding.activityMainUpdatedTextview.setVisibility(View.VISIBLE);

            String lastUpdated = getString(R.string.activity_main_last_updated, getString(R.string.activity_main_last_updated_now));

            if (System.currentTimeMillis() - timestamp > DateUtils.MINUTE_IN_MILLIS) {
                lastUpdated = getString(R.string.activity_main_last_updated, DateUtils.getRelativeTimeSpanString(timestamp));
                binding.activityMainUpdatedTextview.setText(getString(R.string.activity_main_last_updated, DateUtils
                        .getRelativeTimeSpanString(timestamp)));
            }

            binding.activityMainUpdatedTextview.setText(lastUpdated);
        }
    }

    @Override
    public void onAddPaddingBottom() {
        int paddingBottom = (int) getResources().getDimension(R.dimen.toolbar_content_padding_bottom);
        int paddingLeft = (int) getResources().getDimension(R.dimen.toolbar_content_padding_left);
        int paddingRight = (int) getResources().getDimension(R.dimen.toolbar_content_padding_right);
        binding.activityMainUpdatedTextview.setPadding(paddingLeft, 0, paddingRight, paddingBottom);
    }

    @Override
    public void onRemovePaddingBottom() {
        int paddingLeft = (int) getResources().getDimension(R.dimen.toolbar_content_padding_left);
        int paddingRight = (int) getResources().getDimension(R.dimen.toolbar_content_padding_right);
        binding.activityMainUpdatedTextview.setPadding(paddingLeft, 0, paddingRight, 0);
    }

    @Override
    public void onExpandAppBar() {
        binding.activityMainAppbarlayout.setExpanded(true, false);
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentInjector;
    }
}