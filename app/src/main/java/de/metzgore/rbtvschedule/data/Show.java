package de.metzgore.rbtvschedule.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Show implements Parcelable {

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
    @SerializedName("youtube")
    @Expose
    private String mYoutubeId;

    public enum Type {
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

    public String getYoutubeId() {
        return mYoutubeId;
    }

    public boolean isOnYoutube() {
        return mYoutubeId != null && !TextUtils.isEmpty(mYoutubeId);
    }

    public boolean isCurrentlyRunning() {
        Date now = new Date();
        return !now.before(mTimeStart) && !now.after(mTimeEnd);
    }

    public boolean wasAlreadyShown() {
        Date now = new Date();
        return now.before(mTimeEnd);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mId);
        dest.writeString(this.mTitle);
        dest.writeString(this.mTopic);
        dest.writeString(this.mShow);
        dest.writeLong(this.mTimeStart != null ? this.mTimeStart.getTime() : -1);
        dest.writeLong(this.mTimeEnd != null ? this.mTimeEnd.getTime() : -1);
        dest.writeInt(this.mLength);
        dest.writeInt(this.mType == null ? -1 : this.mType.ordinal());
        dest.writeString(this.mGame);
    }

    public Show() {
    }

    protected Show(Parcel in) {
        this.mId = in.readInt();
        this.mTitle = in.readString();
        this.mTopic = in.readString();
        this.mShow = in.readString();
        long tmpMTimeStart = in.readLong();
        this.mTimeStart = tmpMTimeStart == -1 ? null : new Date(tmpMTimeStart);
        long tmpMTimeEnd = in.readLong();
        this.mTimeEnd = tmpMTimeEnd == -1 ? null : new Date(tmpMTimeEnd);
        this.mLength = in.readInt();
        int tmpMType = in.readInt();
        this.mType = tmpMType == -1 ? null : Type.values()[tmpMType];
        this.mGame = in.readString();
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