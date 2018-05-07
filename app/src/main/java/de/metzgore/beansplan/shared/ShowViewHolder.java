package de.metzgore.beansplan.shared;

import android.support.v7.widget.RecyclerView;

import de.metzgore.beansplan.databinding.ListItemShowBinding;

public class ShowViewHolder extends RecyclerView.ViewHolder {

    private ListItemShowBinding binding;

    public ShowViewHolder(ListItemShowBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(ShowViewModel viewModel) {
        binding.setViewModel(viewModel);
        binding.executePendingBindings();
    }
}
