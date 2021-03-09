package de.metzgore.beansplan.weeklyschedule;

import android.content.Context;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import de.metzgore.beansplan.R;
import de.metzgore.beansplan.baseschedule.BaseFragment;
import de.metzgore.beansplan.data.Resource;
import de.metzgore.beansplan.data.Status;
import de.metzgore.beansplan.data.room.relations.WeeklyScheduleWithDailySchedules;
import de.metzgore.beansplan.databinding.FragmentWeeklyScheduleBinding;
import de.metzgore.beansplan.util.BadgeDrawableUtil;
import de.metzgore.beansplan.util.DateFormatter;
import de.metzgore.beansplan.util.di.WeeklyScheduleViewModelFactory;

public class WeeklyScheduleFragment extends BaseFragment {

    private static final String TAG = WeeklyScheduleFragment.class.getSimpleName();

    private static final String SELECTED_DATE_TIMESTAMP = "selected_date_timestamp";

    private WeeklySchedulePagerAdapter weeklyScheduleAdapter;
    private FragmentWeeklyScheduleBinding binding;
    private Snackbar snackbar;
    private Date selectedDate;
    private WeeklyScheduleViewModel viewModel;
    private boolean scheduleContainsCurrentDay;
    private int dayOfMonth = 0;

    @Inject
    WeeklyScheduleRepository repo;

    public static Fragment newInstance() {
        return new WeeklyScheduleFragment();
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            selectedDate = new Date(savedInstanceState.getLong(SELECTED_DATE_TIMESTAMP));
        } else {
            selectedDate = new Date();
        }

        dayOfMonth = Calendar.getInstance(Locale.getDefault()).get(Calendar.DAY_OF_MONTH);

        setHasOptionsMenu(true);

        weeklyScheduleAdapter = new WeeklySchedulePagerAdapter(getContext(), getChildFragmentManager());

