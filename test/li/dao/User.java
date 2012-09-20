package li.dao;

import li.annotation.Bean;
import li.annotation.Table;

@Bean
@Table("t_account")
public class User extends Record<User> {
	private static final long serialVersionUID = -2274465783698819130L;
}