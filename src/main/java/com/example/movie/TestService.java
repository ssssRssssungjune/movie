package com.example.movie;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Service
public class TestService {

    private final String apiKey = "3a6920958689c3bf449ae013bcb1c38d"; // 여기에 TMDB API 키를 입력하세요
    private final String tmdbApiUrl = "https://api.themoviedb.org/3/search/movie";

    // 영화 제목을 이용하여 TMDB API에서 영화 검색
    public String searchMoviesByTitle(String title) {
        try {
            // 검색어를 URL 인코딩
            String encodedTitle = URLEncoder.encode(title, "UTF-8");
            // TMDB API에 요청을 보내는 URL (언어 파라미터 추가)
            String url = tmdbApiUrl + "?api_key=" + apiKey + "&query=" + encodedTitle + "&language=ko-KR";

            // RestTemplate을 이용해 API 요청을 보냅니다.
            RestTemplate restTemplate = new RestTemplate();
            return restTemplate.getForObject(url, String.class);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
