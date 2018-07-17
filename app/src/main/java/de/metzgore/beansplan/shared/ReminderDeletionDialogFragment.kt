package de.metzgore.beansplan.shared

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment


class ReminderDeletionDialogFragment : DialogFragment() {

    interface ReminderDeletionDialogAction {
        fun confirmDeletion(text: String)
    }

    private lateinit var reminderDeletionDialogAction: ReminderDeletionDialogAction

    companion object {
        private const val TITLE_KEY = "title_key"

        fun newInstance(text: String): ReminderDeletionDialogFragment {
            val fragment = ReminderDeletionDialogFragment()

            // Supply num input as an argument.
            val args = Bundle().apply {
                putString(TITLE_KEY, text)
            }
            fragment.arguments = args

            return fragment
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        try {
            reminderDeletionDialogAction = targetFragment as ReminderDeletionDialogAction
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + " must implement ReminderDeletionDialogAction")
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val text = arguments?.getString(TITLE_KEY)

        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.apply {
            setTitle("Erinnerung löschen")
            setMessage("Möchtest du die Erinnerung zur Sendung $text wirklich löschen?")
            setNegativeButton("Nein", null)
            setPositiveButton("Ja") { _, _ -> reminderDeletionDialogAction.confirmDeletion(text!!) }
        }

        return alertDialogBuilder.create()
    }
}