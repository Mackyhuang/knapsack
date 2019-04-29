package vip.ifmm;

import vip.ifmm.knapsack.entity.*;
import vip.ifmm.knapsack.core.Knapsack;
import vip.ifmm.knapsack.core.QualifierSack;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Knapsack knapsack = new Knapsack();
//        QualifierSack.bindQualifierClassToPool(Person.class, Stu.class);
//        QualifierSack.bindQualifierClassToPool(Person.class, Teacher.class);
        knapsack.link(Person.class).with(Stu.class);
        knapsack.link(Person.class).with(Teacher.class);
//        ClassRoom classRoom = knapsack.sew().takeOutInstance(ClassRoom.class);
        ClassRoom classRoom =  (ClassRoom) knapsack.sew("test.properties").enhanceInstance(ClassRoom.class, SimpleAdapter.class, Recordlog.class);
        ClassRoom classRoom1 =  (ClassRoom) knapsack.sew().enhanceInstance(ClassRoom.class, SimpleAdapter.class, Recordlog.class);
        System.out.println(classRoom1 == classRoom);
        System.out.println(classRoom);
        classRoom.sleep(1);

        knapsack.link(Node.class).with(NodeA.class);
        knapsack.link(Node.class).with(NodeB.class);
        Root root = knapsack.takeOutInstance(Root.class);
        System.out.println(root);
    }
}
