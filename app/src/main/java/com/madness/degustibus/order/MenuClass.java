package com.madness.degustibus.order;

public class MenuClass {

    private Integer avail;
    private Integer count;
    private String description;
    private String dish;
    private String pic;
    private Integer popular;
    private Float price;
    private Integer rating;

    public MenuClass() {
    }

    public MenuClass(Integer avail, Integer count, String description, String dish, String pic, Integer popular, Float price, Integer rating) {
        this.avail = avail;
        this.count = count;
        this.description = description;
        this.dish = dish;
        this.pic = pic;
        this.popular = popular;
        this.price = price;
        this.rating = rating;
    }

    public Integer getAvail() {
        return avail;
    }

    public void setAvail(Integer avail) {
        this.avail = avail;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDish() {
        return dish;
    }

    public void setDish(String dish) {
        this.dish = dish;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public Integer getPopular() {
        return popular;
    }

    public void setPopular(Integer popular) {
        this.popular = popular;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
}
