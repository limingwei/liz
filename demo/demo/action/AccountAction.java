package demo.action;

import li.annotation.Arg;
import li.annotation.At;
import li.annotation.Bean;
import li.annotation.Inject;
import li.mvc.AbstractAction;
import li.util.Convert;
import li.util.Page;
import demo.record.Account;

@Bean
public class AccountAction extends AbstractAction {
	@Inject
	Account accountDao;

	@At("account_list")
	public void list(@Arg("pn") Page page) {
		setSession("page", page).setRequest("accounts", accountDao.list(page));
		view("account_list");
	}

	@At("account_add")
	public void add() {
		passParams("pn");
		view("account_add");
	}

	@At("account_delete")
	public void delete(Integer id) {
		accountDao.delete(id);
		redirect("account_list?pn=" + getParameter("pn"));
	}

	@At(value = "account_save", method = "POST")
	public void save(Account account) {
		accountDao.save(account.set("password", Convert.toMD5(account.get("password"))));
		redirect("account_list?pn=" + getParameter("pn"));
	}

	@At("account_edit")
	public void edit(Integer id) {
		passParams("pn").setRequest("account", accountDao.find(id));
		view("account_edit");
	}

	@At(value = "account_update", method = "post")
	public void update(Account account) {
		accountDao.update(account.set("password", Convert.toMD5(account.get("password"))));
		redirect("account_list?pn=" + getParameter("pn"));
	}

	@At("account_login")
	public void login() {
		view("account_login");
	}

	@At(value = { "account_login", "login" }, method = "POST")
	public void login(Account account) {
		Account ac = account.login(account);
		if (null != ac) {
			setSession("account", ac);
		}
		redirect(getRequest().getHeader("referer"));
	}

	@At("account_signup")
	public void signup() {
		forward("account_signup");
	}

	@At(value = "account_signup", method = "POST")
	public void signup(Account account) {
		String password2 = getParameter("password2");
		if (account.get("password").equals(password2)) {
			account.set("password", Convert.toMD5(account.get("password")));
			if (account.save(account)) {
				write("signup success");
			}
		} else {
			write("signup failed");
		}
	}

	@At("account_logout")
	public void logout() {
		removeSession("account");
		redirect(getRequest().getHeader("referer"));
	}
}