package Dao;

import entity.Classes;
import utils.JDBCutil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ClassDao {

    private static final Logger log = LoggerFactory.getLogger(ClassDao.class);

    public Classes getClassById(int classid) {
        String sql = "select * from `class` where classid = ?";
        try (Connection conn = JDBCutil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, classid);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Classes cls = new Classes();
                    cls.setClassid(rs.getInt("classid"));
                    cls.setClassname(rs.getString("classname"));
                    return cls; // 查到数据，返回对象
                }
                // ========== 关键！下面这行不能少 ==========
                return null; // 查不到这条班级记录，返回null
            }
        } catch (Exception e) {
            log.error("按班级号查询失败 classid={}", classid, e);
            throw new RuntimeException(e);
        }
    }

    public int Addclass(Classes classes) {
        String sql = "insert into `class` (classname) values (?)";
        try(Connection conn = JDBCutil.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, classes.getClassname());
            return ps.executeUpdate();
        } catch (Exception e) {
            log.error("添加班级失败 classname={}", classes.getClassname(), e);
            throw new RuntimeException(e);
        }
    }

    public int Deleteclass(int classid) {
        String sql = "delete from `class` where classid = ?";
        try(Connection conn = JDBCutil.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classid);
            return ps.executeUpdate();
        } catch (Exception e) {
            log.error("删除班级失败 classid={}", classid, e);
            throw new RuntimeException(e);
        }
    }

    public int Updateclass(Classes classes) {
        String sql = "update `class` set classname = ? where classid = ?";
        try(Connection conn = JDBCutil.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, classes.getClassname());
            ps.setInt(2, classes.getClassid());
            return ps.executeUpdate();
        } catch (Exception e) {
            log.error("修改班级失败 classid={} classname={}", classes.getClassid(), classes.getClassname(), e);
            throw new RuntimeException(e);
        }
    }

    public Classes queryClassByName(String className) {
        String sql = "select * from `class` where classname = ?";
        try (Connection conn = JDBCutil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, className);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Classes cls = new Classes();
                    cls.setClassid(rs.getInt("classid"));
                    cls.setClassname(rs.getString("classname"));
                    return cls; // 查到数据，返回对象
                }
                // ========== 关键！下面这行不能少 ==========
                return null; // 查不到这条班级记录，返回null
            }
        } catch (Exception e) {
            log.error("按班级名查询失败 classname={}", className, e);
            throw new RuntimeException(e);
        }
    }

    public List<Classes> queryAllClasses() {
        String sql = "select * from `class` order by classid";
        try (Connection conn = JDBCutil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Classes> list = new ArrayList<>();
            while (rs.next()) {
                Classes cls = new Classes();
                cls.setClassid(rs.getInt("classid"));
                cls.setClassname(rs.getString("classname"));
                list.add(cls);
            }
            return list;
        } catch (Exception e) {
            log.error("查询全部班级失败", e);
            throw new RuntimeException(e);
        }
    }
}
