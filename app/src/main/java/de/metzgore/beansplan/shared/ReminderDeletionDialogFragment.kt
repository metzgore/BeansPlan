package de.metzgore.beansplan.shared

import android.app.AlertDialog
import android.app.Dialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import dagger.android.support.AndroidSupportInjection
import de.metzgore.beansplan.R
import de.metzgore.beansplan.data.room.relations.ShowWithReminder
import de.metzgore.beansplan.notifications.NotificationHelper
import de.metzgore.beansplan.reminders.RemindersRepository
import de.metzgore.beansplan.reminders.RemindersViewModel
import de.metzgore.beansplan.settings.repository.AppSettings
import de.metzgore.beansplan.util.di.RemindersViewModelFactory
import javax.inject.Inject


class ReminderDeletionDialogFragment : androidx.fragment.app.DialogFragment() {

    @Inject
    lateinit var repo: RemindersRepository

    @Inject
    lateinit var appSettings: AppSettings

    private lateinit var show: ShowWithReminder

    private lateinit var viewModel: RemindersViewModel


    companion object {
        fun newInstance(): ReminderDeletionDialogFragment {
            return ReminderDeletionDialogFragment()
        }
    }

    override fun onAttach(context: Context) {
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
                NotificationHelper.unscheduleNotification(context, appSettings, show)
            }
        }

        return alertDialogBuilder.create()
    }
}