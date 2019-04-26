package vip.ifmm.knapsack.enhancer;

import net.sf.cglib.proxy.Enhancer;
import vip.ifmm.knapsack.entity.EnhanceInfo;
import vip.ifmm.knapsack.exception.DoProxyException;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;

/**
 * 专门为目标对象生成代理类
 * 不仅实现了基于cglib的动态代理，还实现基于jdk的动态代理
 * 如果你需要被代理的类，是一个不被final修饰的类, 那么会自动调用cglib动态带出
 * 否则的话会尝试使用jdk的动态代理，基于实现接口的代理
 * 当然，如果连接口都没有，那可就只能无情报错 (注意，这里报错是在调用处，你总是找不到一个类来接收它的赋值)
 * 将需要增强的业务操作，完成在这个接口的实现类中{@see EnhancementAdapter}
 * @author: mackyhuang
 * <p>email: mackyhuang@163.com <p>
 * <p>date: 2019/4/23 </p>
 */
public class Enhancement implements InvocationHandler, net.sf.cglib.proxy.InvocationHandler {

    //增强对象
    private Object target;
    //增强方法
    private EnhancementAdapter adapter;
    //增强注解
    private Class annotation;

    /**
     * 初始化类字段， 生成代理对象
     * @return 生成的代理对象
     */
    public Object doProxy(Object target, EnhancementAdapter adapter, Class annotation){
        this.target = target;
        this.adapter = adapter;
        this.annotation = annotation;
        Object result = null;
        if (!Modifier.isFinal(target.getClass().getModifiers())){
            result = cglibDoProxy();
        } else {
            result = jdkDoProxy();
        }
        return result;
    }

    /**
     * 基于cglib动态代理生成代理对象
     */
    private Object cglibDoProxy(){
        //创建增强器
        Enhancer enhancer = new Enhancer();
        //告知增强对象
        enhancer.setSuperclass(this.target.getClass());
        //设置回调，代理类上的方法调用时会调用Callback -> 需要实现intercept
        enhancer.setCallback(this);
        //返回cglib生成的动态代理对象
        return enhancer.create();
    }

    /**
     * 基于jdk动态代理生成代理对象
     */
    private Object jdkDoProxy(){
        //jdk动态代理生成代理对象
        return Proxy.newProxyInstance(this.target.getClass().getClassLoader(), target.getClass().getInterfaces(), this);
    }

    /**
     * 实现cglib和jdk的InvocationHandler就必须实现这个方法作为回调
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //获取代理类所执行方法的指定注解
        Annotation hasAnnotation = method.getAnnotation(this.annotation);
        //若指定注解为空 则无需使用增强方法，直接执行原本的方法
        if (hasAnnotation == null){
            return method.invoke(target, args);
        }
        EnhanceInfo enhanceInfo = new EnhanceInfo(target, method, args);
        //执行前的操作
        if (adapter.preInvoke(enhanceInfo)){
            return enhanceInfo.getResult();
        }
        //调用实际的业务方法
        Object result = null;
        try {
            result = method.invoke(target, args);
            enhanceInfo.setResult(result);
            //执行后的操作
            adapter.postInvoke(enhanceInfo);
            return enhanceInfo.getResult();
        } catch (Exception e){
            enhanceInfo.setException(e);
            //抛出异常后的操作
            adapter.postThrowing(enhanceInfo);
            return enhanceInfo.getResult();
        } finally {
            //返回结果时的操作
            adapter.postReturning(enhanceInfo);
        }
    }
}
