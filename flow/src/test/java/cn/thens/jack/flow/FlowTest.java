package cn.thens.jack.flow;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import cn.thens.jack.TestX;
import cn.thens.jack.func.Actions;
import cn.thens.jack.func.Func1;
import cn.thens.jack.func.Funcs;
import cn.thens.jack.func.Values;
import cn.thens.jack.scheduler.Schedulers;

/**
 * @author 7hens
 */
public class FlowTest {
    @Test
    public void interval() {
        AtomicInteger i = new AtomicInteger();
        TestX.Logger logger = TestX.logger();
        Flow.interval(600, TimeUnit.MILLISECONDS)
//                .mergeWith(Flow.interval(200, TimeUnit.MILLISECONDS))
//                .mergeWith(Flow.interval(300, TimeUnit.MILLISECONDS))
                .flatMap(Funcs.always(Flow.interval(100, TimeUnit.MILLISECONDS)
                                .onCollect(TestX.collector("A"))
                                .flowOn(TestX.scheduler("a"))
                                .take(3)
//                        .onCollect(TestX.collector("B"))
                                .flowOn(TestX.scheduler("b"))
                                .delayStart(Flow.timer(100, TimeUnit.MILLISECONDS))
                                .onStart(it -> logger.log("============================="))
                ))
                .flowOn(TestX.scheduler("c"))
                .take(10)
                .to(TestX.collect());
    }

    @Test
    public void error() {
        Flow.create(Actions.error(new NullPointerException()))
                .onCollect(TestX.collector("A"))
                .flowOn(TestX.scheduler("a"))
                .to(TestX.collect());

        Flow.error(new NullPointerException())
                .onCollect(TestX.collector("B"))
                .flowOn(TestX.scheduler("b"))
                .to(TestX.collect());
    }

