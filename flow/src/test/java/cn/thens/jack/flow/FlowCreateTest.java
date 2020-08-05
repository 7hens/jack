package cn.thens.jack.flow;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import cn.thens.jack.TestX;

/**
 * @author 7hens
 */
public class FlowCreateTest {
    @Test
    public void interval() {
        Flow.interval(10, TimeUnit.MILLISECONDS)
                .onCollect(TestX.collector("A"))
                .to(TestX.collect());
    }
}
