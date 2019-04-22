package vip.ifmm;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import vip.ifmm.knapsack.Knapsack;

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
                    System.out.println(new Knapsack().sew().producingSack);
                }
            }.run();
        }
    }


}
