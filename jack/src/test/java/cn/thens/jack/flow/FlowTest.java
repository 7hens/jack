package cn.thens.jack.flow;

import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import cn.thens.jack.TestX;
import cn.thens.jack.scheduler.Schedulers;

/**
 * @author 7hens
 */
public class FlowTest {
    @Test
    public void take() {
        Flow.interval(100, TimeUnit.MILLISECONDS)
                .onCollect(TestX.collector("A"))
                .flowOn(TestX.scheduler("a"))
                .take(3)
                .map(it -> it + "..")
                .onCollect(TestX.collector("B"))
                .flowOn(TestX.scheduler("b"))
                .to(TestX.collect());
    }

    @Test
    public void takeUtil() {
        Flow.interval(1, TimeUnit.SECONDS)
                .takeUntil(3L)
                .onCollect(TestX.collector("A"))
                .to(TestX.collect());
    }

    @Test
    public void takeLast() {
        Flow.interval(1, TimeUnit.SECONDS)
                .onCollect(TestX.collector("A"))
                .take(10)
                .takeLast(5)
                .onCollect(TestX.collector("B"))
                .to(TestX.collect());
    }

    @Test
    public void skipLast() {
        Flow.interval(1, TimeUnit.SECONDS)
                .onCollect(TestX.collector("A"))
                .take(10)
                .skipLast(5)
                .onCollect(TestX.collector("B"))
                .to(TestX.collect());
    }

    @Test
    public void elementAt() {
        Flow.interval(1, TimeUnit.SECONDS)
                .onCollect(TestX.collector("A"))
                .take(10)
                .elementAt(5)
                .onCollect(TestX.collector("B"))
                .to(TestX.collect());

        Flow.interval(1, TimeUnit.SECONDS)
                .onCollect(TestX.collector("C"))
                .take(10)
                .elementAt(-5)
                .onCollect(TestX.collector("D"))
                .to(TestX.collect());
    }

    @Test
    public void range() {
        Flow.range(10, 1)
                .onCollect(TestX.collector("A"))
                .to(TestX.collect());
    }

    @Test
    public void timeout() {
        Flow.timer(2, TimeUnit.SECONDS)
                .onCollect(TestX.collector("A"))
                .timeout(1, TimeUnit.SECONDS)
                .onCollect(TestX.collector("B"))
                .to(TestX.collect());
    }

    @Test
    public void flowOn() {
        Flow.<String>create(emitter -> {
            String threadName = Thread.currentThread().getName();
            TestX.logger().log("emitter (" + threadName + ")");

            emitter.data("hello");
            emitter.data("world");
            emitter.cancel();
            emitter.complete();
        })/////////
                .onCollect(TestX.collector("A"))
                .flowOn(TestX.scheduler("a"))
                .map(it -> it + "..")
                .onCollect(TestX.collector("B"))
                .flowOn(TestX.scheduler("b"))
                .to(TestX.collect());
    }

    @Test
    public void retry() {
        Flow.range(1, 10)
                .map(it -> {
                    if (it > 1) throw new Exception();
                    return it;
                })
                .onCollect(TestX.collector("A"))
                .retry(3)
                .onCollect(TestX.collector("B"))
                .to(TestX.collect());
    }

    @Test
    public void autoSwitch() {
        Flow.interval(1, TimeUnit.SECONDS)
                .onCollect(TestX.collector("A"))
                .autoCancel(Flow.timer(2, TimeUnit.SECONDS))
                .onCollect(TestX.collector("B"))
                .to(TestX.collect());

        Flow.interval(1, TimeUnit.SECONDS)
                .onCollect(TestX.collector("C"))
                .autoSwitch(Flow.timer(2, TimeUnit.SECONDS), Flow.just(22L))
                .onCollect(TestX.collector("D"))
                .to(TestX.collect());
    }

