package de.metzgore.rbtvschedule.data;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

public class WeeklySchedule {

    @SerializedName("schedule")
    @Expose
    private TreeMap<Date, List<Show>> schedule = new TreeMap<>();

    public TreeMap<Date, List<Show>> getSchedule() {
        return schedule;
    }

    public int getPositionOfCurrentDay() {
        Date today = getCurrentDate();
        Date[] dates = getKeysAsDate();

        for (int i = 0; i < dates.length; i++) {
            if (dates[i].equals(today))
                return i;
        }

        return -1;
    }

    public boolean containsScheduleForCurrentDay() {
        Date today = getCurrentDate();
        Date[] dates = getKeysAsDate();

        for (Date date : dates) {
            if (date.equals(today))
                return true;
        }

        return false;
    }

    public Date getStartDate() {
        return schedule.firstKey();
    }

    public Date getEndDate() {
        return schedule.lastKey();
    }

    @NonNull
    private Date getCurrentDate() {
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

        Date today = getCurrentDate();
        Date[] schedules = getKeysAsDate();

        for (Date date : schedules) {
            if (date.equals(today)) {
                List<Show> showsOfToday = schedule.get(date);
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
        Date today = getCurrentDate();
        Date[] schedules = getKeysAsDate();

        for (Date date : schedules) {
            if (date.before(today))
                schedule.remove(date);
        }
    }

    private Date[] getKeysAsDate() {
        return schedule.keySet().toArray(new Date[schedule.size()]);
    }

    @Override
    public String toString() {
        StringBuilder shows = new StringBuilder();

        for (Date key : schedule.keySet()) {
            shows.append(key).append("\n");
            for (Show show : schedule.get(key)) {
                shows.append(show.toString()).append("\n");
            }
        }

        return shows.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WeeklySchedule)) return false;

        WeeklySchedule that = (WeeklySchedule) o;

        return schedule != null ? schedule.equals(that.schedule) : that.schedule == null;
    }

    @Override
    public int hashCode() {
        return schedule != null ? schedule.hashCode() : 0;
    }
}
