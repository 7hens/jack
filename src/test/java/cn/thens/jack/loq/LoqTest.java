package cn.thens.jack.loq;

import org.junit.Before;
import org.junit.Test;

public class LoqTest {
    @Before
    public void init() {
        LoqX.setDefaultInstance(Loq.logger(new PrettyLogger(Logger.SYSTEM)));
    }

    @Test
    public void longText() {
    }

    @Test
    public void error() {
        Loq.e(new Throwable());
    }
}
