import Dao.ClassDao;
import Dao.StudentDao;
import entity.Classes;
import entity.Student;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StudentDao 增删改查")
public class StudentDaoTest {

    private static final String TEST_NAME = "JUnit测试学生";
    private static final int NOT_EXIST_ID = 999999;

    private final StudentDao studentDao = new StudentDao();
    private final ClassDao classDao = new ClassDao();

    private int classid;

    @BeforeEach
    void setUp() {
        classid = findAnyClassId();
        cleanTestData();
    }

    @AfterEach
    void tearDown() {
        cleanTestData();
    }

    @Test
    @DisplayName("新增学生：能落库，且按姓名查回来字段完全一致")
    void testAddStudent() {
        Student stu = new Student(TEST_NAME, "男", 20, classid, "12345678901");

        int rows = studentDao.AddStudent(stu);
        assertEquals(1, rows, "新增应该影响 1 行");

        Student saved = studentDao.queryStudentByName(TEST_NAME);
        assertNotNull(saved, "新增后应该能按姓名查到");
        assertTrue(saved.getId() > 0, "自增主键应该大于 0");
        assertEquals(TEST_NAME, saved.getName());
        assertEquals("男", saved.getGender());
        assertEquals(20, saved.getAge());
        assertEquals(classid, saved.getClassid());
        assertEquals("12345678901", saved.getPhone());
    }

    @Test
    @DisplayName("按学号查：能查到刚插入的那条")
    void testQueryStudentById() {
        Student saved = insertOne();

        Student found = studentDao.queryStudentById(saved.getId());
        assertNotNull(found, "按刚插入的 id 应该能查到");
        assertEquals(saved.getId(), found.getId());
        assertEquals(TEST_NAME, found.getName());
        assertEquals(classid, found.getClassid());
    }

    @Test
    @DisplayName("按学号查：id 不存在时返回 null")
    void testQueryStudentByIdNotFound() {
        assertNull(studentDao.queryStudentById(NOT_EXIST_ID), "不存在的 id 应该返回 null");
    }

    @Test
    @DisplayName("按姓名查：姓名不存在时返回 null")
    void testQueryStudentByNameNotFound() {
        assertNull(studentDao.queryStudentByName("这个名字肯定不存在zzz"));
    }

    @Test
    @DisplayName("按班级查：结果包含本条，且按 id 升序")
    void testQueryByClassId() {
        Student saved = insertOne();

        List<Student> list = studentDao.queryByClassId(classid);
        assertFalse(list.isEmpty(), "该班级下至少有刚插入的这条");

        boolean contains = false;
        for (Student s : list) {
            if (s.getId() == saved.getId()) {
                contains = true;
                break;
            }
        }
        assertTrue(contains, "查询结果里应该包含刚插入的测试数据");

        for (int i = 1; i < list.size(); i++) {
            assertTrue(list.get(i - 1).getId() <= list.get(i).getId(), "应该按 id 升序排列");
        }
    }

    @Test
    @DisplayName("修改学生：影响 1 行，重新查询字段已更新")
    void testUpdateStudent() {
        Student saved = insertOne();

        saved.setGender("女");
        saved.setAge(21);
        saved.setPhone("13900139000");

        int rows = studentDao.UpdateStudent(saved);
        assertEquals(1, rows, "修改应该影响 1 行");

        Student updated = studentDao.queryStudentById(saved.getId());
        assertNotNull(updated);
        assertEquals("女", updated.getGender());
        assertEquals(21, updated.getAge());
        assertEquals("13900139000", updated.getPhone());
        assertEquals(TEST_NAME, updated.getName());
    }

    @Test
    @DisplayName("修改学生：id 不存在时影响 0 行")
    void testUpdateStudentNotFound() {
        Student ghost = new Student(TEST_NAME, "男", 20, classid, "12345678901");
        ghost.setId(NOT_EXIST_ID);

        assertEquals(0, studentDao.UpdateStudent(ghost), "目标不存在时应该影响 0 行");
    }

    @Test
    @DisplayName("删除学生：影响 1 行，删完再也查不到")
    void testDeleteStudent() {
        Student saved = insertOne();

        int rows = studentDao.DeleteStudent(saved.getId());
        assertEquals(1, rows, "删除应该影响 1 行");
        assertNull(studentDao.queryStudentById(saved.getId()), "删除后应该查不到");
    }

    @Test
    @DisplayName("删除学生：id 不存在时影响 0 行")
    void testDeleteStudentNotFound() {
        assertEquals(0, studentDao.DeleteStudent(NOT_EXIST_ID), "id 不存在时应该影响 0 行");
    }

    private Student insertOne() {
        Student stu = new Student(TEST_NAME, "男", 20, classid, "12345678901");
        assertEquals(1, studentDao.AddStudent(stu), "前置准备：插入测试数据应该成功");

        Student saved = studentDao.queryStudentByName(TEST_NAME);
        assertNotNull(saved, "前置准备：插入后应该能查到");
        return saved;
    }

    private int findAnyClassId() {
        for (String className : new String[]{"Java2301", "Java2302", "软件工程2301"}) {
            Classes cls = classDao.queryClassByName(className);
            if (cls != null) {
                return cls.getClassid();
            }
        }
        fail("数据库里找不到任何班级，请先执行 fix_database.sql");
        return 0;
    }

    private void cleanTestData() {
        int guard = 0;
        Student old;
        while ((old = studentDao.queryStudentByName(TEST_NAME)) != null && guard++ < 20) {
            studentDao.DeleteStudent(old.getId());
        }
    }
}
