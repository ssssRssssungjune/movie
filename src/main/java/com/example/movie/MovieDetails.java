package com.example.movie;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class MovieDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "movie_id")
    private Movie movie; // Movie와의 관계

    private String overview;
    private String posterPath;
    private Double rating;
    private String releaseDate;
    private Long tmdbId; // tmdbId 추가
}

