package com.example.movie;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;          // 영화 제목
    private String releaseDate;     // 개봉 연도
    private String filePath;        // 파일의 실제 경로 (선택 사항)

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // 생성 시간 필드

    @OneToOne(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private MovieDetails movieDetails; // MovieDetails와의 1:1 관계

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
