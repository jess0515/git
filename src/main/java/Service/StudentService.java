package Service;

import Dao.StudentDao;
import entity.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import Exception.StudentExistException;

public class StudentService {

    private static final Logger log = LoggerFactory.getLogger(StudentService.class);

    private StudentDao studentDao = new StudentDao();


    public boolean addStudent(Student student) {

        // 校验 1：对象和姓名不能为空
        if (student == null
                || student.getName() == null
                || student.getName().trim().isEmpty()) {
            log.warn("新增学生被拒绝：姓名为空");
            return false;
        }

        // 校验 2：按姓名防重复
        if (studentDao.queryStudentByName(student.getName()) != null) {
            //增加小部分异常处理
            throw new StudentExistException("学生姓名重复，不能添加");
        }

        boolean ok = studentDao.AddStudent(student) > 0;
        if (ok) {
            log.info("新增学生成功 name={} classid={}", student.getName(), student.getClassid());
        } else {
            log.warn("新增学生失败：影响行数为 0 name={}", student.getName());
        }
        return ok;
    }

    public Student queryStudentById(int id) {
        return studentDao.queryStudentById(id);
    }

    public Student queryStudentByName(String name) {
        return studentDao.queryStudentByName(name);
    }

    public List<Student> queryAllStudentByClassId(int classid) {
        return studentDao.queryByClassId(classid);
    }

    /**
     * 修改学生信息，成功返回修改后的对象，失败返回 null。
     */
    public Student updateStudent(Student student) {
        if (student == null || student.getId() <= 0) {
            log.warn("修改学生被拒绝：id 非法");
            return null;
        }
        // 目标必须存在，否则数据库返回"影响 0 行"，界面上却看不出区别
        if (studentDao.queryStudentById(student.getId()) == null) {
            log.warn("修改学生失败：id={} 不存在", student.getId());
            return null;
        }
        if (studentDao.UpdateStudent(student) > 0) {
            log.info("修改学生成功 id={} name={}", student.getId(), student.getName());
            return student;
        }
        log.warn("修改学生失败：影响行数为 0 id={}", student.getId());
        return null;
    }

    public boolean deleteStudent(int id) {
        boolean ok = studentDao.DeleteStudent(id) > 0;
        if (ok) {
            log.info("删除学生成功 id={}", id);
        } else {
            log.warn("删除学生失败：id={} 不存在", id);
        }
        return ok;
    }
}
