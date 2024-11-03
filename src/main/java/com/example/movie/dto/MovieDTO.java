package com.example.movie.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieDTO {

    private Long id; // TMDB ID 필드

    private Long tmdbId;

    private String title;
    private String overview;

    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("release_date")
    private String releaseDate;

    @JsonProperty("vote_average")
    private Double rating;  // 평점 필드

    private String filePath; // 파일 경로 필드
}
