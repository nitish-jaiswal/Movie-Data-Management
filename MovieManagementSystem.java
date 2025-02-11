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
    static List<Movie> movies = new ArrayList<>();
    static List<Actor> actors = new ArrayList<>();
    static List<Director> directors = new ArrayList<>();
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        loadData();
        while (true) {
            showMenu();
            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1: getMovieInfo(); break;
                case 2: getTopRatedMovies(); break;
                case 3: getMoviesByGenre(); break;
                case 4: getMoviesByDirector(); break;
                case 5: getMoviesByYear(); break;
                case 6: getMoviesByYearRange(); break;
                case 7: addNewMovie(); break;
                case 8: updateMovieRating(); break;
                case 9: deleteMovie(); break;
                case 10: getSortedMoviesByYear(); break;
                case 11: getTopDirectors(); break;
                case 12: getMostActiveActor(); break;
                case 13: getYoungestActorMovies(); break;
                case 14: System.exit(0);
                default: System.out.println("Invalid choice!");
            }
        }
    }

    static void showMenu() {
        System.out.println("\n=== Movie Management System ===");
        System.out.println("1. Get Movie Information");
        System.out.println("2. Get Top 10 Rated Movies");
        System.out.println("3. Get Movies by Genre");
        System.out.println("4. Get Movies by Director");
        System.out.println("5. Get Movies by Release Year");
        System.out.println("6. Get Movies by Year Range");
        System.out.println("7. Add New Movie");
        System.out.println("8. Update Movie Rating");
        System.out.println("9. Delete Movie");
        System.out.println("10. Get 15 Movies Sorted by Year");
        System.out.println("11. Get Top 5 Directors");
        System.out.println("12. Get Most Active Actor");
        System.out.println("13. Get Youngest Actor's Movies");
        System.out.println("14. Exit");
        System.out.print("Enter your choice: ");
    }

    static void loadData() {
        try {
            // Load Actors
            Scanner actorFile = new Scanner(new File("src/main/java/org/example/resources/actors_large.csv"));
            actorFile.nextLine(); // skip header
            while (actorFile.hasNextLine()) {
                String[] data = actorFile.nextLine().split("\t");
                actors.add(new Actor(
                        Integer.parseInt(data[0]),
                        data[1],
                        LocalDate.parse(data[2]),
                        data[3]
                ));
            }

            // Load Directors
            Scanner dirFile = new Scanner(new File("src/main/java/org/example/resources/directors_large.csv"));
            dirFile.nextLine(); // skip header
            while (dirFile.hasNextLine()) {
                String[] data = dirFile.nextLine().split("\t");
                directors.add(new Director(
                        Integer.parseInt(data[0]),
                        data[1],
                        LocalDate.parse(data[2]),
                        data[3]
                ));
            }

            // Load Movies
            Scanner movieFile = new Scanner(new File("src/main/java/org/example/resources/movies_large.csv"));
            movieFile.nextLine(); // skip header
            while (movieFile.hasNextLine()) {
                String[] data = movieFile.nextLine().split("\t");

                // Find director
                int dirId = Integer.parseInt(data[6]);
                Director director = directors.stream()
                        .filter(d -> d.id == dirId)
                        .findFirst()
                        .orElse(null);

                // Find actors
                List<Actor> movieActors = new ArrayList<>();
                String[] actorIds = data[7].split(",");
                for (String id : actorIds) {
                    int actorId = Integer.parseInt(id);
                    actors.stream()
                            .filter(a -> a.id == actorId)
                            .findFirst()
                            .ifPresent(movieActors::add);
                }

                movies.add(new Movie(
                        Integer.parseInt(data[0]),
                        data[1],
                        Integer.parseInt(data[2]),
                        data[3],
                        Double.parseDouble(data[4]),
                        Integer.parseInt(data[5]),
                        director,
                        movieActors
                ));
            }
        } catch (Exception e) {
            System.out.println("Error loading data: " + e.getMessage());
            System.exit(1);
        }
    }

    static void getMovieInfo() {
        System.out.print("Enter movie ID or title: ");
        String input = sc.nextLine();

        movies.stream()
                .filter(m -> String.valueOf(m.id).equals(input) || m.title.equalsIgnoreCase(input))
                .findFirst()
                .ifPresentOrElse(
                        m -> {
                            System.out.println("\nMovie Details:");
                            System.out.println("Title: " + m.title);
                            System.out.println("Year: " + m.year);
                            System.out.println("Genre: " + m.genre);
                            System.out.println("Rating: " + m.rating);
                            System.out.println("Duration: " + m.duration + " minutes");
                            System.out.println("Director: " + m.director.name);
                            System.out.println("Actors: " +
                                    m.actors.stream()
                                            .map(a -> a.name)
                                            .reduce((a, b) -> a + ", " + b)
                                            .orElse("No actors listed")
                            );
                        },
                        () -> System.out.println("Movie not found!")
                );
    }

    static void getTopRatedMovies() {
        movies.stream()
                .sorted((a, b) -> Double.compare(b.rating, a.rating))
                .limit(10)
                .forEach(m -> System.out.println(m.title + " - Rating: " + m.rating));
    }

    static void getMoviesByGenre() {
        System.out.print("Enter genre: ");
        String genre = sc.nextLine();

        movies.stream()
                .filter(m -> m.genre.equalsIgnoreCase(genre))
                .forEach(m -> System.out.println(m.title + " (" + m.year + ")"));
    }

    static void getMoviesByDirector() {
        System.out.print("Enter director name: ");
        String name = sc.nextLine();

        movies.stream()
                .filter(m -> m.director.name.equalsIgnoreCase(name))
                .forEach(m -> System.out.println(m.title + " (" + m.year + ")"));
    }

    static void getMoviesByYear() {
        System.out.print("Enter year: ");
        int year = sc.nextInt();

        movies.stream()
                .filter(m -> m.year == year)
                .forEach(m -> System.out.println(m.title + " - " + m.genre));
    }

    static void getMoviesByYearRange() {
        System.out.print("Enter start year: ");
        int start = sc.nextInt();
        System.out.print("Enter end year: ");
        int end = sc.nextInt();

        movies.stream()
                .filter(m -> m.year >= start && m.year <= end)
                .forEach(m -> System.out.println(m.title + " (" + m.year + ")"));
    }

    static void addNewMovie() {
        try {
            System.out.println("Enter movie details:");
            System.out.print("ID: ");
            int id = sc.nextInt();
            sc.nextLine();

            System.out.print("Title: ");
            String title = sc.nextLine();

            System.out.print("Year: ");
            int year = sc.nextInt();
            sc.nextLine();

            System.out.print("Genre: ");
            String genre = sc.nextLine();

            System.out.print("Rating: ");
            double rating = sc.nextDouble();

            System.out.print("Duration (minutes): ");
            int duration = sc.nextInt();

            System.out.print("Director ID: ");
            int dirId = sc.nextInt();

            Director director = directors.stream()
                    .filter(d -> d.id == dirId)
                    .findFirst()
                    .orElseThrow(() -> new Exception("Director not found"));

            List<Actor> movieActors = new ArrayList<>();
            System.out.print("Enter actor IDs (comma-separated): ");
            sc.nextLine();
            String[] actorIds = sc.nextLine().split(",");

            for (String actorId : actorIds) {
                int aid = Integer.parseInt(actorId.trim());
                actors.stream()
                        .filter(a -> a.id == aid)
                        .findFirst()
                        .ifPresent(movieActors::add);
            }

            movies.add(new Movie(id, title, year, genre, rating, duration, director, movieActors));
            System.out.println("Movie added successfully!");

        } catch (Exception e) {
            System.out.println("Error adding movie: " + e.getMessage());
        }
    }

    static void updateMovieRating() {
        System.out.print("Enter movie ID: ");
        int id = sc.nextInt();
        System.out.print("Enter new rating: ");
        double rating = sc.nextDouble();

        movies.stream()
                .filter(m -> m.id == id)
                .findFirst()
                .ifPresentOrElse(
                        m -> {
                            m.rating = rating;
                            System.out.println("Rating updated successfully!");
                        },
                        () -> System.out.println("Movie not found!")
                );
    }

    static void deleteMovie() {
        System.out.print("Enter movie ID: ");
        int id = sc.nextInt();

        if (movies.removeIf(m -> m.id == id)) {
            System.out.println("Movie deleted successfully!");
        } else {
            System.out.println("Movie not found!");
        }
    }

    static void getSortedMoviesByYear() {
        movies.stream()
                .sorted((a, b) -> Integer.compare(a.year, b.year))
                .limit(15)
                .forEach(m -> System.out.println(m.title + " (" + m.year + ")"));
    }

    static void getTopDirectors() {
        Map<Director, Integer> directorCount = new HashMap<>();

        for (Movie m : movies) {
            directorCount.put(m.director, directorCount.getOrDefault(m.director, 0) + 1);
        }

        directorCount.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(5)
                .forEach(e -> System.out.println(e.getKey().name + " - Movies: " + e.getValue()));
    }

    static void getMostActiveActor() {
        Map<Actor, Integer> actorCount = new HashMap<>();

        for (Movie m : movies) {
            for (Actor a : m.actors) {
                actorCount.put(a, actorCount.getOrDefault(a, 0) + 1);
            }
        }

        actorCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .ifPresent(e -> {
                    Actor a = e.getKey();
                    System.out.println("Most active actor: " + a.name);
                    System.out.println("Movies acted in: " + e.getValue());
                    System.out.println("Nationality: " + a.nationality);
                    System.out.println("Date of Birth: " + a.dob);
                });
    }

    static void getYoungestActorMovies() {
        LocalDate asOf = LocalDate.of(2025, 2, 10);

        Actor youngest = actors.stream()
                .min(Comparator.comparing(a -> a.getAge(asOf)))
                .orElse(null);

        if (youngest != null) {
            System.out.println("Youngest actor: " + youngest.name);
            System.out.println("Age as of " + asOf + ": " + youngest.getAge(asOf));
            System.out.println("\nMovies:");

            movies.stream()
                    .filter(m -> m.actors.contains(youngest))
                    .forEach(m -> System.out.println(m.title + " (" + m.year + ")"));
        }
    }
}