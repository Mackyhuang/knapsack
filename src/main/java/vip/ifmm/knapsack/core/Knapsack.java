package vip.ifmm.knapsack.core;

import vip.ifmm.knapsack.enhancer.EnhancementDriver;


 /**
 * @author: mackyhuang
 * <p>email: mackyhuang@163.com <p>
 * <p>date: 2019/4/22 </p>
 */
public class Knapsack {

    //Double Check Locking 双检查锁机制下实现的懒汉式单例模式
    public static volatile ProducingSack producingSack = null;

     /**
      * 对用实例化ProducingSack
      * 单例模式保证全局只有一个 ProducingSack
      */
    public Knapsack sew(){
        synchronized (Knapsack.class){
            if (producingSack == null){
                producingSack = new ProducingSack();
            }
        }
        return this;
    }

    /**
     * 获取一个指定的实例
     * @param clazz 指定的实例的Class
     */
    public <T> T takeOutInstance(Class<T> clazz){
        return producingSack.producingObject(clazz);
    }

    /**
     * 容器中装载一个指定的实例，并且获得这个实例的代理对象
     * 使用这个接口，那么就是需要实现AOP的时候
     * 这里是基于注解的AOP，在方法上注解上自定义的注解
     * 实现EnhancementAdapter接口中的方法
     * 把他们当作参数传入这个方法，容器会返回已经代理的对象
     * @param clazz 指定的实例的Class
     * @param adapter 实现EnhancementAdapter接口的实现类的CLass
     * @param annotation 自定义注解的Class
     */
    public <T> Object enhanceInstance(Class<T> clazz, Class adapter, Class annotation){
        Object result = null;
        String key = EnhancementDriver.proxyPoolKeyRing(clazz, adapter, annotation);
        if ((result = (T) EnhancementDriver.proxyObjectPool.get(key)) != null){
            return result;
        }
        T target = producingSack.producingObject(clazz);
        try {
            result = EnhancementDriver.prepare(target, adapter, annotation);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
