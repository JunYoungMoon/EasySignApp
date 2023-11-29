package com.member.easysignapp.util;

public class FileUtil {

    public static String sanitizeFileName(String fileName) {
        // 특수 문자 및 공백을 언더스코어로 대체하는 정규표현식 사용
        // 원하는 정리 규칙에 따라 수정 가능
        return fileName.replaceAll("[^a-zA-Z0-9.-]", "_");
    }
}

