package de.metzgore.beansplan.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.Objects;

public class Show implements Parcelable {

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
        return "Show{" + "id=" + id + ", title='" + title + '\'' + ", topic='" + topic + '\'' +
                ", show='" + show + '\'' + ", timeStart=" + timeStart + ", timeEnd=" + timeEnd +
                ", length=" + length + ", type=" + type + ", game='" + game + '\'' + ", " +
                "youtubeId='" + youtubeId + '\'' + ", isOver=" + isOver + ", isRunning=" +
                isRunning + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Show show1 = (Show) o;

        if (id != show1.id)
            return false;
        if (length != show1.length)
            return false;
        if (isOver != show1.isOver)
            return false;
        if (isRunning != show1.isRunning)
            return false;
        if (title != null ? !title.equals(show1.title) : show1.title != null)
            return false;
        if (topic != null ? !topic.equals(show1.topic) : show1.topic != null)
            return false;
        if (show != null ? !show.equals(show1.show) : show1.show != null)
            return false;
        if (timeStart != null ? !timeStart.equals(show1.timeStart) : show1.timeStart != null)
            return false;
        if (timeEnd != null ? !timeEnd.equals(show1.timeEnd) : show1.timeEnd != null)
            return false;
        if (type != show1.type)
            return false;
        if (game != null ? !game.equals(show1.game) : show1.game != null)
            return false;
        return youtubeId != null ? youtubeId.equals(show1.youtubeId) : show1.youtubeId == null;
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeString(this.topic);
        dest.writeString(this.show);
        dest.writeLong(this.timeStart != null ? this.timeStart.getTime() : -1);
        dest.writeLong(this.timeEnd != null ? this.timeEnd.getTime() : -1);
        dest.writeInt(this.length);
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeString(this.game);
        dest.writeString(this.youtubeId);
        dest.writeByte(this.isOver ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isRunning ? (byte) 1 : (byte) 0);
    }

    protected Show(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.topic = in.readString();
        this.show = in.readString();
        long tmpTimeStart = in.readLong();
        this.timeStart = tmpTimeStart == -1 ? null : new Date(tmpTimeStart);
        long tmpTimeEnd = in.readLong();
        this.timeEnd = tmpTimeEnd == -1 ? null : new Date(tmpTimeEnd);
        this.length = in.readInt();
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : Type.values()[tmpType];
        this.game = in.readString();
        this.youtubeId = in.readString();
        this.isOver = in.readByte() != 0;
        this.isRunning = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Show> CREATOR = new Parcelable.Creator<Show>() {
        @Override
        public Show createFromParcel(Parcel source) {
            return new Show(source);
        }

        @Override
        public Show[] newArray(int size) {
            return new Show[size];
        }
    };
}