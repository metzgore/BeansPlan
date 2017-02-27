package de.metzgore.rbtvschedule.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

public class Schedule {

    @SerializedName("schedule")
    @Expose
    private TreeMap<Date, List<Show>> mWeeklySchedule = new TreeMap<>();

    @Override
    public String toString() {
        StringBuilder shows = new StringBuilder();

        for (Date key : mWeeklySchedule.keySet()) {
            shows.append(key).append("\n");
            for (Show show : mWeeklySchedule.get(key)) {
                shows.append(show.toString()).append("\n");
            }
        }

        return shows.toString();
    }

    public TreeMap<Date, List<Show>> getWeeklySchedule() {
        return mWeeklySchedule;
    }

    @Nullable
    public List<Show> getTodaysSchedule() {
        return mWeeklySchedule.get(getTodaysDate());
    }

    public int getIndexOfTodaysSchedule() {
        Date today = getTodaysDate();
        Date[] schedules = getKeysAsDate();

        for(int i = 0; i < schedules.length; i++) {
            if(schedules[i].equals(today))
                return i;
        }

        return -1;
    }

    @NonNull
    private Date getTodaysDate() {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    public void removePastShows() {
        removePastDays();

        Date today = getTodaysDate();
        Date[] schedules = getKeysAsDate();

        for(Date date : schedules) {
            if(date.equals(today)) {
                List<Show> showsOfToday = mWeeklySchedule.get(date);
                Iterator<Show> iterator = showsOfToday.iterator();
                while (iterator.hasNext()) {
                    Show show = iterator.next();
                    if (show.isOver())
                        iterator.remove();
                }
            }
        }
    }

    public void removePastDays() {
        Date today = getTodaysDate();
        Date[] schedules = getKeysAsDate();

        for(Date date : schedules) {
            if(date.before(today))
                mWeeklySchedule.remove(date);
        }
    }

    private Date[] getKeysAsDate() {
        return mWeeklySchedule.keySet().toArray(new Date[mWeeklySchedule.size()]);
    }
}
