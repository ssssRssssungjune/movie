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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.util.List;

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

    @GetMapping("/selectDetails")
    public String showMovieDetails(@RequestParam("movieId") Long movieId, Model model) {
        // movieId를 사용하여 영화 상세 정보를 가져옴
        Movie movie = movieService.getMovieDetails(movieId);
        model.addAttribute("movie", movie);
        return "selectDetails";  // selectDetails.html 페이지로 이동
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
    public String selectMovie(@RequestParam Long movieId,
                              @RequestParam String title,
                              @RequestParam String releaseDate,
                              @RequestParam Double rating,
                              @RequestParam String overview,
                              @RequestParam String posterPath,
                              @RequestParam Long tmdbId) {
        // Movie 엔티티를 업데이트
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found with ID: " + movieId));

        // Movie 엔티티 업데이트
        movie.setTitle(title);
        movie.setReleaseDate(releaseDate);
        movie.setRating(rating);
        movie.setOverview(overview);
        movie.setPosterPath(posterPath);
        movie.setTmdbId(tmdbId); // tmdbId를 저장하는 메서드가 있어야 함

        // movie 엔티티 저장
        movieRepository.save(movie);

        // MovieDetails 엔티티 생성 및 저장
        MovieDetails movieDetails = new MovieDetails();
        movieDetails.setMovie(movie); // movie와의 관계 설정
        movieDetails.setOverview(overview);
        movieDetails.setPosterPath(posterPath);
        movieDetails.setRating(rating);
        movieDetails.setReleaseDate(releaseDate);

        movieDetailsRepository.save(movieDetails); // movieDetails 저장

        return "redirect:/"; // 적절한 리다이렉트 경로 설정
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

    @Transactional
    public void updateMovie(Long movieId, MovieDTO movieData) {
        // Movie 엔티티 조회 (없으면 예외 발생)
        Movie movie = movieRepository.findById(movieId).orElseThrow(() ->
                new IllegalArgumentException("Movie not found with ID: " + movieId));

        // Movie 엔티티 필드 업데이트
        movie.setTitle(movieData.getTitle());
        movie.setReleaseDate(movieData.getReleaseDate());

        // MovieDetails 업데이트
        MovieDetails movieDetails = movie.getMovieDetails();
        if (movieDetails == null) {
            movieDetails = new MovieDetails();
            movieDetails.setMovie(movie); // Movie와의 관계 설정
        }
        movieDetails.setOverview(movieData.getOverview());
        movieDetails.setPosterPath(movieData.getPosterPath());
        movieDetails.setRating(movieData.getRating());
        movieDetails.setReleaseDate(movieData.getReleaseDate());

        // 연관 관계 설정
        movie.setMovieDetails(movieDetails);

        // 저장
        movieRepository.save(movie);
    }




}








