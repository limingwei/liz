package li.mvc;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import li.dao.Record;
import li.model.Action;
import li.model.Field;
import li.util.Convert;
import li.util.Files;
import li.util.Log;
import li.util.Page;
import li.util.Reflect;
import li.util.Verify;

/**
 * MVC的Filter,负责分发HTTP请求
 * 
 * @author li (limw@w.cn)
 * @version 0.1.3 (2012-05-08)
 */
public class ActionFilter implements Filter {
	private static final Log log = Log.init();

	/**
	 * 初始化Filter,设置一些环境变量,只执行一次
	 */
	public void init(FilterConfig config) throws ServletException {
		// 默认的环境变量
		config.getServletContext().setAttribute("root", config.getServletContext().getContextPath() + "/");
		// 根据Locale.getDefault()初始化国际化,存到servletContext
		config.getServletContext().setAttribute("lang", Files.load(Locale.getDefault().toString()));
		log.info(String.format("Setting default language as %s", Locale.getDefault()));
	}

	/**
	 * 从ActionContext中查询到符合当前请求路径的Action并执行,或者chain.doFilter()
	 * 
	 * @see li.util.Reflect#invoke(Object, String, Object...)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		// 设置编码
		String encoding = Files.load("config.properties").getProperty("servlet.encoding", "UTF-8");
		request.setCharacterEncoding(encoding);
		response.setCharacterEncoding(encoding);

		// 根据Parameter参数设置国际化,存到session
		String lang = request.getParameter("lang");
		if (!Verify.isEmpty(lang)) {
			((HttpServletRequest) request).getSession().setAttribute("lang", Files.load(lang));
			log.info(String.format("Setting language for %s", lang));
		}

		// 请求路径路由
		Action action = ActionContext.getInstance().getAction(((HttpServletRequest) request).getServletPath(), ((HttpServletRequest) request).getMethod());
		if (null != action) {
			// 初始化Context
			Context.init(request, response, action);
			log.info(String.format("ACTION FOUND: path=\"%s\",method=\"%s\" action=%s.%s()", Context.getRequest().getServletPath(), Context.getRequest().getMethod(), action.actionInstance.getClass().getName(), action.actionMethod.getName()));

			// Action方法参数适配
			Object[] args = new Object[action.argTypes.length]; // Action方法参数值列表
			for (int i = 0; i < action.argTypes.length; i++) {
				String key = (null == action.argAnnotations[i]) ? action.argNames[i] : action.argAnnotations[i].value();// ParameterKey
				if (Verify.basicType(action.argTypes[i]) && !action.argTypes[i].isArray()) { // 单个基本类型
					args[i] = Convert.toType(action.argTypes[i], Context.getParameter(key));
				} else if (Verify.basicType(action.argTypes[i]) && action.argTypes[i].isArray()) { // 基本类型的数组
					args[i] = Context.getArray(action.argTypes[i].getComponentType(), key);
				} else if (ServletRequest.class.isAssignableFrom(action.argTypes[i])) { // Request
					args[i] = Context.getRequest();
				} else if (ServletResponse.class.isAssignableFrom(action.argTypes[i])) { // Response
					args[i] = Context.getResponse();
				} else if (Page.class.isAssignableFrom(action.argTypes[i])) { // Page
					args[i] = Context.getPage(key);
				} else if (Field.list(action.argTypes[i], false).size() > 0 || Record.class.isAssignableFrom(action.argTypes[i])) {
					key = (null == action.argAnnotations[i]) ? action.argNames[i] + "." : action.argAnnotations[i].value();
					args[i] = Context.get(action.argTypes[i], key);// 数据对象,POJO,如果没加@Arg注解,则key为参数名+"."
				}
			}

			// 执行Action方法,根据返回值,处理视图
			Object result = Reflect.invoke(action.actionInstance, action.actionMethod, args);
			if (result instanceof String && !result.equals("DONE")) {// 返回值为String且未调用视图方法
				Context.view(result.toString());// 则Context.view返回视图
			}
		} else {
			log.info(String.format("ACTION NOT FOUND: path=\"%s\",method=\"%s\"", ((HttpServletRequest) request).getServletPath(), ((HttpServletRequest) request).getMethod()));
			chain.doFilter(request, response);
		}
	}

	/**
	 * Filter销毁方法
	 */
	public void destroy() {
		log.debug("li.mvc.ActionFilter.destroy()");
	}
}