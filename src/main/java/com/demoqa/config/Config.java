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

}