package com.example.films;

/**
 * Created by Егор on 17.10.2018.
 */

public class Film {
    public int id;
    public String localized_name;
    public String name;
    public int year;
    public double rating;
    public String image_url;
    public String description;
    public boolean isSectionHeader = false;

    public Film(String localized_name, String name, int year, double rating, String image_url, String description) {
        this.id = 0;
        this.localized_name = localized_name;
        this.name = name;
        this.year = year;
        this.rating = rating;
        this.image_url = image_url;
        this.description = description;
        this.isSectionHeader = false;
    }
}
