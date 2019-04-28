package vip.ifmm.knapsack.annotation;

import javax.inject.Qualifier;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author: mackyhuang
 * <p>email: mackyhuang@163.com <p>
 * <p>date: 2019/4/27 </p>
 */
@Qualifier
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {
    String value() default "";
}
