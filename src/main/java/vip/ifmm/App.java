package vip.ifmm;

import vip.ifmm.entity.*;
import vip.ifmm.knapsack.Knapsack;
import vip.ifmm.knapsack.QualifierSack;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Knapsack knapsack = new Knapsack();
        QualifierSack.bindQualifierClassToPool(Person.class, Stu.class);
        QualifierSack.bindQualifierClassToPool(Person.class, Teacher.class);
        ClassRoom classRoom = knapsack.sew().takeOutInstance(ClassRoom.class);
        System.out.println(classRoom);

        QualifierSack.bindQualifierClassToPool(Node.class, NodeA.class);
        QualifierSack.bindQualifierClassToPool(Node.class, NodeB.class);
        Root root = knapsack.takeOutInstance(Root.class);
        System.out.println(root);
    }
}
