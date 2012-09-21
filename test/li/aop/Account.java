package li.aop;

import java.util.List;

import li.annotation.Aop;
import li.annotation.Table;
import li.annotation.Trans;
import li.dao.Record;
import li.util.Page;

@Table("t_account")
public class Account extends Record<Account> {
	private static final long serialVersionUID = -3592765768245992120L;

	@Trans
	@Aop({ LogFilter.class })
	public List<Account> list(Page page) {
		return super.list(page);
	}
}