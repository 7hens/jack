package cn.thens.jack.scheduler;

import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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

        TestX.sleep(2000);
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
    public void interval() {
        TestX.Logger logger = TestX.logger();
        Schedulers.timer().interval(10, TimeUnit.MILLISECONDS)
                .schedule(() -> logger.log("A"));
        TestX.sleep(1000);
    }

    @Test
    public void core() {
        TestX.Logger logger = TestX.logger();
        logger.log("availableProcessors: " + Runtime.getRuntime().availableProcessors());
        AtomicInteger count = new AtomicInteger(0);

        Flow<Object> blockingFLow = Flow.complete(() -> TestX.block(100))
                .flowOn(Schedulers.core())
                .skipAllTo(Flow.just(0));

        Flow.interval(20, TimeUnit.MILLISECONDS)
                .flatMap(it -> blockingFLow)
                .take(100)
                .map(it -> count.getAndIncrement())
                .onCollect(TestX.collector("A"))
                .flowOn(Schedulers.core())
                .to(TestX.collect());
    }

    @Test
    public void stackTrace() {
        TestX.Logger logger = TestX.logger();
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        Schedulers.core().schedule(() -> {
            for (StackTraceElement element : stackTrace) {
                logger.log(element.toString());
            }
        });
        TestX.sleep(1000L);
    }
}
