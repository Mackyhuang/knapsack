package vip.ifmm.knapsack;

import vip.ifmm.enhancer.EnhancementDriver;

/**
 * author: mackyhuang
 * email: mackyhuang@163.com
 * date: 2019/4/22
 * motto: Le vent se lève, il faut tenter de vivre
 */
public class Knapsack {

    //Double Check Locking 双检查锁机制下实现的懒汉式单例模式
    public static volatile ProducingSack producingSack = null;

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
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T takeOutInstance(Class<T> clazz){
        synchronized (Knapsack.class){
            if (producingSack == null){
                producingSack = new ProducingSack();
            }
        }
        return producingSack.producingObject(clazz);
    }

    public <T> T enhanceInstance(Class<T> clazz, Class adapter, Class annotation){
        synchronized (Knapsack.class){
            if (producingSack == null){
                producingSack = new ProducingSack();
            }
        }
        T target = producingSack.producingObject(clazz);
        T result = null;
        try {
            result = EnhancementDriver.prepare(target, adapter, annotation);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
