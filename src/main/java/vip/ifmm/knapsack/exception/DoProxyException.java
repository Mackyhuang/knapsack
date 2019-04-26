package vip.ifmm.knapsack.exception;

/**
 * 动态代理相关异常
 * <p>Dynamic proxy-related anomalies</p>
 * @author: mackyhuang
 * <p>email: mackyhuang@163.com <p>
 * <p>date: 2019/4/25 </p>
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
