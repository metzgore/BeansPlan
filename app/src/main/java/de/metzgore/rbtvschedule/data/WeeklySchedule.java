package de.metzgore.rbtvschedule.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.gsonfire.annotations.PostDeserialize;

public class WeeklySchedule {

    @SerializedName("schedule")
    @Expose
    private TreeMap<Date, List<Show>> scheduleJson = new TreeMap<>();

    private TreeMap<Date, DailySchedule> weeklySchedule = new TreeMap<>();

    public TreeMap<Date, DailySchedule> getSchedule() {
        return weeklySchedule;
    }

    public boolean hasSchedule(Date key) {
        return weeklySchedule.containsKey(key);
    }

    public int getSize() {
        return weeklySchedule.size();
    }

    public boolean isEmpty() {
        return weeklySchedule == null || weeklySchedule.isEmpty();
    }

    public List<Date> getDateKeys() {
        return new ArrayList<>(getSchedule().keySet());
    }

    @PostDeserialize
    @SuppressWarnings("unused")
    public void postDeserializeLogic() {
        for (Map.Entry<Date, List<Show>> entry : scheduleJson.entrySet()) {
            weeklySchedule.put(entry.getKey(), new DailySchedule(entry.getValue()));
        }
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

    @Nullable
    public Date getStartDate() {
        if (!weeklySchedule.isEmpty())
            return weeklySchedule.firstKey();
        else
            return null;
    }

    @Nullable
    public Date getEndDate() {
        if (!weeklySchedule.isEmpty())
            return weeklySchedule.lastKey();
        else
            return null;
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
                List<Show> showsOfToday = weeklySchedule.get(date).getShows();
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
                weeklySchedule.remove(date);
        }
    }

    private Date[] getKeysAsDate() {
        return weeklySchedule.keySet().toArray(new Date[weeklySchedule.size()]);
    }

    @Override
    public String toString() {
        StringBuilder shows = new StringBuilder();

        for (Date key : weeklySchedule.keySet()) {
            shows.append(key).append("\n");
            for (Show show : weeklySchedule.get(key).getShows()) {
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

        return weeklySchedule != null ? weeklySchedule.equals(that.weeklySchedule) : that.weeklySchedule == null;
    }

    @Override
    public int hashCode() {
        return weeklySchedule != null ? weeklySchedule.hashCode() : 0;
    }

    public DailySchedule getDailySchedule(Date date) {
        if (weeklySchedule.containsKey(date))
            return weeklySchedule.get(date);
        else
            return new DailySchedule();
    }
}
