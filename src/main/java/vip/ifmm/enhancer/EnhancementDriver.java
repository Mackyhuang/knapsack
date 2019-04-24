package vip.ifmm.enhancer;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>Enhance the main class</p>
 * author: mackyhuang
 * email: mackyhuang@163.com
 * date: 2019/4/23
 * motto: Le vent se lève, il faut tenter de vivre
 */
public class EnhancementDriver {

//    public static Map<Class<?>, Map<Annotation, Map<Class<?>, Object>>> proxyObjectPool = new ConcurrentHashMap<>();
    public static Map<String, Object> proxyObjectPool = new ConcurrentHashMap<>();

    /**
     * <p>I need you to tell me three important factors</p>
     * @param target The class you need to enhance (the class being proxied)
     * @param adapter How does the implementation handle classes with enhanced interfaces {@link EnhancementAdapter}
     * @param annotation Which annotation do you want to use to mark the class to be enhanced
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static <T> T prepare(T target, Class adapter, Class annotation) throws IllegalAccessException, InstantiationException {
        //创建增强管理类
        Enhancement enhancement = new Enhancement();
        //通过Class创建目标类的实例
//        Object targetObj = target.newInstance();
        //通过Class创建接口的实例
        EnhancementAdapter adapterObj = (EnhancementAdapter) adapter.newInstance();
        //调用管理类的Bind方法返回一个被增强后的实例
        T result = (T) enhancement.bind(target, adapterObj, annotation);
        if (result != null){
            String key = proxyPoolKeyRing(target.getClass(), adapter, annotation);
            proxyObjectPool.put(key, result);
        }
        return result;
    }

    /**
     * 将一个代理类放进proxyObjectPool，需要一个key，这个方法为你提供专属的key
     * @param target
     * @param adapter
     * @param annotation
     * @return
     */
    public static String proxyPoolKeyRing(Class target, Class adapter, Class annotation){
        StringBuilder keyHash = new StringBuilder();
        keyHash.append(target.hashCode());
        keyHash.append(adapter.hashCode());
        keyHash.append(annotation.hashCode());
        return keyHash.toString();
    }
}
