package li.mvc;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import li.annotation.Arg;
import li.annotation.At;
import li.ioc.IocContext;
import li.model.Action;
import li.model.Bean;
import li.util.Log;
import li.util.Reflect;
import li.util.Verify;

/**
 * Action上下文,保存 了所有的Action
 * 
 * @author li (limw@w.cn)
 * @version 0.1.3 (2012-05-08)
 */

public class ActionContext {
	private static final Log log = Log.init();

	/**
	 * 保存所有Action的List
	 */
	private final List<Action> ACTIONS = new ArrayList<Action>();

	/**
	 * ActionContext的一个实例,用于单例地得到ActionContext的实例
	 */
	private static ActionContext ACTION_CONTEXT = null;

	/**
	 * 私有构造器,保障ActionContext是单例的
	 */
	private ActionContext() {
		log.debug("new ActionContext()");
	}

	/**
	 * 单例的得到一个ActionContext对象,第一次时候会初始化ActionContext
	 */
	public static synchronized ActionContext getInstance() {
		if (ACTION_CONTEXT == null) {
			Log.put("MVCSTARTUP", System.currentTimeMillis());

			ACTION_CONTEXT = new ActionContext();
			for (Bean bean : IocContext.getInstance().BEANS) {
				for (Method method : bean.type.getDeclaredMethods()) {
					At at = method.getAnnotation(At.class);
					if (null != at) {
						for (String path : at.value()) {
							Action action = new Action();
							// Action请求路径,如果不以斜杠开始会被加上斜杠,如果注解value值为空则直接使用方法名
							action.path = Verify.isEmpty(path) ? "/" + method.getName() : Verify.startWith(path, "/") ? path : "/" + path;
							action.httpMethod = at.method().toUpperCase(); // HTTP请求类型,这里转换为大写
							action.actionInstance = bean.instance;
							action.actionMethod = method;
							action.argTypes = method.getParameterTypes();
							action.argNames = Reflect.argNames(method);
							action.argAnnotations = Reflect.argAnnotations(method, Arg.class);
							ACTION_CONTEXT.ACTIONS.add(action);

							log.info(String.format("ADD ACTION: @At(value=\"%s\"%s) %s.%s()", action.path, (action.httpMethod.equals(".*") ? "" : ",method=\"" + action.httpMethod + "\""), action.actionInstance.getClass().getName(), action.actionMethod.getName()));
						}
					}
				}
			}

			log.debug(String.format("%s actions started up in %sms", ACTION_CONTEXT.ACTIONS.size(), System.currentTimeMillis() - (Long) Log.get("MVCSTARTUP")));
		}
		return ACTION_CONTEXT;
	}

	/**
	 * 根据请求路径从ActionContext中得到对应的Action,或者NULL
	 */
	public Action getAction(String path, String method) {
		for (Action action : ACTIONS) {
			if (Verify.regex(path, "^" + action.path + "$") && Verify.regex(method, "^" + action.httpMethod + "$")) {
				return action;
			}
		}
		return null;
	}
}