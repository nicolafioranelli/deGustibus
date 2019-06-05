package com.madness.deliveryman.riderReviews;

public class RiderReviewsComparable {

    private String date;
    private String rating;
    private String name;
    private String comment;
    private String key;


    public RiderReviewsComparable(String date, String rating, String name, String comment, String key) {
        this.date = date;
        this.rating = rating;
        this.name = name;
        this.comment = comment;
        this.key = key;
    }

    public RiderReviewsComparable() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRating() {
        return rating;}


    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
