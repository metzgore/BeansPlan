package de.metzgore.beansplan.baseschedule;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

public abstract class RefreshableScheduleFragment extends Fragment {

    public OnScheduleRefreshedListener callback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            callback = (OnScheduleRefreshedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnScheduleRefreshedListener");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        callback.onAddToolbarElevation();
        callback.onExpandAppBar();
        callback.onSubTitleUpdated(null);
        callback.onLastUpdateUpdated(0);
        callback.onRemovePaddingBottom();
    }

    @NonNull
    public OnScheduleRefreshedListener getCallback() {
        return callback;
    }

    public interface OnScheduleRefreshedListener {
        void onSubTitleUpdated(@Nullable String subtitle);

        void onAddToolbarElevation();

        void onRemoveToolbarElevation();

        void onLastUpdateUpdated(long timestamp);

        void onAddPaddingBottom();

        void onRemovePaddingBottom();

        void onExpandAppBar();
    }
}
