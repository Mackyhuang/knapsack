package vip.ifmm.knapsack;

import vip.ifmm.exception.InjectionException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * author: mackyhuang
 * email: mackyhuang@163.com
 * date: 2019/4/22
 * motto: Le vent se lève, il faut tenter de vivre
 */
public class SingletonSack {

    public static Map<Class<?>, Object> singletonObjectPool = new ConcurrentHashMap<>();

    public static Map<Class<?>, Class<?>> singleTonClassPool = new ConcurrentHashMap<>();

    /**
     * 把一个单例对象绑定到单例对象池中
     * @param clazz
     * @param object
     * @param <T>
     * @return
     */
    public static <T> void bindSingletonObjectToPool(Class<T> clazz, T object){
        if (singletonObjectPool.put(clazz, object) != null){
            throw new InjectionException(String.format("重复定义 %s 这个单例对象 ",clazz.getCanonicalName()));
        }
    }

    /**
     * 把一个单例类绑定到单例类池
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> void bindSingletonClassToPool(Class<T> clazz){
        bindSingletonClassToPool(clazz, clazz);
    }

    /**
     * 实现bindSingletonClassToPool(Class<T> clazz)的功能
     * @param parentClazz
     * @param clazz
     * @param <T>
     * @return
     */
    private static <T> void bindSingletonClassToPool(Class<?> parentClazz, Class<T> clazz){
        if(singleTonClassPool.put(parentClazz, clazz) != null){
            throw new InjectionException(String.format("重复加载 %s 这个单例类", clazz.getCanonicalName()));
        }
    }
}
