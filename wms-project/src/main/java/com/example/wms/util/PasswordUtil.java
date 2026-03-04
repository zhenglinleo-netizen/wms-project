package com.example.wms.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 密码工具类
 * 使用 BCrypt 算法进行密码加密和验证
 */
@Component
public class PasswordUtil {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * 加密密码
     * @param rawPassword 原始密码
     * @return 加密后的密码
     */
    public static String encrypt(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    /**
     * 验证密码
     * @param rawPassword 原始密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    public static boolean verify(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        return encoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 检查密码是否已经加密
     * BCrypt 加密后的密码以 $2 开头
     * @param password 密码
     * @return 是否已加密
     */
    public static boolean isEncrypted(String password) {
        if (password == null) {
            return false;
        }
        return password.startsWith("$2");
    }
}
