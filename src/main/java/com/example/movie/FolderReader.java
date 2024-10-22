package com.example.movie;

import java.io.File;

public class FolderReader {
    public static void main(String[] args) {
        // 읽고자 하는 폴더 경로
        String folderPath = "D:/";

        // File 객체 생성
        File folder = new File(folderPath);

        // 폴더가 존재하는지 확인
        if (folder.exists() && folder.isDirectory()) {
            System.out.println("폴더 이름: " + folder.getName());

            // 폴더 내부의 파일 및 하위 폴더 목록 가져오기
            String[] fileNames = folder.list();
            System.out.println("폴더 내 항목들:");
            if (fileNames != null) {
                for (String name : fileNames) {
                    System.out.println(name);
                }
            } else {
                System.out.println("해당 폴더가 비어있습니다.");
            }
        } else {
            System.out.println("폴더가 존재하지 않거나 경로가 잘못되었습니다.");
        }
    }
}
