package de.metzgore.rbtvschedule.weeklyschedule;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.text.format.DateUtils;

import java.util.Date;

import de.metzgore.rbtvschedule.dailyschedule.DailyScheduleFragment;
import de.metzgore.rbtvschedule.data.WeeklySchedule;
import de.metzgore.rbtvschedule.shared.UpdatableScheduleFragment;

import static android.text.format.DateUtils.FORMAT_SHOW_DATE;
import static android.text.format.DateUtils.FORMAT_SHOW_WEEKDAY;

class WeeklyScheduleAdapter extends FragmentStatePagerAdapter {

    private Context context;
    private WeeklySchedule weeklySchedule;
    private Date selectedDate;

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
        selectedDate = (Date) weeklySchedule.getSchedule().keySet().toArray()[position];
        return DailyScheduleFragment.newInstance(selectedDate, weeklySchedule.getDailySchedule(selectedDate));
    }

    @Override
    public String getPageTitle(int position) {
        Date dateOfSchedule = (Date) weeklySchedule.getSchedule().keySet().toArray()[position];
        return DateUtils.formatDateTime(context, dateOfSchedule.getTime(), FORMAT_SHOW_WEEKDAY | FORMAT_SHOW_DATE);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        if (object instanceof UpdatableScheduleFragment) {
            UpdatableScheduleFragment fragment = (UpdatableScheduleFragment) object;
            if (weeklySchedule.getSchedule().containsKey(fragment.getDateKey())) {
                fragment.update(weeklySchedule.getSchedule().get(selectedDate));
                return super.getItemPosition(object);
            } else {
                return PagerAdapter.POSITION_NONE;
            }
        }
        return super.getItemPosition(object);
    }

    public void setSchedule(WeeklySchedule weeklySchedule) {
        if (!this.weeklySchedule.equals(weeklySchedule)) {
            this.weeklySchedule = weeklySchedule;
            notifyDataSetChanged();
        }
    }

    public int getPositionOfCurrentDay() {
        return weeklySchedule.getPositionOfCurrentDay();
    }

    public boolean containsScheduleForCurrentDay() {
        return weeklySchedule.containsScheduleForCurrentDay();
    }
}
