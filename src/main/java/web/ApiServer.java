package web;

import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApiServer {

    private static final Logger log = LoggerFactory.getLogger(ApiServer.class);
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/api/students", new StudentHandler());
        server.createContext("/api/classes", new ClassHandler());
        server.createContext("/", new StaticHandler());

        ExecutorService pool = Executors.newFixedThreadPool(4);
        server.setExecutor(pool);
        server.start();

        log.info("服务已启动 http://localhost:{}", PORT);
        System.out.println("服务已启动：" + "http://localhost:" + PORT);
    }
}
