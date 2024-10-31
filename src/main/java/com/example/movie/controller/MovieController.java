package com.example.movie.controller;

import com.example.movie.service.MovieService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    // GET 요청을 통해 index.html 폼을 반환
    @GetMapping("/index")
    public String showIndexForm() {
        return "index"; // templates/index.html을 반환
    }

    // POST 요청을 통해 디렉토리 인덱싱 수행
    @PostMapping("/index")
    public String indexMovies(@RequestParam String directoryPath, Model model) {
        movieService.fetchAndUpdateMovieData(directoryPath);
        model.addAttribute("message", "Movie indexing and data update complete.");
        return "index"; // 완료 메시지를 표시하며 index.html로 다시 리디렉션
    }
}
