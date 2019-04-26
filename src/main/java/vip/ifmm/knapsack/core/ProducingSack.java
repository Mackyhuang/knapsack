package vip.ifmm.knapsack.core;

import vip.ifmm.knapsack.exception.InjectionException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Consumer;

/**
 * 通过字段、构造函数、参数 实例化相应的对象
 * @author: mackyhuang
 * <p>email: mackyhuang@163.com <p>
 * <p>date: 2019/4/22 </p>
 */
public class ProducingSack {

    //即将创建的对象的类池
    private Set<Class<?>> processClassPool = Collections.synchronizedSet(new HashSet<>());

    /**
     * 从容器通过指定的Class获取一个相应的对象
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T producingObject(Class<T> clazz){
        return producingObject(clazz, null);
    }

    /**
     * 从容器通过指定的Class获取一个相应的对象
     * 并且可以通过Consumer对生成的对象做其他操作
     * @param clazz
     * @param consumer
     * @param <T>
     * @return
     */
    public <T> T producingObject(Class<T> clazz, Consumer<T> consumer){
        //判断单例对象池中是否有这个对象
        Object object = SingletonSack.singletonObjectPool.get(clazz);
        if (object != null){
            return (T) object;
        }

        //检查类的构造器
        T target = null;
        ArrayList<Constructor<T>> constructors = checkAvaliableContructor(clazz);
        //通过构造器构造实例
        processClassPool.add(clazz);
        target = producingFromConstruct(constructors.iterator().next());
        processClassPool.remove(clazz);
        //生成对象的单例判断和操作
        handleSingletonObject(clazz, target);

        //consumer的额外操作
        if (consumer != null){
            consumer.accept(target);
        }
        //为对象填充字段的注入
        fillFieldIntoObject(target);
        return target;
    }

    /**
     * 通过限定对象池得到对象
     * 如果限定对象中不存在这个对象，那么就去限定类池中检查是否有这个类的信息
     * 如果存在就实例化，如果不存在就返回Null
     */
    @SuppressWarnings("unchecked")
    private <T> T producingFromQualifierObjectPool(Class<?> parentClazz, Annotation[] annotationArr, Class<T> clazz){
        // 如果限定池中确实存在这个parentClazz的对应对象
        T product = isTargetInQualifierObjectPool(parentClazz, annotationArr, clazz);
        if (product != null){
            return product;
        }
        //如果没有就去限定类池查找到对应的类 然后实例化一个对象 如果没有会返回Null
        product = isTargetInQualifierClassPool(parentClazz, annotationArr, clazz);
        return product;
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

    /**
     * 对于需要实例化的类的构造函数的验证
     * 若被@Inject注解修饰，那么无论有参无参都被允许
     * 若没有，则只有无参构造函数（包括默认的构造函数）才会被允许
     */
    private <T> ArrayList checkAvaliableContructor(Class<T> clazz){
        ArrayList<Constructor<T>> constructors = new ArrayList<>();
        for (Constructor constructor : clazz.getDeclaredConstructors()){
            if (!constructor.isAnnotationPresent(Inject.class) && constructor.getParameterCount() > 0){
                continue;
            }
            constructor.setAccessible(true);
            constructors.add(constructor);
        }
        if (constructors.size() > 1){
            throw new InjectionException(String.format("%s 找到了多个可用来以来注入的构造器 ", clazz.getCanonicalName()));
        }
        if (constructors.isEmpty()){
            throw new InjectionException(String.format("%s 没有可用来依赖注入的构造器 ", clazz.getCanonicalName()));
        }
        return constructors;
    }

    /**
     * 对于生成的对象，进行单例的判断和操作
     * 只要类被@Singleton修饰，或是存在于单例类池中，就会被放入单例对象池
     */
    private <T> void handleSingletonObject(Class<T> clazz, T target){
        //处理生成的对象
        boolean isSingleton = clazz.isAnnotationPresent(Singleton.class);
        //这个判断是查询单例对象池和单例类池里面 是否存在这个类或对象
        if (!isSingleton){
            isSingleton = SingletonSack.singleTonClassPool.containsKey(clazz);
        }
        if (isSingleton){
            SingletonSack.singletonObjectPool.put(clazz, target);
        }
    }

    /**
     * 判断当前的限定对象池中是否有指定的对象
     */
    @SuppressWarnings("unchecked")
    private <T> T isTargetInQualifierObjectPool(Class<?> parentClazz, Annotation[] annotationArr, Class<T> clazz){
        Map<Annotation, Object> qualifierObjectmapping = QualifierSack.qualifierObjectPool.get(clazz);
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
        return null;
    }

    @SuppressWarnings("unchecked")
    private <T> T isTargetInQualifierClassPool(Class<?> parentClazz, Annotation[] annotationArr, Class<T> clazz){
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
        return null;
    }
}
