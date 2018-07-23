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
import de.metzgore.beansplan.data.room.relations.ShowWithReminder;
import de.metzgore.beansplan.databinding.LayoutScheduleBaseBinding;
import de.metzgore.beansplan.reminders.RemindersRepository;
import de.metzgore.beansplan.reminders.RemindersViewModel;
import de.metzgore.beansplan.shared.ReminderDeletionDialogFragment;
import de.metzgore.beansplan.shared.ReminderDeletionOrUpdateDialogFragment;
import de.metzgore.beansplan.shared.ReminderTimePickerDialogFragment;
import de.metzgore.beansplan.shared.UpdatableScheduleFragment;
import de.metzgore.beansplan.util.di.DailyScheduleViewModelFactory;
import de.metzgore.beansplan.util.di.RemindersViewModelFactory;

public class DailyScheduleFragment extends Fragment implements UpdatableScheduleFragment {

    private static final String TAG = DailyScheduleFragment.class.getSimpleName();

    private static final String ARG_DATE = "arg_date";

    private Date dateKey;
    private DailyScheduleAdapter dailyScheduleAdapter;
    private DailyScheduleViewModel viewModel;
    private RemindersViewModel remindersViewModel;

    @Inject
    DailyScheduleRepository repo;

    @Inject
    RemindersRepository remindersRepo;

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

        Bundle args = getArguments();

        if (args != null) {
            dateKey = new Date(args.getLong(ARG_DATE));
        } else {
            dateKey = new Date();
        }

        viewModel = ViewModelProviders.of(this, new DailyScheduleViewModelFactory(repo)).get
                (DailyScheduleViewModel.class);

        remindersViewModel = ViewModelProviders.of(getActivity(), new RemindersViewModelFactory
                (remindersRepo)).get(RemindersViewModel.class);

        dailyScheduleAdapter = new DailyScheduleAdapter(viewModel, remindersViewModel);

        subscribeUi(viewModel, remindersViewModel);
    }

    private void subscribeUi(DailyScheduleViewModel viewModel, RemindersViewModel
            remindersViewModel) {
        viewModel.getSchedule().observe(this, schedule -> {
            if (schedule != null) {
                dailyScheduleAdapter.setShowList(schedule.sortedShows());
            }
        });

        remindersViewModel.getTriggerDeletionOrUpdateDialog().observe(this, showWithReminder -> {
            if (showWithReminder != null && showWithReminder.getContentIfNotHandled() != null) {
                ReminderDeletionOrUpdateDialogFragment dialog =
                        ReminderDeletionOrUpdateDialogFragment.Companion.newInstance();
                dialog.setTargetFragment(this, 0);
                dialog.show(getFragmentManager(), "DELETION_OR_UPDATE_DIALOG");
            }
        });

        remindersViewModel.getTriggerDeletionDialog().observe(this, showWithReminder -> {
            if (showWithReminder != null && showWithReminder.getContentIfNotHandled() != null) {
                ReminderDeletionDialogFragment dialog = ReminderDeletionDialogFragment.Companion
                        .newInstance();
                dialog.setTargetFragment(this, 0);
                dialog.show(getFragmentManager(), "DELETION_DIALOG");
            }
        });

        remindersViewModel.getTriggerTimePickerDialog().observe(this, showWithReminder -> {
            if (showWithReminder != null) {
                ShowWithReminder content = showWithReminder.getContentIfNotHandled();
                if (content != null) {
                    ReminderTimePickerDialogFragment dialog = ReminderTimePickerDialogFragment
                            .Companion.newInstance(content.getReminder().get(0).getTimestamp()
                                    .getTime());
                    dialog.setTargetFragment(this, 0);
                    dialog.show(getFragmentManager(), "TIME_PICKER_DIALOG");
                }
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
