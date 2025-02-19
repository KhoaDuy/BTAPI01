package com.example.project.utils;

import org.springframework.stereotype.Component;

import java.util.Random;

public class OtpUtil {
    public static String generateOtp(int length){
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int digit = random.nextInt(10);
            builder.append(digit);
        }
        return builder.toString();
    }
}
