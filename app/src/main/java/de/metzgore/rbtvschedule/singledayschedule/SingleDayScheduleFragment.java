package de.metzgore.rbtvschedule.singledayschedule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.metzgore.rbtvschedule.R;
import de.metzgore.rbtvschedule.dailyschedule.ScheduleAdapter;
import de.metzgore.rbtvschedule.data.Show;

public class SingleDayScheduleFragment extends Fragment implements SingleDayScheduleContract.View {

    private static final String TAG = SingleDayScheduleFragment.class.getSimpleName();

    private static final String ARG_SCHEDULE = "arg_schedule";

    //@BindView(R.id.fragment_daily_schedule_recycler_view)
    RecyclerView mDailyScheduleRecyclerView;

    private List<Show> mSchedule;

    private ScheduleAdapter mScheduleAdapter;

    private SingleDaySchedulePresenter mActionsListener;

    private Unbinder mUnbinder;

    public static Fragment newInstance(List<Show> shows) {
        SingleDayScheduleFragment fragment = new SingleDayScheduleFragment();

        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_SCHEDULE, (ArrayList) shows);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScheduleAdapter = new ScheduleAdapter();

        Bundle args = getArguments();

        //mSchedule = args.getParcelableArrayList(ARG_SCHEDULE);

        mActionsListener = new SingleDaySchedulePresenter(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_single_day_schedule, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        mDailyScheduleRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));
        mDailyScheduleRecyclerView.setAdapter(mScheduleAdapter);
        mDailyScheduleRecyclerView.setHasFixedSize(true);
        mDailyScheduleRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mActionsListener.loadDailySchedule(mSchedule);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void showSchedule(List<Show> schedule) {
        // mScheduleAdapter.setSchedule(schedule);
        // mDailyScheduleRecyclerView.smoothScrollToPosition(mScheduleAdapter.getIdxOfCurrentShow());
    }

}
