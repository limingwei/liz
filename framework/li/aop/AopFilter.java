package li.aop;

/**
 * AopFilter
 * 
 * @author li (limw@w.cn)
 * @version 0.1.1 (2012-09-20)
 */
public interface AopFilter {
	public void filter(AopChain chain);
}