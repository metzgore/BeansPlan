package de.metzgore.beansplan.shared

import android.app.AlertDialog
import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import dagger.android.support.AndroidSupportInjection
import de.metzgore.beansplan.data.room.relations.ShowWithReminder
import de.metzgore.beansplan.reminders.RemindersRepository
import de.metzgore.beansplan.reminders.RemindersViewModel
import de.metzgore.beansplan.util.di.RemindersViewModelFactory
import javax.inject.Inject

class ReminderDeletionOrUpdateDialogFragment : DialogFragment() {

    @Inject
    lateinit var repo: RemindersRepository

    private lateinit var show: ShowWithReminder

    private lateinit var viewModel: RemindersViewModel


    companion object {
        fun newInstance(): ReminderDeletionOrUpdateDialogFragment {
            return ReminderDeletionOrUpdateDialogFragment()
        }
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!, RemindersViewModelFactory(repo)).get(RemindersViewModel::class.java)
        viewModel.triggerDeletionOrUpdateDialog.observe(this, Observer {
            it?.peekContent()?.let { showWithReminder ->
                show = showWithReminder
            }
        })
    }

    //TODO texts in resource
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialogBuilder = AlertDialog.Builder(context)
        //TODO move to strings.xml
        alertDialogBuilder.apply {
            setTitle("Erinnerung löschen")
            setMessage("Möchtest du die Erinnerung wirklich löschen?")
            setNegativeButton("Bearbeiten") { _, _ -> viewModel.triggerTimePickerDialog(show) }
            setPositiveButton("Löschen") { _, _ -> viewModel.triggerDeletionDialog(show) }
            setNeutralButton("Abbrechen", null)
        }

        return alertDialogBuilder.create()
    }
}