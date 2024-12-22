package com.example.carpooling;
public class car {
    private String name;
    private int price;
    private int people;

    public car(String name, int price, int people) {
        this.name = name;
        this.price = price;
        this.people = people;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getPeople() {
        return people;
    }

    public void setName(String name) {
        this.name=name;
    }
    public void setPrice(int price) {
        this.price=price;
    }
    public void setPeople(int people) {
        this.people=people;
    }
}