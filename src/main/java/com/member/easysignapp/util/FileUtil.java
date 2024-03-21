package com.member.easysignapp.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class FileUtil {

    // 파일명에서 특수 문자 제거
    public static String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
    }

    // 파일명 생성을 위한 유틸리티 메서드
    public static String generateUniqueFileName(String uuid, String originalFileName, String fileExtension) {
        try {
            // 파일명을 해시로 변환
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(originalFileName.getBytes());

            // 해시를 Base64로 인코딩하고 100자로 자름
            String encoded = Base64.getEncoder().encodeToString(hash);

            // '/' 문자를 '_'로, '+' 문자를 '-'로 대체
            String replaced = encoded.replace('/', '_').replace('+', '-');

            // 최대 길이를 100자로 제한
            String trimmed = replaced.substring(0, Math.min(replaced.length(), 100));

            // UUID와 함께 조합하여 파일명 생성
            return uuid + "：：" + trimmed + fileExtension;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm not found", e);
        }
    }

    // 파일 확장자 추출
    public static String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            return fileName.substring(dotIndex);
        }
        return "";
    }
}

