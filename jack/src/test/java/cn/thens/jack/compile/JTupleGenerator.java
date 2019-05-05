package cn.thens.jack.compile;

import org.junit.Test;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;

import cn.thens.jack.func.JFunc;

public class JTupleGenerator {
    @Test
    public void generateMethods() {
        StringBuilder source = new StringBuilder();
        source.append("\t//region methods\n\n");
        int max = 9;
        for (int i = 1; i <= max; i++) {
            String types = joinToString(i, "V");
            String params = joinToString(i, ", ", it -> String.format("V%d v%d", it, it));
            String args = joinToString(i, "v");
            source.append(String.format("public static <%s> T%d<%s> \nof(%s) {", types, i, types, params))
                    .append(String.format("\n\t return new T%d<>(%s);", i, args))
                    .append("\n}\n\n");
        }
        source.append("\t//endregion");
        System.out.println(source);
        copyToClipboard(source);
    }

    @Test
    public void generateClasses() {
        StringBuilder source = new StringBuilder();
        source.append("\t//region classes\n\n");
        int max = 9;
        for (int i = 1; i <= max; i++) {
            String types = joinToString(i, "V");
            String params = joinToString(i, ", ", it -> String.format("V%d v%d", it, it));
            String args = joinToString(i, "v");
            source.append(String.format("public static final class T%d<%s> implements Base {", i, types))
                    .append(joinToString(i, "", it -> String.format("\n\tpublic final V%d v%d;", it, it)))
                    .append(String.format("\n\n\tprotected T%d(%s) {", i, params))
                    .append(joinToString(i, "", it -> String.format("\n\t\tthis.v%d = v%d;", it, it)))
                    .append("\n\t}")
                    .append("\n}\n\n");
        }
        source.append("\t//endregion");
        System.out.println(source);
        copyToClipboard(source);
    }

    private void copyToClipboard(Object obj) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(obj.toString()), null);
    }

    private String joinToString(int count, String separator, JFunc.T1<Integer, String> transformer) {
        List<String> list = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            list.add(transformer.call(i));
        }
        return String.join(separator, list);
    }

    private String joinToString(int count, String prefix) {
        return joinToString(count, ", ", it -> prefix + it);
    }
}
