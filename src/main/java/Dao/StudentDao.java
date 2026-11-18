package Dao;
import entity.Student;
import java.util.*;
import utils.MyBatisUtil;
public class StudentDao {
    public Student queryStudentById(int id) {
        return MyBatisUtil.execute("Student.queryStudentById", false, s -> s.selectOne("StudentMapper.queryStudentById", id));
    }
    public Student queryStudentByName(String name) {
        return MyBatisUtil.execute("Student.queryStudentByName", false, s -> s.selectOne("StudentMapper.queryStudentByName", name));
    }
    public List<Student> queryByClassId(int classid) {
        return MyBatisUtil.execute("Student.queryByClassId", false, s -> s.selectList("StudentMapper.queryByClassId", classid));
    }
    public int AddStudent(Student student) {
        return MyBatisUtil.execute("Student.AddStudent", true, s -> s.insert("StudentMapper.AddStudent", student));
    }
    public int DeleteStudent(int id) {
        return MyBatisUtil.execute("Student.DeleteStudent", true, s -> s.delete("StudentMapper.DeleteStudent", id));
    }
    public int UpdateStudent(Student student) {
        return MyBatisUtil.execute("Student.UpdateStudent", true, s -> s.update("StudentMapper.UpdateStudent", student));
    }
}
