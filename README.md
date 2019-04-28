# knapsack
-- 重构告一阶段 继续考虑重构粒度 --
#### 介绍
**knapsack** **小背包**，

顾名思义，一个轻量级依赖注入框架，
已经实现IOC依赖注入功能和CGLIB、JDK实现的AOP功能，
- 依赖注入
    - 实现JSR330 (Dependency Injection for Java)中的依赖注入规范
    - 对于接口和实现类的管理，提供了`QualifierSack.bindQualifierClassToPool`方法
- aop
    - 实现CGLIB和JDK的双重动态代理，轻便的进行切面编程
    - 提供 `preInvoke`、`postInvoke`、`postReturning`、`postThrowing`
    四个方法丰富你的业务逻辑，并且`preInvoke`提供可控的中断接口，是否继续后续执行由你决定
- 基于配置文件的功能 （开发中）    
后续实现可配置可选择自定义等多种功能


#### 安装教程
嘀嘀嘀，完成了再说鸭

#### 使用说明
到时候肯定导入包就可以使用


