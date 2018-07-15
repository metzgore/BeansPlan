package de.metzgore.beansplan.reminders

import android.databinding.DataBindingUtil
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import de.metzgore.beansplan.R
import de.metzgore.beansplan.data.room.Reminder
import de.metzgore.beansplan.data.room.relations.ShowWithReminder
import de.metzgore.beansplan.databinding.ListItemReminderBinding
import de.metzgore.beansplan.shared.OnReminderButtonClickListener

class RemindersAdapter(onReminderButtonClickListener: OnReminderButtonClickListener) : RecyclerView.Adapter<ReminderViewHolder>() {

    private var showsWithReminder: List<ShowWithReminder>? = null
    private var listener = onReminderButtonClickListener

    fun setShowsWithReminders(showsWithRemindersList: List<ShowWithReminder>) {
        if (showsWithReminder == null) {
            showsWithReminder = showsWithRemindersList
            notifyItemRangeInserted(0, showsWithRemindersList.size)
        } else {
            //TODO use separate class for here and DailyScheduleAdapter
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return showsWithReminder!!.size
                }

                override fun getNewListSize(): Int {
                    return showsWithRemindersList.size
                }

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return showsWithReminder!!.get(oldItemPosition).show.id == showsWithRemindersList[newItemPosition].show.id
                }

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    val newShow = showsWithRemindersList[newItemPosition].show
                    val oldShow = showsWithReminder!![oldItemPosition].show

                    var newReminder: Reminder? = null
                    var oldReminder: Reminder? = null

                    if (showsWithRemindersList[newItemPosition].reminder != null) {
                        newReminder = showsWithRemindersList[newItemPosition].reminder!!.get(0)
                    }

                    if (showsWithReminder!![oldItemPosition].reminder != null) {
                        oldReminder = showsWithReminder!![oldItemPosition].reminder!![0]
                    }

                    return newShow == oldShow && newReminder == oldReminder
                }
            })
            showsWithReminder = showsWithRemindersList
            result.dispatchUpdatesTo(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val binding = DataBindingUtil.inflate<ListItemReminderBinding>(LayoutInflater.from(parent
                .context), R.layout.list_item_reminder, parent, false)
        return ReminderViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return showsWithReminder?.size ?: 0
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        holder.bind(ReminderItemViewModel(showsWithReminder!![position], listener))
    }
}
