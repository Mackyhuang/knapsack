package vip.ifmm.knapsack;

/**
 * author: mackyhuang
 * email: mackyhuang@163.com
 * date: 2019/4/22
 * motto: Le vent se lève, il faut tenter de vivre
 */
public class Knapsack {

    ProducingSack producingSack = null;

    /**
     * 获取一个指定的实例
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T takeOutInstance(Class<T> clazz){
        if (producingSack == null){
            producingSack = new ProducingSack();
        }
        return producingSack.producingObject(clazz);
    }
}
