package de.metzgore.rbtvschedule.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Schedule {

    @SerializedName("schedule")
    @Expose
    private List<Show> mShows = new ArrayList<>(0);

    public List<Show> getShows() {
        return mShows;
    }

    @Override
    public String toString() {
        StringBuilder shows = new StringBuilder();

        for (Show show : mShows) {
            shows.append(show.toString()).append("\n");
        }

        return shows.toString();
    }

    public int getIdxOfCurrentShow() {
        for (int i = 0; i < mShows.size(); i++) {
            if (mShows.get(i).isCurrentlyRunning())
                return i;
        }

        return 0;
    }
}
