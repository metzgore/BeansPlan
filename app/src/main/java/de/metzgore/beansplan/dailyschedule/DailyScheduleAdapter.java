package de.metzgore.beansplan.dailyschedule;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import de.metzgore.beansplan.R;
import de.metzgore.beansplan.data.Show;
import de.metzgore.beansplan.databinding.ListItemShowBinding;
import de.metzgore.beansplan.shared.ShowViewHolder;
import de.metzgore.beansplan.shared.ShowViewModel;

public class DailyScheduleAdapter extends RecyclerView.Adapter<ShowViewHolder> {

    private List<Show> shows;

    public void setShowList(@NonNull final List<Show> showList) {
        if (shows == null) {
            shows = showList;
            notifyItemRangeInserted(0, showList.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return shows.size();
                }

                @Override
                public int getNewListSize() {
                    return showList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return shows.get(oldItemPosition).getId() == showList.get(newItemPosition).getId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Show newShow = showList.get(newItemPosition);
                    Show oldShow = shows.get(oldItemPosition);
                    return newShow.equals(oldShow);
                }
            });
            shows = showList;
            result.dispatchUpdatesTo(this);
        }
    }

    @Override
    @NonNull
    public ShowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemShowBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.list_item_show,
                        parent, false);
        return new ShowViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowViewHolder holder, int position) {
        holder.bind(new ShowViewModel(shows.get(position)));
    }

    @Override
    public int getItemCount() {
        return shows == null ? 0 : shows.size();
    }
}
