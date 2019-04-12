// Name                 Pierce hepburn
// Student ID           S1711876
// Programme of Study   Computing

package org.s1711876.earthquakes.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

// earthquake class which is also used as schema for room database
@Entity(tableName = "earthquake")
public class Earthquake implements Parcelable {

    @PrimaryKey @NonNull private String link;
    private long pubDate;
    private double latitude;
    private double longitude;
    private String location;
    private int depth;
    private double magnitude;

    @Ignore public Earthquake() { }

    public Earthquake(@NonNull String link, long pubDate, double latitude, double longitude, String location, int depth, double magnitude) {
        this.link = link;
        this.pubDate = pubDate;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = location;
        this.depth = depth;
        this.magnitude = magnitude;
    }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    public long getPubDate() { return pubDate; }
    public void setPubDate(long pubDate) { this.pubDate = pubDate; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int  getDepth() { return depth; }
    public void setDepth(int depth) { this.depth = depth; }

    public double getMagnitude() { return magnitude; }
    public void setMagnitude(double magnitude) { this.magnitude = magnitude; }

    @Override
    public String toString() {
        return "Earthquake{" +
                ", link='" + link + '\'' +
                ", pubDate=" + pubDate +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", location='" + location + '\'' +
                ", depth=" + depth +
                ", magnitude=" + magnitude +
                '}';
    }

    // ide generated parcelable code below here allowing earthquake objects to be passed to intents
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.link);
        dest.writeLong(this.pubDate);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.location);
        dest.writeInt(this.depth);
        dest.writeDouble(this.magnitude);
    }

    protected Earthquake(Parcel in) {
        this.link = in.readString();
        this.pubDate = in.readLong();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.location = in.readString();
        this.depth = in.readInt();
        this.magnitude = in.readDouble();
    }

    public static final Creator<Earthquake> CREATOR = new Creator<Earthquake>() {
        @Override
        public Earthquake createFromParcel(Parcel source) {
            return new Earthquake(source);
        }

        @Override
        public Earthquake[] newArray(int size) {
            return new Earthquake[size];
        }
    };
}
