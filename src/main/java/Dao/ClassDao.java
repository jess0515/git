package Dao;
import entity.Classes;
import java.util.*;
import utils.MyBatisUtil;
public class ClassDao {
    public Classes getClassById(int classid) {
        return MyBatisUtil.execute("Class.getClassById", false, s -> s.selectOne("ClassMapper.getClassById", classid));
    }
    public Classes queryClassByName(String className) {
        return MyBatisUtil.execute("Class.queryClassByName", false, s -> s.selectOne("ClassMapper.queryClassByName", className));
    }
    public List<Classes> queryAllClasses() {
        return MyBatisUtil.execute("Class.queryAllClasses", false, s -> s.selectList("ClassMapper.queryAllClasses", null));
    }
    public int Addclass(Classes classes) {
        return MyBatisUtil.execute("Class.Addclass", true, s -> s.insert("ClassMapper.Addclass", classes));
    }
    public int Deleteclass(int classid) {
        return MyBatisUtil.execute("Class.Deleteclass", true, s -> s.delete("ClassMapper.Deleteclass", classid));
    }
    public int Updateclass(Classes classes) {
        return MyBatisUtil.execute("Class.Updateclass", true, s -> s.update("ClassMapper.Updateclass", classes));
    }
}
