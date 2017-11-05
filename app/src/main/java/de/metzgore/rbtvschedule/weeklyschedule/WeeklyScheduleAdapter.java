package de.metzgore.rbtvschedule.weeklyschedule;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.format.DateUtils;

import java.util.Date;

import de.metzgore.rbtvschedule.data.WeeklySchedule;
import de.metzgore.rbtvschedule.singledayschedule.BaseScheduleFragment;

import static android.text.format.DateUtils.FORMAT_SHOW_DATE;
import static android.text.format.DateUtils.FORMAT_SHOW_WEEKDAY;

class WeeklyScheduleAdapter extends FragmentStatePagerAdapter {

    private Context context;
    private WeeklySchedule weeklySchedule;

    WeeklyScheduleAdapter(Context context, FragmentManager mgr) {
        super(mgr);
        this.context = context;
        weeklySchedule = new WeeklySchedule();
    }

    @Override
    public int getCount() {
        return weeklySchedule.getSchedule().size();
    }

    @Override
    public Fragment getItem(int position) {
        Date selectedDate = (Date) weeklySchedule.getSchedule().keySet().toArray()[position];
        return BaseScheduleFragment.newInstance(weeklySchedule.getSchedule().get(selectedDate));
    }

    @Override
    public String getPageTitle(int position) {
        Date dateOfSchedule = (Date) weeklySchedule.getSchedule().keySet().toArray()[position];
        return DateUtils.formatDateTime(context, dateOfSchedule.getTime(), FORMAT_SHOW_WEEKDAY | FORMAT_SHOW_DATE);
    }

    void setSchedule(WeeklySchedule weeklySchedule) {
        if (!this.weeklySchedule.equals(weeklySchedule)) {
            this.weeklySchedule = weeklySchedule;
            notifyDataSetChanged();
        }
    }
}
