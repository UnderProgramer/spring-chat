package com.example.springChat;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import java.security.Key;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class JwtSecurityTest {

    @Value("${spring.jwt.secret}")
    private String secretKey;

    @Test
    void testSigningKeyStrength() {
        byte[] keyBytes = secretKey.getBytes();
        int keyLength = keyBytes.length;

        System.out.println("=== JWT 키 보안 검사 ===");
        System.out.println("키 길이: " + keyLength + " bytes (" + keyLength * 8 + " bits)");

        // HS256 최소 요구사항: 32바이트 (256bit)
        if (keyLength < 32) {
            System.out.println("❌ 위험: HS256 최소 요구사항(32bytes) 미달!");
            System.out.println("   현재: " + keyLength + "bytes → 최소 32bytes 필요");
        } else {
            System.out.println("✅ 안전: 키 길이 충분");
        }

        // Keys.hmacShaKeyFor()가 실제로 예외 없이 동작하는지 검증
        try {
            Key key = Keys.hmacShaKeyFor(keyBytes);
            System.out.println("✅ 키 생성 성공: " + key.getAlgorithm());
        } catch (WeakKeyException e) {
            System.out.println("❌ WeakKeyException 발생: " + e.getMessage());
            fail("키가 너무 약합니다: " + e.getMessage());
        }

        // 실제로 토큰 생성 → 검증까지 되는지 확인
        try {
            Key key = Keys.hmacShaKeyFor(keyBytes);
            String token = Jwts.builder()
                    .setSubject("test-user")
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 60000))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();

            String subject = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();

            System.out.println("✅ 토큰 생성 및 검증 성공: subject = " + subject);
            assertEquals("test-user", subject);
        } catch (Exception e) {
            System.out.println("❌ 토큰 테스트 실패: " + e.getMessage());
            fail(e.getMessage());
        }
    }
}