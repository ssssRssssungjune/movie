package com.example.movie.repositories;

import com.example.movie.MovieDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MovieDetailsRepository extends JpaRepository<MovieDetails, Long> {
    Optional<MovieDetails> findByMovieId(Long movieId);
}
