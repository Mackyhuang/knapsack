package vip.ifmm.knapsack;

import vip.ifmm.exception.InjectionException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Consumer;

/**
 * author: mackyhuang
 * email: mackyhuang@163.com
 * date: 2019/4/22
 * motto: Le vent se lève, il faut tenter de vivre
 */
public class ProducingSack {

    private Set<Class<?>> processClassPool = Collections.synchronizedSet(new HashSet<>());

    /**
     * 重载提供接口
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T producingObject(Class<T> clazz){
        return producingObject(clazz, null);
    }

    /**
     * 先去单例池子查看有没有 没有就通过构造器（一个方法）创建， 然后判断是不是单例，是的话就放进单例池。
     * @param clazz
     * @param consumer
     * @param <T>
     * @return
     */
    public <T> T producingObject(Class<T> clazz, Consumer<T> consumer){
        Object object = SingletonSack.singletonObjectPool.get(clazz);
        if (object != null){
            return (T) object;
        }
        //检查类的构造器
        ArrayList<Constructor<T>> constructors = new ArrayList<>();
        T target = null;
        for (Constructor constructor : clazz.getDeclaredConstructors()){
            if (!constructor.isAnnotationPresent(Inject.class) && constructor.getParameterCount() > 0){
                continue;
            }
            constructor.setAccessible(true);
//            if (!constructor.isAccessible()){
//                continue;
//            }
            constructors.add(constructor);
        }
        if (constructors.size() > 1){
            throw new InjectionException(String.format("%s 找到了多个可用来以来注入的构造器 ", clazz.getCanonicalName()));
        }
        if (constructors.isEmpty()){
            throw new InjectionException(String.format("%s 没有可用来依赖注入的构造器 ", clazz.getCanonicalName()));
        }
        //通过构造器构造实例
        processClassPool.add(clazz);

        target = producingFromConstruct(constructors.iterator().next());

        processClassPool.remove(clazz);
        //处理生成的对象

        boolean isSingleton = clazz.isAnnotationPresent(Singleton.class);
        //这个判断是查询单例对象池和单例类池里面 是否存在这个类或对象
        if (!isSingleton){
            isSingleton = SingletonSack.singleTonClassPool.containsKey(clazz);
        }

        if (isSingleton){
            SingletonSack.singletonObjectPool.put(clazz, target);
        }
        //consumer
        if (consumer != null){
            consumer.accept(target);
        }
        //为对象填充字段的注入
        fillFieldIntoObject(target);

        return target;
    }

