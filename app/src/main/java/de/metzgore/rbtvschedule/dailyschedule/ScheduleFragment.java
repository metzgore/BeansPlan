package de.metzgore.rbtvschedule.dailyschedule;

import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import de.metzgore.rbtvschedule.R;
import de.metzgore.rbtvschedule.data.Resource;
import de.metzgore.rbtvschedule.data.Schedule;
import de.metzgore.rbtvschedule.databinding.FragmentSingleDayScheduleBinding;
import de.metzgore.rbtvschedule.shared.ScheduleRepository;
import de.metzgore.rbtvschedule.util.di.ScheduleViewModelFactory;

public class ScheduleFragment extends Fragment {

    private final String TAG = ScheduleFragment.class.getSimpleName();

    private ScheduleAdapter scheduleAdapter;
    private FragmentSingleDayScheduleBinding binding;
    private ScheduleViewModel viewModel;
    private Snackbar snackbar;

    public static Fragment newInstance() {
        return new ScheduleFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        scheduleAdapter = new ScheduleAdapter();

        //TODO dagger
        viewModel = ViewModelProviders.of(this, new ScheduleViewModelFactory(new ScheduleRepository())).get(ScheduleViewModel.class);
        subscribeUi(viewModel);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_single_day_schedule, container, false);

        binding.singleDayIncluded.showsList.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));
        binding.singleDayIncluded.showsList.setItemAnimator(null);
        binding.singleDayIncluded.showsList.setHasFixedSize(true);

        binding.swipeRefresh.setOnRefreshListener(() -> {
            //TODO lambda
            viewModel.loadSchedule(true);
        });
        binding.swipeRefresh.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorPrimary));

        binding.singleDayIncluded.showsList.setAdapter(scheduleAdapter);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.loadSchedule(scheduleAdapter.getItemCount() == 0);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        viewModel.loadSchedule(scheduleAdapter.getItemCount() == 0);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_schedule, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                viewModel.loadSchedule(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void subscribeUi(ScheduleViewModel viewModel) {
        viewModel.getSchedule().observe(this, schedule -> {
            handleState(schedule);
            handleData(schedule);
        });
    }

    private void handleData(Resource<Schedule> schedule) {
        if (schedule.data != null) {
            binding.singleDayIncluded.setHadError(false);
            scheduleAdapter.setShowList(schedule.data.getShows());
        }
    }

    private void handleState(Resource<Schedule> schedule) {
        switch (schedule.status) {
            case LOADING:
                showRefreshIndicator(true);
                hideSnackbar();
                break;
            case ERROR:
                binding.singleDayIncluded.setHadError(true);
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
                    //TODO lambda
                    viewModel.loadSchedule(true);
                });
        snackbar.show();
    }

    public void hideSnackbar() {
        if (snackbar != null && snackbar.isShown())
            snackbar.dismiss();
    }
}
