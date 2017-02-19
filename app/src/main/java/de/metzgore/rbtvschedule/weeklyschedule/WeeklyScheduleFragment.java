package de.metzgore.rbtvschedule.weeklyschedule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.metzgore.rbtvschedule.R;
import de.metzgore.rbtvschedule.data.Schedule;

public class WeeklyScheduleFragment extends Fragment implements WeeklyScheduleContract.View {

    private static final String TAG = WeeklyFragmentPresenter.class.getSimpleName();

    private static final String VIEW_PAGER_ITEM = "view_pager_item";

    @BindView(R.id.fragment_weekly_schedule_view_pager)
    ViewPager mWeeklyScheduleViewPager;
    @BindView(R.id.fragment_weekly_schedule_swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.fragment_weekly_schedule_empty_view)
    TextView mEmptyView;
    @BindView(R.id.fragment_weekly_schedule_tabs)
    TabLayout mWeeklyScheduleTabs;
    @BindView(R.id.fragment_weekly_schedule_base)
    LinearLayout mWeeklyScheduleBase;
    @BindView(R.id.fragment_weekly_schedule_toolbar)
    Toolbar mWeeklyScheduleToolbar;

    private WeeklyScheduleAdapter mWeeklyScheduleAdapter;

    private Snackbar mErrorSnackbar;

    private WeeklyFragmentPresenter mActionsListener;

    private Unbinder mUnbinder;

    private int mCurrentViewPagerItem;

    public static Fragment newInstance() {
        return  new WeeklyScheduleFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActionsListener = new WeeklyFragmentPresenter(this);
        mWeeklyScheduleAdapter = new WeeklyScheduleAdapter(getContext(), getChildFragmentManager());

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weekly_schedule, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mWeeklyScheduleToolbar);

        mWeeklyScheduleViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mWeeklyScheduleViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mWeeklyScheduleTabs) {
            @Override
            public void onPageScrolled(int position, float v, int i1) {
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

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mActionsListener.loadWeeklySchedule();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            mCurrentViewPagerItem = savedInstanceState.getInt(VIEW_PAGER_ITEM, 0);
        }

        mActionsListener.loadWeeklySchedule();
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(VIEW_PAGER_ITEM, mWeeklyScheduleViewPager.getCurrentItem());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
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
                mActionsListener.loadWeeklySchedule();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void showSchedule(Schedule schedule) {
        mWeeklyScheduleBase.setVisibility(View.VISIBLE);
        mWeeklyScheduleAdapter.setSchedule(schedule);
        mWeeklyScheduleViewPager.setAdapter(mWeeklyScheduleAdapter);
        mWeeklyScheduleTabs.setupWithViewPager(mWeeklyScheduleViewPager);
        mWeeklyScheduleViewPager.setCurrentItem(mCurrentViewPagerItem);
    }

    @Override
    public void showRetrySnackbar(int messageId) {
        mErrorSnackbar = Snackbar.make(getView(), messageId, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.action_retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mActionsListener.loadWeeklySchedule();
                    }
                });
        mErrorSnackbar.show();
    }

    @Override
    public void hideSnackbar() {
        if (mErrorSnackbar != null && mErrorSnackbar.isShown())
            mErrorSnackbar.dismiss();
    }

    @Override
    public void showRefreshIndicator(final boolean isRefreshing) {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(isRefreshing);
            }
        });
    }

    @Override
    public void showEmptyView(boolean visible) {
        mEmptyView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

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
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setEnabled(enable);
        }
    }
}
