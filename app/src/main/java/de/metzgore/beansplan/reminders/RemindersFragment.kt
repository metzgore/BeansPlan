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
import de.metzgore.beansplan.data.room.Reminder
import de.metzgore.beansplan.data.room.Show
import de.metzgore.beansplan.databinding.FragmentRemindersBinding
import de.metzgore.beansplan.shared.OnReminderButtonClickListener
import de.metzgore.beansplan.shared.ReminderDeletionDialogFragment
import de.metzgore.beansplan.shared.ReminderTimePickerDialogFragment
import de.metzgore.beansplan.util.di.RemindersViewModelFactory
import java.util.*
import javax.inject.Inject

class RemindersFragment : BaseFragment(), OnReminderButtonClickListener, ReminderDeletionDialogFragment.ReminderDeletionDialogAction, ReminderTimePickerDialogFragment.ReminderTimePickedDialogAction {
    private lateinit var remindersAdapter: RemindersAdapter
    private lateinit var remindersViewModel: RemindersViewModel

    @Inject
    lateinit var repo: RemindersRepository

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        remindersViewModel = ViewModelProviders.of(this, RemindersViewModelFactory(repo)).get(RemindersViewModel::class.java)

        remindersAdapter = RemindersAdapter(remindersViewModel)

        subscribeUi(remindersViewModel)
    }

    private fun subscribeUi(viewModel: RemindersViewModel) {
        viewModel.reminders.observe(this, Observer {
            remindersAdapter.setShowsWithReminders(it!!)
        })

        viewModel.triggerDeletionDialog.observe(this, Observer {
            it?.getContentIfNotHandled()?.let { title ->
                val newFragment = ReminderDeletionDialogFragment.newInstance(title)
                newFragment.setTargetFragment(this, 0)
                newFragment.show(fragmentManager, "dialog")
            }
        })

        viewModel.triggerTimePickerDialog.observe(this, Observer {
            it?.getContentIfNotHandled()?.let { date ->
                val newFragment = ReminderTimePickerDialogFragment.newInstance(date.time)
                newFragment.setTargetFragment(this, 0)
                newFragment.show(fragmentManager, "dialog")
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

    override fun onUpsertReminder(show: Show, reminder: Reminder) {
        remindersViewModel.updateReminder(show, reminder)
    }

    override fun deleteReminder(show: Show, reminder: Reminder) {
        remindersViewModel.deleteReminder(show, reminder)
    }

    override fun confirmDeletion(text: String) {
        remindersViewModel.triggerDeletionDialog(text)
    }

    override fun reminderTimePicked(date: Date) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {

        @JvmStatic
        fun newInstance() = RemindersFragment()
    }
}