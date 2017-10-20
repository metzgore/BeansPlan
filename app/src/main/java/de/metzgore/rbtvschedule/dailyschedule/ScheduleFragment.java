package de.metzgore.rbtvschedule.dailyschedule;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.metzgore.rbtvschedule.R;

public class ScheduleFragment extends Fragment {

    private final String TAG = ScheduleFragment.class.getSimpleName();

    private ScheduleViewModel viewModel;

    public static Fragment newInstance() {
        return new ScheduleFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(ScheduleViewModel.class);
        viewModel.init();
        viewModel.getSchedule().observe(this, schedule -> {

        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_single_day_schedule, container, false);
    };
}
