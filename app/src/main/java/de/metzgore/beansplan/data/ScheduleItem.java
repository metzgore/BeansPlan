package de.metzgore.beansplan.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.Objects;

public class ScheduleItem {

    @SerializedName("id")
    @Expose
    private long id;

    //@SerializedName("title")
    //@Expose
    private String title;

    //@SerializedName("topic")
    //@Expose
    private String topic;

    //@SerializedName("game")
    //@Expose
    private String game;

   // @SerializedName("showId")
    //@Expose
    private long showId;

    //@SerializedName("episodeId")
    //@Expose
    private long episodeId;

    //@SerializedName("episodeImage")
   // @Expose
    private long episodeImage;

    //TODO bohnen
    /*@SerializedName("bohnen")
    @Expose
    private long episodeImage;*/

   // @SerializedName("timeStart")
    //@Expose
    private Date timeStart;

   // @SerializedName("timeEnd")
    //@Expose
    private Date timeEnd;

   // @SerializedName("duration")
   // @Expose
    private int duration;

   // @SerializedName("durationClass")
   // @Expose
    private int durationClass;

   // @SerializedName("streamExclusive")
   // @Expose
    private int streamExclusive;

   // @SerializedName("type")
   // @Expose
    private Type type;

    public enum Type {
        @SerializedName("rerun") NONE(0), @SerializedName("premiere") PREMIERE(1), @SerializedName
                ("live") LIVE(2);

        private final int value;

        Type(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getTopic() {
        return topic;
    }

    public String getGame() {
        return game;
    }

    public long getShowId() {
        return showId;
    }

    public long getEpisodeId() {
        return episodeId;
    }

    public long getEpisodeImage() {
        return episodeImage;
    }

    public Date getTimeStart() {
        return timeStart;
    }

    public Date getTimeEnd() {
        return timeEnd;
    }

    public int getDuration() {
        return duration;
    }

    public int getDurationClass() {
        return durationClass;
    }

    public int getStreamExclusive() {
        return streamExclusive;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return "ScheduleItem{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", topic='" + topic + '\'' +
                ", game='" + game + '\'' +
                ", showId=" + showId +
                ", episodeId=" + episodeId +
                ", episodeImage=" + episodeImage +
                ", timeStart=" + timeStart +
                ", timeEnd=" + timeEnd +
                ", duration=" + duration +
                ", durationClass=" + durationClass +
                ", streamExclusive=" + streamExclusive +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduleItem that = (ScheduleItem) o;
        return id == that.id &&
                showId == that.showId &&
                episodeId == that.episodeId &&
                episodeImage == that.episodeImage &&
                duration == that.duration &&
                durationClass == that.durationClass &&
                streamExclusive == that.streamExclusive &&
                Objects.equals(title, that.title) &&
                Objects.equals(topic, that.topic) &&
                Objects.equals(game, that.game) &&
                Objects.equals(timeStart, that.timeStart) &&
                Objects.equals(timeEnd, that.timeEnd) &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, topic, game, showId, episodeId, episodeImage, timeStart, timeEnd, duration, durationClass, streamExclusive, type);
    }
}