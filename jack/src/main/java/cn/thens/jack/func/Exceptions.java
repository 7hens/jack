package cn.thens.jack.func;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class Exceptions {
    private Exceptions() {
    }

    public static RuntimeException wrap(Throwable e) {
        if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        }
        return new Wrapper(e);
    }

    public static Throwable unwrap(Throwable e) {
        if (e instanceof Wrapper) {
            return unwrap(e.getCause());
        }
        return e;
    }

    public static String getStackTraceString(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, false);
        e.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

    private static final class Wrapper extends RuntimeException {
        private Wrapper(Throwable cause) {
            super(cause);
        }
    }
}
