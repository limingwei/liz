package li.mvc;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import li.model.Action;
import li.util.Convert;
import li.util.Files;
import li.util.Log;
import li.util.Page;
import li.util.Reflect;
import li.util.Verify;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.bee.tl.core.Config;
import org.bee.tl.core.GroupTemplate;

import freemarker.template.Configuration;

/**
 * 访问HTTP请求上下文的工具类,使用ThreadLocal
 * 
 * @author li (limw@w.cn)
 * @version 0.1.1 (2012-07-20)
 * 
 * @see li.mvc.AbstractAction
 */
public class Context {
	private static final Log log = Log.init();

	/**
	 * 存储HttpServletRequest的ThreadLocal
	 */
	private static final ThreadLocal<HttpServletRequest> REQUEST = new ThreadLocal<HttpServletRequest>();

	/**
	 * 存储HttpServletResponse的ThreadLocal
	 */
	private static final ThreadLocal<HttpServletResponse> RESPONSE = new ThreadLocal<HttpServletResponse>();

	/**
	 * 存储Action的ThreadLocal
	 */
	private static final ThreadLocal<Action> ACTION = new ThreadLocal<Action>();

	/**
	 * 一个static final的AbstractAction,作为某些方法的返回值,目的是方便链式调用
	 */
	private static final AbstractAction ABSTRACT_ACTION = new AbstractAction() {
	};

	/**
	 * 从request,sesstion,servletContext中取出Attributes转为Map
	 */
	private static Map<String, Object> getAttributes() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("application", getServletContext());
		map.put("session", getSession());
		map.put("request", getRequest());
		map.put("response", getResponse());

		// 1. 存入servletContext的值
		Enumeration<String> servletContextEnumeration = getServletContext().getAttributeNames();
		while (servletContextEnumeration.hasMoreElements()) {
			String name = servletContextEnumeration.nextElement();
			map.put(name, getServletContext().getAttribute(name));
		}

		// 2. 存入session的值
		Enumeration<String> sessionEnumeration = getSession().getAttributeNames();
		while (sessionEnumeration.hasMoreElements()) {
			String name = sessionEnumeration.nextElement();
			map.put(name, getSession().getAttribute(name));
		}

		// 3. 存入request的值
		Enumeration<String> requestEnumeration = getRequest().getAttributeNames();
		while (requestEnumeration.hasMoreElements()) {// request
			String name = requestEnumeration.nextElement();
			map.put(name, getRequest().getAttribute(name));
		}

