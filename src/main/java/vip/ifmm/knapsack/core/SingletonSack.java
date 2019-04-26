package vip.ifmm.knapsack.core;

import vip.ifmm.knapsack.exception.InjectionException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 单例对象池和和单例类池的操作类
 * @author: mackyhuang
 * <p>email: mackyhuang@163.com <p>
 * <p>date: 2019/4/22 </p>
 */
public class SingletonSack {

    //单例对象池
    public static Map<Class<?>, Object> singletonObjectPool = new ConcurrentHashMap<>();
    //单例类池
    public static Map<Class<?>, Class<?>> singleTonClassPool = new ConcurrentHashMap<>();

    /**
     * 把一个单例对象绑定到单例对象池中
     */
    public static <T> void bindSingletonObjectToPool(Class<T> clazz, T object){
        if (singletonObjectPool.put(clazz, object) != null){
            throw new InjectionException(String.format("重复定义 %s 这个单例对象 ",clazz.getCanonicalName()));
        }
    }

    /**
     * 把一个单例类绑定到单例类池
     */
    public static <T> void bindSingletonClassToPool(Class<T> clazz){
        bindSingletonClassToPool(clazz, clazz);
    }

    /**
     * 实现 把一个单例类绑定到单例类池 的功能
     * @param parentClazz
     */
    private static <T> void bindSingletonClassToPool(Class<?> parentClazz, Class<T> clazz){
        if(singleTonClassPool.put(parentClazz, clazz) != null){
            throw new InjectionException(String.format("重复加载 %s 这个单例类", clazz.getCanonicalName()));
        }
    }
}
