package vip.ifmm.knapsack.exception;

/**
 * author: mackyhuang
 * <p>email: mackyhuang@163.co </p>
 * <p>date: 2019/4/28 </p>
 */
public class ResourceLoadException extends RuntimeException{

    public ResourceLoadException(){
        super();
    }

    public ResourceLoadException(String message, Throwable cause){
        super(message, cause);
    }

    public ResourceLoadException(String message){
        super(message);
    }

    public ResourceLoadException(Throwable cause){
        super(cause);
    }
}
