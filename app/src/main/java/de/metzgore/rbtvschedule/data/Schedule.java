package de.metzgore.rbtvschedule.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Schedule {

    @SerializedName("schedule")
    @Expose
    private List<Show> shows = new ArrayList<>();

    public List<Show> getShows() {
        return shows;
    }

    @Override
    public String toString() {
        StringBuilder showsBuilder = new StringBuilder();

        for (Show show : shows) {
            showsBuilder.append(show.toString()).append("\n");
        }

        return showsBuilder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Schedule schedule = (Schedule) o;

        return getShows().equals(schedule.getShows());
    }

    @Override
    public int hashCode() {
        return getShows().hashCode();
    }
}
