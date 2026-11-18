package utils;
import java.io.InputStream;
import java.util.function.Function;
import org.apache.ibatis.session.*;
public final class MyBatisUtil {
    private static final SqlSessionFactory FACTORY = build();
    private MyBatisUtil() {}
    private static SqlSessionFactory build() {
        try (InputStream in = MyBatisUtil.class.getResourceAsStream("/mybatis-config.xml")) {
            if (in == null) throw new IllegalStateException("缺少 mybatis-config.xml");
            return new SqlSessionFactoryBuilder().build(in);
        } catch (Exception e) { throw new IllegalStateException("MyBatis 初始化失败，请检查配置", e); }
    }
    public static <T> T execute(String operation, boolean write, Function<SqlSession,T> action) {
        try (SqlSession session = FACTORY.openSession()) {
            try {
                T result = action.apply(session);
                if (write) session.commit();
                return result;
            } catch (RuntimeException e) {
                if (write) session.rollback();
                throw new IllegalStateException("数据库操作失败：" + operation, e);
            }
        }
    }
}
