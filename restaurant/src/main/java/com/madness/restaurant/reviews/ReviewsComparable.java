package com.madness.restaurant.reviews;

public class ReviewsComparable {

    private String date;
    private Long rating;
    private String name;
    private String comment;
    private String key;


    public ReviewsComparable(String date, Long rating, String name, String comment, String key) {
        this.date = date;
        this.rating = rating;
        this.name = name;
        this.comment = comment;
        this.key = key;
    }

    public ReviewsComparable() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getRating() {
        return rating;}


    public void setRating(Long rating) {
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
