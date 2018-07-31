package de.metzgore.beansplan.reminders

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.AndroidSupportInjection
import de.metzgore.beansplan.R
import de.metzgore.beansplan.baseschedule.BaseFragment
import de.metzgore.beansplan.databinding.FragmentRemindersBinding
import de.metzgore.beansplan.notifications.NotificationHelper
import de.metzgore.beansplan.settings.repository.AppSettings
import de.metzgore.beansplan.shared.ReminderDeletionDialogFragment
import de.metzgore.beansplan.shared.ReminderTimePickerDialogFragment
import de.metzgore.beansplan.util.di.RemindersViewModelFactory
import javax.inject.Inject

class RemindersFragment : BaseFragment() {
    private lateinit var remindersAdapter: RemindersAdapter
    private lateinit var remindersViewModel: RemindersViewModel

    @Inject
    lateinit var repo: RemindersRepository

    @Inject
    lateinit var appSettings: AppSettings

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        remindersViewModel = ViewModelProviders.of(activity!!, RemindersViewModelFactory(repo)).get(RemindersViewModel::class.java)

        remindersAdapter = RemindersAdapter(remindersViewModel)

        subscribeUi(remindersViewModel)
    }

    private fun subscribeUi(viewModel: RemindersViewModel) {
        viewModel.reminders.observe(this, Observer {
            remindersAdapter.setShowsWithReminders(it!!)

            for (showWithReminder in it) {
                NotificationHelper.scheduleNotification(context!!, appSettings, showWithReminder)
            }
        })

        viewModel.triggerDeletionDialog.observe(this, Observer {
            it?.getContentIfNotHandled()?.let { _ ->
                val newFragment = ReminderDeletionDialogFragment.newInstance()
                newFragment.setTargetFragment(this, 0)
                newFragment.show(fragmentManager, "DELETION_DIALOG")
            }
        })

        viewModel.triggerTimePickerDialog.observe(this, Observer {
            it?.getContentIfNotHandled()?.let { showWithReminder ->
                val newFragment = ReminderTimePickerDialogFragment.newInstance(showWithReminder.reminder!![0].timestamp.time)
                newFragment.setTargetFragment(this, 0)
                newFragment.show(fragmentManager, "TIMEPICKET_DIALOG")
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FragmentRemindersBinding>(inflater, R.layout.fragment_reminders, container, false)

        binding.apply {
            viewModel = remindersViewModel
            setLifecycleOwner(this@RemindersFragment)
        }

        binding.fragmentRemindersList.apply {
            adapter = remindersAdapter
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity != null)
            (activity as FragmentActivity).title = getString(R.string.drawer_item_reminders)
    }

    companion object {

        @JvmStatic
        fun newInstance() = RemindersFragment()
    }
}
