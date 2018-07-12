package de.metzgore.beansplan.data;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DailyScheduleResponse {

    @SerializedName("schedule")
    @Expose
    private List<ShowResponse> shows;

    public DailyScheduleResponse(List<ShowResponse> shows) {
        this.shows = shows;
    }

    public DailyScheduleResponse() {
        this.shows = new ArrayList<>();
    }

    public List<ShowResponse> getShows() {
        return shows;
    }

    public boolean isEmpty() {
        return shows == null || shows.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder showsBuilder = new StringBuilder();

        for (ShowResponse showResponse : shows) {
            showsBuilder.append(showResponse.toString()).append("\n");
        }

        return showsBuilder.toString();
    }

    @Nullable
    public Date getDate() {
        if (shows.size() > 0)
            return shows.get(0).getTimeStart();
        else
            return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        DailyScheduleResponse dailyScheduleResponse = (DailyScheduleResponse) o;

        return getShows().equals(dailyScheduleResponse.getShows());
    }

    @Override
    public int hashCode() {
        return getShows().hashCode();
    }
}
