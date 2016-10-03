package de.metzgore.rbtvschedule.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Show {

    @SerializedName("id")
    @Expose
    private int mId;
    @SerializedName("title")
    @Expose
    private String mTitle;
    @SerializedName("topic")
    @Expose
    private String mTopic;
    @SerializedName("show")
    @Expose
    private String mShow;
    @SerializedName("timeStart")
    @Expose
    private Date mTimeStart;
    @SerializedName("timeEnd")
    @Expose
    private Date mTimeEnd;
    @SerializedName("length")
    @Expose
    private int mLength;
    @SerializedName("type")
    @Expose
    private Type mType;
    @SerializedName("game")
    @Expose
    private String mGame;

    private enum Type {
        @SerializedName("")
        NONE,
        @SerializedName("premiere")
        PREMIERE,
        @SerializedName("live")
        LIVE
    }

    public int getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getTopic() {
        return mTopic;
    }

    public String getShow() {
        return mShow;
    }

    public Date getTimeStart() {
        return mTimeStart;
    }

    public Date getTimeEnd() {
        return mTimeEnd;
    }

    public int getLength() {
        return mLength;
    }

    public Type getType() {
        return mType;
    }

    public String getGame() {
        return mGame;
    }

    @Override
    public String toString() {
        return "Show{" +
                "mId=" + mId +
                ", mTitle='" + mTitle + '\'' +
                ", mTopic='" + mTopic + '\'' +
                ", mShow='" + mShow + '\'' +
                ", mTimeStart='" + mTimeStart + '\'' +
                ", mTimeEnd='" + mTimeEnd + '\'' +
                ", mLength=" + mLength +
                ", mType='" + mType + '\'' +
                ", mGame='" + mGame + '\'' +
                '}';
    }
}