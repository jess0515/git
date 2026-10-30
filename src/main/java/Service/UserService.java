package Service;

import Dao.UserDao;
import entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private UserDao userDao = new UserDao();

    public boolean login(String username, String password) {
        User user = userDao.findUserByUsernameAndPwd(username, password);
        if (user == null) {
            log.warn("登录失败：账号或密码错误 username={}", username);
            return false;
        }
        log.info("登录成功 username={}", username);
        return true;
    }
}
