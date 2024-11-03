package com.example.movie;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class MovieDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String overview;        // 영화 개요
    private String posterPath;      // 포스터 경로
    private String releaseDate;     // 개봉 연도
    private Double rating;          // 평점

    @OneToOne
    @JoinColumn(name = "movie_id", referencedColumnName = "id", unique = true)
    private Movie movie; // movie 테이블과 연관된 ID
}
