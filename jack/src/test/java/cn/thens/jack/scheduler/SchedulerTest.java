package cn.thens.jack.scheduler;

import org.junit.Test;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.concurrent.TimeUnit;

import cn.thens.jack.TestX;
import cn.thens.jack.flow.Flow;

/**
 * @author 7hens
 */
public class SchedulerTest {
    @Test
    public void unconfined() {
        TestX.Logger logger = TestX.logger();
        Schedulers.unconfined().schedule(() -> {
            logger.log("A");
        });
        logger.log("B");

        Schedulers.unconfined().schedule(() -> {
            logger.log("C");
        }, 1, TimeUnit.SECONDS);
        logger.log("D");

        Schedulers.unconfined().schedulePeriodically(() -> {
            logger.log("E: " + System.currentTimeMillis());
        }, 2, 1, TimeUnit.SECONDS);
        logger.log("F");
    }

    @Test
    public void unconfinedFlow() {
        Flow.interval(100, TimeUnit.MILLISECONDS)
                .take(10)
                .onCollect(TestX.collector("A"))
                .flowOn(Schedulers.unconfined())
                .onCollect(TestX.collector("B"))
                .flowOn(TestX.scheduler("b"))
                .to(TestX.collect());
    }

    @Test
    public void processId() {
        TestX.Logger logger = TestX.logger();
        logger.log("threadId: " + Thread.currentThread().getId());

        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        logger.log("processName: " + runtimeMXBean.getName());
        logger.log("vmVendor: " + runtimeMXBean.getVmVendor());
    }
}
