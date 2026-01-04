package be.couderiannello.config;

import javax.servlet.ServletContext;

public final class AppConfig {

    private static ServletContext context;

    private AppConfig() {}

    public static void init(ServletContext ctx) {
        context = ctx;
    }

    public static String get(String key) {
        if (context == null) {
            throw new IllegalStateException("AppConfig non initialisé");
        }
        return context.getInitParameter(key);
    }
}