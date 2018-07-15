package de.metzgore.beansplan.dailyschedule;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;
import java.util.Objects;

import de.metzgore.beansplan.R;
import de.metzgore.beansplan.data.room.Reminder;
import de.metzgore.beansplan.data.room.Show;
import de.metzgore.beansplan.data.room.relations.ShowWithReminder;
import de.metzgore.beansplan.databinding.ListItemShowBinding;
import de.metzgore.beansplan.shared.OnReminderButtonClickListener;
import de.metzgore.beansplan.shared.ShowViewHolder;
import de.metzgore.beansplan.shared.ShowViewModel;

public class DailyScheduleAdapter extends RecyclerView.Adapter<ShowViewHolder> {

    private List<ShowWithReminder> shows;
    private OnReminderButtonClickListener listener;

    DailyScheduleAdapter(OnReminderButtonClickListener listener) {
        this.listener = listener;
    }

    public void setShowList(@NonNull final List<ShowWithReminder> showList) {
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
                    return shows.get(oldItemPosition).show.getId() == showList.get
                            (newItemPosition).show.getId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Show newShow = showList.get(newItemPosition).show;
                    Show oldShow = shows.get(oldItemPosition).show;

                    Reminder newReminder = null;
                    Reminder oldReminder = null;

                    if (showList.get(newItemPosition).getReminder() != null) {
                        newReminder = showList.get(newItemPosition).getReminder().get(0);
                    }

                    if (shows.get(oldItemPosition).getReminder() != null) {
                        oldReminder = shows.get(oldItemPosition).getReminder().get(0);
                    }

                    return newShow.equals(oldShow) && Objects.equals(newReminder, oldReminder);
                }
            });
            shows = showList;
            result.dispatchUpdatesTo(this);
        }
    }

    @Override
    @NonNull
    public ShowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemShowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent
                .getContext()), R.layout.list_item_show, parent, false);
        return new ShowViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowViewHolder holder, int position) {
        Reminder reminder = null;

        if (shows.get(position).getReminder() != null) {
            reminder = shows.get(position).getReminder().get(0);
        }

        holder.bind(new ShowViewModel(shows.get(position).show, reminder, listener));
    }

    @Override
    public int getItemCount() {
        return shows == null ? 0 : shows.size();
    }
}
