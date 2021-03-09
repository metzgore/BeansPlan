package de.metzgore.beansplan.dailyschedule;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;

import java.util.Date;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import de.metzgore.beansplan.R;
import de.metzgore.beansplan.data.room.relations.ShowWithReminder;
import de.metzgore.beansplan.databinding.LayoutScheduleBaseBinding;
import de.metzgore.beansplan.notifications.NotificationHelper;
import de.metzgore.beansplan.reminders.RemindersRepository;
import de.metzgore.beansplan.reminders.RemindersViewModel;
import de.metzgore.beansplan.settings.repository.AppSettings;
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

    @Inject
    AppSettings appSettings;

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

        dailyScheduleAdapter = new DailyScheduleAdapter(remindersViewModel);

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
                            .Companion.newInstance(content.getReminder().getTimestamp()
                                    .getTime());
                    dialog.setTargetFragment(this, 0);
                    dialog.show(getFragmentManager(), "TIME_PICKER_DIALOG");
                }
            }
        });

        remindersViewModel.getReminders().observe(this, showWithReminders -> {
            if (showWithReminders != null) {
                for (ShowWithReminder showWithReminder : showWithReminders) {
                    NotificationHelper.INSTANCE.scheduleNotification(getContext(), appSettings,
                            showWithReminder);
                }
            }
        });

        remindersViewModel.getTriggerReminderInserted().observe(this, triggered -> {
            if (triggered != null && triggered.getContentIfNotHandled() != null) {
                Toast.makeText(getContext(), "Erinnerungen angelegt", Toast.LENGTH_LONG).show();
            }
        });

        remindersViewModel.getTriggerReminderDeleted().observe(this, triggered -> {
            if (triggered != null && triggered.getContentIfNotHandled() != null) {
                Toast.makeText(getContext(), "Erinnerungen gelöscht", Toast.LENGTH_LONG).show();
            }
        });

        remindersViewModel.getTriggerReminderUpdated().observe(this, triggered -> {
            if (triggered != null && triggered.getContentIfNotHandled() != null) {
                Toast.makeText(getContext(), "Erinnerungen aktualisiert", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        LayoutScheduleBaseBinding binding = DataBindingUtil.inflate(inflater, R.layout
                .layout_schedule_base, container, false);

        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
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
