package vip.ifmm.knapsack.exception;

/**
 * 依赖注入相关异常
 * <p>Dependency Injection Related Anomalies</p>
 * author: mackyhuang
 * <p>email: mackyhuang@163.co </p>
 * <p>date: 2019/4/22 </p>
 */
public class InjectionException extends RuntimeException{

    public InjectionException(){
        super();
    }

    public InjectionException(String message, Throwable cause){
        super(message, cause);
    }

    public InjectionException(String message){
        super(message);
    }

    public InjectionException(Throwable cause){
        super(cause);
    }
}
