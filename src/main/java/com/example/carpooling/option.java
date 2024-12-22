package com.example.carpooling;

public class option {
    public String username;
    public float rating;
    public int price;
    public String car_name;
    public String From;
    public String To;
    public String Time;

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username =username;
    }

    public String getTime() {
        return Time;
    }
    public void setTime(String Time) {
        this.Time =Time;
    }


    public float getRating() {
        return rating;
    }
    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price=price;
    }

    public String getCarName() {
        return car_name;
    }
    public void setCarName(String name) {
        this.car_name =name;
    }
    public String getFrom() {
        return From;
    }
    public void setFrom(String from) {
        this.From =from;
    }
    public String getTo() {
        return To;
    }
    public void setTo(String to) {
        this.To =to;
    }

}
