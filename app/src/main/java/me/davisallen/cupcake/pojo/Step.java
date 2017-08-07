package me.davisallen.cupcake.pojo;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Package Name:   me.davisallen.cake
 * Project:        Cake
 * Created by davis, on 8/1/17
 */

public class Step implements Parcelable {
    private static final String LOG_TAG = Step.class.getSimpleName();

    private int id;
    private String shortDescription;
    private String description;
    private String videoURL;
    private String thumbnailURL;

    public Step(int id, String shortDescription, String description, String videoURL, String thumbnailURL) {
        this.id = id;
        this.shortDescription = shortDescription;
        this.description = description;
        this.videoURL = videoURL;
        this.thumbnailURL = thumbnailURL;
    }

    protected Step(Parcel in) {
        id = in.readInt();
        shortDescription = in.readString();
        description = in.readString();
        videoURL = in.readString();
        thumbnailURL = in.readString();
    }

    public static final Creator<Step> CREATOR = new Creator<Step>() {
        @Override
        public Step createFromParcel(Parcel in) {
            return new Step(in);
        }

        @Override
        public Step[] newArray(int size) {
            return new Step[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getDescription() {
        return description;
    }

    private Uri getVideoUri() {
        return Uri.parse(videoURL);
    }

    private Uri getThumbnailURL() {
        return Uri.parse(thumbnailURL);
    }

    public Uri getVideoOrThumbnailUri() {
        if (videoURL != null && videoURL.length() > 0) {
            return getVideoUri();
        }

        if (thumbnailURL != null && thumbnailURL.length() > 0) {
            return getThumbnailURL();
        }

        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(shortDescription);
        parcel.writeString(description);
        parcel.writeString(videoURL);
        parcel.writeString(thumbnailURL);
    }
}
