package de.metzgore.beansplan.reminders

import android.support.v7.widget.RecyclerView
import de.metzgore.beansplan.databinding.ListItemReminderBinding

class ReminderViewHolder(val binding: ListItemReminderBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(itemViewModel: ReminderItemViewModel) {
        binding.viewModel = itemViewModel
        binding.executePendingBindings()
    }
}
