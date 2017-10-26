package de.metzgore.rbtvschedule.dailyschedule;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.metzgore.rbtvschedule.R;
import de.metzgore.rbtvschedule.data.Resource;
import de.metzgore.rbtvschedule.data.Schedule;
import de.metzgore.rbtvschedule.databinding.FragmentSingleDayScheduleBinding;
import de.metzgore.rbtvschedule.util.di.ScheduleViewModelFactory;

public class ScheduleFragment extends Fragment {

    private final String TAG = ScheduleFragment.class.getSimpleName();

    private ScheduleAdapter scheduleAdapter;

    private FragmentSingleDayScheduleBinding binding;

    private ScheduleViewModel viewModel;

    private ScheduleRepository repo;

    private Snackbar snackbar;

    public static Fragment newInstance() {
        return new ScheduleFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_single_day_schedule, container, false);

        binding.showsList.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));
        binding.showsList.setItemAnimator(null);

        binding.swipeRefresh.setOnRefreshListener(() -> {
            loadSchedule(true);
        });

        scheduleAdapter = new ScheduleAdapter();
        binding.showsList.setAdapter(scheduleAdapter);


        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        repo = new ScheduleRepository();
        viewModel = ViewModelProviders.of(this, new ScheduleViewModelFactory(repo)).get(ScheduleViewModel.class);
        loadSchedule(true);
        subscribeUi(viewModel);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void loadSchedule(boolean forceNetworkRefresh) {
        viewModel.loadSchedule(forceNetworkRefresh);
    }

    private void subscribeUi(ScheduleViewModel viewModel) {
        viewModel.getSchedule().observe(this, schedule -> {
            handleState(schedule);
            handleData(schedule);
        });
    }

    private void handleData(Resource<Schedule> schedule) {
        if (schedule.data != null) {
            binding.setHadError(false);
            scheduleAdapter.setShowList(schedule.data.getShows());
        }
    }

    private void handleState(Resource<Schedule> schedule) {
        Log.d(TAG, schedule.status.toString());
        switch (schedule.status) {
            case LOADING:
                showRefreshIndicator(true);
                hideSnackbar();
                break;
            case ERROR:
                binding.setHadError(true);
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
            binding.swipeRefresh.post(() -> {
                //TODO lambda
                binding.swipeRefresh.setRefreshing(isRefreshing);
            });
        }
    }

    public void showRetrySnackbar() {
        snackbar = Snackbar.make(getView(), R.string.error_message_schedule_general, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.action_retry, view -> {
                    loadSchedule(true);
                });
        snackbar.show();
    }

    public void hideSnackbar() {
        if (snackbar != null && snackbar.isShown())
            snackbar.dismiss();
    }
}
