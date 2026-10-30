package utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JDBCutil {

    private static final String CONFIG_FILE = "/db.properties";
    private static final Properties CONFIG = loadConfig();

    private static Properties loadConfig() {
        Properties props = new Properties();
        try (InputStream in = JDBCutil.class.getResourceAsStream(CONFIG_FILE)) {
            if (in == null) {
                throw new IllegalStateException("类路径下找不到 " + CONFIG_FILE + "，请照着 db.properties.example 建一个");
            }
            try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                props.load(reader);
            }
        } catch (IOException e) {
            throw new IllegalStateException("读取 " + CONFIG_FILE + " 失败", e);
        }

        String url = props.getProperty("jdbc.url");
        String username = props.getProperty("jdbc.username");
        String password = props.getProperty("jdbc.password");
        if (url == null || url.isBlank() || username == null || password == null) {
            throw new IllegalStateException(CONFIG_FILE + " 里缺少 jdbc.url / jdbc.username / jdbc.password");
        }
        return props;
    }

    //注册驱动
    static  {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL 驱动没找到。检查 Maven 依赖里有没有 com.mysql:mysql-connector-j", e);
        }
    }

    //创建连接
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                CONFIG.getProperty("jdbc.url"),
                CONFIG.getProperty("jdbc.username"),
                CONFIG.getProperty("jdbc.password"));
    }
}
