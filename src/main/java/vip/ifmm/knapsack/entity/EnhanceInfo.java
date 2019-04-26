package vip.ifmm.knapsack.entity;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 重写代理方法时的参数封装类
 * 在你使用时
 * 字段proxy，method，args是必定有的
 * result和exception啥时候有你肯定是知道的
 * <p>Parametric encapsulation classes when overriding proxy methods</p>
 * @author: mackyhuang
 * <p>email: mackyhuang@163.com <p>
 * <p>date: 2019/4/26 </p>
*/
public class EnhanceInfo {

    //方法执行后的返回值
    private Object result;
    //被代理的对象
    private Object proxy;
    //被执行的方法
    private Method method;
    //方法的参数列表
    private Object[] args;
    //运行中可能抛出的异常
    private Exception exception;

    public EnhanceInfo() {
    }

    public EnhanceInfo(Object proxy, Method method, Object[] args) {
        this.proxy = proxy;
        this.method = method;
        this.args = args;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Object getProxy() {
        return proxy;
    }

    public void setProxy(Object proxy) {
        this.proxy = proxy;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    @Override
    public String toString() {
        return "EnhanceInfo{" +
                "result=" + result +
                ", proxy=" + proxy +
                ", method=" + method +
                ", args=" + Arrays.toString(args) +
                ", exception=" + exception +
                '}';
    }
}
