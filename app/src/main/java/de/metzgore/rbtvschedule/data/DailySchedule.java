package de.metzgore.rbtvschedule.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DailySchedule extends BaseSchedule implements Parcelable {

    @SerializedName("schedule")
    @Expose
    private List<Show> shows;

    DailySchedule(List<Show> shows) {
        this.shows = shows;
    }

    public DailySchedule() {
        this.shows = new ArrayList<>();
    }

    public List<Show> getShows() {
        return shows;
    }

    public boolean isEmpty() {
        return shows == null || shows.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder showsBuilder = new StringBuilder();

        for (Show show : shows) {
            showsBuilder.append(show.toString()).append("\n");
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DailySchedule dailySchedule = (DailySchedule) o;

        return getShows().equals(dailySchedule.getShows());
    }

    @Override
    public int hashCode() {
        return getShows().hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.shows);
    }

    protected DailySchedule(Parcel in) {
        this.shows = in.createTypedArrayList(Show.CREATOR);
    }

    public static final Parcelable.Creator<DailySchedule> CREATOR = new Parcelable.Creator<DailySchedule>() {
        @Override
        public DailySchedule createFromParcel(Parcel source) {
            return new DailySchedule(source);
        }

        @Override
        public DailySchedule[] newArray(int size) {
            return new DailySchedule[size];
        }
    };
}
