package li.test;

import li.model.Action;
import li.mvc.ActionContext;
import li.mvc.Context;
import li.util.Verify;

//import org.springframework.mock.web.MockHttpServletRequest;
//import org.springframework.mock.web.MockHttpServletResponse;
//import org.springframework.mock.web.MockHttpSession;
//import org.springframework.mock.web.MockServletConfig;
//import org.springframework.mock.web.MockServletContext;

/**
 * Action测试类的基类，准备内request response等的Mock对象
 * 
 * @author li (limw@w.cn)
 * @version 0.1.1 (2012-07-21)
 */
public class BaseActionTest extends BaseTest {
	/**
	 * 模拟的servletContext
	 */
	// protected MockServletContext servletContext = new MockServletContext();

	/**
	 * 模拟的servletConfig
	 */
	// protected MockServletConfig servletConfig = new MockServletConfig(servletContext);

	/**
	 * 模拟的request
	 */
	// protected MockHttpServletRequest request = new MockHttpServletRequest(servletContext);

	/**
	 * 模拟的response
	 */
	// protected MockHttpServletResponse response = new MockHttpServletResponse();

	/**
	 * 模拟的session
	 */
	// protected MockHttpSession session = new MockHttpSession(servletContext);

	/**
	 * 初始化方法，由于不会启动Filter，所以这里为Context ThreadLocal设值
	 */
	public BaseActionTest() {
		// Context.init(request, response, null);
	}

	protected void setRequestURL(String path, String method) {
		path = Verify.startWith(path, "/") ? path : "/" + path;
		// request.setRequestURI(path);
		// request.setMethod(method);
		// request.setServletPath(path);

		Action action = ActionContext.getInstance().getAction(path, method);
		// Context.init(request, response, action);
	}
}