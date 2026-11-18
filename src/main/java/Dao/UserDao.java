package Dao;
import entity.User;
import java.util.*;
import utils.MyBatisUtil;
public class UserDao {
    public User findUserById(int userid) {
        return MyBatisUtil.execute("User.findUserById", false, s -> s.selectOne("UserMapper.findUserById", userid));
    }
    public User findUserByUsernameAndPwd(String username, String password) { Map<String,Object> params = new HashMap<>(); params.put("username",username); params.put("password",password);
        return MyBatisUtil.execute("User.findUserByUsernameAndPwd", false, s -> s.selectOne("UserMapper.findUserByUsernameAndPwd", params));
    }
}
