package vip.ifmm.enhancer;

/**
 * <p>An interface that provides methods for adding user-defined enhancements.
 * The user implements the required actions through method overrides</p>
 * author: mackyhuang
 * email: mackyhuang@163.com
 * date: 2019/4/23
 * motto: Le vent se lève, il faut tenter de vivre
 */
public interface EnhancementAdapter {

    /**
     * <p>Enhancement method executed before the original method</p>
     */
    void preInvoke();

    /**
     * <p>Enhancement method executed after the original method</p>
     */
    void postInvoke();

    /**
     * <p>Enhancement method executed after the original method returning</p>
     * @param result
     */
    void postReturning(Object result);

    /**
     * <p>Enhancement method executed after the original method throwing</p>
     * @param e
     */
    void postThrowing(Exception e);
}
