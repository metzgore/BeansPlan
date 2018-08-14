package de.metzgore.beansplan.dailyschedule

import android.databinding.DataBindingUtil
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import de.metzgore.beansplan.R
import de.metzgore.beansplan.data.room.Reminder
import de.metzgore.beansplan.data.room.relations.ShowWithReminder
import de.metzgore.beansplan.databinding.ListItemShowBinding
import de.metzgore.beansplan.reminders.RemindersViewModel
import de.metzgore.beansplan.shared.OnReminderButtonClickListener
import de.metzgore.beansplan.shared.ShowViewHolder
import de.metzgore.beansplan.shared.ShowViewModel

class DailyScheduleAdapter(private val remindersViewModel: RemindersViewModel) : RecyclerView.Adapter<ShowViewHolder>() {

    private var showsWithReminder: List<ShowWithReminder>? = null

    fun setShowList(showList: List<ShowWithReminder>) {
        if (showsWithReminder == null) {
            showsWithReminder = showList
            notifyItemRangeInserted(0, showList.size)
        } else {
            //TODO use separate class for here and DailyScheduleAdapter
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return showsWithReminder!!.size
                }

                override fun getNewListSize(): Int {
                    return showList.size
                }

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return showsWithReminder!![oldItemPosition].show.id == showList[newItemPosition].show.id
                }

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    val newShow = showList[newItemPosition].show
                    val oldShow = showsWithReminder!![oldItemPosition].show

                    var newReminder: Reminder? = null
                    var oldReminder: Reminder? = null

                    if (showList[newItemPosition].reminder != null) {
                        newReminder = showList[newItemPosition].reminder!![0]
                    }

                    if (showsWithReminder!![oldItemPosition].reminder != null) {
                        oldReminder = showsWithReminder!![oldItemPosition].reminder!![0]
                    }

                    //TODO move somewhere else
                    /*if (newReminder != oldReminder) {
                        if (oldReminder == null && newReminder != null) {
                            remindersViewModel.triggerReminderInserted()
                        } else if (oldReminder != null && newReminder == null) {
                            remindersViewModel.triggerReminderDeleted()
                        } else {
                            remindersViewModel.triggerReminderUpdated()
                        }
                    }*/

                    return newShow == oldShow && newReminder == oldReminder
                }
            })
            showsWithReminder = showList
            result.dispatchUpdatesTo(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowViewHolder {
        val binding = DataBindingUtil.inflate<ListItemShowBinding>(LayoutInflater.from(parent
                .context), R.layout.list_item_show, parent, false)
        return ShowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShowViewHolder, position: Int) {
        val listener = object : OnReminderButtonClickListener {
            override fun deleteOrUpdateReminder(showWithReminder: ShowWithReminder) {
                remindersViewModel.triggerDeletionOrUpdateDialog(showWithReminder)
            }

            override fun onUpsertReminder(showWithReminder: ShowWithReminder) {
                remindersViewModel.triggerTimePickerDialog(showWithReminder)
            }

            override fun deleteReminder(showWithReminder: ShowWithReminder) {
                // NOOP
            }
        }

        holder.bind(ShowViewModel(showsWithReminder!![position], listener))
    }

    override fun getItemCount(): Int {
        return showsWithReminder?.size ?: 0
    }
}
