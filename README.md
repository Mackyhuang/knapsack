# knapsack
-- 重构告一阶段 继续考虑重构粒度 --
### 介绍
**knapsack** **小背包**，

顾名思义，一个轻量级依赖注入框架，
已经实现IOC依赖注入功能和CGLIB、JDK实现的AOP功能，还可以通过配置properties文件配置需要的属性
- 依赖注入
    - 实现JSR330 (Dependency Injection for Java)中的依赖注入规范
    - 对于接口和实现类的管理，提供了`QualifierSack.bindQualifierClassToPool`方法
- aop
    - 实现CGLIB和JDK的双重动态代理，轻便的进行切面编程
    - 提供 `preInvoke`、`postInvoke`、`postReturning`、`postThrowing`
    四个方法丰富你的业务逻辑，并且`preInvoke`提供可控的中断接口，是否继续后续执行由你决定
- 基于配置文件的功能 
    - 接口和实现类的绑定可通过配置文件实现


### 安装教程
maven中心仓库申请ing...

### 使用说明

- 引入Knapsack依赖后
- 首先声明容器， 如果依赖注入中需要使用到从实现类到接口的注入，类似于
```
    @Named("stu")
    @Inject
    public Person stu;
```

 这样的依赖注入(向Person注入实现类Stu)，就需要实现告知容器对应关系，然后就可以使用
 ```
 public <T> T takeOutInstance(Class<T> clazz)
 ```
 获取容器管理的实例，参数为需要获得实例的类
    
    先列出其他辅助类，帮助理解
   ```
         public abstract class Person
         ——————
         //声明单例Stu
         @Singleton
         @Named()
         public class Stu extends Person
         ——————
         //声明单例Teacher
         @Singleton
         @Named()
         public class Teacher extends Person
         ——————
         public class ClassRoom{
             @Named("stu")
             @Inject
             public Person stu;
         
             @Named("teacher")
             @Inject
             public Person teacher;
         }
   ```
   @Singleton觉得实例在容器中是否为单例
   
   @Named("id") 觉得在构建时或者注入时的id，构建时若id为空，默认使用类名小写
   注入时，则需要@Named与@Inject同时存在，且id必填
   
 - 这里提供2种方式：
    - 代码形式：
     ``` 
         Knapsack knapsack = new Knapsack();
         //绑定接口与实现类
         knapsack.link(Person.class).with(Stu.class);
         knapsack.link(Person.class).with(Teacher.class);
         ClassRoom classRoom = knapsack.sew()
                                       .takeOutInstance(ClassRoom.class); 
     ```
    - 配置文件形式：
     更加简洁的代码，sew的参数是配置文件的位置
     ```
         Knapsack knapsack = new Knapsack();
         ClassRoom classRoom = knapsack.sew("test.properties")
                                       .takeOutInstance(ClassRoom.class);
     ```
     配置文件的内容
     ```
     #指定包扫描位置
     scanPackage=vip.ifmm.knapsack
     #指定接口与实现类的关系，若有多个实现类，使用$分隔
     qualifier.Person=Stu$Teacher
     ```
- 在上述例子中，使用到`takeOutInstance`的位置，就是获取实例的位置，
那么，如果你把它改为
```
    ClassRoom classRoom = (ClassRoom) knapsack.enhanceInstance(ClassRoom.class, SimpleAdapter.class, Recordlog.class);
```
参数为

① 需要进行增强的类

② 实现 `EnhancementAdapter` 接口的类（业务实现处）

③ 增强标志注解（类中被标记此注解的方法才会被增强）


