package zzy.jmd.server;

import zzy.jmd.server.httpway.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


public class HttpServer {
    private static HttpServer ourInstance = new HttpServer();
    private final FrontwayModule m;


    public static HttpServer getInstance() {
        return ourInstance;
    }

    private Frontway frontway;

    private HttpServer() {
        frontway = new JsonFrontway("/api/([a-z0-9_/]+)\\.json", "/$1");
        m = new FrontwayModule("app");
    }

    private final static Map<String, String> safe = new HashMap<>();

    static {
    }

    private static String replaceSafe(String str) {
        String a = str;
        for (Map.Entry<String, String> entry : safe.entrySet()) {
            a = a.replace(entry.getKey(), entry.getValue());
        }
        return a;
    }

    public static String snakeCase(String str) {
        String s = replaceSafe(str);
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < s.length(); ++i) {
            char ch = s.charAt(i);
            if (ch >= 'A' && ch <= 'Z') {
                char ch_ucase = (char) (ch + 32);
                if (i > 0) {
                    buf.append('_');
                }
                buf.append(ch_ucase);
            } else {
                buf.append(ch);
            }
        }
        return buf.toString();
    }

    public void reg(String rootRoute, Object o) {
        if (o == null) {
            return;
        }
        Class<?> clazz = o.getClass();
        for (Method method : clazz.getMethods()) {
            if (method.getDeclaringClass().equals(clazz)) {
                String name = snakeCase(method.getName());
                addAction(rootRoute + "/" + name, o, method);
            }
        }
    }

    public void addAction(String route, Object object, Method method) {
        getInstance().m.addAction(route, object, method);
    }

    public void run() {
        frontway.registerModule(m);
        StandardThreadExecutor standardThreadExecutor = new StandardThreadExecutor();
        HttpwayServer httpWayServer = new HttpwayServer(8080, standardThreadExecutor, frontway);
        httpWayServer.run();
    }
}
