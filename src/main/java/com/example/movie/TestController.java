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

    // 검색을 처리 (영화, TV, 인물 통합 검색)
    @GetMapping("/search")
    public String getSearchResults(@RequestParam("query") String query, Model model) {
        // 통합 검색 결과를 가져옵니다.
        String jsonResult = testService.searchByQuery(query);

        // JSON 데이터를 파싱하여 결과 목록에 추가
        JSONObject jsonResponse = new JSONObject(jsonResult);
        JSONArray results = jsonResponse.getJSONArray("results");

        // 모델에 검색 결과를 추가 (검색 결과 리스트를 넘겨줍니다)
        model.addAttribute("searchResults", results);
        return "test"; // 검색 결과를 포함하여 test.html 템플릿을 반환
    }
}
