package service;

import java.security.SecureRandom;

/**
 * 간단한 텍스트 기반 캡차 생성기.
 */
public class CaptchaService {

    private static final String CHAR_POOL = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public String issueToken(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int idx = RANDOM.nextInt(CHAR_POOL.length());
            sb.append(CHAR_POOL.charAt(idx));
        }
        return sb.toString();
    }

    public boolean verify(String expected, String provided) {
        if (expected == null || provided == null) {
            return false;
        }
        return expected.equalsIgnoreCase(provided.trim());
    }
}

