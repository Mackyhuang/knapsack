package vip.ifmm.knapsack.enhancer;

import vip.ifmm.knapsack.entity.EnhanceInfo;

/**
 * 增强方法
 * 提供 方法执行前 方法执行完成 参数返回完成前 抛出异常时 四个状态
 * 只需要编写一个类，继承这个接口，然后在对应的方法中实现需要的业务即可
 * {@see EnhanceInfo} 这个类中封装了你可以弄到的运行时数据
 * @author: mackyhuang
 * <p>email: mackyhuang@163.com <p>
 * <p>date: 2019/4/23 </p>
 */
public interface EnhancementAdapter {

    /**
     * 想要在方法执行前做一些操作就需要重写这个方法
     * 需要注意的是这个返回值，决定了后续流程是否继续进行
     * 例如redis查缓存的操作，如果已经存在这样的key-value，那么直接返回，无需执行方法
     * 所以，若你需要这样类似的操作，重写这个方法的时候，将结果写入result，返回true
     * 如果你只是常规的代理，那么返回false即可
     * @return 这个boolean决定后续流程是否继续进行
     */
    boolean preInvoke(EnhanceInfo enhanceInfo);

    /**
     * 想要在方法执行后做一些操作就需要重写这个方法
     * 在这里，可以对result大肆的修改
     */
    void postInvoke(EnhanceInfo enhanceInfo);

    /**
     * 想要在方法返回后做一些操作就需要重写这个方法
     * 这里一般是进行资源释放类似的工作
     * 千万不要想着在这里修改result，因为臣妾做不到
     * （这里是通过finally实现的，所以才会做不到）
     */
    void postReturning(EnhanceInfo enhanceInfo);

    /**
     * 想要在方法抛出异常时做一些操作就需要重写这个方法
     * 如果发生异常你还想给外界返回点啥，大胆的给result设置值，它会帮你送出去的
     */
    void postThrowing(EnhanceInfo enhanceInfo);
}