		return map;
	}

	/**
	 * 初始化方法,会将request,response,action分别存入ThreadLocal
	 */
	public static AbstractAction init(ServletRequest request, ServletResponse response, Action action) {
		REQUEST.set((HttpServletRequest) request);
		RESPONSE.set((HttpServletResponse) response);
		ACTION.set(action);
		return ABSTRACT_ACTION;
	}

	/**
	 * 返回Action引用
	 */
	public static Action getAction() {
		return ACTION.get();
	}

	/**
	 * 返回request引用
	 */
	public static HttpServletRequest getRequest() {
		return REQUEST.get();
	}

	/**
	 * 返回response引用
	 */
	public static HttpServletResponse getResponse() {
		return RESPONSE.get();
	}

	/**
	 * 返回ServletContext
	 */
	public static ServletContext getServletContext() {
		return getRequest().getServletContext();
	}

	/**
	 * 返回Session引用
	 */
	public static HttpSession getSession() {
		return getRequest().getSession();
	}

	/**
	 * 得到Session中得一个值
	 */
	public static Object getSession(String key) {
		return getSession().getAttribute(key);
	}

	/**
	 * 返回指定key的QueryString参数
	 */
	public static String getParameter(String key) {
		return getRequest().getParameter(key);
	}

	/**
	 * 返回基本类型的数组参数
	 */
	public static <T> T[] getArray(Class<T> type, String key) {
		return Convert.toType(type, getRequest().getParameterValues(key));
	}

	/**
	 * 移除一个Session
	 */
	public static AbstractAction removeSession(String key) {
		getSession().removeAttribute(key);
		return ABSTRACT_ACTION;
	}

	/**
	 * 向request中设值
	 */
	public static AbstractAction setRequest(String key, Object value) {
		getRequest().setAttribute(key, value);
		return ABSTRACT_ACTION;
	}

	/**
	 * 向session中设值
	 */
	public static AbstractAction setSession(String key, Object value) {
		getRequest().getSession().setAttribute(key, value);
		return ABSTRACT_ACTION;
	}

	/**
	 * 路径中对应于Action的url正则表达式的可变部分的值的数组
	 * 
	 * @see li.annotation.At
	 */
	public static String[] pathParams() {
		Matcher matcher = Pattern.compile(".*" + getAction().path + ".*").matcher(getRequest().getRequestURL().toString());
		String[] params = new String[matcher.groupCount()];
		for (int i = 0; matcher.matches() && i < matcher.groupCount(); i++) {
			params[i] = matcher.group(i + 1);
		}
		return params;
	}

	/**
	 * 根据QueryString中的页码参数构建一个Page,或者一个默认的Page
	 */
	public static Page getPage(String pageNumberKey) {
		Page page = (Page) getSession().getAttribute("page");
		if (null == page) {
			page = new Page();
		}
		String pnStr = getParameter(pageNumberKey);
		page.setPageNumber(Verify.isEmpty(pnStr) ? 1 : Integer.parseInt(pnStr));
		return page;
	}

	/**
	 * 从 request 的 parameters中得到数据组装成一个type类型的对象
	 * 
	 * @param type 对象类型
	 * @param prefix 查询 Parameters 时候的 key 的前缀
	 */
	public static <T> T get(Class<T> type, String prefix) {
		T t = Reflect.born(type);
		for (Entry<String, String[]> entry : getRequest().getParameterMap().entrySet()) {// 迭代ParameterMap
			if (Verify.isEmpty(prefix) || Verify.startWith(entry.getKey(), prefix)) {// 前缀为空或者参数Key以前缀开头
				String field = entry.getKey().substring(prefix.length());// 属性名
				Reflect.set(t, field, entry.getValue()[0]);
			}
		}
		return t;
	}

	/**
	 * 将QueryString中对应key的参数设置到request里面
	 */
	public static AbstractAction passParams(String... keys) {
		for (String key : keys) {
			getRequest().setAttribute(key, getParameter(key));
		}
		return ABSTRACT_ACTION;
	}

	/**
	 * 返回项目文件系统跟路径
	 */
	public static String getRootPath() {
		return getRequest().getServletContext().getRealPath("/");
	}

	/**
	 * 返回项目HTTP根路径
	 */
	public static String getRootUrl() {
		return getRequest().getScheme() + "://" + getRequest().getServerName() + ":" + getRequest().getServerPort() + getRequest().getContextPath() + "/";
	}

	/**
	 * 主视图方法,以冒号分割前缀表示视图类型,支持 forward:路径,redirect:路径,write:内容,freemarker:路径,velocity:路径,beetl:路径
	 * 
	 * @see #forward(String)
	 * @see #redirect(String)
	 * @see #write(String)
	 * @see #freemarker(String)
	 * @see #velocity(String)
	 * @see #beetl(String)
	 */
	public static String view(String path) {
		// 默认视图类型,如果未在config.properties中配置则为forward
		String view_type = Files.load("config.properties").getProperty("view.type", "forward");
		String view_prefix = Files.load("config.properties").getProperty("view.prefix", "");
		String view_suffix = Files.load("config.properties").getProperty("view.suffix", "");

		String viewType = path.contains(":") ? path.split(":")[0] : view_type;// 视图类型
		String viewPath = path.startsWith(viewType + ":") ? path.split(viewType + ":")[1] : path;// path冒号后的部分或者path

		if ("forward".equals(viewType) || "fw".equals(viewType)) {// forward视图
			forward(view_prefix + viewPath + view_suffix);
		} else if ("freemarker".equals(viewType) || "fm".equals(viewType)) {// freemarker视图
			freemarker(view_prefix + viewPath + view_suffix);
		} else if ("velocity".equals(viewType) || "vl".equals(viewType)) {// velocity视图
			velocity(view_prefix + viewPath + view_suffix);
		} else if ("beetl".equals(viewType) || "bt".equals(viewType)) {// beetl视图
			beetl(view_prefix + viewPath + view_suffix);
		} else if ("redirect".equals(viewType) || "rd".equals(viewType)) {// redirect跳转
			redirect(viewPath);
		} else if ("write".equals(viewType) || "wt".equals(viewType)) {// 向页面write数据
			write(viewPath);
		} else {
			throw new RuntimeException("view error, not supported viewtype: " + viewType);
		}
		return "DONE";
	}

	/**
	 * 执行客户端跳转
	 */
	public static String redirect(String path) {
		log.info("redirect to " + path);
		try {
			getResponse().sendRedirect(path);
		} catch (Exception e) {
			throw new RuntimeException("redirect error: ", e);
		}
		return "DONE";
	}

	/**
	 * 返回forward视图
	 */
	public static String forward(String path) {
		log.info("forward to " + path);
		try {
			getRequest().getRequestDispatcher(path).forward(getRequest(), getResponse());
		} catch (Exception e) {
			throw new RuntimeException("forword error: ", e);
		}
		return "DONE";
	}

	/**
	 * 返回velocity视图
	 */
	public static String velocity(String path) {
		try {
			Object initialized = Log.get("velocityInitialized"); // 从缓存中查找velocity是否初始化的标记
			if (null == initialized) { // 缓存中没有
				log.debug("velocity initializing ..");
				Properties properties = new Properties();// 默认的参数设置
				properties.put("file.resource.loader.path", getServletContext().getRealPath("/"));
				properties.put("input.encoding", "UTF-8");
				properties.put("output.encoding", "UTF-8");
				properties.putAll(Files.load("velocity.properties"));// velocity.properties中的参数设置
				Velocity.init(properties);// 初始化Velocity
				Log.put("velocityInitialized", true); // 设置velocityInitialized标记
			}
			// velocity值栈
			VelocityContext context = new VelocityContext(getAttributes());
			org.apache.velocity.Template template = Velocity.getTemplate(path);// velocity模板
			template.merge(context, getResponse().getWriter());
			log.info("velocity to: " + path);
		} catch (Exception e) {
			throw new RuntimeException("forword to velocity failed: ", e);
		}
		return "DONE";
	}

	/**
	 * 返回freemarker视图
	 */
	public static String freemarker(String path) {
		try {
			Configuration configuration = (Configuration) Log.get("freemarkerConfiguration"); // 从缓存中查找freemarkerTemplate
			if (null == configuration) { // 缓存中没有
				log.debug("freemarker initializing ..");
				configuration = new Configuration(); // 初始化freemarkerTemplate
				configuration.setServletContextForTemplateLoading(getServletContext(), "/");// 设置模板加载跟路径
				Properties properties = new Properties();// 默认的参数设置
				properties.put("default_encoding", "UTF-8");
				properties.putAll(Files.load("freemarker.properties"));// freemarker.properties中的参数设置
				configuration.setSettings(properties);
				Log.put("freemarkerConfiguration", configuration); // 缓存freemarkerTemplate
			}
			freemarker.template.Template template = configuration.getTemplate(path);// 加载模板
			template.process(getAttributes(), getResponse().getWriter());
			log.info("freemarker to: " + path);
		} catch (Exception e) {
			throw new RuntimeException("forword to freemarker failed: ", e);
		}
		return "DONE";
	}

	/**
	 * 返回beetl视图
	 */
	public static String beetl(String path) {
		try {
			GroupTemplate groupTemplate = (GroupTemplate) Log.get("groupTemplate");// 从缓存中查找GroupTemplate
			if (null == groupTemplate) {
				log.debug("beetl initializing ...");
				Config config = new Config();// 加载默认配置
				config.put("TEMPLATE_ROOT", getServletContext().getRealPath("/"));
				config.put("TEMPLATE_CHARSET", "UTF-8");
				Properties properties = Files.load("beetl.properties");// 加载自定义配置,覆盖默认
				for (Entry<Object, Object> entry : properties.entrySet()) {
					config.put(entry.getKey().toString(), entry.getValue().toString());
				}
				groupTemplate = config.createGroupTemplate();// 生成GroupTemplate,并缓存之
				Log.put("groupTemplate", groupTemplate);
			}
			org.bee.tl.core.Template template = groupTemplate.getFileTemplate(path);// 生成模板
			for (Entry<String, Object> entry : getAttributes().entrySet()) {
				template.set(entry.getKey(), entry.getValue());// 设置变量
			}
			template.getText(getResponse().getWriter());// merge 模板和模型，将内容输出到Writer里
			log.info("forword to beetl: " + path);
		} catch (Exception e) {
			throw new RuntimeException("forword to beetl failed: ", e);
		}
		return "DONE";
	}

	/**
	 * 把 content写到页面上
	 */
	public static AbstractAction write(String content) {
		final String JSON_REGEX = "^[\\[]*[{]+.*[}]+[]]*$", XML_REGEX = "^<.*>$";
		if (!Verify.isEmpty(content) && Verify.regex(content, XML_REGEX)) {// 如果内容是XML
			getResponse().setContentType("text/xml;charset=UTF-8");
		} else if (!Verify.isEmpty(content) && Verify.regex(content, JSON_REGEX)) {// 如果内容是JSON
			getResponse().setContentType("application/json;charset=UTF-8");
		} else if (!Verify.isEmpty(content)) {
			getResponse().setContentType("text/plain;charset=UTF-8");
		}
		try {
			getResponse().getWriter().write(content);
		} catch (Exception e) {
			throw new RuntimeException("Exception at li.mvc.Context.write(String)", e);
		}
		return ABSTRACT_ACTION;
	}

	/**
	 * 上传文件
	 */
	public static AbstractAction upload(String uploadPath) {
		try {
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			List<?> fileItems = upload.parseRequest(getRequest());
			for (Object fileItem : fileItems) {
				File saveFile = new File(uploadPath, ((FileItem) fileItem).getName());
				((FileItem) fileItem).write(saveFile);
			}
			log.info("upload success");
		} catch (Throwable e) {
			throw new RuntimeException("upload failed: ", e);
		}
		return ABSTRACT_ACTION;
	}
}