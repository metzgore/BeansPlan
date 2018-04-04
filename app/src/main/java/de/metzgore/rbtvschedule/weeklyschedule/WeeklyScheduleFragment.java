package de.metzgore.rbtvschedule.weeklyschedule;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import de.metzgore.rbtvschedule.R;
import de.metzgore.rbtvschedule.data.Resource;
import de.metzgore.rbtvschedule.data.WeeklySchedule;
import de.metzgore.rbtvschedule.databinding.FragmentWeeklyScheduleBinding;
import de.metzgore.rbtvschedule.shared.ScheduleRepository;
import de.metzgore.rbtvschedule.util.di.WeeklyScheduleViewModelFactory;

public class WeeklyScheduleFragment extends Fragment {

    private static final String TAG = WeeklyScheduleFragment.class.getSimpleName();

    //private static final String VIEW_PAGER_ITEM = "view_pager_item";

    private WeeklyScheduleAdapter weeklyScheduleAdapter;
    private FragmentWeeklyScheduleBinding binding;
    private Snackbar snackbar;
    private int mCurrentViewPagerItem;
    private WeeklyScheduleViewModel viewModel;
    private boolean scheduleContainsCurrentDay;

    public static Fragment newInstance() {
        return new WeeklyScheduleFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        weeklyScheduleAdapter = new WeeklyScheduleAdapter(getContext(), getChildFragmentManager());

        //TODO dagger
        viewModel = ViewModelProviders.of(this, new WeeklyScheduleViewModelFactory(new ScheduleRepository())).get(WeeklyScheduleViewModel.class);
        subscribeUi(viewModel);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_weekly_schedule, container, false);

        binding.fragmentWeeklyScheduleViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        binding.fragmentWeeklyScheduleViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.fragmentWeeklyScheduleTabs) {
            @Override
            public void onPageScrolled(int position, float v, int i1) {
                //NOOP
            }

            @Override
            public void onPageSelected(int position) {
                mCurrentViewPagerItem = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                enableDisableSwipeRefresh(state == ViewPager.SCROLL_STATE_IDLE);
            }
        });

        binding.fragmentWeeklyScheduleViewPager.setAdapter(weeklyScheduleAdapter);
        binding.fragmentWeeklyScheduleViewPager.setOffscreenPageLimit(3);

        binding.fragmentWeeklyScheduleSwipeRefresh.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        binding.fragmentWeeklyScheduleSwipeRefresh.setOnRefreshListener(() -> {
            viewModel.loadSchedule(true);
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() != null)
            getActivity().setTitle(R.string.drawer_item_weekly_schedule);
    }

   /*@Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            mCurrentViewPagerItem = savedInstanceState.getInt(VIEW_PAGER_ITEM, 0);
        }
    }    */

    @Override
    public void onStart() {
        super.onStart();
        viewModel.loadSchedule(weeklyScheduleAdapter.getCount() == 0);
    }

    /*@Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(VIEW_PAGER_ITEM, mWeeklyScheduleViewPager.getCurrentItem());
    }  */

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (!weeklyScheduleAdapter.containsScheduleForCurrentDay())
            menu.removeItem(R.id.action_today);
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
                viewModel.loadSchedule(true);
                return true;
            case R.id.action_today:
                goToCurrentDay();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
            handleState(schedule);
            handleData(schedule);
        });
    }

    private void handleData(Resource<WeeklySchedule> schedule) {
        if (schedule.data != null) {
            binding.setIsEmpty(false);
            weeklyScheduleAdapter.setSchedule(schedule.data);
            boolean containsCurrentDay = weeklyScheduleAdapter.containsScheduleForCurrentDay();

            if (getActivity() != null && containsCurrentDay != scheduleContainsCurrentDay) {
                scheduleContainsCurrentDay = containsCurrentDay;
                getActivity().invalidateOptionsMenu();
            }

            //workaround for weird tab updating
            //see https://stackoverflow.com/a/43436472/5925185
            new Handler().postDelayed(() -> {
                final TabLayout.Tab selectedTab = binding.fragmentWeeklyScheduleTabs.getTabAt(
                        binding.fragmentWeeklyScheduleTabs.getSelectedTabPosition());
                if (selectedTab != null) {
                    selectedTab.select();
                }
            }, 100);
        } else {
            binding.setIsEmpty(true);
        }
    }

    private void handleState(Resource<WeeklySchedule> schedule) {
        switch (schedule.status) {
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
            binding.fragmentWeeklyScheduleSwipeRefresh.post(() -> {
                //TODO lambda
                binding.fragmentWeeklyScheduleSwipeRefresh.setRefreshing(isRefreshing);
            });
        }
    }

    public void showRetrySnackbar() {
        snackbar = Snackbar.make(getView(), R.string.error_message_schedule_general, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.action_retry, view -> {
                    //TODO lambda
                    viewModel.loadSchedule(true);
                });
        snackbar.show();
    }

    public void hideSnackbar() {
        if (snackbar != null && snackbar.isShown())
            snackbar.dismiss();
    }

    /*@Override
    public void showCurrentDay(int idxOfCurrentDay) {
        if (idxOfCurrentDay != mCurrentViewPagerItem)
            mCurrentViewPagerItem = idxOfCurrentDay;

        mWeeklyScheduleViewPager.setCurrentItem(mCurrentViewPagerItem);
    }  */

    private class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
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
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

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
