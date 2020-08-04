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
        Scheduler scheduler = Schedulers.unconfined();
        scheduler.schedule(() -> {
            logger.log("A");
        });

        Cancellable cancellable = scheduler.schedule(() -> {
            logger.log("B");
        }, 1, TimeUnit.SECONDS);

        logger.log("C");

        scheduler.schedule(() -> {
            logger.log("D (cancel B)");
            cancellable.cancel();
        }, 100, TimeUnit.MILLISECONDS);

        scheduler.schedulePeriodically(() -> {
            logger.log("E: " + System.currentTimeMillis());
        }, 200, 100, TimeUnit.MILLISECONDS);
        logger.log("F");

        TestX.delay(2000);
    }

    @Test
    public void unconfinedFlow() {
        Flow.interval(100, TimeUnit.MILLISECONDS)
                .onCollect(TestX.collector("A"))
                .flowOn(Schedulers.unconfined())
                .take(10)
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
