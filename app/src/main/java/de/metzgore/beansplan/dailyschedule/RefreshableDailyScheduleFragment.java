package de.metzgore.beansplan.dailyschedule;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.view.*;
import dagger.android.support.AndroidSupportInjection;
import de.metzgore.beansplan.R;
import de.metzgore.beansplan.baseschedule.RefreshableScheduleFragment;
import de.metzgore.beansplan.data.DailySchedule;
import de.metzgore.beansplan.data.Resource;
import de.metzgore.beansplan.databinding.FragmentDailyScheduleBinding;
import de.metzgore.beansplan.shared.ScheduleRepository;
import de.metzgore.beansplan.util.DateFormatter;
import de.metzgore.beansplan.util.di.DailyScheduleViewModelFactory;

import javax.inject.Inject;

public class RefreshableDailyScheduleFragment extends RefreshableScheduleFragment {

    private final String TAG = RefreshableDailyScheduleFragment.class.getSimpleName();

    private DailyScheduleAdapter dailyScheduleAdapter;
    private FragmentDailyScheduleBinding binding;
    private DailyScheduleViewModel viewModel;
    private Snackbar snackbar;

    @Inject
    ScheduleRepository<DailySchedule> repo;

    public static Fragment newInstance() {
        return new RefreshableDailyScheduleFragment();
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        dailyScheduleAdapter = new DailyScheduleAdapter();

        viewModel = ViewModelProviders.of(this, new DailyScheduleViewModelFactory(repo)).get
                (DailyScheduleViewModel.class);
        subscribeUi(viewModel);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_daily_schedule, container,
                false);

        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);

        binding.singleDayIncluded.fragmentBaseScheduleShowsList.addItemDecoration(new DividerItemDecoration
                (getActivity(), DividerItemDecoration.VERTICAL));
        binding.singleDayIncluded.fragmentBaseScheduleShowsList.setItemAnimator(null);
        binding.singleDayIncluded.fragmentBaseScheduleShowsList.setHasFixedSize(true);

        binding.swipeRefresh.setOnRefreshListener(() -> viewModel.loadScheduleFromNetwork());
        binding.swipeRefresh.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color
                .colorPrimary));

        binding.singleDayIncluded.fragmentBaseScheduleShowsList.setAdapter(dailyScheduleAdapter);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() != null)
            getActivity().setTitle(R.string.drawer_item_daily_schedule);
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.loadSchedule();
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
                viewModel.loadScheduleFromNetwork();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hideSnackbar();
    }

    private void subscribeUi(DailyScheduleViewModel viewModel) {
        viewModel.getSchedule().observe(this, schedule -> {
            handleState(schedule);
            handleData(schedule);
        });
    }

    private void handleData(Resource<DailySchedule> schedule) {
        if (schedule.getData() != null) {
            dailyScheduleAdapter.setShowList(schedule.getData().getShows());

            String subTitle = null;

            if (schedule.getData().getDate() != null)
                subTitle = getString(R.string.fragment_daily_schedule_subtitle, DateFormatter
                        .formatDate(getContext(), schedule.getData().getDate()));

            getCallback().onSubTitleUpdated(subTitle);
        }
    }

    private void handleState(Resource<DailySchedule> schedule) {
        switch (schedule.getStatus()) {
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
            binding.swipeRefresh.post(() -> binding.swipeRefresh.setRefreshing(isRefreshing));
        }
    }

    public void showRetrySnackbar() {
        snackbar = Snackbar.make(getView(), R.string.error_message_daily_schedule_loading_failed,
                Snackbar.LENGTH_INDEFINITE).setAction(R.string.action_retry, view -> viewModel
                .loadScheduleFromNetwork());
        snackbar.show();
    }

    public void hideSnackbar() {
        if (snackbar != null && snackbar.isShown())
            snackbar.dismiss();
    }
}
