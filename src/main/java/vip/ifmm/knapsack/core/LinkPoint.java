package vip.ifmm.knapsack.core;

/**
 * @author: mackyhuang
 * <p>email: mackyhuang@163.com <p>
 * <p>date: 2019/4/28 </p>
 */
public class LinkPoint {

    private Class parentClazz;

    private Class clazz;

    public LinkPoint(Class parentClazz) {
        this.parentClazz = parentClazz;
    }

    public Class getParentClazz() {
        return parentClazz;
    }

    public void setParentClazz(Class parentClazz) {
        this.parentClazz = parentClazz;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }


    public void with(Class clazz){
        this.clazz = clazz;
        QualifierSack.bindQualifierClassToPool(parentClazz, clazz);
    }


    @Override
    public String toString() {
        return "LinkPoint{" +
                "parentClazz=" + parentClazz +
                ", clazz=" + clazz +
                '}';
    }
}
