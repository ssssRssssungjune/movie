package com.example.movie.service;

import com.example.movie.Movie;
import com.example.movie.MovieDetails;
import com.example.movie.dto.MovieDTO;
import com.example.movie.dto.MovieResponse;
import com.example.movie.repositories.MovieDetailsRepository;
import com.example.movie.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final MovieDetailsRepository movieDetailsRepository;
    private final RestTemplate restTemplate;

    @Value("${tmdb.api.key}")
    private String tmdbApiKey;

    public MovieService(MovieRepository movieRepository, MovieDetailsRepository movieDetailsRepository, RestTemplate restTemplate) {
        this.movieRepository = movieRepository;
        this.movieDetailsRepository = movieDetailsRepository;
        this.restTemplate = restTemplate;
    }

    public void fetchAndSaveMovieDetails() {
        List<Movie> movies = movieRepository.findAll();

        for (Movie movie : movies) {
            String title = movie.getTitle();
            String releaseYear = movie.getReleaseDate();

            // 한국어로 데이터 요청
            String urlKo = String.format(
                    "https://api.themoviedb.org/3/search/movie?api_key=%s&query=%s&year=%s&language=ko-KR",
                    tmdbApiKey, title, releaseYear
            );

            MovieResponse response = restTemplate.getForObject(urlKo, MovieResponse.class);

            // 한국어 데이터가 없으면 영어로 다시 요청
            if (response == null || response.getResults().isEmpty()) {
                String urlEn = String.format(
                        "https://api.themoviedb.org/3/search/movie?api_key=%s&query=%s&year=%s&language=en-US",
                        tmdbApiKey, title, releaseYear
                );
                response = restTemplate.getForObject(urlEn, MovieResponse.class);
            }

            // 데이터를 저장하거나 업데이트하는 부분
            if (response != null && !response.getResults().isEmpty()) {
                MovieDTO movieData = response.getResults().get(0);

                // 기존 MovieDetails를 가져와서 업데이트 또는 새로 저장
                MovieDetails movieDetails = movieDetailsRepository.findByMovieId(movie.getId())
                        .orElse(new MovieDetails());

                // 영화 정보 설정
                movieDetails.setMovie(movie);
                movieDetails.setOverview(movieData.getOverview());
                movieDetails.setPosterPath(movieData.getPosterPath());
                movieDetails.setReleaseDate(movieData.getReleaseDate().substring(0, 4));
                movieDetails.setRating(movieData.getRating());

                // 새로운 데이터든 업데이트든 저장
                movieDetailsRepository.save(movieDetails);
            }
        }
    }


    public List<Movie> getAllMoviesWithDetails() {
        return movieRepository.findAllMoviesWithDetails();
    }
}
