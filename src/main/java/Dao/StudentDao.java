package Dao;

import entity.Student;
import utils.JDBCutil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class StudentDao {

    private static final Logger log = LoggerFactory.getLogger(StudentDao.class);

    private Student mapRow(ResultSet rs) throws Exception {
        Student student = new Student();
        student.setId(rs.getInt("id"));
        student.setName(rs.getString("name"));
        student.setGender(rs.getString("gender"));
        student.setAge(rs.getInt("age"));
        student.setClassid(rs.getInt("classid"));   // ★ 原来漏了
        student.setPhone(rs.getString("phone"));    // ★ 原来漏了
        return student;
    }


    // ================== 查询 ==================

    /** 按学号查询，查不到返回 null */
    public Student queryStudentById(int id) {
        String sql = "select * from student where id = ?";

        try (Connection conn = JDBCutil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (Exception e) {
            log.error("按学号查询失败 id={}", id, e);
            throw new RuntimeException("按学号查询失败：id = " + id, e);
        }
        return null;
    }

    /** 按姓名精确查询，查不到返回 null */
    public Student queryStudentByName(String name) {
        String sql = "select * from student where name = ?";

        try (Connection conn = JDBCutil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (Exception e) {
            log.error("按姓名查询失败 name={}", name, e);
            throw new RuntimeException("按姓名查询失败：name = " + name, e);
        }
        return null;
    }


    public List<Student> queryByClassId(int classid) {
        String sql = "select * from student where classid = ? order by id";

        List<Student> list = new ArrayList<>();

        try (Connection conn = JDBCutil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, classid);

            try (ResultSet rs = ps.executeQuery()) {
                // 多条数据用 while，不是 if —— 这是它和查单条的唯一区别
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (Exception e) {
            log.error("按班级查询失败 classid={}", classid, e);
            throw new RuntimeException("按班级查询失败：classid = " + classid, e);
        }
        return list;
    }


    // ================== 增删改 ==================
    //int类型代表的是改变的行数（数据数）
    /** 添加学生，返回影响行数。id 不写进 SQL —— 数据库自增生成 */
    public int AddStudent(Student student) {
        String sql = "insert into student(name, age, gender, classid, phone) "
                   + "values(?, ?, ?, ?, ?)";

        try (Connection conn = JDBCutil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // 下标顺序严格对应 SQL 里 ? 出现的先后，从 1 开始
            ps.setString(1, student.getName());
            ps.setInt(2, student.getAge());
            ps.setString(3, student.getGender());
            ps.setInt(4, student.getClassid());
            ps.setString(5, student.getPhone());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        student.setId(keys.getInt(1));
                    }
                }
            }
            return rows;
        } catch (Exception e) {
            log.error("添加学生失败 student={}", student, e);
            throw new RuntimeException("添加学生失败：" + student, e);
        }
    }

    /** 删除学生，返回影响行数（id 不存在时是 0） */
    public int DeleteStudent(int id) {
        String sql = "delete from student where id = ?";

        try (Connection conn = JDBCutil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (Exception e) {
            log.error("删除学生失败 id={}", id, e);
            throw new RuntimeException("删除学生失败：id = " + id, e);
        }
    }

    /** 修改学生信息，返回影响行数。注意 id 放在 WHERE 里，是最后一个 ? */
    public int UpdateStudent(Student student) {
        String sql = "update student set name = ?, age = ?, gender = ?, "
                   + "classid = ?, phone = ? where id = ?";

        try (Connection conn = JDBCutil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, student.getName());
            ps.setInt(2, student.getAge());
            ps.setString(3, student.getGender());
            ps.setInt(4, student.getClassid());
            ps.setString(5, student.getPhone());
            ps.setInt(6, student.getId());

            return ps.executeUpdate();
        } catch (Exception e) {
            log.error("修改学生失败 student={}", student, e);
            throw new RuntimeException("修改学生失败：" + student, e);
        }
    }
}
