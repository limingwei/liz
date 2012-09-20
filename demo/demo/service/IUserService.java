package demo.service;

import li.service.IBaseService;
import demo.model.User;

/**
 * 泛型的IBaseService接口中定义了几个基础的方法,你可以选择使你的Service接口继承于它,当然也可以不
 */
public interface IUserService extends IBaseService<User> {
	public Boolean login(User user);
}