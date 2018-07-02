package de.metzgore.beansplan.dailyschedule;

import android.arch.lifecycle.ViewModelProviders;
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

import java.util.Date;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import de.metzgore.beansplan.R;
import de.metzgore.beansplan.data.room.DailyScheduleWithShows;
import de.metzgore.beansplan.databinding.LayoutScheduleBaseBinding;
import de.metzgore.beansplan.shared.ScheduleRepository;
import de.metzgore.beansplan.shared.UpdatableScheduleFragment;
import de.metzgore.beansplan.util.di.DailyScheduleViewModelFactory;

public class DailyScheduleFragment extends Fragment implements UpdatableScheduleFragment {

    private static final String TAG = DailyScheduleFragment.class.getSimpleName();

    private static final String ARG_DATE = "arg_date";

    private Date dateKey;
    private DailyScheduleAdapter dailyScheduleAdapter;
    private DailyScheduleViewModel viewModel;

    @Inject
    ScheduleRepository<DailyScheduleWithShows> repo;

    public static Fragment newInstance(Date date) {
        DailyScheduleFragment fragment = new DailyScheduleFragment();

        Bundle args = new Bundle();
        args.putLong(ARG_DATE, date.getTime());

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dailyScheduleAdapter = new DailyScheduleAdapter();

        Bundle args = getArguments();

        if (args != null) {
            dateKey = new Date(args.getLong(ARG_DATE));
        } else {
            dateKey = new Date();
        }

        viewModel = ViewModelProviders.of(this, new DailyScheduleViewModelFactory(repo)).get
                (DailyScheduleViewModel.class);

        subscribeUi(viewModel);
    }

    private void subscribeUi(DailyScheduleViewModel viewModel) {
        viewModel.getSchedule().observe(this, schedule -> {
            if (schedule != null) {
                dailyScheduleAdapter.setShowList(schedule.sortedShows());
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        LayoutScheduleBaseBinding binding = DataBindingUtil.inflate(inflater, R.layout
                .layout_schedule_base, container, false);

        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        binding.fragmentBaseScheduleShowsList.addItemDecoration(new DividerItemDecoration
                (getActivity(), DividerItemDecoration.VERTICAL));
        binding.fragmentBaseScheduleShowsList.setHasFixedSize(true);

        binding.fragmentBaseScheduleShowsList.setAdapter(dailyScheduleAdapter);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.loadSchedule(dateKey);
    }

    @Override
    public void update(@NonNull Date date) {
        viewModel.loadSchedule(date);
    }

    @Override
    public Date getDateKey() {
        return dateKey;
    }
}
