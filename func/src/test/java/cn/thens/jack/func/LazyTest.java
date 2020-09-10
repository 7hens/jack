package cn.thens.jack.func;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author 7hens
 */
public class LazyTest {

    @Test
    public void get() throws Throwable {
        for (int i = 0; i < 1000; i++) {
            Thread.sleep(10);
            int finalI = i;
            Lazy<String> lazy = Lazy.of(() -> {
                Thread.sleep(10);
                return "" + finalI;
            });
            for (int j = 0; j < 20; j++) {
                int finalJ = j;
                new Thread() {
                    @Override
                    public void run() {
                        Assert.assertNotNull(lazy.get());
                        System.out.println(lazy.get() + "." + finalJ);
                    }
                }.start();
            }
        }
        Thread.sleep(1000);
    }
}