    /**
     * 内部使用
     * 通过限定对象池得到对象 如果不存在对象就去限定类池拿类信息创建对象 如果还是没有就返回Null
     * @param parentClazz
     * @param annotationArr
     * @param clazz
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    private <T> T producingFromQualifierObjectPool(Class<?> parentClazz, Annotation[] annotationArr, Class<T> clazz){
        Map<Annotation, Object> qualifierObjectmapping = QualifierSack.qualifierObjectPool.get(clazz);
        // 如果限定池中确实存在这个parentClazz的对应对象
        if (qualifierObjectmapping != null){
            Set<Object> products = new HashSet<>();
            for (Annotation annotation : annotationArr){
                Object pending = qualifierObjectmapping.get(annotation);
                if (pending != null){
                    products.add(pending);
                }
            }
            if (products.size() > 1){
                throw new InjectionException(String.format("%s 和 %s 这对关系的对象被重复定义", parentClazz.getCanonicalName(), clazz.getCanonicalName()));
            }
            if (!products.isEmpty()){
                return (T) (products.iterator().next());
            }
        }
        //如果没有就去限定类池查找到对应的类 然后实例化一个对象
        Map<Annotation, Class<?>> qualifierClassMapping = QualifierSack.qualifierClassPool.get(clazz);
        if (qualifierClassMapping != null){
            Set<Class<?>> classes = new HashSet<>();
            Annotation currentAnno = null;
            for (Annotation annotation : annotationArr){
                Class<?> pending = qualifierClassMapping.get(annotation);
                if (pending != null){
                    classes.add(pending);
                    currentAnno = annotation;
                }
            }
            if (classes.size() > 1){
                throw new InjectionException(String.format("%s 和 %s 这对关系的类被重复定义", parentClazz.getCanonicalName(), clazz.getCanonicalName()));
            }
            //若限定类池里面有这个类，就用它生成一个对象 并且使用consumer lambda将这个创建好的对象放进限定对象池
            if (!classes.isEmpty()){
                final Annotation finalAnno = currentAnno;
                T result = (T) producingObject(classes.iterator().next(), target -> {
                    QualifierSack.bindQualifierObjectToPool((Class<T>)clazz, finalAnno, (T) target);
                });
                return result;
            }
        }
        //如果类池也没有，就返回Null
        return null;
    }

    /**
     * 通过构造函数 newInstance 一个对象
     * 由于构造函数需要参数，所以这里需要使用 producingFromParameter
     * @param constructor
     * @param <T>
     * @return
     */
    private <T> T producingFromConstruct(Constructor<T> constructor){
        ArrayList<Object> paramObjectList = new ArrayList();
        for (Parameter parameter : constructor.getParameters()){
            if (processClassPool.contains(parameter.getType())){
                throw new InjectionException(String.format("%s 出现循环依赖 ", constructor.getDeclaringClass().getCanonicalName()));
            }
            T paramObject = producingFromParameter(parameter);
            if (paramObject == null){
                throw new InjectionException(String.format("%s 的构造器参数 %s 为空", constructor.getDeclaringClass().getCanonicalName(), parameter.getName()));
            }
            paramObjectList.add(paramObject);
        }
        try {
            if (paramObjectList.size() == 0){
                return constructor.newInstance();
            }else {
                paramObjectList.forEach(tt -> {
                    System.out.println(tt.getClass().getTypeName());
                });
                return constructor.newInstance(paramObjectList.toArray());
            }
        } catch (Exception e) {
            throw new InjectionException(String.format("%s 构造器在实例化时出现错误：", constructor.getDeclaringClass().getCanonicalName()), e);
        }
    }

    /**
     * 给参数生成一个实例对象用来依赖注入
     * @param parameter
     * @param <T>
     * @return
     */
    private <T> T producingFromParameter(Parameter parameter){
        Class clazz = parameter.getType();
        //调用这个函数 在对象池就拿 不在就去类池拿来创建然后放进对象池
        T result = (T) producingFromQualifierObjectPool(parameter.getDeclaringExecutable().getDeclaringClass(), parameter.getAnnotations(), clazz);
        if (result != null){
            return result;
        }
        //为空就代表它都不在上面的俩个容器，直接创建
        return (T) producingObject(clazz, null);
    }

    /**
     * 给字段生成一个对象用来依赖注入
     * @param field
     * @param <T>
     * @return
     */
    private <T> T producingFromField(Field field){
        Class clazz = field.getType();
        T result = (T) producingFromQualifierObjectPool(field.getDeclaringClass(), field.getAnnotations(), clazz);
        if (result != null){
            return result;
        }

        return (T) producingObject(clazz, null);
    }

    /**
     * 创建对象的时候 注入 需要注入的字段
     * @param object
     * @param <T>
     */
    private <T> void fillFieldIntoObject(T object){
        ArrayList<Field> fieldList = new ArrayList<>();
        //遍历获取需要注入的字段（被@Inject修饰的）
        for (Field field : object.getClass().getDeclaredFields()){
            //TODO 私有field无法注入
            if (field.isAnnotationPresent(Inject.class)){
                field.setAccessible(true);
                fieldList.add(field);
            }
        }
        //遍历需要注入的字段，生成对象并设值
        Iterator<Field> iterator = fieldList.iterator();
        while (iterator.hasNext()){
            Field currentField = iterator.next();
            Object fieldObject = producingFromField(currentField);
            try {
                currentField.set(object, fieldObject);
            } catch (Exception e) {
                throw new InjectionException(String.format("为 %s 注入字段 %s 时出现错误 ", object.getClass().getCanonicalName(), currentField.getName()), e);
            }
        }
    }
}