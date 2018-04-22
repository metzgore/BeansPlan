package de.metzgore.rbtvschedule.baseschedule;

import android.content.Context;
import android.support.annotation.NonNull;
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

        callback.onScheduleRefreshed(null);
    }

    @NonNull
    public OnScheduleRefreshedListener getCallback() {
        return callback;
    }

    public interface OnScheduleRefreshedListener {
        void onScheduleRefreshed(String subtitle);
    }
}
