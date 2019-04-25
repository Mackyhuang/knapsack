package vip.ifmm.enhancer;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * <p>This is where you manage your <strong>enhancement objects</strong> and <strong>enhancement methods</strong> and <strong>enhancement annotation</strong></p>
 * <P>and building this class requires the three sections mentioned above</P>
 * <p>enhancement methods needs to implement an interface {@link EnhancementAdapter}</p>
 * author: mackyhuang
 * email: mackyhuang@163.com
 * date: 2019/4/23
 * motto: Le vent se lève, il faut tenter de vivre
 */
public class Enhancement implements InvocationHandler, net.sf.cglib.proxy.InvocationHandler {

    //增强对象
    private Object target;
    //增强方法
    private EnhancementAdapter adapter;
    //增强注解
    private Class annotation;

    public Object bind(Object target, EnhancementAdapter adapter, Class annotation){
        this.target = target;
        this.adapter = adapter;
        this.annotation = annotation;

        //创建增强器
        Enhancer enhancer = new Enhancer();
        //告知增强对象
        enhancer.setSuperclass(this.target.getClass());
        //设置回调，代理类上的方法调用时会调用Callback -> 需要实现intercept
        enhancer.setCallback(this);
        //返回动态代理对象
        Object result = enhancer.create();
        return result;
    }

    //
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        return null;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //获取代理类所执行方法的指定注解
        Annotation hasAnnotation = method.getAnnotation(this.annotation);
        //若指定注解为空 则无需使用增强方法，直接执行原本的方法
        if (hasAnnotation == null){
            return method.invoke(target, args);
        }
        //执行 前 增强
        adapter.preInvoke();
        //调用实际的业务方法
        Object result = null;
        try {
            result = method.invoke(target, args);
            //执行 后 增强
            adapter.postInvoke();
        } catch (Exception e){
            //抛出异常以后的处理
            adapter.postThrowing(e);
        } finally {
            //返回结果的处理
            adapter.postReturning(result);
        }

        return result;
    }
}
