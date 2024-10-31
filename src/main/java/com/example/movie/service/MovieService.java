package com.example.movie.service;

import com.example.movie.Movie;
import com.example.movie.dto.MovieDTO;
import com.example.movie.dto.MovieResponse;
import com.example.movie.repositories.MovieRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class MovieService {

    private final FileIndexService fileIndexService;
    private final MovieRepository movieRepository;
    private final RestTemplate restTemplate;
    private final String apiKey = "3a6920958689c3bf449ae013bcb1c38d";

    public MovieService(FileIndexService fileIndexService, MovieRepository movieRepository, RestTemplate restTemplate) {
        this.fileIndexService = fileIndexService;
        this.movieRepository = movieRepository;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public void fetchAndUpdateMovieData(String directoryPath) {
        fileIndexService.indexFiles(directoryPath); // 파일 인덱싱 수행

        List<Movie> movies = movieRepository.findAll();
        for (Movie movie : movies) {
            if (movie.getOverview() == null) { // 아직 API 데이터를 가져오지 않은 영화만 처리
                MovieDTO fetchedMovieDTO = fetchMovieData(movie.getTitle());
                if (fetchedMovieDTO != null) {
                    movie.setOverview(fetchedMovieDTO.getOverview());
                    movie.setPosterPath(fetchedMovieDTO.getPosterPath());
                    movie.setReleaseDate(fetchedMovieDTO.getReleaseDate());
                    movieRepository.save(movie); // 데이터베이스 업데이트
                }
            }
        }
    }

    private MovieDTO fetchMovieData(String title) {
        String url = "https://api.themoviedb.org/3/search/movie?api_key=" + apiKey + "&query=" + title;
        MovieResponse response = restTemplate.getForObject(url, MovieResponse.class);

        if (response != null && !response.getResults().isEmpty()) {
            return response.getResults().get(0); // 첫 번째 결과만 사용
        }
        return null;
    }
}
