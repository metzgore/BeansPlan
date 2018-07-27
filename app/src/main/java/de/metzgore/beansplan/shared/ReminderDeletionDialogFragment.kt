package de.metzgore.beansplan.shared

import android.app.AlertDialog
import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import dagger.android.support.AndroidSupportInjection
import de.metzgore.beansplan.R
import de.metzgore.beansplan.data.room.relations.ShowWithReminder
import de.metzgore.beansplan.notifications.NotificationHelper
import de.metzgore.beansplan.reminders.RemindersRepository
import de.metzgore.beansplan.reminders.RemindersViewModel
import de.metzgore.beansplan.util.di.RemindersViewModelFactory
import javax.inject.Inject


class ReminderDeletionDialogFragment : DialogFragment() {

    @Inject
    lateinit var repo: RemindersRepository

    private lateinit var show: ShowWithReminder

    private lateinit var viewModel: RemindersViewModel


    companion object {
        fun newInstance(): ReminderDeletionDialogFragment {
            return ReminderDeletionDialogFragment()
        }
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!, RemindersViewModelFactory(repo)).get(RemindersViewModel::class.java)
        viewModel.triggerDeletionDialog.observe(this, Observer {
            it?.peekContent()?.let { showWithReminder ->
                show = showWithReminder
            }
        })
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.apply {
            setTitle(getString(R.string.reminder_deletion_dialog_title))
            setMessage(getString(R.string.reminder_deletion_dialog_message))
            setNegativeButton(getString(R.string.reminder_deletion_dialog_negative), null)
            setPositiveButton(getString(R.string.reminder_deletion_dialog_positive)) { _, _ ->
                viewModel.deleteReminder(show)
                NotificationHelper.unscheduleNotification(context, show)
            }
        }

        return alertDialogBuilder.create()
    }
}