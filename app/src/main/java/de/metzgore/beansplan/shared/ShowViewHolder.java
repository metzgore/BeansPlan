package de.metzgore.beansplan.shared;

import androidx.recyclerview.widget.RecyclerView;

import de.metzgore.beansplan.databinding.ListItemShowBinding;

public class ShowViewHolder extends RecyclerView.ViewHolder {

    private final ListItemShowBinding binding;

    public ShowViewHolder(ListItemShowBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(ShowViewModel viewModel) {
        binding.setViewModel(viewModel);
        binding.executePendingBindings();
    }
}
