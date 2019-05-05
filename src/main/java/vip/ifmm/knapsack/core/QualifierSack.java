package vip.ifmm.knapsack.core;

import vip.ifmm.knapsack.annotation.Component;
import vip.ifmm.knapsack.exception.InjectionException;

import javax.inject.Named;
import javax.inject.Qualifier;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 限定对象池和限定类池的操作类
 * author: mackyhuang
 * <p>email: mackyhuang@163.co </p>
 * <p>date: 2019/4/22 </p>
 */
public class QualifierSack {

    //限定对象池
    public static Map<Class<?>, Map<Annotation, Object>> qualifierObjectPool = new ConcurrentHashMap<>();
    //限定类弛
    public static Map<Class<?>, Map<Annotation, Class<?>>> qualifierClassPool = new ConcurrentHashMap<>();

    /**
     * 把一个限定对象绑定到限定对象池中、
     * 对象map根据类限定名和注解觉得唯一性
     * 注解请务必被@Named修饰
     */
    static <T> void bindQualifierObjectToPool(Class<T> clazz, Annotation annotation, T object){
        if (!annotation.annotationType().isAnnotationPresent(Qualifier.class)){
            throw new InjectionException(String.format("%s 的注解 %s 尚未关联 -> @Qualifier ", object.getClass().getCanonicalName(), annotation.annotationType().getCanonicalName()));
        }
        //获取类信息绑定的map
        Map<Annotation, Object> qualifierObjectMapping = qualifierObjectPool.get(clazz);
        //若空需要创建一个map
        if (qualifierObjectMapping == null){
            qualifierObjectMapping = new ConcurrentHashMap<>();
            qualifierObjectPool.put(clazz, qualifierObjectMapping);
        }
        if (qualifierObjectMapping.put(annotation, object) != null){
            throw new InjectionException(String.format("重复定义 注解%s的%s 这个限定对象 ", annotation.annotationType().getCanonicalName(), clazz.getCanonicalName()));
        }
    }

    /**
     * 把一个限定类绑定到限定类池的检查工作
     * @param parentClazz
     * @param clazz
     * @param <T>
     * @return
     */
    static <T> void bindQualifierClassToPool(Class<?> parentClazz, Class<T> clazz){
        Annotation[] annotationArr = clazz.getAnnotations();
        for (Annotation annotation : annotationArr){
            if (annotation.annotationType().isAnnotationPresent(Qualifier.class)){
                //这里 判断如果注解的value为空，就自动为的value改成类名小写
                autoCompleteAnno(clazz, annotation);
                //检查完后正式放入类池
                bindQualifierClassToPool(parentClazz, annotation, clazz);
                return;
            }
        }
        throw new InjectionException(String.format("限定类 %s 需要关联 @Qualifier ", clazz.getCanonicalName()));
    }

    /**
     * 把一个限定类绑定到限定类池
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

    /**
     * 限定类的注解value为空时，使用这个类的类名小写代替空字符串
     * 使用反射机制修改注解中的属性value的值
     * @param clazz
     * @param annotation
     */
    private static void autoCompleteAnno(Class clazz, Annotation annotation){
        String value = ((Named) annotation).value();
        if ("".equals(value)){
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(((Named) annotation));
            try {
                Field memberValues = invocationHandler.getClass().getDeclaredField("memberValues");
                memberValues.setAccessible(true);
                Map memberMap = (Map) memberValues.get(invocationHandler);
                memberMap.put("value", clazz.getSimpleName().toLowerCase());
            } catch (Exception e) {
                throw new InjectionException(e);
            }
        }
    }
}
