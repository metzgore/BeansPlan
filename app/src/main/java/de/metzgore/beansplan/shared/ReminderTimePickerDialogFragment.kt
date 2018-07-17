package de.metzgore.beansplan.shared

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.format.DateFormat
import android.widget.TimePicker
import java.util.*

class ReminderTimePickerDialogFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    interface ReminderTimePickedDialogAction {
        //TODO call action here
        fun reminderTimePicked(date: Date)
    }

    private lateinit var reminderTimePickedDialogAction: ReminderTimePickedDialogAction

    companion object {
        private const val DATE_KEY = "date_key"

        fun newInstance(timestamp: Long): ReminderTimePickerDialogFragment {
            val fragment = ReminderTimePickerDialogFragment()

            // Supply num input as an argument.
            val args = Bundle().apply {
                putLong(DATE_KEY, timestamp)
            }
            fragment.arguments = args

            return fragment
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            reminderTimePickedDialogAction = targetFragment as ReminderTimePickedDialogAction
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + " must implement ReminderTimePickedDialogAction")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val timestamp = arguments?.getLong(DATE_KEY)

        val calendar = GregorianCalendar()
        if (timestamp != null) calendar.time = Date(timestamp)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        return TimePickerDialog(context, this, hour, minute,
                DateFormat.is24HourFormat(context))
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        // Do something with the time chosen by the user
    }
}