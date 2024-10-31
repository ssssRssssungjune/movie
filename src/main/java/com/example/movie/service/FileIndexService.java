package com.example.movie.service;

import com.example.movie.Movie;
import com.example.movie.repositories.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.io.File;


@Service
public class FileIndexService {

    private final MovieRepository movieRepository;

    public FileIndexService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public void indexFiles(String directoryPath) {
        File directory = new File(directoryPath);
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".mp4") || name.endsWith(".mkv")); // 원하는 확장자만 필터

        if (files != null) {
            List<Movie> movies = new ArrayList<>();
            for (File file : files) {
                String title = extractTitle(file.getName());
                Movie movie = new Movie();
                movie.setTitle(title);
                movie.setFilePath(file.getAbsolutePath());
                movies.add(movie);
            }
            movieRepository.saveAll(movies);
        }
    }

    private String extractTitle(String fileName) {
        // 예시: "The.Super.Mario.Bros.Movie.2023.mp4"에서 제목 추출
        String title = fileName.replaceAll("\\.\\w+$", ""); // 확장자 제거
        title = title.replaceAll("[._]", " "); // 구분자 처리
        return title;
    }
}