package vip.ifmm.exception;

/**
 * author: mackyhuang
 * email: mackyhuang@163.com
 * date: 2019/4/25
 * motto: Le vent se lève, il faut tenter de vivre
 */
public class DoProxyException extends RuntimeException{

    public DoProxyException(){
        super();
    }

    public DoProxyException(String message, Throwable cause){
        super(message, cause);
    }

    public DoProxyException(String message){
        super(message);
    }

    public DoProxyException(Throwable cause){
        super(cause);
    }
}
