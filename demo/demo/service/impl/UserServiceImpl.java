package demo.service.impl;

import li.annotation.Bean;
import li.annotation.Inject;
import li.service.AbstractService;
import demo.dao.IUserDao;
import demo.model.User;
import demo.service.IUserService;

/**
 * 你的Service可以继承自泛型的AbstractService,AbstractService中已经存在的几个方法使用的Dao是通过泛型参数匹配查找的,如需更改可覆盖getDao方法
 */
@Bean
public class UserServiceImpl extends AbstractService<User> implements IUserService {
	@Inject
	IUserDao userDao;

	public Boolean login(User user) {
		User u = userDao.findByUsername(user.getUsername());
		if (null == u) {
			u = userDao.findByEmail(user.getEmail());
			if (null == u) {
				return false;
			}
		}
		return user.getPassword().equals(u.getPassword());
	}
}