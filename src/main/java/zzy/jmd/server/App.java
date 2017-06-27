package zzy.jmd.server;

import zzy.jmd.server.mdservice.MarkdownService;

public class App {
    private final static Object lk = new Object();

    public static void main(String[] args) throws Throwable {
        MarkdownService markdownService = new MarkdownService();
        HttpServer httpServer = HttpServer.getInstance();
        httpServer.reg("", markdownService);
        httpServer.run();
        synchronized (lk) {
            lk.wait();
        }
    }

}