    @Test
    public void take() {
        Flow.interval(100, TimeUnit.MILLISECONDS)
                .onCollect(TestX.collector("A"))
                .flowOn(TestX.scheduler("a"))
                .take(3)
                .map(it -> it + 100)
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
    public void skipTimeout() {
        Flow.interval(100, TimeUnit.MILLISECONDS)
                .onCollect(TestX.collector("Before"))
                .skip(Flow.timer(3, TimeUnit.SECONDS))
                .onCollect(TestX.collector("After"))
                .take(3)
                .flowOn(TestX.scheduler("a"))
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
    public void skipAll() {
        Flow.interval(100, TimeUnit.MILLISECONDS)
                .onCollect(TestX.collector("A"))
                .take(3)
                .skipAllTo(Flow.just(100))
                .onCollect(TestX.collector("B"))
                .to(TestX.collect());
        Flow.empty()
                .skipAllTo(Flow.just(1001))
                .onCollect(TestX.collector("C"))
                .to(TestX.collect());
        Flow.error(new NullPointerException())
                .skipAllTo(Flow.just(1001))
                .onCollect(TestX.collector("D"))
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
        Flow.timer(200, TimeUnit.MILLISECONDS)
                .onCollect(TestX.collector("A"))
                .timeout(Flow.timer(100, TimeUnit.MILLISECONDS), Flow.just(33L))
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
    public void catchError() {
        Flow.just(0, 1)
                .onEach(it -> Values.require(it <= 0))
                .onCollect(TestX.collector("A"))
                .flowOn(TestX.scheduler("a"))
                .catchError(Flow.just(100))
                .onCollect(TestX.collector("B"))
                .flowOn(TestX.scheduler("b"))
                .to(TestX.collect());

//        Flow.<Integer>create(Actions.error(new NullPointerException()))
        Flow.single(Funcs.error(new NullPointerException()))
                .onCollect(TestX.collector("C"))
                .flowOn(TestX.scheduler("c"))
                .catchError(Flow.just(100))
                .onCollect(TestX.collector("D"))
                .flowOn(TestX.scheduler("d"))
                .to(TestX.collect());


    }

    @Test
    public void retry() {
        Flow.interval(100, TimeUnit.MILLISECONDS)
                .take(3)
                .map(it -> {
                    if (it > 1) throw new Exception();
                    return it;
                })
                .onCollect(TestX.collector("A"))
                .retry(4)
                .onCollect(TestX.collector("B"))
                .to(TestX.collect());

        Flow.interval(100, TimeUnit.MILLISECONDS)
                .take(10)
                .map(it -> {
                    if (it > 1) throw new Exception();
                    return it;
                })
                .onCollect(TestX.collector("C"))
                .retry(Flow.timer(400, TimeUnit.MILLISECONDS))
                .onCollect(TestX.collector("D"))
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
    public void delayError() {
        Flow.interval(100, TimeUnit.MILLISECONDS)
                .onCollect(TestX.collector("A"))
                .onEach(it -> Values.require(it < 3L))
                .delayError(Flow.timer(3, TimeUnit.SECONDS))
//                .take(3)
                .onCollect(TestX.collector("B"))
                .to(TestX.collect());
    }

    @Test
    public void emitter() {
        AtomicInteger i = new AtomicInteger();
        Flow.create(emitter -> {
            int data = i.incrementAndGet();
            for (int j = 0; j < 10; j++) {
                emitter.data(data + "." + j);
            }
            emitter.complete();
        })////////
                .delayStart(Flow.timer(10, TimeUnit.MILLISECONDS))
                .onCollect(TestX.collector("A"))
                .flowOn(Schedulers.io())
                .delay(Flow.empty())
                .onCollect(TestX.collector("B"))
                .flowOn(Schedulers.io())
                .repeat()
                .ifEmpty(Flow.error(new NullPointerException()))
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
    public void groupBy() {
        Flow.interval(100, TimeUnit.MILLISECONDS)
                .groupBy(it -> it % 3)
                .map(it -> it.onCollect(TestX.collector("A:" + it.getKey())))
                .to(FlowX.poly())
                .flatMerge()
                .onCollect(TestX.collector("B"))
                .take(10)
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
        throttle(it -> it.throttleFirst(Flow.timer(2, TimeUnit.SECONDS)));
    }

    @Test
    public void throttleLast() {
        throttle(it -> it.throttleLast(Flow.timer(2, TimeUnit.SECONDS)));
    }

    private void throttle(Func1<Flow<String>, Flow<String>> operator) {
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
                .to(operator)
                .onCollect(TestX.collector("B"))
                .take(3)
                .to(TestX.collect());
    }

    @Test
    public void lift() {
        Collector<Long> collector = TestX.collector("Lift");
        Flow.interval(100, TimeUnit.MILLISECONDS)
                .onCollect(TestX.collector("Before"))
                .take(5)
                .lift(emitter -> collector)
                .timeout(Flow.timer(1, TimeUnit.SECONDS))
                .to(TestX.collect());

        Flow.<Long>error(new NullPointerException())
                .lift(emitter -> collector)
                .flowOn(TestX.scheduler("c"))
                .timeout(Flow.timer(1, TimeUnit.SECONDS))
                .to(TestX.collect());
    }

    private void backPressure(BackPressure<Long> backpressure) {
        Flow.interval(100, TimeUnit.MILLISECONDS)
                .take(100)
//                .onCollect(TestX.collector("A"))
                .onBackPressure(backpressure)
                .map(it -> {
                    TestX.sleep(1000);
                    return it;
                })
//                .take(6)
                .onCollect(TestX.collector("B"))
                .flowOn(Schedulers.core())
                .to(TestX.collect());
    }

    @Test
    public void rangeOnBackpressure() {
        Flow.range(0, 20)
                .onCollect(TestX.collector("A"))
                .onBackPressure(BackPressures.<Integer>buffer(4).dropOldest())
                .flowOn(TestX.scheduler("a"))
//                .onBackpressure(Backpressure.buffer(16))
                .onCollect(TestX.collector("B"))
                .flowOn(TestX.scheduler("b"))
                .to(TestX.collect());
    }

    @Test
    public void onBackpressureBuffer() {
        backPressure(BackPressures.buffer(32));
    }

    @Test
    public void onBackpressureDropOldest() {
        backPressure(BackPressures.<Long>buffer(2).dropOldest());
    }

    @Test
    public void onBackpressureDropLatest() {
        backPressure(BackPressures.<Long>buffer(2).dropLatest());
    }

    @Test
    public void onBackpressureDropAll() {
        backPressure(BackPressures.<Long>buffer(2).dropAll());
    }

    @Test
    public void copy() throws FileNotFoundException {
        File sourceFile = new File("build.gradle");
        File destFile = new File("build/build.gradle");
        Flow.copy(new FileInputStream(sourceFile), new FileOutputStream(destFile))
                .onCollect(TestX.collector("A"))
                .to(TestX.collect());
    }

    @Test
    public void terminalPoly() {
        Flow.just(
                Flow.timer(100, TimeUnit.MILLISECONDS)
                        .onCollect(TestX.collector("A")),
                Flow.timer(200, TimeUnit.MILLISECONDS)
                        .onCollect(TestX.collector("B")),
                Flow.error(new NullPointerException())
                        .delayError(Flow.timer(300, TimeUnit.MILLISECONDS))
                        .onCollect(TestX.collector("C"))
        ).to(FlowX.terminalPoly()).flatMerge()
                .onCollect(TestX.collector("X"))
                .to(TestX.collect());
    }

    @Test
    public void sample() {
        Flow.interval(100, TimeUnit.MILLISECONDS)
//                .onCollect(TestX.collector("A"))
                .take(20)
                .sampleFirst(Flow.interval(500, TimeUnit.MILLISECONDS))
                .onCollect(TestX.collector("B"))
                .to(TestX.collect());

        Flow.interval(100, TimeUnit.MILLISECONDS)
//                .onCollect(TestX.collector("C"))
                .take(20)
                .sampleLast(Flow.interval(500, TimeUnit.MILLISECONDS))
                .onCollect(TestX.collector("D"))
                .to(TestX.collect());
    }

    @Test
    public void ifEmpty() {
        Flow.empty().ifEmpty(Flow.just(100))
                .onCollect(TestX.collector("empty"))
                .to(TestX.collect());

        Flow.error(new NullPointerException()).ifEmpty(Flow.just(0))
                .onCollect(TestX.collector("error"))
                .to(TestX.collect());

        Flow.just(0).ifEmpty(Flow.just(100))
                .onCollect(TestX.collector("just"))
                .to(TestX.collect());
    }

    @Test
    public void repeat() {
        Flow.interval(100, TimeUnit.MILLISECONDS)
                .take(1)
                .onCollect(TestX.collector("A"))
                .flowOn(TestX.scheduler("a"))
                .repeat(3)
                .onCollect(TestX.collector("B"))
                .flowOn(TestX.scheduler("b"))
                .to(TestX.collect());
    }
}
