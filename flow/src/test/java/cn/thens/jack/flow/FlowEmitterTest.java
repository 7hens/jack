package cn.thens.jack.flow;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import cn.thens.jack.TestX;

/**
 * @author 7hens
 */
public class FlowEmitterTest {
    @Test
    public void publish() {
        test(FlowEmitter.publish());
    }

    @Test
    public void behavior() {
        test(FlowEmitter.behavior());
        test(FlowEmitter.behavior(100L));
    }

    @Test
    public void replay() {
        test(FlowEmitter.replay());
    }

    private void test(FlowEmitter<Long> emitter) {
        emitter.asFlow()
                .onCollect(TestX.collector("A"))
                .collect();

        TestX.sleep(100);
        emitter.next(-1L);

        Flow.interval(100, TimeUnit.MILLISECONDS)
                .onCollect(emitter)
                .take(5)
                .collect();
        emitter.asFlow()
                .onCollect(TestX.collector("B"))
                .collect();

        TestX.sleep(600);
        emitter.asFlow()
                .onCollect(TestX.collector("C"))
                .to(TestX.collect());
    }

    @Test
    public void lazyFlow() {
        Flow<Long> publishFlow = Flow.interval(1, TimeUnit.SECONDS)
                .onCollect(TestX.collector("publish"))
                .publish(FlowEmitter::publish);

        Flow.timer(3, TimeUnit.SECONDS)
                .onCollect(TestX.collector("Delay 3s"))
                .flatMap(it -> publishFlow)
                .onCollect(TestX.collector("3s"))
                .take(1)
                .collect();

        Flow.timer(6, TimeUnit.SECONDS)
                .onCollect(TestX.collector("Delay 6s"))
                .flatMap(it -> publishFlow)
                .onCollect(TestX.collector("6s"))
                .take(3)
                .to(TestX.collect());
    }

    @Test
    public void autoCancel() {
        Flow<Long> publishFlow = Flow.interval(100, TimeUnit.MILLISECONDS)
                .take(2)
//                .concatWith(Flow.error(new NullPointerException()))
                .onCollect(TestX.collector("publish"))
                .publish(() -> FlowEmitter.<Long>behavior().autoCancel());

//        Flow.timer(400, TimeUnit.MILLISECONDS)
////                .onCollect(TestX.collector("Delay 4"))
//                .flatMap(it -> publishFlow)
//                .onCollect(TestX.collector("4"))
//                .collect();

        Flow.timer(800, TimeUnit.MILLISECONDS)
//                .onCollect(TestX.collector("Delay 8"))
                .flatMap(it -> publishFlow)
                .take(1)
                .onCollect(TestX.collector("8"))
                .to(TestX.collect());
    }
}
