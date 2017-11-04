package de.metzgore.rbtvschedule.singledayschedule;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.metzgore.rbtvschedule.R;
import de.metzgore.rbtvschedule.dailyschedule.ScheduleAdapter;
import de.metzgore.rbtvschedule.data.Show;
import de.metzgore.rbtvschedule.databinding.LayoutScheduleBaseBinding;

public class BaseScheduleFragment extends Fragment {

    private static final String TAG = BaseScheduleFragment.class.getSimpleName();

    private static final String ARG_SCHEDULE = "arg_schedule";

    private LayoutScheduleBaseBinding binding;

    private List<Show> showList;
    private ScheduleAdapter scheduleAdapter;

    public static Fragment newInstance(List<Show> shows) {
        BaseScheduleFragment fragment = new BaseScheduleFragment();

        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_SCHEDULE, (ArrayList) shows);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scheduleAdapter = new ScheduleAdapter();

        Bundle args = getArguments();

        if (args != null) {
            showList = args.getParcelableArrayList(ARG_SCHEDULE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.layout_schedule_base, container, false);

        binding.showsList.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));
        binding.showsList.setItemAnimator(null);
        binding.showsList.setHasFixedSize(true);
        binding.showsList.setAdapter(scheduleAdapter);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        scheduleAdapter.setShowList(showList);
    }
}
