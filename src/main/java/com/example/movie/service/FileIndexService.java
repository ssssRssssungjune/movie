package com.example.movie.service;

import com.example.movie.Movie;
import com.example.movie.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class FileIndexService {

    @Autowired
    private MovieRepository movieRepository;

    public void indexFiles(String directoryPath) {
        File directory = new File(directoryPath);
        indexFilesRecursively(directory);
    }

    private void indexFilesRecursively(File directory) {
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // 하위 폴더에 대해 재귀 호출
                    indexFilesRecursively(file);
                } else if (file.isFile() && isValidFile(file)) {
                    String title = extractTitleFromFile(file);
                    int releaseYear = extractYearFromFile(file);

                    // releaseDate와 filePath 설정
                    String releaseDateStr = String.valueOf(releaseYear);
                    String filePath = file.getAbsolutePath();

                    // 중복 체크
                    boolean exists = movieRepository.existsByTitleAndReleaseDate(title, releaseDateStr);
                    if (!exists) {
                        Movie movie = new Movie();
                        movie.setTitle(title);
                        movie.setReleaseDate(releaseDateStr); // 연도만 저장
                        movie.setFilePath(filePath); // 파일 경로 저장

                        movieRepository.save(movie);
                    }
                }
            }
        }
    }

    private boolean isValidFile(File file) {
        String fileName = file.getName().toLowerCase();
        return !(fileName.endsWith(".srt") || fileName.endsWith(".sub") || fileName.endsWith(".vtt")
                || fileName.endsWith(".smi") || fileName.endsWith(".txt") || fileName.endsWith(".png")
                || fileName.endsWith(".jpg") || fileName.endsWith(".ass"));
    }

    private String extractTitleFromFile(File file) {
        String fileName = file.getName();
        fileName = fileName.substring(0, fileName.lastIndexOf('.')); // 확장자 제거

        // 정규식을 사용하여 제목과 연도 추출
        Pattern pattern = Pattern.compile("(.+?)\\.(\\d{4})");
        Matcher matcher = pattern.matcher(fileName);
        if (matcher.find()) {
            return matcher.group(1).replace(".", " ").trim(); // 제목 추출
        }
        return fileName; // 연도가 없으면 전체 파일명을 반환
    }

    private int extractYearFromFile(File file) {
        String fileName = file.getName();
        fileName = fileName.substring(0, fileName.lastIndexOf('.')); // 확장자 제거

        Pattern pattern = Pattern.compile("(.+?)\\.(\\d{4})");
        Matcher matcher = pattern.matcher(fileName);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(2)); // 연도 추출
        }
        return 0; // 연도가 없으면 0 반환
    }
}