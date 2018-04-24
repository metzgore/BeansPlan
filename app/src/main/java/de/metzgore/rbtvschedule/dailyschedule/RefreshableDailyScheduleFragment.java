package de.metzgore.rbtvschedule.dailyschedule;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import de.metzgore.rbtvschedule.baseschedule.RefreshableScheduleFragment;
import de.metzgore.rbtvschedule.data.DailySchedule;
import de.metzgore.rbtvschedule.data.Resource;
import de.metzgore.rbtvschedule.databinding.FragmentDailyScheduleBinding;
import de.metzgore.rbtvschedule.shared.ScheduleRepository;
import de.metzgore.rbtvschedule.util.DateFormatter;
import de.metzgore.rbtvschedule.util.di.ScheduleViewModelFactory;

public class RefreshableDailyScheduleFragment extends RefreshableScheduleFragment {

    private final String TAG = RefreshableDailyScheduleFragment.class.getSimpleName();

    private DailyScheduleAdapter dailyScheduleAdapter;
    private FragmentDailyScheduleBinding binding;
    private DailyScheduleViewModel viewModel;
    private Snackbar snackbar;

    public static Fragment newInstance() {
        return new RefreshableDailyScheduleFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        dailyScheduleAdapter = new DailyScheduleAdapter();

        //TODO dagger
        viewModel = ViewModelProviders.of(this,
                new ScheduleViewModelFactory(new ScheduleRepository(), savedInstanceState == null)).get(DailyScheduleViewModel.class);
        subscribeUi(viewModel);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_daily_schedule, container, false);

        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);

        binding.singleDayIncluded.showsList.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));
        binding.singleDayIncluded.showsList.setItemAnimator(null);
        binding.singleDayIncluded.showsList.setHasFixedSize(true);

        binding.swipeRefresh.setOnRefreshListener(() -> viewModel.loadSchedule(true));
        binding.swipeRefresh.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorPrimary));

        binding.singleDayIncluded.showsList.setAdapter(dailyScheduleAdapter);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() != null)
            getActivity().setTitle(R.string.drawer_item_todays_schedule);
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.loadSchedule(false);
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

    private void subscribeUi(DailyScheduleViewModel viewModel) {
        viewModel.getSchedule().observe(this, schedule -> {
            handleState(schedule);
            handleData(schedule);
        });
    }

    private void handleData(Resource<DailySchedule> schedule) {
        if (schedule.data != null) {
            dailyScheduleAdapter.setShowList(schedule.data.getShows());

            String subTitle = null;
            
            if (schedule.data.getDate() != null)
                subTitle = getString(R.string.fragment_daily_schedule_subtitle, DateFormatter.formatDate(getContext(), schedule.data.getDate()));

            getCallback().onScheduleRefreshed(subTitle);
        }
    }

    private void handleState(Resource<DailySchedule> schedule) {
        switch (schedule.status) {
            case LOADING:
                showRefreshIndicator(true);
                hideSnackbar();
                break;
            case ERROR:
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
                binding.swipeRefresh.setRefreshing(isRefreshing);
            });
        }
    }

    public void showRetrySnackbar() {
        snackbar = Snackbar.make(getView(), R.string.error_message_schedule_general, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.action_retry, view -> {
                    viewModel.loadSchedule(true);
                });
        snackbar.show();
    }

    public void hideSnackbar() {
        if (snackbar != null && snackbar.isShown())
            snackbar.dismiss();
    }
}
