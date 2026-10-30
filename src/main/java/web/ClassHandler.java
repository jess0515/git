package web;

import Service.ClassService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import entity.Classes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class ClassHandler implements HttpHandler {

    private static final Logger log = LoggerFactory.getLogger(ClassHandler.class);

    private final ClassService classService = new ClassService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                Json.send(exchange, 204, "");
                return;
            }
            if (!"GET".equals(exchange.getRequestMethod())) {
                Json.send(exchange, 501, "{\"error\":\"班级接口只做了 GET\"}");
                return;
            }

            List<Classes> list = classService.queryAllClasses();
            Json.sendObject(exchange, 200, list);
        } catch (Exception e) {
            log.error("班级接口处理失败 uri={}", exchange.getRequestURI(), e);
            Json.send(exchange, 500, "{\"error\":\"服务器内部错误\"}");
        } finally {
            exchange.close();
        }
    }
}
