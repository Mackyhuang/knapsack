package vip.ifmm.enhancer;

import vip.ifmm.entity.EnhanceInfo;

import java.lang.reflect.Method;

/**
 * 增强方法
 * 提供 方法执行前 方法执行完成 参数返回完成前 抛出异常时 四个状态
 * 只需要编写一个类，继承这个接口，然后在对应的方法中实现需要的业务即可
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
    void preInvoke(EnhanceInfo enhanceInfo);

    /**
     * <p>Enhancement method executed after the original method</p>
     */
    void postInvoke(EnhanceInfo enhanceInfo);

    /**
     * <p>Enhancement method executed after the original method returning</p>
     */
    void postReturning(EnhanceInfo enhanceInfo);

    /**
     * <p>Enhancement method executed after the original method throwing</p>
     */
    void postThrowing(EnhanceInfo enhanceInfo);
}
