package de.metzgore.beansplan.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

public class WeeklyScheduleResponse {

    @SerializedName("schedule")
    @Expose
    private TreeMap<Date, List<Show>> scheduleJson = new TreeMap<>();

    public TreeMap<Date, List<Show>> getSchedule() {
        return scheduleJson;
    }

    public int getSize() {
        return scheduleJson.size();
    }

    public boolean isEmpty() {
        return scheduleJson == null || scheduleJson.isEmpty();
    }

    public List<Date> getDateKeys() {
        return new ArrayList<>(scheduleJson.keySet());
    }

    @Override
    public String toString() {
        StringBuilder shows = new StringBuilder();

        for (Date key : getDateKeys()) {
            shows.append(key).append("\n");
            for (Show show : scheduleJson.get(key)) {
                shows.append(show.toString()).append("\n");
            }
        }

        return shows.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof WeeklyScheduleResponse))
            return false;

        WeeklyScheduleResponse that = (WeeklyScheduleResponse) o;

        return scheduleJson != null ? scheduleJson.equals(that.scheduleJson) : that.scheduleJson
                == null;
    }

    @Override
    public int hashCode() {
        return scheduleJson != null ? scheduleJson.hashCode() : 0;
    }
}
