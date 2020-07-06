package cn.thens.jack.ref;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author 7hens
 */
public class RefTest {
    @Test
    public void elvis() {
        Assert.assertEquals("hello", Ref.<String>empty().elvis("hello"));
        Assert.assertEquals("hello", Ref.of("hello").elvis("world"));
    }

    @Test
    public void mutable() {
        TestX.Logger logger = TestX.logger();
        MutableRef<Integer> ref = Ref.of(123)
                .mutable()
                .observe((v1, v2) -> {
                    logger.log("v1: " + v1 + ", v2: " + v2);
                });
        ref.set(56);
        ref.set(78);
        logger.log("result: " + ref.get());
    }

    @Test
    public void property() {
        TestX.Logger logger = TestX.logger();
        AtomicReference<String> hello = new AtomicReference<>("hello");
        MutableRef<String> ref = Ref.from(hello::get).mutable(hello::set);
        Assert.assertEquals("hello", ref.get());
        ref.set("world");
        Assert.assertEquals("world", hello.get());
    }
}
