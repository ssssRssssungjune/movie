package com.example.movie.repositories;

import com.example.movie.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query("SELECT m FROM Movie m LEFT JOIN FETCH m.movieDetails")
    List<Movie> findAllMoviesWithDetails();
    boolean existsByTitleAndReleaseDate(String title, String releaseDate);
}


