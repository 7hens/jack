package cn.thens.jack.flow;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import cn.thens.jack.TestX;
import cn.thens.jack.func.Func1;
import cn.thens.jack.func.Things;

/**
 * @author 7hens
 */
public class PolyFlowTest {
    private void flat(Func1<PolyFlow<String>, Flow<String>> converter) {
        // 0. 0, 1, 2, 3, 4, 5
        // 1.       0, 1, 2, 3, 4, 5
        // 2.             0, 1, 2, 3, 4, 5
        Flow.interval(2, TimeUnit.SECONDS)
                .take(3)
                .onCollect(TestX.collector("A"))
                .flowOn(TestX.scheduler("a"))
                .mapToFlow(it -> Flow.interval(1, TimeUnit.SECONDS)
                        .take(5)
                        .onEach(i -> Things.require(i < 4))
                        .map(i -> it + "." + i))
                .polyTo(converter)
                .onCollect(TestX.collector("B"))
                .flowOn(TestX.scheduler("b"))
                .to(TestX.collect());
    }

    @Test
    public void delayErrors() {
        flat(flow -> flow.delayErrors().flatMerge());
    }

    @Test
    public void flatZip() {
        flat(flow -> flow.flatZip().map(it -> String.join(", ", it)));
    }

    @Test
    public void flatSwitch() {
        flat(PolyFlow::flatSwitch);
    }

    @Test
    public void flatMerge() {
        flat(PolyFlow::flatMerge);
    }

    @Test
    public void flatConcat() {
        flat(PolyFlow::flatConcat);
    }

    @Test
    public void flatJoin() {
        flat(flow -> flow.flatJoin().map(it -> String.join(", ", it)));
    }
}
