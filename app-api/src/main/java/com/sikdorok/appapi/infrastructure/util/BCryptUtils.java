package com.sikdorok.appapi.infrastructure.util;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BCryptUtils {

    public static String salt;

    @Value("${jwt.salt}")
    public void setSalt(String salt) {
        BCryptUtils.salt = salt;
    }

    public static String hash(String password) {
        return new String(BCrypt.withDefaults().hash(12, salt.getBytes(), password.getBytes()));
    }

    public static boolean verify(String password, String hashedPassword) {
        return BCrypt.verifyer().verify(password.toCharArray(), hashedPassword).verified;
    }

    public static boolean verifyForInit(String password, String hashedPassword) {
        return BCrypt.verifyer().verify(hash(password).toCharArray(), hashedPassword).verified;
    }

}
