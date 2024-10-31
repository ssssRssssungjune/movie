package com.example.movie.dto;

import java.util.List;

public class MovieResponse {

    private List<MovieDTO> results;

    // Getter, Setter
    public List<MovieDTO> getResults() {
        return results;
    }

    public void setResults(List<MovieDTO> results) {
        this.results = results;
    }
}
