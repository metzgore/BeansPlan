package de.metzgore.rbtvschedule;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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

    @BindView(R.id.activity_main_drawer_layout)
    DrawerLayout mDrawer;
    @BindView(R.id.activity_main_toolbar)
    Toolbar toolbar;
    @BindView(R.id.activity_main_navigation_view)
    NavigationView mNavigationView;

    private ActionBarDrawerToggle mDrawerToggle;

    private SparseArray<ScheduleCreator> defaultSchedules = new SparseArray<>(2);
    private AppSettings settings = new AppSettingsImp(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawer.addDrawerListener(mDrawerToggle);

        setupDrawerContent(mNavigationView);

        FragmentManager fm = getSupportFragmentManager();
        Fragment defaultFragment = fm.findFragmentById(R.id.fragment_container);

        if (defaultFragment == null) {
            defaultFragment = createDefaultFragment();
            fm.beginTransaction().add(R.id.fragment_container, defaultFragment).commit();
        }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    private void createDefaultSchedules() {
        defaultSchedules.put(0, () -> ScheduleFragment.newInstance());
        defaultSchedules.put(1, () -> WeeklyScheduleFragment.newInstance());
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            selectDrawerItem(menuItem);
            return true;
        });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_today_schedule:
                replaceFragment(ScheduleFragment.class, menuItem);
                break;
            case R.id.nav_weekly_schedule:
                replaceFragment(WeeklyScheduleFragment.class, menuItem);
                break;
            case R.id.nav_settings:
                openActivity(SettingsActivity.class);
                break;
        }

        mDrawer.closeDrawers();
    }

    private void openActivity(Class<? extends AppCompatActivity> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }

    private void replaceFragment(Class<? extends Fragment> fragmentClass, MenuItem menuItem) {
        Fragment fragment = null;

        try {
            fragment = fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();

        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
    }


    protected Fragment createDefaultFragment() {
        if (defaultSchedules.size() == 0) {
            createDefaultSchedules();
        }

        int defaultScheduleValue = settings.getDefaultScheduleValue();

        ScheduleCreator creator = defaultSchedules.get(defaultScheduleValue, () -> ScheduleFragment.newInstance());

        return creator.createDefaultScheduleFragment();
    }
}

interface ScheduleCreator {
    Fragment createDefaultScheduleFragment();
}