package org.arited.lawconnect.cache.context;

public class SessionContext {
    private static final ThreadLocal<String> TOKEN = new ThreadLocal<>();

    public static void setToken(String token) { TOKEN.set(token); }
    public static String getToken() { return TOKEN.get(); }
    public static void clearToken() { TOKEN.remove(); }
}