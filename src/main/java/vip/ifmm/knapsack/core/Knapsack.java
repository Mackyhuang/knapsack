package vip.ifmm.knapsack.core;

import vip.ifmm.knapsack.enhancer.EnhancementDriver;
import vip.ifmm.knapsack.loader.PropertiesLoader;
import vip.ifmm.knapsack.packageScan.ClasspathPackageScanner;
import vip.ifmm.knapsack.packageScan.PackageScanner;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Set;


/**
 * @author: mackyhuang
 * <p>email: mackyhuang@163.com <p>
 * <p>date: 2019/4/22 </p>
 */
public class Knapsack {

    //Double Check Locking 双检查锁机制下实现的懒汉式单例模式
    public static volatile ProducingSack producingSack = null;
    public static volatile PackageScanner packageScanner = null;
    public static List<String> fullyQualifiedClassNameList = null;

    /**
     * 无需使用到配置文件的初始化接口
     * @return
     */
    public Knapsack sew(){
        return sew(null);
    }

     /**
      * 需使用到配置文件的初始化接口
      * 对用实例化ProducingSack
      * 单例模式保证全局只有一个 ProducingSack
      * 加载配置文件 完成属性加载
      */
    public Knapsack sew(String path){
        synchronized (Knapsack.class){
            if (producingSack == null){
                producingSack = new ProducingSack();
            }
        }
        loadPropertiesAndClass(path);
        return this;
    }

    public LinkPoint link(Class parentClazz){
        LinkPoint linkPoint = new LinkPoint(parentClazz);
        return linkPoint;
    }

    /**
     * 获取一个指定的实例
     * @param clazz 指定的实例的Class
     */
    public <T> T takeOutInstance(Class<T> clazz){
        return producingSack.producingObject(clazz);
    }

    /**
     * 容器中装载一个指定的实例，并且获得这个实例的代理对象
     * 使用这个接口，那么就是需要实现AOP的时候
     * 这里是基于注解的AOP，在方法上注解上自定义的注解
     * 实现EnhancementAdapter接口中的方法
     * 把他们当作参数传入这个方法，容器会返回已经代理的对象
     * @param clazz 指定的实例的Class
     * @param adapter 实现EnhancementAdapter接口的实现类的CLass
     * @param annotation 自定义注解的Class
     */
    public <T> Object enhanceInstance(Class<T> clazz, Class adapter, Class annotation){
        Object result = null;
        String key = EnhancementDriver.proxyPoolKeyRing(clazz, adapter, annotation);
        if ((result = (T) EnhancementDriver.proxyObjectPool.get(key)) != null){
            return result;
        }
        T target = producingSack.producingObject(clazz);
        try {
            result = EnhancementDriver.prepare(target, adapter, annotation);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 通过path加载properties文件 然后读取里面的包名
     * 通过包名获取包下类文件
     * 遍历文件里qualifier.开头的属性
     * 如果类文件其中包含它，就生成一个Class
     * 同理生成一个value的Class, 这就是接口和实现类的Class，然后加载到限定类池
     */
    private void loadPropertiesAndClass(String path){
        if (path == null){
            return;
        }
        //获取配置文件键值对
        Properties loader = PropertiesLoader.loader(path);
        //获取包属性
        String scanPackage = loader.getProperty("scanPackage");
        if (scanPackage != null){
            //初始化包扫描主类
            packageScanner = new ClasspathPackageScanner(scanPackage);
            try {
                //初始化全限丁磊名列表
                fullyQualifiedClassNameList = packageScanner.getFullyQualifiedClassNameList();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (fullyQualifiedClassNameList != null){
            Set<String> keySet = loader.stringPropertyNames();
            for (String key : keySet){
                if (key.startsWith("qualifier.")){
                    //检查key
                    String parentClass = ClasspathPackageScanner.isContain(key.substring(key.indexOf(".") + 1));
                    if (parentClass != null){
                        //可能包含多个value，用$分割
                        String value = loader.getProperty(key);
                        String[] splitValues = value.split("\\$");
                        //遍历value
                        for (String split : splitValues){
                            String clazz = ClasspathPackageScanner.isContain(split);
                            Class parent = null;
                            Class child = null;
                            if (clazz != null){
                                try {
                                    parent = Class.forName(parentClass);
                                    child = Class.forName(clazz);
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }
                                //绑定到限定类池
                                QualifierSack.bindQualifierClassToPool(parent, child);
                            }
                        }
                    }
                }
            }
        }
    }
}
