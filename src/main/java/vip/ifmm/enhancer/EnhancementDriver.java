package vip.ifmm.enhancer;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 对类执行动态代理的对外类
 * <p>Enhance the main class</p>
 * author: mackyhuang
 * email: mackyhuang@163.com
 * date: 2019/4/23
 * motto: Le vent se lève, il faut tenter de vivre
 */
public class EnhancementDriver {

    //存储已被代理的对象池
    public static Map<String, Object> proxyObjectPool = new ConcurrentHashMap<>();

    /**
     * 对外的封装的代理主类
     * 主要负责创建Enhancement，以及实例化adapter
     * <p>I need you to tell me three important factors</p>
     * @param target The class you need to enhance (the class being proxied)
     * @param adapter How does the implementation handle classes with enhanced interfaces {@link EnhancementAdapter}
     * @param annotation Which annotation do you want to use to mark the class to be enhanced
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static Object prepare(Object target, Class adapter, Class annotation) throws IllegalAccessException, InstantiationException {
        //创建增强管理类
        Enhancement enhancement = new Enhancement();
        //通过Class创建目标类的实例
        EnhancementAdapter adapterObj = (EnhancementAdapter) adapter.newInstance();
        //调用管理类的Bind方法返回一个被增强后的实例
        Object result = enhancement.doProxy(target, adapterObj, annotation);
        String key = proxyPoolKeyRing(target.getClass(), adapter, annotation);
        bindProxyObjectToPool(result, key);
        return result;
    }

    /**
     * 将一个代理类放进proxyObjectPool，需要一个key，这个方法为你提供专属的key
     * 为保证相同类在不同注解和增强方法下在对象池中是不同的，这里将这三类信息的hashCode相加
     * @return
     */
    public static String proxyPoolKeyRing(Class target, Class adapter, Class annotation){
        StringBuilder keyHash = new StringBuilder();
        keyHash.append(target.hashCode());
        keyHash.append(adapter.hashCode());
        keyHash.append(annotation.hashCode());
        return keyHash.toString();
    }

    /**
     * 将生成的代理对象添加进对象池
     * @param proxyObject 代理对象
     * @param key map的Key值， 生成方式 {@see proxyPoolKeyRing}
     */
    private static void bindProxyObjectToPool(Object proxyObject, String key){
        if (proxyObject != null){
            proxyObjectPool.put(key, proxyObject);
        }
    }
}
