package de.metzgore.rbtvschedule.dailyschedule;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.DividerItemDecoration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.time.DurationFormatUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.metzgore.rbtvschedule.R;
import de.metzgore.rbtvschedule.data.Show;

public class DailyScheduleFragment extends Fragment implements DailyScheduleContract.View {

    private static final String TAG = DailyScheduleFragment.class.getSimpleName();

    private static final String ARG_SCHEDULE = "arg_schedule";

    @BindView(R.id.fragment_daily_schedule_recycler_view)
    RecyclerView mDailyScheduleRecyclerView;

    private List<Show> mSchedule;

    private ScheduleAdapter mScheduleAdapter;

    private DailySchedulePresenter mActionsListener;

    private Unbinder mUnbinder;

    public static Fragment newInstance(List<Show> shows) {
        DailyScheduleFragment fragment = new DailyScheduleFragment();

        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_SCHEDULE, (ArrayList) shows);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScheduleAdapter = new ScheduleAdapter();

        Bundle args = getArguments();

        mSchedule = args.getParcelableArrayList(ARG_SCHEDULE);

        mActionsListener = new DailySchedulePresenter(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_schedule, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        mDailyScheduleRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));
        mDailyScheduleRecyclerView.setAdapter(mScheduleAdapter);
        mDailyScheduleRecyclerView.setHasFixedSize(true);
        mDailyScheduleRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mActionsListener.loadDailySchedule(mSchedule);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void showSchedule(List<Show> schedule) {
        mScheduleAdapter.setSchedule(schedule);
        mDailyScheduleRecyclerView.smoothScrollToPosition(mScheduleAdapter.getIdxOfCurrentShow());
    }

    protected class ShowHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.list_item_show_title_text_view)
        TextView mTitleTextView;
        @BindView(R.id.list_item_show_topic_text_view)
        TextView mTopicTextView;
        @BindView(R.id.list_item_show_start_text_view)
        TextView mStartTextView;
        @BindView(R.id.list_item_show_time_dash)
        TextView mShowTimeDash;
        @BindView(R.id.list_item_show_end_text_view)
        TextView mEndTextView;
        @BindView(R.id.list_item_show_length_text_view)
        TextView mLengthTextView;
        @BindView(R.id.list_item_show_type_text_view)
        TextView mTypeTextView;
        @BindView(R.id.list_item_show_base)
        LinearLayout mBase;
        @BindView(R.id.list_view_item_show_youtube_image_view)
        ImageView mYoutubeImageView;

        Drawable mNowPlaying = ContextCompat.getDrawable(getActivity(), R.drawable.ic_videocam);

        private Show mShow;

        private SimpleDateFormat mTimeFormat = new SimpleDateFormat("HH:mm");

        ShowHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindView(Show show) {
            mShow = show;

            mTitleTextView.setText(mShow.getTitle());
            mTopicTextView.setText(mShow.getTopic());
            mStartTextView.setText(mTimeFormat.format(mShow.getTimeStart()));
            mEndTextView.setText(mTimeFormat.format(mShow.getTimeEnd()));
            mLengthTextView.setText(DurationFormatUtils.formatDuration(mShow.getLength() * 1000,
                    mShow.getLength() > 3600 ? "H 'h' mm 'min'" : "m 'min'"));

            switch (mShow.getType()) {
                case LIVE:
                case PREMIERE:
                    mTypeTextView.setText(mShow.getType().toString());
                    break;
                case NONE:
                default:
                    mTypeTextView.setText("");
                    break;
            }

            if (mShow.isCurrentlyRunning()) {
                //padding is gone on api level < 21
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    int paddingTop = mBase.getPaddingTop();
                    int paddingLeft = mBase.getPaddingLeft();
                    int paddingRight = mBase.getPaddingRight();
                    int paddingBottom = mBase.getPaddingBottom();
                    mBase.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.border_current_show));
                    mBase.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
                } else {
                    mBase.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.border_current_show));
                }
                mTitleTextView.setCompoundDrawablesWithIntrinsicBounds(mNowPlaying, null, null, null);
            } else {
                mBase.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.white));
                mBase.getBackground().setAlpha(100);
                mTitleTextView.setCompoundDrawables(null, null, null, null);
            }

            if (mShow.isOnYoutube()) {
                mYoutubeImageView.setVisibility(View.VISIBLE);
            } else {
                mYoutubeImageView.setVisibility(View.INVISIBLE);

            }

            setAllViewsEnabled(mShow.wasAlreadyShown());
        }

        private void setAllViewsEnabled(boolean enabled) {
            mTitleTextView.setEnabled(enabled);
            mTopicTextView.setEnabled(enabled);
            mStartTextView.setEnabled(enabled);
            mShowTimeDash.setEnabled(enabled);
            mEndTextView.setEnabled(enabled);
            mLengthTextView.setEnabled(enabled);
            mTypeTextView.setEnabled(enabled);
            mYoutubeImageView.setEnabled(enabled);
            mYoutubeImageView.setImageAlpha(enabled ? 100 : 25);
        }
    }

    private class ScheduleAdapter extends RecyclerView.Adapter<ShowHolder> {

        private List<Show> mSchedule;

        @Override
        public ShowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_item_show, parent, false);
            return new ShowHolder(view);
        }

        @Override
        public void onBindViewHolder(ShowHolder holder, int position) {
            holder.bindView(mSchedule.get(position));
        }

        @Override
        public int getItemCount() {
            return mSchedule.size();
        }

        void setSchedule(List<Show> schedule) {
            mSchedule = schedule;
            notifyDataSetChanged();
        }

        int getIdxOfCurrentShow() {
            for (int i = 0; i < mSchedule.size(); i++) {
                if (mSchedule.get(i).isCurrentlyRunning())
                    return i;
            }

            return 0;
        }
    }
}
