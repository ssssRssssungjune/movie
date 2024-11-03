package com.example.movie.controller;

import com.example.movie.Movie;
import com.example.movie.MovieDetails;
import com.example.movie.dto.MovieDTO;
import com.example.movie.dto.MovieResponse;
import com.example.movie.repositories.MovieDetailsRepository;
import com.example.movie.repositories.MovieRepository;
import com.example.movie.service.FileIndexService;
import com.example.movie.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.util.List;
import java.util.Optional;

@Controller
public class MovieController {

    private final FileIndexService fileIndexService;
    private final MovieService movieService;
    private final MovieRepository movieRepository;
    private final MovieDetailsRepository movieDetailsRepository;
    private final RestTemplate restTemplate;

    @Value("${tmdb.api.key}") // application.properties에 설정한 API 키를 주입받음
    private String tmdbApiKey;

    @Autowired
    public MovieController(FileIndexService fileIndexService, MovieService movieService, MovieRepository movieRepository, MovieDetailsRepository movieDetailsRepository, RestTemplate restTemplate) {
        this.fileIndexService = fileIndexService;
        this.movieService = movieService;
        this.movieRepository = movieRepository;
        this.movieDetailsRepository = movieDetailsRepository;
        this.restTemplate = restTemplate;
    }

    // 루트 경로와 기본 경로로 홈 화면을 보여주는 GET 요청
    @GetMapping({"/", "/home"})
    public String showHomePage(Model model) {
        List<Movie> movies = movieService.getAllMoviesWithDetails();
        model.addAttribute("movies", movies); // 영화 목록 추가
        return "home"; // templates/home.html 반환
    }

    // 폴더 경로를 입력받아 인덱싱을 수행하는 POST 요청
    @PostMapping("/index")
    public String indexMovies(@RequestParam String directory, Model model) {
        try {
            fileIndexService.indexFiles(directory); // 폴더 인덱싱 수행
            movieService.fetchAndSaveMovieDetails(); // 영화 세부 정보 업데이트
            model.addAttribute("message", "영화 인덱싱 및 데이터 업데이트가 완료되었습니다.");
        } catch (Exception e) {
            model.addAttribute("error", "인덱싱 중 오류가 발생했습니다: " + e.getMessage());
        }
        List<Movie> movies = movieService.getAllMoviesWithDetails();
        model.addAttribute("movies", movies); // 업데이트된 영화 목록 추가
        return "home";
    }

    @GetMapping("/movies/list")
    public String showMovieList(Model model) {
        List<Movie> movies = movieService.getAllMoviesWithDetails();
        model.addAttribute("movies", movies); // 영화 목록 추가
        return "movieList"; // templates/movieList.html 반환
    }

    @GetMapping("/movies/search")
    public String searchMovie(@RequestParam String query, Model model) {
        String url = String.format(
                "https://api.themoviedb.org/3/search/movie?api_key=%s&query=%s&language=ko-KR",
                tmdbApiKey, query
        );

        MovieResponse response = restTemplate.getForObject(url, MovieResponse.class);
        if (response != null && !response.getResults().isEmpty()) {
            model.addAttribute("searchResults", response.getResults());
        } else {
            model.addAttribute("noResults", true);
        }
        model.addAttribute("query", query);
        return "searchResults"; // 검색 결과를 표시할 템플릿
    }

    @PostMapping("/movies/select")
    public String selectMovie(@RequestParam Long movieId, Model model) {
        MovieDTO movieData = fetchMovieDataFromTMDB(movieId);
        if (movieData != null) {
            saveOrUpdateMovie(movieId, movieData);
        }
        return "redirect:/movies/list"; // 영화 목록 페이지로 리디렉션
    }




    @GetMapping("/movies/search/ajax")
    public String searchMovieAjax(@RequestParam String query, Model model) {
        try {
            String url = String.format(
                    "https://api.themoviedb.org/3/search/movie?api_key=%s&query=%s&language=ko-KR",
                    tmdbApiKey, URLEncoder.encode(query, "UTF-8")
            );

            MovieResponse response = restTemplate.getForObject(url, MovieResponse.class);
            if (response != null && !response.getResults().isEmpty()) {
                model.addAttribute("searchResults", response.getResults());
                model.addAttribute("noResults", false);  // 결과가 있을 때는 false 설정
            } else {
                model.addAttribute("noResults", true);  // 결과가 없을 때는 true 설정
            }
        } catch (Exception e) {
            System.err.println("API 호출 중 오류 발생: " + e.getMessage());
            model.addAttribute("noResults", true);  // 예외 발생 시에도 true 설정
        }
        return "searchResults :: searchResults";
    }



    // TMDB에서 영화 정보를 가져오는 메서드
    private MovieDTO fetchMovieDataFromTMDB(Long movieId) {
        String url = String.format(
                "https://api.themoviedb.org/3/movie/%d?api_key=%s&language=ko-KR",
                movieId, tmdbApiKey
        );
        return restTemplate.getForObject(url, MovieDTO.class);
    }

    private void saveOrUpdateMovie(Long movieId, MovieDTO movieData) {
        Optional<Movie> existingMovie = movieRepository.findById(movieId);
        Movie movie;
        if (existingMovie.isPresent()) {
            movie = existingMovie.get();
        } else {
            movie = new Movie();
            movie.setId(movieId); // 새로운 Movie 객체에 ID 설정
        }
        movie.setTitle(movieData.getTitle());
        movie.setReleaseDate(movieData.getReleaseDate().substring(0, 4));
        movieRepository.save(movie);

        // MovieDetails 생성 또는 업데이트
        MovieDetails movieDetails = movie.getMovieDetails() != null ? movie.getMovieDetails() : new MovieDetails();
        movieDetails.setMovie(movie);
        movieDetails.setOverview(movieData.getOverview());
        movieDetails.setPosterPath(movieData.getPosterPath());
        movieDetails.setReleaseDate(movieData.getReleaseDate().substring(0, 4));
        movieDetails.setRating(movieData.getRating());

        movieDetailsRepository.save(movieDetails);
    }

}
