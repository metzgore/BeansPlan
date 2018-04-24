package de.metzgore.rbtvschedule.weeklyschedule;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.text.format.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.metzgore.rbtvschedule.dailyschedule.DailyScheduleFragment;
import de.metzgore.rbtvschedule.data.WeeklySchedule;
import de.metzgore.rbtvschedule.shared.UpdatableScheduleFragment;

import static android.text.format.DateUtils.FORMAT_SHOW_DATE;
import static android.text.format.DateUtils.FORMAT_SHOW_WEEKDAY;

class WeeklyScheduleAdapter extends FragmentPagerAdapter {

    private Context context;
    private WeeklySchedule weeklySchedule;
    private List<Date> dateKeys;

    WeeklyScheduleAdapter(Context context, FragmentManager mgr) {
        super(mgr);
        this.context = context;
        weeklySchedule = new WeeklySchedule();
        dateKeys = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return weeklySchedule.getSchedule().size();
    }

    @Override
    public Fragment getItem(int position) {
        Date selectedDate = dateKeys.get(position);
        return DailyScheduleFragment.newInstance(selectedDate, weeklySchedule.getDailySchedule(selectedDate));
    }

    @Override
    public String getPageTitle(int position) {
        Date dateOfSchedule = dateKeys.get(position);
        return DateUtils.formatDateTime(context, dateOfSchedule.getTime(), FORMAT_SHOW_WEEKDAY | FORMAT_SHOW_DATE);
    }

    @Override
    public long getItemId(int position) {
        return dateKeys.get(position).getTime();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        if (object instanceof UpdatableScheduleFragment) {
            UpdatableScheduleFragment fragment = (UpdatableScheduleFragment) object;
            Date dateKey = fragment.getDateKey();
            if (weeklySchedule.getSchedule().containsKey(dateKey)) {
                fragment.update(weeklySchedule.getSchedule().get(dateKey));
                return dateKeys.indexOf(dateKey);
            } else {
                return PagerAdapter.POSITION_NONE;
            }
        }
        return super.getItemPosition(object);
    }

    public void setSchedule(WeeklySchedule weeklySchedule) {
        if (!this.weeklySchedule.equals(weeklySchedule)) {
            this.weeklySchedule = weeklySchedule;
            dateKeys = new ArrayList<>(this.weeklySchedule.getSchedule().keySet());
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
