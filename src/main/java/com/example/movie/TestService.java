package com.example.movie;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Service
public class TestService {

    private final String tmdbApiUrl = "https://api.themoviedb.org/3/search/multi";
    private final String apiKey = "3a6920958689c3bf449ae013bcb1c38d"; // API 키를 상수로 정의

    // 검색어를 이용하여 TMDB API에서 통합 검색
    public String searchByQuery(String query) {
        try {
            // 검색어를 URL 인코딩
            String encodedQuery = URLEncoder.encode(query, "UTF-8");
            // TMDB API에 요청을 보내는 URL (언어 파라미터 추가)
            String url = tmdbApiUrl + "?api_key=" + apiKey + "&query=" + encodedQuery + "&language=ko-KR";

            // RestTemplate을 이용해 API 요청을 보냅니다.
            RestTemplate restTemplate = new RestTemplate();
            return restTemplate.getForObject(url, String.class);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
