package com.radioserver.bristolbeat.models;

public class RadioSong {
    private String mTitle;
    private String mGuide;
    private String mDescription;
    private String mThumbnailUrl;

    public RadioSong(String title, String guide, String description, String thumbnailUrl) {
        mTitle = title;
        mGuide = guide;
        mDescription = description;
        mThumbnailUrl = thumbnailUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getGuide() {
        return mGuide;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getThumbnailUrl() {
        return mThumbnailUrl;
    }

    @Override
    public String toString() {
        return "Title: " + mTitle + "\n" +
                "Guide: " + mGuide + "\n" +
                "Description: " + mDescription + "\n" +
                "Thumbnail URL: " + mThumbnailUrl + "\n";
    }
}
