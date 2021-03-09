package de.metzgore.beansplan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;

import java.io.File;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;
import de.metzgore.beansplan.about.AboutActivity;
import de.metzgore.beansplan.baseschedule.BaseFragment;
import de.metzgore.beansplan.databinding.ActivityMainBinding;
import de.metzgore.beansplan.reminders.RemindersFragment;
import de.metzgore.beansplan.settings.SettingsActivity;
import de.metzgore.beansplan.settings.repository.AppSettings;
import de.metzgore.beansplan.weeklyschedule.WeeklyScheduleFragment;

public class MainActivity extends AppCompatActivity implements BaseFragment.OnScheduleRefreshedListener,
        BaseFragment.OnAppBarUpdatedListener, HasAndroidInjector {

    @Inject
    DispatchingAndroidInjector<Object> fragmentInjector;

    @Inject
    AppSettings settings;

    private static final String CURRENT_FRAGMENT_TAG = "current_fragment_tag";

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

        //TODO remove in future version
        deleteRecursive(getDir("dualcachedaily_schedule_key", Context.MODE_PRIVATE));
        deleteRecursive(getDir("dualcacheweekly_schedule_key", Context.MODE_PRIVATE));

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
            selectDrawerItem(R.id.nav_weekly_schedule);
        }
    }

    //TODO remove in future version
    void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.exists()) {
            if (fileOrDirectory.isDirectory())
                for (File child : fileOrDirectory.listFiles())
                    deleteRecursive(child);

            fileOrDirectory.delete();
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
            case R.id.nav_weekly_schedule:
                binding.activityMainNavigationView.setCheckedItem(menuItemId);
                replaceFragmentIfPossible(WeeklyScheduleFragment.class);
                break;
            case R.id.nav_reminders:
                binding.activityMainNavigationView.setCheckedItem(menuItemId);
                replaceFragmentIfPossible(RemindersFragment.class);
                break;
            case R.id.nav_settings:
                openActivity(SettingsActivity.class);
                break;
            case R.id.nav_about:
                openActivity(AboutActivity.class);
                break;
        }
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

    private boolean isFragmentAlreadyVisible(Class<? extends Fragment> fragmentClass) {
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);
        return currentFragment != null && currentFragment.getClass() == fragmentClass && currentFragment.isVisible();

    }

    @Override
    public void onSubTitleUpdated(String subtitle) {
        binding.activityMainToolbar.setSubtitle(subtitle);
    }

    @Override
    public void onAddToolbarElevation() {
        ViewCompat.setElevation(binding.activityMainAppbarlayout, getResources().getDimension(R.dimen.toolbar_elevation));
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
    public AndroidInjector<Object> androidInjector() {
        return fragmentInjector;
    }
}