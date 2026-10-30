package web;

import Exception.StudentExistException;
import Service.StudentService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import entity.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class StudentHandler implements HttpHandler {

    private static final Logger log = LoggerFactory.getLogger(StudentHandler.class);

    private final StudentService studentService = new StudentService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        try {
            if ("OPTIONS".equals(method)) {
                Json.send(exchange, 204, "");
                return;
            }

            switch (method) {
                case "GET" -> handleGet(exchange);
                case "POST" -> handlePost(exchange);
                case "PUT" -> handlePut(exchange);
                case "DELETE" -> handleDelete(exchange);
                default -> Json.send(exchange, 405, "{\"error\":\"不支持的方法：" + method + "\"}");
            }
        } catch (Exception e) {
            log.error("接口处理失败 method={} uri={}", method, exchange.getRequestURI(), e);
            Json.send(exchange, 500, "{\"error\":\"服务器内部错误\"}");
        } finally {
            exchange.close();
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String classidText = queryParam(exchange.getRequestURI(), "classid");
        if (classidText == null || classidText.isBlank()) {
            Json.send(exchange, 400, "{\"error\":\"缺少 classid 参数，例如 /api/students?classid=8\"}");
            return;
        }

        int classid;
        try {
            classid = Integer.parseInt(classidText);
        } catch (NumberFormatException e) {
            Json.send(exchange, 400, "{\"error\":\"classid 必须是数字\"}");
            return;
        }

        List<Student> students = studentService.queryAllStudentByClassId(classid);
        Json.sendObject(exchange, 200, students);
    }
    private void handlePost(HttpExchange exchange) throws IOException {
        String body = readBody(exchange);
        Student stu = Json.MAPPER.readValue(body, Student.class);

        try {
            boolean ok = studentService.addStudent(stu);
            if (!ok) {
                Json.send(exchange, 400, "{\"error\":\"姓名不能为空\"}");
                return;
            }
            Json.sendObject(exchange, 201, stu);
        } catch (StudentExistException e) {
            Json.send(exchange, 409, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private void handlePut(HttpExchange exchange) throws IOException {
        int id = pathId(exchange);
        if (id <= 0) {
            Json.send(exchange, 400, "{\"error\":\"路径里缺少学生 id，例如 /api/students/6\"}");
            return;
        }
        Student stu = Json.MAPPER.readValue(readBody(exchange), Student.class);
        stu.setId(id);
        Student updated = studentService.updateStudent(stu);
        if (updated == null) {
            Json.send(exchange, 404, "{\"error\":\"学生不存在\"}");
            return;
        }
        Json.sendObject(exchange, 200, updated);
    }
    private void handleDelete(HttpExchange exchange) throws IOException {
        int id = pathId(exchange);
        if (id <= 0) {
            Json.send(exchange, 400, "{\"error\":\"路径里缺少学生 id，例如 /api/students/6\"}");
            return;
        }
        boolean deleted = studentService.deleteStudent(id);
        if (!deleted) {
            Json.send(exchange, 404, "{\"error\":\"学生不存在\"}");
            return;
        }
        exchange.sendResponseHeaders(204, -1);
        exchange.getResponseBody().close();
    }

    private String queryParam(URI uri, String key) {
        String query = uri.getRawQuery();
        if (query == null || query.isEmpty()) {
            return null;
        }
        for (String pair : query.split("&")) {
            int eq = pair.indexOf('=');
            if (eq > 0 && pair.substring(0, eq).equals(key)) {
                return pair.substring(eq + 1);
            }
        }
        return null;
    }

    private int pathId(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        int slash = path.lastIndexOf('/');
        try {
            return Integer.parseInt(path.substring(slash + 1));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private String readBody(HttpExchange exchange) throws IOException {
        try (InputStream in = exchange.getRequestBody()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
