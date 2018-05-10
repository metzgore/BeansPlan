package de.metzgore.beansplan.dailyschedule;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;

import de.metzgore.beansplan.R;
import de.metzgore.beansplan.data.DailySchedule;
import de.metzgore.beansplan.data.Resource;
import de.metzgore.beansplan.databinding.LayoutScheduleBaseBinding;
import de.metzgore.beansplan.shared.UpdatableScheduleFragment;
import de.metzgore.beansplan.util.di.DailyScheduleViewModelFactory;

public class DailyScheduleFragment extends Fragment implements UpdatableScheduleFragment {

    private static final String TAG = DailyScheduleFragment.class.getSimpleName();

    private static final String ARG_DATE = "arg_date";
    private static final String ARG_SCHEDULE = "arg_schedule";

    private Date dateKey;
    private DailyScheduleAdapter dailyScheduleAdapter;
    private DailyScheduleViewModel viewModel;

    public static Fragment newInstance(Date date, DailySchedule dailySchedule) {
        DailyScheduleFragment fragment = new DailyScheduleFragment();

        Bundle args = new Bundle();
        args.putLong(ARG_DATE, date.getTime());
        args.putParcelable(ARG_SCHEDULE, dailySchedule);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dailyScheduleAdapter = new DailyScheduleAdapter();

        DailySchedule dailySchedule;
        Bundle args = getArguments();

        if (args != null) {
            dateKey = new Date(args.getLong(ARG_DATE));
            dailySchedule = args.getParcelable(ARG_SCHEDULE);
        } else {
            dateKey = new Date();
            dailySchedule = new DailySchedule();
        }

        viewModel = ViewModelProviders.of(this,
                new DailyScheduleViewModelFactory(dailySchedule)).get(DailyScheduleViewModel.class);

        subscribeUi(viewModel);
    }

    private void subscribeUi(DailyScheduleViewModel viewModel) {
        viewModel.getSchedule().observe(this, schedule -> dailyScheduleAdapter.setShowList(schedule.getData().getShows()));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        LayoutScheduleBaseBinding binding = DataBindingUtil.inflate(inflater, R.layout.layout_schedule_base, container, false);

        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        binding.showsList.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));
        binding.showsList.setItemAnimator(null);
        binding.showsList.setHasFixedSize(true);
        binding.showsList.setAdapter(dailyScheduleAdapter);

        return binding.getRoot();
    }

    @Override
    public void update(DailySchedule dailySchedule) {
        if (dailySchedule != null)
            viewModel.setSchedule(Resource.Companion.success(dailySchedule, false));
    }

    @Override
    public Date getDateKey() {
        return dateKey;
    }
}
