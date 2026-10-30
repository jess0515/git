package Service;

import Dao.ClassDao;
import entity.Classes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 班级业务层
 */
public class ClassService {

    private static final Logger log = LoggerFactory.getLogger(ClassService.class);

    private ClassDao classDao = new ClassDao();


    public boolean Addclass(String className) {
        if (className == null || className.trim().isEmpty()) {
            log.warn("添加班级被拒绝：班级名为空");
            return false;
        }

        // 班级名在数据库里是 UNIQUE 约束，重名会直接抛异常。
        // 这里先查一次，把"数据库报错"变成"友好的提示"。
        if (classDao.queryClassByName(className) != null) {
            log.warn("添加班级被拒绝：班级名已存在 classname={}", className);
            return false;
        }

        Classes newCls = new Classes();
        newCls.setClassname(className);

        boolean ok = classDao.Addclass(newCls) > 0;
        if (ok) {
            log.info("添加班级成功 classname={}", className);
        } else {
            log.warn("添加班级失败：影响行数为 0 classname={}", className);
        }
        return ok;
    }

    public Classes queryClassByName(String className) {
        return classDao.queryClassByName(className);
    }

    public Classes queryClassById(int classid) {
        return classDao.getClassById(classid);
    }

    public List<Classes> queryAllClasses() {
        return classDao.queryAllClasses();
    }

    public boolean UpdateClass(Classes classes) {
        if (classes == null
                || classes.getClassname() == null
                || classes.getClassname().trim().isEmpty()) {
            log.warn("修改班级被拒绝：班级名为空");
            return false;
        }
        boolean ok = classDao.Updateclass(classes) > 0;
        if (ok) {
            log.info("修改班级成功 classid={} classname={}", classes.getClassid(), classes.getClassname());
        } else {
            log.warn("修改班级失败：classid={} 不存在", classes.getClassid());
        }
        return ok;
    }

    /**
     * 删除班级。
     *
     * 【提醒】student 表的 classid 有外键指向 class 表。
     * 如果这个班级下还有学生，删除会失败（外键约束保护了你的数据）。
     * 这时应该先处理那些学生，而不是强行删班级。
     * 现在界面会提示"该班级下可能还有学生"，而不是抛一堆看不懂的异常。
     */
    public boolean DeleteClass(int classid) {
        boolean ok = classDao.Deleteclass(classid) > 0;
        if (ok) {
            log.info("删除班级成功 classid={}", classid);
        } else {
            log.warn("删除班级失败：classid={} 不存在，或该班级下还有学生", classid);
        }
        return ok;
    }
}
