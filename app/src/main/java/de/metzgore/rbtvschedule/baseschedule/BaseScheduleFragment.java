package de.metzgore.rbtvschedule.baseschedule;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.metzgore.rbtvschedule.R;
import de.metzgore.rbtvschedule.dailyschedule.DailyScheduleAdapter;
import de.metzgore.rbtvschedule.data.Show;
import de.metzgore.rbtvschedule.data.WeeklySchedule;
import de.metzgore.rbtvschedule.databinding.LayoutScheduleBaseBinding;
import de.metzgore.rbtvschedule.shared.UpdatableScheduleFragment;

public class BaseScheduleFragment extends Fragment implements UpdatableScheduleFragment {

    private static final String TAG = BaseScheduleFragment.class.getSimpleName();

    private static final String ARG_SCHEDULE = "arg_schedule";
    private static final String ARG_DATE = "arg_date";

    public OnScheduleUpdatedListener callback;

    private Date dateKey;
    private List<Show> showList;
    private DailyScheduleAdapter dailyScheduleAdapter;

    public static Fragment newInstance(Date date, List<Show> shows) {
        BaseScheduleFragment fragment = new BaseScheduleFragment();

        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_SCHEDULE, (ArrayList) shows);
        args.putLong(ARG_DATE, date.getTime());

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            callback = (OnScheduleUpdatedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dailyScheduleAdapter = new DailyScheduleAdapter();

        Bundle args = getArguments();

        if (args != null) {
            showList = args.getParcelableArrayList(ARG_SCHEDULE);
            dateKey = new Date(args.getLong(ARG_DATE));
        } else {
            showList = new ArrayList<>();
            dateKey = new Date();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        LayoutScheduleBaseBinding binding = DataBindingUtil.inflate(inflater, R.layout.layout_schedule_base, container, false);

        binding.showsList.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));
        binding.showsList.setItemAnimator(null);
        binding.showsList.setHasFixedSize(true);
        binding.showsList.setAdapter(dailyScheduleAdapter);
        dailyScheduleAdapter.setShowList(showList);

        return binding.getRoot();
    }

    @Override
    public void update(WeeklySchedule weeklySchedule) {
        List<Show> shows = weeklySchedule.getSchedule().get(dateKey);
        if (shows != null)
            dailyScheduleAdapter.setShowList(shows);
    }

    @NonNull
    public OnScheduleUpdatedListener getCallback() {
        return callback;
    }

    public interface OnScheduleUpdatedListener {
        void onScheduleUpdated(String subtitle);
    }
}
