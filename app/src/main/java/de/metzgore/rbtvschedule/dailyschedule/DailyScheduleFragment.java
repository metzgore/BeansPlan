package de.metzgore.rbtvschedule.dailyschedule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_schedule, container, false);
        mUnbinder = ButterKnife.bind(this, view);

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mActionsListener.loadDailySchedule();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void showSchedule(Schedule schedule) {
        mScheduleAdapter.setSchedule(schedule);
    }

    @Override
    public void showRefreshIndicator(boolean isRefreshing) {
        mSwipeRefreshLayout.setRefreshing(isRefreshing);
    }

    protected class ShowHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.list_item_show_title_text_view)
        TextView mTitleTextView;

        private Show mShow;

        ShowHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindView(Show show) {
            mShow = show;

            mTitleTextView.setText(mShow.getTitle());
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
    }
}
