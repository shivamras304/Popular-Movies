package com.example.shivam.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by shivam on 09/03/16.
 */
public class MovieInfo implements Parcelable {

    private String posterURL;
    private String originalTitle;
    private String plotSynopsis;
    private double userRating;
    private String releaseDate;

    //This constructor will be used to create an instance of the class from the parcel
    private MovieInfo(Parcel source) {
        posterURL = source.readString();
        originalTitle = source.readString();
        plotSynopsis = source.readString();
        userRating = source.readDouble();
        releaseDate = source.readString();
    }

    //The above constructor hides the default constructor
    MovieInfo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(posterURL);
        dest.writeString(originalTitle);
        dest.writeString(plotSynopsis);
        dest.writeDouble(userRating);
        dest.writeString(releaseDate);
    }


    //It will unwrap the parcel and create an instance of type MovieInfo
    //and named as CREATOR
    public static final Parcelable.Creator<MovieInfo> CREATOR = new Creator<MovieInfo>() {

        //Creates new instance of Parcelable class
        @Override
        public MovieInfo createFromParcel(Parcel source) {
            return new MovieInfo(source);
        }

        @Override
        public MovieInfo[] newArray(int size) {
            return new MovieInfo[0];
        }
    };



    //SETTERS
    public void setPosterURL(String posterURL) {
        this.posterURL = posterURL;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public void setPlotSynopsis(String plotSynopsis) {
        this.plotSynopsis = plotSynopsis;
    }

    public void setUserRating(double userRating) {
        this.userRating = userRating;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    //GETTERS
    public String getPosterURL() {
        return posterURL;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getPlotSynopsis() {
        return plotSynopsis;
    }

    public double getUserRating() {
        return userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }


}
