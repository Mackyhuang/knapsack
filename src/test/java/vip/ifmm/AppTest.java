package vip.ifmm;

import static org.junit.Assert.assertTrue;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.junit.Test;
import vip.ifmm.knapsack.entity.*;
import vip.ifmm.knapsack.core.Knapsack;
import vip.ifmm.knapsack.core.QualifierSack;
import vip.ifmm.knapsack.loader.PropertiesLoader;

import java.lang.reflect.Method;
import java.util.Properties;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void SingletonFactoryInConcurrent()
    {
        for (int i = 0; i < 10; i++){
            new Runnable(){
                @Override
                public void run() {
                    //System.out.println(new Knapsack().sew().producingSack);
                }
            }.run();
        }
    }

    @Test
    public void Cglibtest(){
        ClassRoom classRoom = new ClassRoom();
        classRoom.teacher = new Teacher();
        classRoom.stu = new Stu();
        Enhancer enhancer = new Enhancer();
        //告知增强对象
        enhancer.setSuperclass(classRoom.getClass());
        //设置回调，代理类上的方法调用时会调用Callback -> 需要实现intercept
        enhancer.setCallback(new MethodInterceptor(){

            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                return method.invoke(classRoom, args);
            }
        });
        //返回动态代理对象
        Object result = enhancer.create();
        System.out.println(result);
    }

    @Test
    public void enhanceWhat(){
        Knapsack knapsack = new Knapsack();
//        knapsack.link(Person.class).with(Stu.class);
//        knapsack.link(Person.class).with(Teacher.class);

//        ClassRoom classRoom = knapsack.sew("test.properties").takeOutInstance(ClassRoom.class);
        ClassRoom classRoom = (ClassRoom) knapsack.sew("test.properties").enhanceInstance(ClassRoom.class, SimpleAdapter.class, Recordlog.class);
        ClassRoom classRoom1 = (ClassRoom) knapsack.enhanceInstance(ClassRoom.class, SimpleAdapter.class, Recordlog.class);
        System.out.println(classRoom1 == classRoom);
        System.out.println(classRoom);
        System.out.println(classRoom.sleep(1));
    }

    @Test
    public void propertiesLoader(){
        Properties loader = PropertiesLoader.loader("test.properties");
        System.out.println(loader);
    }

    @Test
    public void testClassforName(){
        try {
            Class<?> aClass = Class.forName("vip.ifmm.knapsack.entity.Person.class");
            System.out.println(aClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSplit(){
        String s = "Stu";
        String[] ss = s.split("\\$");
        System.out.println(ss.length);
    }

}
