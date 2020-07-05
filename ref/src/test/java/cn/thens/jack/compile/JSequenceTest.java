package cn.thens.jack.compile;

import org.junit.Test;

import cn.thens.jack.util.JSequence;

import static junit.framework.TestCase.assertEquals;

/**
 * @author 7hens
 */
public class JSequenceTest {
    @Test
    public void toArray() {
        String[] values = JSequence.of("0", "1", "2", "3", "4").toArray(new String[0]);
        assertEquals(5, values.length);
        for (int i = 0; i < 5; i++) {
            assertEquals("" + i, values[i]);
        }
    }
}
