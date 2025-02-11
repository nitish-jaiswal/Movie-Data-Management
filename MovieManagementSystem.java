package org.example;

import java.io.File;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

class Movie {
    int id;
    String title;
    int year;
    String genre;
    double rating;
    int duration;
    Director director;
    List<Actor> actors;

    public Movie(int id, String title, int year, String genre, double rating,
                 int duration, Director director, List<Actor> actors) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.genre = genre;
        this.rating = rating;
        this.duration = duration;
        this.director = director;
        this.actors = actors;
    }
}

class Actor {
    int id;
    String name;
    LocalDate dob;
    String nationality;

    public Actor(int id, String name, LocalDate dob, String nationality) {
        this.id = id;
        this.name = name;
        this.dob = dob;
        this.nationality = nationality;
    }

    public int getAge(LocalDate asOf) {
        return Period.between(dob, asOf).getYears();
    }
}

class Director {
    int id;
    String name;
    LocalDate dob;
    String nationality;

    public Director(int id, String name, LocalDate dob, String nationality) {
        this.id = id;
        this.name = name;
        this.dob = dob;
        this.nationality = nationality;
    }
}

public class MovieManagementSystem {

}