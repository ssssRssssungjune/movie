package com.example.movie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.json.JSONArray;
import org.json.JSONObject;

@Controller
public class TestController {

    @Autowired
    private TestService testService;

    // 기본 경로로 접속했을 때 검색 페이지를 반환
    @GetMapping("/")
    public String showSearchPage() {
        return "test"; // test.html 템플릿을 반환
    }

    // 영화 검색을 처리
    @GetMapping("/movie")
    public String getMovieDetails(@RequestParam String title, Model model) {
        // 영화 검색 결과를 가져옵니다.
        String jsonResult = testService.searchMoviesByTitle(title);

        // JSON 데이터를 파싱하여 movieList에 추가
        JSONObject jsonResponse = new JSONObject(jsonResult);
        JSONArray results = jsonResponse.getJSONArray("results");

        // 모델에 검색 결과를 추가
        model.addAttribute("movieList", results);
        return "test"; // 검색 결과를 포함하여 test.html 템플릿을 반환
    }
}
