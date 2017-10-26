package de.metzgore.rbtvschedule.data;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.time.DurationFormatUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Show {

    private static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("topic")
    @Expose
    private String topic;
    @SerializedName("show")
    @Expose
    private String show;
    @SerializedName("timeStart")
    @Expose
    private Date timeStart;
    @SerializedName("timeEnd")
    @Expose
    private Date timeEnd;
    @SerializedName("length")
    @Expose
    private int length;
    @SerializedName("type")
    @Expose
    private Type type;
    @SerializedName("game")
    @Expose
    private String game;
    @SerializedName("youtube")
    @Expose
    private String youtubeId;

    public enum Type {
        @SerializedName("")
        NONE,
        @SerializedName("premiere")
        PREMIERE,
        @SerializedName("live")
        LIVE
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getTopic() {
        return topic;
    }

    public String getShow() {
        return show;
    }

    public Date getTimeStart() {
        return timeStart;
    }

    public Date getTimeEnd() {
        return timeEnd;
    }

    public int getLength() {
        return length;
    }

    public Type getType() {
        return type;
    }

    public String getGame() {
        return game;
    }

    public String getYoutubeId() {
        return youtubeId;
    }

    public boolean isOnYoutube() {
        return youtubeId != null && !TextUtils.isEmpty(youtubeId);
    }

    public boolean isCurrentlyRunning() {
        Date now = new Date();
        return !now.before(timeStart) && !now.after(timeEnd);
    }

    public boolean isOver() {
        Date now = new Date();
        return timeEnd.before(now);
    }

    @NonNull
    public String getTimeStartFormatted() {
        return timeFormat.format(timeStart);
    }

    @NonNull
    public String getTimeEndFormatted() {
        return timeFormat.format(timeEnd);
    }

    @NonNull
    public String getLengthFormatted() {
        return DurationFormatUtils.formatDuration(length * 1000,
                length > 3600 ? "H 'h' mm 'min'" : "m 'min'");
    }

    @NonNull
    public String getTypeFormatted() {
        switch (type) {
            case LIVE:
            case PREMIERE:
                return type.toString();
            case NONE:
            default:
                return "";
        }
    }

    @Override
    public String toString() {
        return "Show{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", topic='" + topic + '\'' +
                ", show='" + show + '\'' +
                ", timeStart='" + timeStart + '\'' +
                ", timeEnd='" + timeEnd + '\'' +
                ", length=" + length +
                ", type='" + type + '\'' +
                ", game='" + game + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Show show1 = (Show) o;

        if (getId() != show1.getId()) return false;
        if (getLength() != show1.getLength()) return false;
        if (getTitle() != null ? !getTitle().equals(show1.getTitle()) : show1.getTitle() != null)
            return false;
        if (getTopic() != null ? !getTopic().equals(show1.getTopic()) : show1.getTopic() != null)
            return false;
        if (getShow() != null ? !getShow().equals(show1.getShow()) : show1.getShow() != null)
            return false;
        if (getTimeStart() != null ? !getTimeStart().equals(show1.getTimeStart()) : show1.getTimeStart() != null)
            return false;
        if (getTimeEnd() != null ? !getTimeEnd().equals(show1.getTimeEnd()) : show1.getTimeEnd() != null)
            return false;
        if (getType() != show1.getType()) return false;
        if (getGame() != null ? !getGame().equals(show1.getGame()) : show1.getGame() != null)
            return false;
        if (isCurrentlyRunning() != show1.isCurrentlyRunning()) return false;
        if (isOver() != show1.isOver()) return false;
        return getYoutubeId() != null ? getYoutubeId().equals(show1.getYoutubeId()) : show1.getYoutubeId() == null;
    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + (getTitle() != null ? getTitle().hashCode() : 0);
        result = 31 * result + (getTopic() != null ? getTopic().hashCode() : 0);
        result = 31 * result + (getShow() != null ? getShow().hashCode() : 0);
        result = 31 * result + (getTimeStart() != null ? getTimeStart().hashCode() : 0);
        result = 31 * result + (getTimeEnd() != null ? getTimeEnd().hashCode() : 0);
        result = 31 * result + getLength();
        result = 31 * result + (getType() != null ? getType().hashCode() : 0);
        result = 31 * result + (getGame() != null ? getGame().hashCode() : 0);
        result = 31 * result + (getYoutubeId() != null ? getYoutubeId().hashCode() : 0);
        return result;
    }
}