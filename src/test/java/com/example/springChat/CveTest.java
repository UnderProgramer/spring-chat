package com.example.springChat;

import org.apache.commons.lang3.ClassUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CveTest {

    @Test
    void testCVE_2025_48924() {
        String longClassName = "a.".repeat(100000) + "Foo";

        System.out.println("테스트 시작: 입력 길이 = " + longClassName.length());

        try {
            ClassUtils.getClass(longClassName, false);
            System.out.println("취약점 미발생");
        } catch (StackOverflowError e) {
            System.out.println("CVE-2025-48924 확인: StackOverflowError 발생!");
        } catch (ClassNotFoundException e) {
            System.out.println("ClassNotFoundException (정상 동작)");
        } catch (Exception e) {
            System.out.println("기타 예외: " + e.getClass() + " - " + e.getMessage());
        }
    }
}