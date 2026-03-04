package com.demoqa.config;


import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Properties P = new Properties();

    static {
        try (InputStream is = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            P.load(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String baseUrl() { return System.getProperty("base.url", P.getProperty("base.url")); }
    public static String apiBaseUrl() { return System.getProperty("api.baseUrl", P.getProperty("api.baseUrl")); }
    public static String loginIvan(){return System.getProperty("userName", P.getProperty("userName")); };
    public static String passwordIvan(){return System.getProperty("password", P.getProperty("password")); };
    public static String loginUri() { return System.getProperty("login.uri", P.getProperty("login.uri")); }
    public static String registrationUrl(){return  System.getProperty("registration.url", P.getProperty("registration.url")); }
    public static String getInfoAccountUrl() {return  System.getProperty("getInfoAccount.url", P.getProperty("getInfoAccount.url")); }
    public static String generateTokenUrl() {return  System.getProperty("generateToken.url", P.getProperty("generateToken.url")); }
    public static String deleteAccountUrl() {return System.getProperty("deleteAccount.url", P.getProperty("deleteAccount.url"));}
    public static String authorizedUrl() {return System.getProperty("authorized.url", P.getProperty("authorized.url"));}
}