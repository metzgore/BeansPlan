package de.metzgore.rbtvschedule.dailyschedule;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.metzgore.rbtvschedule.R;
import de.metzgore.rbtvschedule.data.Schedule;
import de.metzgore.rbtvschedule.databinding.FragmentSingleDayScheduleBinding;
import de.metzgore.rbtvschedule.util.di.ScheduleViewModelFactory;

public class ScheduleFragment extends Fragment {

    private final String TAG = ScheduleFragment.class.getSimpleName();

    private ScheduleAdapter scheduleAdapter;

    private FragmentSingleDayScheduleBinding binding;

    private ScheduleViewModel viewModel;

    private ScheduleRepository repo;

    public static Fragment newInstance() {
        return new ScheduleFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_single_day_schedule, container, false);

        scheduleAdapter = new ScheduleAdapter();
        binding.showsList.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));

        binding.showsList.setAdapter(scheduleAdapter);
        binding.showsList.setItemAnimator(null);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        repo = new ScheduleRepository();
        viewModel = ViewModelProviders.of(this, new ScheduleViewModelFactory(repo)).get(ScheduleViewModel.class);
        subscribeUi(viewModel);
    }

    private void subscribeUi(ScheduleViewModel viewModel) {
        viewModel.getSchedule().observe(this, new Observer<Schedule>() {

            @Override
            public void onChanged(@Nullable Schedule schedule) {
                if (schedule != null) {
                    binding.setIsLoading(false);
                    scheduleAdapter.setShowList(schedule.getShows());
                } else {
                    binding.setIsLoading(true);
                }

                binding.executePendingBindings();
            }
        });
    }
}
