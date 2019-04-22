package vip.ifmm.exception;

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
