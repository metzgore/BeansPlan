package de.metzgore.beansplan.dailyschedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.metzgore.beansplan.R
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

                    val newReminder = showList[newItemPosition].reminder
                    val oldReminder = showsWithReminder!![oldItemPosition].reminder

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
