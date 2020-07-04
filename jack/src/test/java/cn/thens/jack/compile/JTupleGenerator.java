package cn.thens.jack.compile;

import org.junit.Test;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;

import cn.thens.jack.func.Func1;

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

    @Test
    public void generateClasses2() {
        StringBuilder source = new StringBuilder();
        source.append("\t//region classes\n\n");
        int max = 9;
        for (int i = 1; i <= max; i++) {
            String types = joinToString(i, "V");
            String params = joinToString(i, ", ", it -> String.format("V%d v%d", it, it));
            String args = joinToString(i, "v");
            String superTypes = joinToString(i - 1, "V");
            String superParams = joinToString(i - 1, ", ", it -> String.format("V%d v%d", it, it));
            String superArgs = joinToString(i - 1, "v");
            source.append(String.format("private static class T%d<%s>", i, types))
                    .append(String.format(" extends T%d<%s>", i - 1, superTypes))
                    .append(String.format("\n\t\timplements JTuple%d<%s> {", i, types))
                    .append(String.format("\n\tprivate final V%d v%d;", i, i))
                    .append(String.format("\n\n\tprotected T%d(%s) {", i, params))
                    .append(String.format("\n\t\tsuper(%s);", superArgs))
                    .append(String.format("\n\t\tthis.v%d = v%d;", i, i))
                    .append("\n\t}")
                    .append("\n\n\t@Override")
                    .append(String.format("\n\tpublic V%d v%d() {", i, i))
                    .append(String.format("\n\t\treturn this.v%d;", i))
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

    private String joinToString(int count, String separator, Func1<Integer, String> transformer) {
        List<String> list = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            list.add(transformer.invoke(i));
        }
        return String.join(separator, list);
    }

    private String joinToString(int count, String prefix) {
        return joinToString(count, ", ", it -> prefix + it);
    }
}
