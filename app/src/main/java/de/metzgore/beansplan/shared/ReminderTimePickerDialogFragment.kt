package de.metzgore.beansplan.shared

import android.app.Dialog
import android.app.TimePickerDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.format.DateFormat
import android.widget.TimePicker
import dagger.android.support.AndroidSupportInjection
import de.metzgore.beansplan.data.room.relations.ShowWithReminder
import de.metzgore.beansplan.reminders.RemindersRepository
import de.metzgore.beansplan.reminders.RemindersViewModel
import de.metzgore.beansplan.util.di.RemindersViewModelFactory
import java.util.*
import javax.inject.Inject

//TODO male generic
class ReminderTimePickerDialogFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    @Inject
    lateinit var repo: RemindersRepository

    private lateinit var show: ShowWithReminder

    private lateinit var viewModel: RemindersViewModel

    private var timestamp: Long = 0

    companion object {
        private const val DATE_KEY = "date_key"

        fun newInstance(timestamp: Long): ReminderTimePickerDialogFragment {
            val fragment = ReminderTimePickerDialogFragment()

            val args = Bundle().apply {
                putLong(DATE_KEY, timestamp)
            }
            fragment.arguments = args

            return fragment
        }
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!, RemindersViewModelFactory(repo)).get(RemindersViewModel::class.java)
        viewModel.triggerTimePickerDialog.observe(this, Observer {
            it?.peekContent()?.let { showWithReminder ->
                show = showWithReminder
            }
        })
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        timestamp = arguments?.getLong(DATE_KEY)!!

        val calendar = GregorianCalendar()
        calendar.time = Date(timestamp)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        return TimePickerDialog(context, this, hour, minute,
                DateFormat.is24HourFormat(context))
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        val calendar = GregorianCalendar()
        calendar.time = Date(timestamp)
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val showWithReminder = ShowWithReminder()
        showWithReminder.show = show.show
        showWithReminder.reminder = listOf(show.reminder!![0].copy(timestamp = calendar.time))

        viewModel.upsertReminder(showWithReminder)
    }


}