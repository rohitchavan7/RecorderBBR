package com.bbr.recorder.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Recordings implements Parcelable {

    String Uri, fileName;
    boolean isPlaying = false;

    public Recordings(String uri, String fileName, boolean isPlaying) {
        Uri = uri;
        this.fileName = fileName;
        this.isPlaying = isPlaying;
    }

    public Recordings() {
    }

    protected Recordings(Parcel in) {
        Uri = in.readString();
        fileName = in.readString();
        isPlaying = in.readByte() != 0;
    }

    public static final Creator<Recordings> CREATOR = new Creator<Recordings>() {
        @Override
        public Recordings createFromParcel(Parcel in) {
            return new Recordings(in);
        }

        @Override
        public Recordings[] newArray(int size) {
            return new Recordings[size];
        }
    };

    @Override
    public String toString() {
        return "Recordings{" +
                "Uri='" + Uri + '\'' +
                ", fileName='" + fileName + '\'' +
                ", isPlaying=" + isPlaying +
                '}';
    }

    public String getUri() {
        return Uri;
    }

    public void setUri(String uri) {
        Uri = uri;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Uri);
        dest.writeString(fileName);
        dest.writeByte((byte) (isPlaying ? 1 : 0));
    }
}