    @Test
    public void reduce() {
        Flow.interval(1, TimeUnit.SECONDS)
                .take(5)
                .onCollect(TestX.collector("A"))
                .reduce(Long::sum)
                .onCollect(TestX.collector("B"))
                .to(TestX.collect());

        Flow.interval(1, TimeUnit.SECONDS)
                .take(5)
                .onCollect(TestX.collector("C"))
                .reduce(100L, Long::sum)
                .onCollect(TestX.collector("D"))
                .to(TestX.collect());
    }

    @Test
    public void delay() {
        Flow.interval(1, TimeUnit.SECONDS)
                .onCollect(TestX.collector("A"))
                .take(3)
                .delay(Flow.timer(5, TimeUnit.SECONDS))
                .onCollect(TestX.collector("B"))
                .to(TestX.collect());
    }

    @Test
    public void delayStart() {
        Flow.interval(1, TimeUnit.SECONDS)
                .onCollect(TestX.collector("A"))
                .take(3)
                .delayStart(Flow.timer(5, TimeUnit.SECONDS))
                .onCollect(TestX.collector("B"))
                .to(TestX.collect());
    }

    @Test
    public void toList() {
        Flow.interval(1, TimeUnit.SECONDS)
                .take(3)
                .toList()
                .onCollect(TestX.collector("A"))
                .to(TestX.collect());
    }

    @Test
    public void buffer() {
        Flow.interval(1, TimeUnit.SECONDS)
                .buffer(Flow.interval(2, TimeUnit.SECONDS))
                .take(5)
                .onCollect(TestX.collector("B"))
                .to(TestX.collect());
    }

    @Test
    public void bufferFilter() {
        Flow.interval(1, TimeUnit.SECONDS)
                .buffer(3)
                .take(5)
                .onCollect(TestX.collector("B"))
                .to(TestX.collect());
    }

    @Test
    public void throttleFirst() {
        throttle(FlowX.pipe(it -> it.throttleFirst(Flow.timer(2, TimeUnit.SECONDS))));
    }

    @Test
    public void throttleLast() {
        throttle(FlowX.pipe(it -> it.throttleLast(Flow.timer(2, TimeUnit.SECONDS))));
    }

    private void throttle(FlowOperator<String, String> operator) {
        AtomicLong count = new AtomicLong(0);
        final long startTime = System.currentTimeMillis();
        Flow.just(1, 1, 3)
                .mapToFlow(it -> Flow.timer(it, TimeUnit.SECONDS))
                .flatConcat()
                .repeat()
                .map(it -> {
                    long elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000;
                    return count.incrementAndGet() + " " + elapsedSeconds + "s";
                })
                .onCollect(TestX.collector("A"))
                .transform(operator)
                .onCollect(TestX.collector("B"))
                .take(3)
                .to(TestX.collect());
    }

    @Test
    public void pipe() {
        Flow.just(1, 2, 3)
                .onCollect(TestX.collector("A"))
                .transform(FlowX.pipe(it -> it.map(i -> i + 10)))
                .onCollect(TestX.collector("B"))
                .to(TestX.collect());
    }

    private void backpressure(Backpressure<Long> backpressure) {
        Flow.interval(100, TimeUnit.MILLISECONDS)
                .take(100)
//                .onCollect(TestX.collector("A"))
                .onBackpressure(backpressure)
                .map(it -> {
                    TestX.delay(1000);
                    return it;
                })
//                .take(6)
                .onCollect(TestX.collector("B"))
                .flowOn(Schedulers.core())
                .to(TestX.collect());
    }

    @Test
    public void onBackpressureBuffer() {
        backpressure(Backpressure.<Long>buffer(32));
    }

    @Test
    public void onBackpressureDropOldest() {
        backpressure(Backpressure.<Long>buffer(2).dropOldest());
    }

    @Test
    public void onBackpressureDropLatest() {
        backpressure(Backpressure.<Long>buffer(2).dropLatest());
    }

    @Test
    public void onBackpressureDropAll() {
        backpressure(Backpressure.<Long>buffer(2).dropAll());
    }
}
