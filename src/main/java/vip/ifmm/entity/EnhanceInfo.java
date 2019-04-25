package vip.ifmm.entity;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * author: mackyhuang
 * email: mackyhuang@163.com
 * date: 2019/4/26
 * motto: Le vent se lève, il faut tenter de vivre
 */
public class EnhanceInfo {

    private Object result;
    private Object proxy;
    private Method method;
    private Object[] args;
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
