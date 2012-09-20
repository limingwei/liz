package demo.dao.impl;

import li.annotation.Bean;
import li.dao.AbstractDao;
import li.util.Convert;
import demo.dao.IUserDao;
import demo.model.User;

/**
 * 你的Dao可以继承自泛型的AbstractDao并扩展更多的方法
 */
@Bean
public class UserDaoImpl extends AbstractDao<User> implements IUserDao {
	public User findByEmail(String email) {
		return super.find("WHERE email=?", email);
	}

	public User findByUsername(String username) {
		return super.find("WHERE username=:username", Convert.toMap(":username", username));
	}
}