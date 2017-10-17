package de.metzgore.rbtvschedule.weeklyschedule;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.format.DateUtils;
import android.util.Log;

import java.util.Date;

import de.metzgore.rbtvschedule.singledayschedule.SingleDayScheduleFragment;
import de.metzgore.rbtvschedule.data.Schedule;

import static android.text.format.DateUtils.FORMAT_SHOW_DATE;
import static android.text.format.DateUtils.FORMAT_SHOW_WEEKDAY;

class WeeklyScheduleAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private Schedule mSchedule;

    WeeklyScheduleAdapter(Context context, FragmentManager mgr) {
        super(mgr);
        mContext = context;
        mSchedule = new Schedule();
    }

    @Override
    public int getCount() {
        return mSchedule.getWeeklySchedule().size();
    }

    @Override
    public Fragment getItem(int position) {
        Date selectedDate = (Date) mSchedule.getWeeklySchedule().keySet().toArray()[position];
        return SingleDayScheduleFragment.newInstance(mSchedule.getWeeklySchedule().get(selectedDate));
    }

    @Override
    public String getPageTitle(int position) {
        Date dateOfSchedule = (Date) mSchedule.getWeeklySchedule().keySet().toArray()[position];
        return DateUtils.formatDateTime(mContext, dateOfSchedule.getTime(), FORMAT_SHOW_WEEKDAY|FORMAT_SHOW_DATE);
    }

    @Override
    public int getItemPosition(Object object){
        return POSITION_NONE;
    }

    @Override
    public long getItemId(int position) {
        return System.currentTimeMillis();
    }

    void setSchedule(Schedule schedule) {
        mSchedule = schedule;
        notifyDataSetChanged();
    }
}
