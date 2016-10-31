package de.metzgore.rbtvschedule.dailyschedule;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.DividerItemDecoration;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.time.DurationFormatUtils;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.metzgore.rbtvschedule.R;
import de.metzgore.rbtvschedule.data.Schedule;
import de.metzgore.rbtvschedule.data.Show;

public class DailyScheduleFragment extends Fragment implements DailyScheduleContract.View {

    private static final String TAG = DailyScheduleFragment.class.getSimpleName();

    @BindView(R.id.fragment_daily_schedule_recycler_view)
    RecyclerView mDailyScheduleRecyclerView;
    @BindView(R.id.fragment_daily_schedule_swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private Snackbar mErrorSnackbar;

    private ScheduleAdapter mScheduleAdapter;

    private DailySchedulePresenter mActionsListener;

    private Unbinder mUnbinder;

    public static Fragment newInstance() {
        return  new DailyScheduleFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActionsListener = new DailySchedulePresenter(this);
        mScheduleAdapter = new ScheduleAdapter();

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_schedule, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        mDailyScheduleRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));
        mDailyScheduleRecyclerView.setAdapter(mScheduleAdapter);
        mDailyScheduleRecyclerView.setHasFixedSize(true);
        mDailyScheduleRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mActionsListener.loadDailySchedule();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        mActionsListener.loadDailySchedule();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_daily_schedule, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                mActionsListener.loadDailySchedule();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void showSchedule(Schedule schedule) {
        mScheduleAdapter.setSchedule(schedule);
        mDailyScheduleRecyclerView.smoothScrollToPosition(mScheduleAdapter.getIdxOfCurrentShow());
    }

    @Override
    public void showRefreshIndicator(boolean isRefreshing) {
        mSwipeRefreshLayout.setRefreshing(isRefreshing);
    }

    @Override
    public void showRetrySnackbar(int messageId) {
        mErrorSnackbar = Snackbar.make(getView(), messageId, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.action_retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mActionsListener.loadDailySchedule();
                    }
                });
        mErrorSnackbar.show();
    }

    @Override
    public void hideSnackbar() {
        if (mErrorSnackbar != null && mErrorSnackbar.isShown())
            mErrorSnackbar.dismiss();
    }

    protected class ShowHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.list_item_show_title_text_view)
        TextView mTitleTextView;
        @BindView(R.id.list_item_show_topic_text_view)
        TextView mTopicTextView;
        @BindView(R.id.list_item_show_start_text_view)
        TextView mStartTextView;
        @BindView(R.id.list_item_show_time_dash)
        TextView mShowTimeDash;
        @BindView(R.id.list_item_show_end_text_view)
        TextView mEndTextView;
        @BindView(R.id.list_item_show_length_text_view)
        TextView mLengthTextView;
        @BindView(R.id.list_item_show_type_text_view)
        TextView mTypeTextView;
        @BindView(R.id.list_item_show_base)
        LinearLayout mBase;

        Drawable mNowPlaying = ContextCompat.getDrawable(getActivity(), R.drawable.ic_videocam);

        private Show mShow;

        private SimpleDateFormat mTimeFormat = new SimpleDateFormat("HH:mm");

        ShowHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindView(Show show) {
            mShow = show;

            mTitleTextView.setText(mShow.getTitle());
            mTopicTextView.setText(mShow.getTopic());
            mStartTextView.setText(mTimeFormat.format(mShow.getTimeStart()));
            mEndTextView.setText(mTimeFormat.format(mShow.getTimeEnd()));
            mLengthTextView.setText(DurationFormatUtils.formatDuration(mShow.getLength() * 1000,
                    mShow.getLength() > 3600 ? "H 'h' mm 'min'" : "m 'min'"));

            switch (mShow.getType()) {
                case LIVE:
                case PREMIERE:
                    mTypeTextView.setText(mShow.getType().toString());
                    break;
                case NONE:
                default:
                    mTypeTextView.setText("");
                    break;
            }

            if (mShow.isCurrentlyRunning()) {
                //padding is gone on api level < 21
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    int paddingTop = mBase.getPaddingTop();
                    int paddingLeft = mBase.getPaddingLeft();
                    int paddingRight = mBase.getPaddingRight();
                    int paddingBottom = mBase.getPaddingBottom();
                    mBase.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.border_current_show));
                    mBase.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
                } else {
                    mBase.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.border_current_show));
                }
                mTitleTextView.setCompoundDrawablesWithIntrinsicBounds(mNowPlaying, null, null, null);
            } else {
                mBase.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.white));
                mBase.getBackground().setAlpha(100);
                mTitleTextView.setCompoundDrawables(null, null, null, null);
            }

            setAllViewsEnabled(mShow.wasAlreadyShown());
        }

        private void setAllViewsEnabled(boolean enabled) {
            mTitleTextView.setEnabled(enabled);
            mTopicTextView.setEnabled(enabled);
            mStartTextView.setEnabled(enabled);
            mShowTimeDash.setEnabled(enabled);
            mEndTextView.setEnabled(enabled);
            mLengthTextView.setEnabled(enabled);
            mTypeTextView.setEnabled(enabled);
        }
    }

    private class ScheduleAdapter extends RecyclerView.Adapter<ShowHolder> {

        private Schedule mSchedule = new Schedule();

        @Override
        public ShowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_item_show, parent, false);
            return new ShowHolder(view);
        }

        @Override
        public void onBindViewHolder(ShowHolder holder, int position) {
            holder.bindView(mSchedule.getShows().get(position));
        }

        @Override
        public int getItemCount() {
            return mSchedule.getShows().size();
        }

        void setSchedule(Schedule schedule) {
            mSchedule = schedule;
            notifyDataSetChanged();
        }

        int getIdxOfCurrentShow() {
            return mSchedule.getIdxOfCurrentShow();
        }
    }
}
