package de.metzgore.beansplan.baseschedule;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

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

        callback.onExpandAppBar();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        callback.onSubTitleUpdated(null);
        callback.onLastUpdateUpdated(0);
        callback.onRemovePaddingBottom();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        callback.onAddToolbarElevation();
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
