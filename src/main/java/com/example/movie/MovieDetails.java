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
    @JoinColumn(name = "movie_id") // 외래 키 컬럼 명 지정
    private Movie movie; // Movie와의 1:1 관계

    private String overview;
    private String posterPath;
    private Double rating;
    private String releaseDate;

    @OneToOne
    @JoinColumn(name = "movie_id", referencedColumnName = "id", unique = true)
    private Movie movie; // movie 테이블과 연관된 ID
}