        viewModel = ViewModelProviders.of(this, new WeeklyScheduleViewModelFactory(repo)).get(WeeklyScheduleViewModel.class);
        subscribeUi(viewModel);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_weekly_schedule, container, false);

        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);

        binding.fragmentWeeklyScheduleViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        binding.fragmentWeeklyScheduleViewPager.setAdapter(weeklyScheduleAdapter);
        //TODO check if there is a better solution
        binding.fragmentWeeklyScheduleViewPager.setSaveFromParentEnabled(false);
        binding.fragmentWeeklyScheduleViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding
                .fragmentWeeklyScheduleTabs) {
            @Override
            public void onPageScrollStateChanged(int state) {
                enableDisableSwipeRefresh(binding.fragmentWeeklyScheduleSwipeRefresh.isRefreshing() || state == ViewPager
                        .SCROLL_STATE_IDLE);
            }

            @Override
            public void onPageSelected(int position) {
                selectedDate = weeklyScheduleAdapter.getDayFromPosition(position);
            }
        });

        binding.fragmentWeeklyScheduleSwipeRefresh.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        binding.fragmentWeeklyScheduleSwipeRefresh.setOnRefreshListener(() -> viewModel.loadScheduleFromNetwork());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() != null)
            getActivity().setTitle(R.string.drawer_item_weekly_schedule);
    }

    @Override
    public void onStart() {
        super.onStart();

        int currentDay = Calendar.getInstance(Locale.getDefault()).get(Calendar.DAY_OF_MONTH);

        if (getActivity() != null && currentDay != dayOfMonth) {
            dayOfMonth = currentDay;
            getActivity().invalidateOptionsMenu();
        }

        viewModel.loadSchedule();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (selectedDate != null)
            bundle.putLong(SELECTED_DATE_TIMESTAMP, selectedDate.getTime());
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (!weeklyScheduleAdapter.containsScheduleForCurrentDay())
            menu.removeItem(R.id.action_today);
        else {
            MenuItem item = menu.findItem(R.id.action_today);
            LayerDrawable icon = (LayerDrawable) item.getIcon();

            BadgeDrawableUtil.INSTANCE.setNumber(getContext(), icon, dayOfMonth);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_weekly_schedule, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                viewModel.loadScheduleFromNetwork();
                return true;
            case R.id.action_today:
                goToCurrentDay();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hideSnackbar();
    }

    private void goToCurrentDay() {
        int positionOfCurrentDay = weeklyScheduleAdapter.getPositionOfCurrentDay();
        if (positionOfCurrentDay >= 0 && positionOfCurrentDay < weeklyScheduleAdapter.getCount()) {
            binding.fragmentWeeklyScheduleViewPager.setCurrentItem(positionOfCurrentDay);
        } else {
            Toast.makeText(getContext(), R.string.error_message_no_day_found, Toast.LENGTH_LONG).show();
        }
    }

    private void subscribeUi(WeeklyScheduleViewModel viewModel) {
        viewModel.getSchedule().observe(this, schedule -> {
            if (schedule != null) {
                handleState(schedule.getStatus());
                handleData(schedule);
            }
        });
    }

    private void handleData(Resource<WeeklyScheduleWithDailySchedules> schedule) {
        if (schedule.getData() != null) {
            getOnAppBarUpdatedListenerr().onRemoveToolbarElevation();

            weeklyScheduleAdapter.setSchedule(schedule.getData().getDailySchedulesWithShows());
            binding.fragmentWeeklyScheduleTabs.notifyDataSetChanged();
            binding.fragmentWeeklyScheduleViewPager.setCurrentItem(weeklyScheduleAdapter.getPositionFromDate(selectedDate));

            if (schedule.getData().getStartDate() != null && schedule.getData().getEndDate() != null) {
                String subTitle = getString(R.string.hyphen_separated_text, DateFormatter.formatDate(getContext(), schedule
                        .getData().getStartDate()), DateFormatter.formatDate(getContext(), schedule.getData().getEndDate()));

                getOnScheduleRefreshedListener().onSubTitleUpdated(subTitle);
            }

            getOnScheduleRefreshedListener().onLastUpdateUpdated(schedule.getData().weeklySchedule.getTimestamp());

            boolean containsCurrentDay = weeklyScheduleAdapter.containsScheduleForCurrentDay();

            if (getActivity() != null && containsCurrentDay != scheduleContainsCurrentDay) {
                scheduleContainsCurrentDay = containsCurrentDay;
                getActivity().invalidateOptionsMenu();
            }
        }
    }

    private void handleState(Status status) {
        switch (status) {
            case LOADING:
                showRefreshIndicator(true);
                hideSnackbar();
                break;
            case ERROR:
                showRefreshIndicator(false);
                showRetrySnackbar();
                break;
            case SUCCESS:
                showRefreshIndicator(false);
                hideSnackbar();
                break;
        }
    }

    public void showRefreshIndicator(final boolean isRefreshing) {
        if (binding != null) {
            binding.fragmentWeeklyScheduleSwipeRefresh.post(() -> binding.fragmentWeeklyScheduleSwipeRefresh.setRefreshing(isRefreshing));
        }
    }

    public void showRetrySnackbar() {
        snackbar = Snackbar.make(getView(), R.string.error_message_weekly_schedule_loading_failed, Snackbar.LENGTH_INDEFINITE).setAction
                (R.string.action_retry, view -> viewModel.loadScheduleFromNetwork());
        snackbar.show();
    }

    public void hideSnackbar() {
        if (snackbar != null && snackbar.isShown())
            snackbar.dismiss();
    }

    private class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.95f;
        private static final float MIN_ALPHA = 0.75f;

        public void transformPage(@NonNull View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

    private void enableDisableSwipeRefresh(boolean enable) {
        if (binding.fragmentWeeklyScheduleSwipeRefresh != null) {
            binding.fragmentWeeklyScheduleSwipeRefresh.setEnabled(enable);
        }
    }
}
