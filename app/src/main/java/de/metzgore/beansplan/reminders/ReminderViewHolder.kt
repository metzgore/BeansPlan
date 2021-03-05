package de.metzgore.beansplan.reminders

import androidx.recyclerview.widget.RecyclerView
import de.metzgore.beansplan.databinding.ListItemReminderBinding

class ReminderViewHolder(val binding: ListItemReminderBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

    fun bind(itemViewModel: ReminderItemViewModel) {
        binding.viewModel = itemViewModel
        binding.executePendingBindings()
    }
}
