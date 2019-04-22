package vip.ifmm.knapsack;

import vip.ifmm.exception.InjectionException;

import javax.inject.Named;
import javax.inject.Qualifier;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * author: mackyhuang
 * email: mackyhuang@163.com
 * date: 2019/4/22
 * motto: Le vent se lève, il faut tenter de vivre
 */
public class QualifierSack {

    public static Map<Class<?>, Map<Annotation, Object>> qualifierObjectPool = new ConcurrentHashMap<>();

    public static Map<Class<?>, Map<Annotation, Class<?>>> qualifierClassPool = new ConcurrentHashMap<>();

    /**
     * 把一个限定对象绑定到限定对象池中
     * @param clazz
     * @param annotation
     * @param object
     * @param <T>
     * @return
     */
    public static <T> void bindQualifierObjectToPool(Class<T> clazz, Annotation annotation, T object){
        if (!annotation.annotationType().isAnnotationPresent(Qualifier.class)){
            throw new InjectionException(String.format("%s 的注解 %s 尚未关联 -> @Qualifier ", object.getClass().getCanonicalName(), annotation.annotationType().getCanonicalName()));
        }
        Map<Annotation, Object> qualifierObjectMapping = qualifierObjectPool.get(clazz);
        if (qualifierObjectMapping == null){
            qualifierObjectMapping = new ConcurrentHashMap<>();
            qualifierObjectPool.put(clazz, qualifierObjectMapping);
        }
        if (qualifierObjectMapping.put(annotation, object) != null){
            throw new InjectionException(String.format("重复定义 注解%s的%s 这个限定对象 ", annotation.annotationType().getCanonicalName(), clazz.getCanonicalName()));
        }
    }

    /**
     * 把一个限定类绑定到限定类池
     * @param parentClazz
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> void bindQualifierClassToPool(Class<?> parentClazz, Class<T> clazz){
        Annotation[] annotationArr = clazz.getAnnotations();
        for (Annotation annotation : annotationArr){
            if (annotation.annotationType().isAnnotationPresent(Qualifier.class)){
                //这里 判断如果注解的value为空，就自动为的value改成类名小写
                String value = ((Named) annotation).value();
                if ("".equals(value)){
                    InvocationHandler invocationHandler = Proxy.getInvocationHandler(((Named) annotation));
                    try {
                        Field memberValues = invocationHandler.getClass().getDeclaredField("memberValues");
                        memberValues.setAccessible(true);
                        Map memberMap = (Map) memberValues.get(invocationHandler);
                        memberMap.put("value", clazz.getSimpleName().toLowerCase());
                    } catch (Exception e) {
                        //TODO Exception msg
                        e.printStackTrace();
                    }
                }
                //
                bindQualifierClassToPool(parentClazz, annotation, clazz);
                return;
            }
        }
        throw new InjectionException(String.format("限定类 %s 需要关联 @Qualifier ", clazz.getCanonicalName()));
    }

    /**
     * 实现bindQualifierClassToPool(Class<?> parentClazz, Class<T> clazz)的功能
     * @param parentClazz
     * @param annotation
     * @param clazz
     * @param <T>
     * @return
     */
    private static <T> void bindQualifierClassToPool(Class<?> parentClazz, Annotation annotation, Class<T> clazz){
        if (!annotation.annotationType().isAnnotationPresent(Qualifier.class)){
            throw new InjectionException(String.format("%s 的注解 %s 尚未关联 -> @Qualifier ", clazz.getCanonicalName(), annotation.annotationType().getCanonicalName()));
        }
        Map<Annotation, Class<?>> qualifierClassMapping = qualifierClassPool.get(parentClazz);
        if (qualifierClassMapping == null){
            qualifierClassMapping = new ConcurrentHashMap<>();
            qualifierClassPool.put(parentClazz, qualifierClassMapping);
        }
        if (qualifierClassMapping.put(annotation, clazz) != null){
            throw new InjectionException(String.format("重复加载 注解%s的%s 这个限定类 ", annotation.annotationType().getCanonicalName(), parentClazz.getCanonicalName()));
        }
    }
}
