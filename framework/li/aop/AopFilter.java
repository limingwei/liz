package li.aop;

/**
 * AopFilter接口,实现类需要是一个Bean,实现类的方法不能Aop
 * 
 * @author li (limw@w.cn)
 * @version 0.1.1 (2012-09-20)
 */
public interface AopFilter {
	/**
	 * AopFilter的子类需要实现的方法
	 */
	public void doFilter(AopChain chain);
}