package de.metzgore.beansplan.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.Objects;

public class ShowResponse {

    @SerializedName("id")
    @Expose
    private long id;
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

    private boolean isOver;

    private boolean isRunning;

    public enum Type {
        @SerializedName("") NONE(0), @SerializedName("premiere") PREMIERE(1), @SerializedName
                ("live") LIVE(2);

        private int value;

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

    @Override
    public String toString() {
        return "ShowResponse{" + "id=" + id + ", title='" + title + '\'' + ", topic='" + topic +
                '\'' + ", show='" + show + '\'' + ", timeStart=" + timeStart + ", timeEnd=" +
                timeEnd + ", length=" + length + ", type=" + type + ", game='" + game + '\'' + "," +
                " " + "youtubeId='" + youtubeId + '\'' + ", isOver=" + isOver + ", isRunning=" +
                isRunning + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ShowResponse showResponse1 = (ShowResponse) o;

        if (id != showResponse1.id)
            return false;
        if (length != showResponse1.length)
            return false;
        if (isOver != showResponse1.isOver)
            return false;
        if (isRunning != showResponse1.isRunning)
            return false;
        if (title != null ? !title.equals(showResponse1.title) : showResponse1.title != null)
            return false;
        if (topic != null ? !topic.equals(showResponse1.topic) : showResponse1.topic != null)
            return false;
        if (show != null ? !show.equals(showResponse1.show) : showResponse1.show != null)
            return false;
        if (timeStart != null ? !timeStart.equals(showResponse1.timeStart) :
                showResponse1.timeStart != null)
            return false;
        if (timeEnd != null ? !timeEnd.equals(showResponse1.timeEnd) : showResponse1.timeEnd !=
                null)
            return false;
        if (type != showResponse1.type)
            return false;
        if (game != null ? !game.equals(showResponse1.game) : showResponse1.game != null)
            return false;
        return youtubeId != null ? youtubeId.equals(showResponse1.youtubeId) :
                showResponse1.youtubeId == null;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getId());
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (topic != null ? topic.hashCode() : 0);
        result = 31 * result + (show != null ? show.hashCode() : 0);
        result = 31 * result + (timeStart != null ? timeStart.hashCode() : 0);
        result = 31 * result + (timeEnd != null ? timeEnd.hashCode() : 0);
        result = 31 * result + length;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (game != null ? game.hashCode() : 0);
        result = 31 * result + (youtubeId != null ? youtubeId.hashCode() : 0);
        result = 31 * result + (isOver ? 1 : 0);
        result = 31 * result + (isRunning ? 1 : 0);
        return result;
    }
}