package service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import model.MemberDTO;

/**
 * 간단한 파일 기반 인증 서비스.
 * 로컬 파일에 사용자 정보를 저장하여 로그인/회원가입을 처리한다.
 */
public class AuthService {

    private final Path storePath;

    public AuthService() {
        this(Paths.get("data", "users.db"));
    }

    public AuthService(Path storePath) {
        this.storePath = storePath;
        ensureStore();
    }

    private void ensureStore() {
        try {
            if (storePath.getParent() != null) {
                Files.createDirectories(storePath.getParent());
            }
            if (!Files.exists(storePath)) {
                Files.createFile(storePath);
            }
        } catch (IOException e) {
            throw new IllegalStateException("사용자 저장소 초기화 실패: " + e.getMessage(), e);
        }
    }

    public synchronized boolean register(String id, String name, String password) {
        if (isBlank(id) || isBlank(name) || isBlank(password)) {
            return false;
        }
        Map<String, MemberDTO> users = loadUsers();
        if (users.containsKey(id)) {
            return false;
        }
        users.put(id, new MemberDTO(id, hashPassword(password), name));
        return saveUsers(users);
    }

    public synchronized MemberDTO login(String id, String password) {
        if (isBlank(id) || isBlank(password)) {
            return null;
        }
        Map<String, MemberDTO> users = loadUsers();
        MemberDTO user = users.get(id);
        if (user == null) {
            return null;
        }
        return user.getPassword().equals(hashPassword(password)) ? user : null;
    }

    private Map<String, MemberDTO> loadUsers() {
        Map<String, MemberDTO> users = new HashMap<>();
        if (!Files.exists(storePath)) {
            return users;
        }
        try (BufferedReader reader = Files.newBufferedReader(storePath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";", -1);
                if (parts.length >= 3) {
                    users.put(parts[0], new MemberDTO(parts[0], parts[2], parts[1]));
                }
            }
        } catch (IOException e) {
            // 파일 파싱 오류 시 빈 맵 반환 (로그인은 실패하도록)
        }
        return users;
    }

    private boolean saveUsers(Map<String, MemberDTO> users) {
        try (BufferedWriter writer = Files.newBufferedWriter(storePath, StandardCharsets.UTF_8)) {
            for (MemberDTO user : users.values()) {
                writer.write(user.getId());
                writer.write(';');
                writer.write(user.getName());
                writer.write(';');
                writer.write(user.getPassword());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("해시 생성 실패", e);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

