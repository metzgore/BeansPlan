package de.metzgore.beansplan.baseschedule;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment {

    private OnScheduleRefreshedListener onScheduleRefreshedListener;
    private OnAppBarUpdatedListener onAppBarUpdatedListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            onScheduleRefreshedListener = (OnScheduleRefreshedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement " +
                    "OnScheduleRefreshedListener");
        }

        try {
            onAppBarUpdatedListener = (OnAppBarUpdatedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement " +
                    "OnAppBarUpdatedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onScheduleRefreshedListener = null;
        onAppBarUpdatedListener = null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onAppBarUpdatedListener.onAddToolbarElevation();
        onAppBarUpdatedListener.onExpandAppBar();
        onScheduleRefreshedListener.onSubTitleUpdated(null);
        onScheduleRefreshedListener.onLastUpdateUpdated(0);
        onAppBarUpdatedListener.onRemovePaddingBottom();
    }

    @NonNull
    public OnScheduleRefreshedListener getOnScheduleRefreshedListener() {
        return onScheduleRefreshedListener;
    }

    @NonNull
    public OnAppBarUpdatedListener getOnAppBarUpdatedListenerr() {
        return onAppBarUpdatedListener;
    }

    public interface OnScheduleRefreshedListener {
        void onSubTitleUpdated(@Nullable String subtitle);

        void onLastUpdateUpdated(long timestamp);
    }

    public interface OnAppBarUpdatedListener {
        void onAddToolbarElevation();

        void onRemoveToolbarElevation();

        void onAddPaddingBottom();

        void onRemovePaddingBottom();

        void onExpandAppBar();
    }
}
