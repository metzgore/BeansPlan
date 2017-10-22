package de.metzgore.rbtvschedule.data;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.time.DurationFormatUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Show {

    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

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

        Show show = (Show) o;

        if (id != show.id) return false;
        if (length != show.length) return false;
        if (title != null ? !title.equals(show.title) : show.title != null) return false;
        if (topic != null ? !topic.equals(show.topic) : show.topic != null) return false;
        if (this.show != null ? !this.show.equals(show.show) : show.show != null) return false;
        if (timeStart != null ? !timeStart.equals(show.timeStart) : show.timeStart != null)
            return false;
        if (timeEnd != null ? !timeEnd.equals(show.timeEnd) : show.timeEnd != null)
            return false;
        if (type != show.type) return false;
        if (game != null ? !game.equals(show.game) : show.game != null) return false;
        return youtubeId != null ? youtubeId.equals(show.youtubeId) : show.youtubeId == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (topic != null ? topic.hashCode() : 0);
        result = 31 * result + (show != null ? show.hashCode() : 0);
        result = 31 * result + (timeStart != null ? timeStart.hashCode() : 0);
        result = 31 * result + (timeEnd != null ? timeEnd.hashCode() : 0);
        result = 31 * result + length;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (game != null ? game.hashCode() : 0);
        result = 31 * result + (youtubeId != null ? youtubeId.hashCode() : 0);
        return result;
    }
}