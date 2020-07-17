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

        TestX.delay(100);
        emitter.data(-1L);

        Flow.interval(100, TimeUnit.MILLISECONDS)
                .onEach(emitter::data)
                .onTerminate(it -> emitter.complete())
                .take(5)
                .collect();
        emitter.asFlow()
                .onCollect(TestX.collector("B"))
                .collect();

        TestX.delay(600);
        emitter.asFlow()
                .onCollect(TestX.collector("C"))
                .to(TestX.collect());
    }
}
