package Dao;
import entity.User;
import utils.JDBCutil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {

    private static final Logger log = LoggerFactory.getLogger(UserDao.class);

    // 根据id查询单个用户
    public User findUserById(int userid) {
        String sql = "select * from user where id = ?";
        User user = null;
        try (Connection conn = JDBCutil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                }
            }
        } catch (SQLException e) {
            log.error("按用户id查询失败 userid={}", userid, e);
            throw new RuntimeException(e);
        }
        return user;
    }


    // 根据用户名+密码查询用户，用于登录
    public User findUserByUsernameAndPwd(String username, String password) {
        String sql = "select * from `user` where username = ? and password = ?";
        User user = null;
        try (Connection conn = JDBCutil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                }
            }
        } catch (SQLException e) {
            log.error("登录查询失败 username={}", username, e);
            throw new RuntimeException(e);
        }
        return user;
    }

}
